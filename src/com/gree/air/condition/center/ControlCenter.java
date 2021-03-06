package com.gree.air.condition.center;

import com.gree.air.condition.Run;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.file.FileModel;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.gpio.GpioPin;
import com.gree.air.condition.gpio.GpioTool;
import com.gree.air.condition.sms.SmsServer;
import com.gree.air.condition.tcp.TcpServer;
import com.gree.air.condition.tcp.model.LoginModel;
import com.gree.air.condition.tcp.model.ParamModel;
import com.gree.air.condition.tcp.model.TimeModel;
import com.gree.air.condition.tcp.model.TransmitModel;
import com.gree.air.condition.uart.UartServer;
import com.gree.air.condition.uart.model.DoChoose;
import com.gree.air.condition.variable.Variable;

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
	private static int Transmit_Mark_Open = 0;
	// 关机标志位
	private static int Transmit_Mark_Close = 0;

	public static long Transmit_Period_Time = 0L;

	// 上报达到停止标志位
	public static boolean Arrive_Stop_Mark = false;

	// 判断模块是否登录
	public static boolean Gprs_Login = false;

	public static boolean Warning_Transmit = false;

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

		Gprs_Login = true;
		TimeModel.heart();
	}

	/**
	 * 心跳响应处理
	 */
	public static void heartBeatResp() {

		if (waittingHeart) {

			requestStartUpload();

		} else if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

			stopTcpServer();
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

		if (!Gprs_Login) {

			waittingHeart = true;
			return;
		}

		waittingHeart = false;
		TransmitModel.start();
	}

	/**
	 * 恢复数据上传
	 */
	public static void recoverUpload() {

		Constant.GPRS_ERROR_TYPE = Constant.GPRS_ERROR_TYPE_NO;
		waittingHeart = true;
		login();
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
		Gprs_Login = false;
	}

	/* =========== 数据中心控制相关 ============== */

	/**
	 * 重新选举
	 */
	public static void chooseRest() {

		DoChoose.reset();
		Constant.Gprs_Choosed = false;
		GpioPin.communicationDark();
		GpioPin.errorDark();
		Constant.GPRS_ERROR_TYPE = Constant.GPRS_ERROR_TYPE_NO;
		FileWriteModel.setNotChoosed();
		destoryUploadData();
		Variable.Transmit_Cache_Type = Constant.TRANSMIT_TYPE_CHECK;
	}

	/**
	 * 选中GPRS
	 */
	public static void chooseGprs() {

		ControlTimer.Choose_Prower_Time = 0;
		Constant.Gprs_Choosed = true;
		GpioPin.communicationLight();
		FileWriteModel.setIsChoosed();
		Variable.Transmit_Choose_Or_Power = false;
		ControlTimer.chooseTransmit = false;
	}

	/**
	 * 选举上报
	 */
	public static void chooseTransmit() {

		ControlTimer.Choose_Prower_Time = 0;
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

		ControlTimer.Choose_Prower_Time = 0;
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
	 * 上传数据
	 */
	public static void uploadData() {

		DataCenter.notifyUploadData();
	}

	/**
	 * 暂停上传
	 */
	public static void pauseUploadData() {

		DataCenter.pauseUploadData();
	}

	/**
	 * 停止上传
	 */
	public static void stopUploadData() {

		DataCenter.stopUploadData();
		stopTcpServer();
	}

	/**
	 * 销毁上报
	 */
	public static void destoryUploadData() {

		Warning_Transmit = false;
		stopUploadData();
		FileWriteModel.setStopTransm();
		Variable.Transmit_Cache_Type = Constant.TRANSMIT_TYPE_STOP;
	}

	/**
	 * 设置标志位
	 * 
	 * @param debug
	 * @param error
	 * @param warning
	 * @param change
	 * @param open
	 * @param close
	 */
	public static void setMarker(int debug, int error, int warning, int change, int open, int close) {

		if (!Constant.Init_Success) {

			return;
		}

		if (!Variable.Transmit_Choose_Or_Power) {

			return;
		}

		if (Constant.Transmit_Type != Constant.TRANSMIT_TYPE_DEBUG && debug == 1) {

			DataCenter.debugTransmit();

		} else if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_DEBUG && debug == 0) {

			Arrive_Stop_Mark = false;
			DataCenter.stopUploadData();

			if (Transmit_Mark_Error == 0 && error == 1) {

				// 故障标志位由0-1，启动故障上报
				DataCenter.errorTransmit();

			} else if (Transmit_Mark_Open == 0 && open == 1) {

				DataCenter.openTransmit();

			} else if (Transmit_Mark_Close == 0 && close == 1) {

				DataCenter.closeTransmit();

			} else if (Transmit_Mark_Change == 0 && change == 1) {

				// 厂家参数变化标志位由0-1，启动参数变化上报
				DataCenter.changeTransmit();

			} else if (Transmit_Mark_Warning == 0 && warning == 1) {

				// 亚健康标志位由0-1，启动亚健康上报
				DataCenter.warningTransmit();

			} else {

				stopUploadData();
			}

		} else if (Transmit_Mark_Error == 0 && error == 1) {

			// 故障标志位由0-1，启动故障上报
			DataCenter.errorTransmit();

		} else if (Transmit_Mark_Open == 0 && open == 1) {

			Arrive_Stop_Mark = false;
			DataCenter.openTransmit();

		} else if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_OPEN && Arrive_Stop_Mark && open == 0) {

			Arrive_Stop_Mark = false;
			DataCenter.stopUploadData();

			if (Transmit_Mark_Close == 0 && close == 1) {

				DataCenter.closeTransmit();

			} else if (Transmit_Mark_Change == 0 && change == 1) {

				// 厂家参数变化标志位由0-1，启动参数变化上报
				DataCenter.changeTransmit();

			} else if (Transmit_Mark_Warning == 0 && warning == 1) {

				// 亚健康标志位由0-1，启动亚健康上报
				DataCenter.warningTransmit();

			} else {

				stopUploadData();
			}

		} else if (Transmit_Mark_Close == 0 && close == 1) {

			Arrive_Stop_Mark = false;
			DataCenter.closeTransmit();

		} else if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_CLOSE && Arrive_Stop_Mark && close == 0) {

			Arrive_Stop_Mark = false;
			DataCenter.stopUploadData();

			if (Transmit_Mark_Open == 0 && open == 1) {

				DataCenter.openTransmit();

			} else if (Transmit_Mark_Change == 0 && change == 1) {

				// 厂家参数变化标志位由0-1，启动参数变化上报
				DataCenter.changeTransmit();

			} else if (Transmit_Mark_Warning == 0 && warning == 1) {

				// 亚健康标志位由0-1，启动亚健康上报
				DataCenter.warningTransmit();

			} else {

				stopUploadData();
			}

		} else if (Transmit_Mark_Change == 0 && change == 1) {

			// 厂家参数变化标志位由0-1，启动参数变化上报
			Arrive_Stop_Mark = false;
			DataCenter.changeTransmit();

		} else if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_CHANGE && Arrive_Stop_Mark && change == 0) {

			Arrive_Stop_Mark = false;
			DataCenter.stopUploadData();

			if (Transmit_Mark_Warning == 0 && warning == 1) {

				// 亚健康标志位由0-1，启动亚健康上报
				DataCenter.warningTransmit();

			} else {

				stopUploadData();
			}

		} else if (Transmit_Mark_Warning == 0 && warning == 1) {

			// 亚健康标志位由0-1，启动亚健康上报
			DataCenter.warningTransmit();

		} else if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_WARNING && Transmit_Mark_Warning == 1
				&& warning == 0) {

			// 亚健康标志位由1-0，停止亚健康上报
			Warning_Transmit = false;
			DataCenter.stopUploadData();

		} else if (Warning_Transmit && warning == 1) {

			// 缓存上报模式为亚健康上报，标志位为1，继续亚健康上报
			DataCenter.warningTransmit();
		}

		Transmit_Mark_Error = error;
		Transmit_Mark_Warning = warning;
		Transmit_Mark_Change = change;
		Transmit_Mark_Open = open;
		Transmit_Mark_Close = close;
	}

	/**
	 * get change mark
	 * 
	 * @return
	 */
	public static boolean getTransmit_Mark_Change() {

		if (Transmit_Mark_Change == 1) {

			return true;
		}
		return false;
	}

	/**
	 * get open mark
	 * 
	 * @return
	 */
	public static boolean getTransmit_Mark_Open() {

		if (Transmit_Mark_Open == 1) {

			return true;
		}

		return false;
	}

	/**
	 * get close mark
	 * 
	 * @return
	 */
	public static boolean getTransmit_Mark_Close() {

		if (Transmit_Mark_Close == 1) {

			return true;
		}

		return false;
	}

	/**
	 * 重置系统
	 */
	public static void resetSystem() {

		Run.Running_State = false;
		GpioPin.communicationLight();
		GpioPin.errorLight();
		GpioTool.setSignLevel(20);
		FileModel.deleteAllFile();
		uploadData();
		destoryUploadData();
		SmsServer.closeConnect();
		UartServer.stopServer();
	}
}
