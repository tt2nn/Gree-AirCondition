package com.gree.air.condition.tcp.model;

import com.gree.air.condition.center.ControlCenter;
import com.gree.air.condition.center.DataCenter;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.tcp.TcpModel;
import com.gree.air.condition.utils.Logger;
import com.gree.air.condition.utils.Utils;

/**
 * 时间
 * 
 * @author lihaotian
 *
 */
public class TimeModel {

	/**
	 * 心跳
	 */
	public static void heart() {

		Constant.Tcp_Out_Buffer[18] = (byte) 0xF3;

		TcpModel.build(1, 19);
	}

	/**
	 * 心跳响应
	 */
	public static void heartResponse() {

		int year = (Constant.Tcp_In_Buffer[19] & 0xFF) + 2000;
		int month = Constant.Tcp_In_Buffer[20] & 0xFF;
		int date = Constant.Tcp_In_Buffer[21] & 0xFF;
		int hour = Constant.Tcp_In_Buffer[22] & 0xFF;
		int min = Constant.Tcp_In_Buffer[23] & 0xFF;
		int sec = Constant.Tcp_In_Buffer[24] & 0xFF;

		Constant.System_Time = Utils.getTime(year, month, date, hour, min, sec);
		Constant.Heart_Beat_Time = Constant.System_Time;
		Constant.System_Delta_Time = Constant.System_Time - System.currentTimeMillis();

		Logger.log("Tcp Heart Time", Constant.System_Time + "");

		ControlCenter.heartBeatResp();
	}

	/**
	 * 静默时间 响应
	 */
	public static void stopTime() {

		Constant.Tcp_Out_Buffer[18] = (byte) 0x98;
		Constant.Tcp_Out_Buffer[19] = (byte) 0x00;

		TcpModel.build(1, 20);
	}

	/**
	 * 服务器下发静默时间
	 */
	public static void stopTimeResponse() {

		int year = (Constant.Tcp_In_Buffer[19] & 0xFF) + 2000;
		int month = Constant.Tcp_In_Buffer[20] & 0xFF;
		int date = Constant.Tcp_In_Buffer[21] & 0xFF;
		int hour = Constant.Tcp_In_Buffer[22] & 0xFF;
		int min = Constant.Tcp_In_Buffer[23] & 0xFF;
		int sec = Constant.Tcp_In_Buffer[24] & 0xFF;

		// 如果下发的时间 是 2000-1-1 0-0-0 清空静默时间
		if (year == 2000 && month == 1 && date == 1 && hour == 0 && min == 0 && sec == 0) {

			Constant.Stop_Time = 0;
			stopTime();

			return;
		}

		Constant.Stop_Time = Utils.getTime(year, month, date, hour, min, sec);

		if (Constant.System_Time < Constant.Stop_Time) {
			// 如果当前时间 小于静默时间 则 停止传输

			DataCenter.destoryUploadData();
		}

		stopTime();
	}

}
