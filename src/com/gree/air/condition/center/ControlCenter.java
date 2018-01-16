package com.gree.air.condition.center;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.file.FileModel;
import com.gree.air.condition.tcp.TcpServer;
import com.gree.air.condition.tcp.model.LoginModel;
import com.gree.air.condition.tcp.model.ParamModel;
import com.gree.air.condition.tcp.model.TimeModel;
import com.gree.air.condition.tcp.model.TransmitModel;

/**
 * 工作中心
 * 
 * @author lihaotian
 *
 */
public class ControlCenter {

	public static boolean waittingHeart = false;

	public static long Transmit_Check_Time = 0L;

	/**
	 * 判断App是否可以工作
	 * 
	 * @return
	 */
	public static boolean canWorking() {

		if (Constant.Gprs_Choosed && Constant.System_Time > 946656000000L) {

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

		if (!TcpServer.isServerWorking()) {

			TcpServer.startServer();
			return;
		}

		TimeModel.heart();
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
		FileModel.setNotChoosed();
		DataCenter.stopUploadData();

	}

	/**
	 * 选中GPRS
	 */
	public static void chooseGprs() {

		Constant.Gprs_Choosed = true;
		FileModel.setIsChoosed();
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

		DataCenter.powerTransmit();
	}

	/**
	 * 启动打卡上报
	 */
	public static void startCheckTransmit() {

		DataCenter.registerCheckTransmit();
	}

	/**
	 * 周期打卡上报
	 */
	public static void periodCheckTransmit() {

		Transmit_Check_Time = Constant.System_Time;

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

}
