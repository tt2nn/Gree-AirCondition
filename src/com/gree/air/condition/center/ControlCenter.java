package com.gree.air.condition.center;

import com.gree.air.condition.tcp.TcpServer;
import com.gree.air.condition.tcp.model.LoginModel;
import com.gree.air.condition.tcp.model.ParamModel;
import com.gree.air.condition.tcp.model.TimeModel;
import com.gree.air.condition.tcp.model.TransmModel;

/**
 * 工作中心
 * 
 * @author lihaotian
 *
 */
public class ControlCenter {

	public static boolean waittingHeart = false;

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
		TransmModel.start();
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
	 */
	public static void transmitData(int length) {

		TransmModel.dataTransm(length);
	}

	/**
	 * 停止TCP
	 */
	public static void stopTcpServer() {

		TcpServer.stopServer();
	}

}
