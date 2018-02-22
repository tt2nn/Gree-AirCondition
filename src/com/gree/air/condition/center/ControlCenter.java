package com.gree.air.condition.center;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.gpio.GpioPin;
import com.gree.air.condition.tcp.TcpServer;
import com.gree.air.condition.tcp.model.LoginModel;
import com.gree.air.condition.tcp.model.ParamModel;
import com.gree.air.condition.tcp.model.TimeModel;
import com.gree.air.condition.tcp.model.TransmitModel;
import com.gree.air.condition.uart.model.DoChoose;

/**
 * 工作中心
 * 
 * @author lihaotian
 *
 */
public class ControlCenter {

	private static boolean waittingHeart = false;

	// 故障标志位
	private static int Transmit_Mark_Error = 0;
	// 异常标志位
	private static int Transmit_Mark_Warning = 0;
	// 参数变化标志位
	private static int Transmit_Mark_Change = 0;
	// 开机标志位
	private static int Transmit_Mark_Boot = 0;

	public static long Transmit_Period_Time = 0L;

	/**
	 * 判断App是否可以工作
	 * 
	 * @return
	 */
	public static boolean canWorking() {

		if (Constant.Init_Success && Constant.Gprs_Choosed && Constant.System_Time > 946656000000L) {

			return true;
		}

		return false;
	}

	/**
	 * 缓存机组数据
	 */
	public static void cacheData() {

		DataCenter.saveDataBuffer();
	}

	/**
	 * 打包机组数据
	 */
	public static void packageData() {

		ControlTimer.packageTime = Constant.System_Time;
		DataCenter.packageData();
	}

	/* =========== TCP 通信相关 ============== */

	/**
	 * 登录
	 */
	public static void login() {

		LoginModel.login();
	}

	/**
	 * 心跳
	 */
	public static void heartBeat() {

		Constant.Heart_Beat_Time += 10 * 1000;

		if (!TcpServer.isServerWorking()) {

			TcpServer.startServer();
			return;
		}

		TimeModel.heart();
	}

	/**
	 * 心跳响应处理
	 */
	public static void heartBeatResp() {

		if (waittingHeart) {

			ControlCenter.requestStartUpload();

		} else if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

			ControlCenter.stopTcpServer();
		}

	}

	/**
	 * 请求开始传输
	 */
	public static void requestStartUpload() {

		if (!TcpServer.isServerWorking()) {

			waittingHeart = true;

			TcpServer.startServer();

			return;
		}

		waittingHeart = false;
		TransmitModel.start();
	}

	/**
	 * 发送GPRS模块信息
	 */
	public static void sendGprsSignal() {

		ParamModel.gprsSignal();
	}

	/**
	 * 传输数据
	 * 
	 * @param length
	 * @param time
	 */
	public static void transmitData(int length, long time) {

		TransmitModel.dataTransm(length, time);
	}

	/**
	 * 停止TCP
	 */
	public static void stopTcpServer() {

		TcpServer.stopServer();
	}

	/* =========== 数据中心控制相关 ============== */

	/**
	 * 重新选举
	 */
	public static void chooseRest() {

		Constant.Gprs_Choosed = false;
		GpioPin.communicationDark();
		FileWriteModel.setNotChoosed();
		DataCenter.destoryUploadData();

	}

	/**
	 * 选中GPRS
	 */
	public static void chooseGprs() {

		Constant.Gprs_Choosed = true;
		GpioPin.communicationLight();
		FileWriteModel.setIsChoosed();
		DataCenter.chooseTransmit();
	}

	/**
	 * 实时传输
	 */
	public static void alwaysTransmit() {

		DataCenter.alwaysTransmit();
	}

	/**
	 * 上电点名
	 */
	public static void powerCall() {

		DoChoose.choosed();
		GpioPin.communicationLight();
		DataCenter.powerTransmit();
	}

	/**
	 * 按键上报
	 */
	public static void pushKeyTransmit() {

		DataCenter.pushKeyTransmit();

	}

	/**
	 * 启动开机上报
	 */
	public static void startBootTransmit() {

		Transmit_Period_Time = 0L;
		DataCenter.registerBootTransmit();
	}

	/**
	 * 周期性开机上报
	 */
	public static void periodBootTransmit() {

		Transmit_Period_Time = Constant.System_Time;
		DataCenter.bootTransmit();

	}

	/**
	 * 启动打卡上报
	 */
	public static void startCheckTransmit() {

		Transmit_Period_Time = 0L;
		DataCenter.registerCheckTransmit();
	}

	/**
	 * 周期打卡上报
	 */
	public static void periodCheckTransmit() {

		Transmit_Period_Time = Constant.System_Time;

		DataCenter.checkTransmit();
	}

	/**
	 * 判断是否进行打卡上报
	 * 
	 * @return
	 */
	public static boolean isCheckTransmit() {

		if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_CHECK) {

			return true;
		}

		return false;
	}

	/**
	 * 上传数据
	 */
	public static void uploadData() {

		DataCenter.notifyUploadData();
	}

	/**
	 * 停止上传
	 */
	public static void stopUploadData() {

		DataCenter.stopUploadData();
	}

	/**
	 * 销毁上报
	 */
	public static void destoryUploadData() {

		DataCenter.destoryUploadData();
	}

	/**
	 * 设置标志位
	 * 
	 * @param error
	 * @param warning
	 * @param change
	 * @param boot
	 */
	public static void setMarker(int error, int warning, int change, int boot) {

		if (Constant.Transmit_Power_Type == Constant.TRANSMIT_TYPE_BOOT && Transmit_Mark_Boot == 0 && boot == 1) {

			// 开机上报时，开机标志位为1，启动开机实时上报
			DataCenter.registerBootTransmit();

		} else if (Transmit_Mark_Error == 0 && error == 1) {

			// 故障标志位由0-1，启动故障上报
			DataCenter.errorTransmit();

		} else if (Transmit_Mark_Change == 0 && change == 1) {

			// 厂家参数变化标志位由0-1，启动参数变化上报
			DataCenter.changeTransmit();

		} else if (Transmit_Mark_Warning == 0 && warning == 1) {

			// 亚健康标志位由0-1，启动亚健康上报
			DataCenter.warningTransmit();

		} else if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_WARNING && warning == 0) {

			// 亚健康标志位由0-1，停止亚健康上报
			DataCenter.Transmit_Cache_Warning = false;
			DataCenter.stopUploadData();

		} else if (DataCenter.Transmit_Cache_Warning && warning == 1) {

			// 缓存上报模式为亚健康上报，标志位为1，继续亚健康上报
			DataCenter.warningTransmit();

		} else if (Constant.Transmit_Power_Type == Constant.TRANSMIT_TYPE_BOOT && Transmit_Mark_Boot == 1
				&& boot == 0) {

			// 开机上报时，开机标志位为0，启动开机周期上报
			DataCenter.registerBootTransmit();
		}

		Transmit_Mark_Error = error;
		Transmit_Mark_Warning = warning;
		Transmit_Mark_Change = change;
		Transmit_Mark_Boot = boot;

	}

	public static boolean getBootMark() {

		if (Transmit_Mark_Boot == 1) {

			return true;
		}

		return false;
	}

}
