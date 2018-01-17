package com.gree.air.condition.tcp.model;

import com.gree.air.condition.center.ControlCenter;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.utils.Utils;

/**
 * 工装检测
 * 
 * @author zhangzhuang
 *
 */
public class FrockCheckModel {

	/**
	 * 将工装检测帧响应给服务器
	 */
	public static void frockCheck() {

		byte[] heard = new byte[] { (byte) 0xAA, (byte) 0x55, (byte) 0xAA, (byte) 0x55, (byte) 0x15, (byte) 0x01,
				(byte) 0x39 };

		for (int i = 0; i < heard.length; i++) {
			Constant.Uart_Out_Buffer[i] = heard[i];
		}

		/* IMEI码 */
		byte[] imeiHeard = new byte[] { (byte) 0x81, (byte) 0xCA, (byte) 0x00, (byte) 0x0F };
		byte[] imeiBody = new byte[15];
		for (int i = 0; i < imeiHeard.length; i++) {
			Constant.Uart_Out_Buffer[i + 7] = heard[i];
		}
		for (int i = 0; i < imeiBody.length; i++) {
			Constant.Uart_Out_Buffer[i + 11] = heard[i];
		}

		/* IMSI码 */
		byte[] imsiHeard = new byte[] { (byte) 0x81, (byte) 0xCB, (byte) 0x00, (byte) 0x0F };
		byte[] imsiBody = new byte[15];
		for (int i = 0; i < imsiHeard.length; i++) {
			Constant.Uart_Out_Buffer[i + 26] = heard[i];
		}
		for (int i = 0; i < imsiBody.length; i++) {
			Constant.Uart_Out_Buffer[i + 30] = heard[i];
		}

		/* 版本信息 */
		Constant.Uart_Out_Buffer[45] = (byte) 0x81;
		Constant.Uart_Out_Buffer[46] = (byte) 0xC1;
		String ver = "V1.0.0 2017 1 17 12:11:30";
		byte[] verBody = ver.getBytes();
		Constant.Uart_Out_Buffer[47] = Utils.intToBytes(ver.length())[0];
		Constant.Uart_Out_Buffer[48] = Utils.intToBytes(ver.length())[1];
		for (int i = 0; i < verBody.length; i++) {
			Constant.Uart_Out_Buffer[i + 49] = heard[i];
		}

	}

	/**
	 * 解析服务器下发的工装检测命令
	 */
	public static void frockCheckResponse() {

		// byte[] response = new byte[] { (byte) 0x55, (byte) 0xAA, (byte) 0x55, (byte)
		// 0xAA, (byte) 0x15, (byte) 0x00,
		// (byte) 0x00, (byte) 0x5D, (byte) 0x36 };
		frockCheck();
	}

}
