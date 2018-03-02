package com.gree.air.condition.tcp.model;

import com.gree.air.condition.center.ControlCenter;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.tcp.TcpModel;
import com.gree.air.condition.utils.Utils;

/**
 * 登录
 * 
 * @author lihaotian
 *
 */
public class LoginModel {

	/**
	 * 登录
	 */
	public static void login() {

		Constant.Tcp_Out_Buffer[18] = (byte) 0x90;

		byte[] macCheck = Utils.intToBytes((Constant.Gprs_Mac[0] & 0xFF) * 3 + (Constant.Gprs_Mac[1] & 0xFF) * 6
				+ (Constant.Gprs_Mac[2] & 0xFF) * 0 + (Constant.Gprs_Mac[3] & 0xFF) * 9
				+ (Constant.Gprs_Mac[4] & 0xFF) * 7 + (Constant.Gprs_Mac[5] & 0xFF) * 4
				+ (Constant.Gprs_Mac[6] & 0xFF) * 10);

		// 源地址检验码
		Constant.Tcp_Out_Buffer[19] = macCheck[0];
		Constant.Tcp_Out_Buffer[20] = macCheck[1];

		// 版本号
		Constant.Tcp_Out_Buffer[21] = (byte) 0x00;
		Constant.Tcp_Out_Buffer[22] = (byte) 0x00;

		// IMEI
		byte[] imeiBytes = Constant.device.getImei().getBytes();
		for (int i = 23; i < 38; i++) {

			Constant.Tcp_Out_Buffer[i] = imeiBytes[i - 23];
		}
		Constant.Tcp_Out_Buffer[38] = (byte) 0x00;

		byte[] mncBytes = Utils.intToBytes(Constant.device.getMnc());
		Constant.Tcp_Out_Buffer[39] = mncBytes[0];
		Constant.Tcp_Out_Buffer[40] = mncBytes[1];

		byte[] lacBytes = Utils.intToBytes(Constant.device.getLac());
		Constant.Tcp_Out_Buffer[41] = lacBytes[0];
		Constant.Tcp_Out_Buffer[42] = lacBytes[1];

		byte[] cidBytes = Utils.intToBytes(Constant.device.getMcc());
		Constant.Tcp_Out_Buffer[43] = cidBytes[0];
		Constant.Tcp_Out_Buffer[44] = cidBytes[1];

		Constant.Tcp_Out_Buffer[45] = (byte) 0x00;

		// 手机序列号
		byte[] ccidBytes = Constant.device.getIccid().getBytes();
		for (int i = 46; i < 66; i++) {

			if (i - 46 < ccidBytes.length) {

				Constant.Tcp_Out_Buffer[i] = ccidBytes[i - 46];

			} else {

				Constant.Tcp_Out_Buffer[i] = (byte) 0x00;
			}
		}
		Constant.Tcp_Out_Buffer[66] = (byte) 0x00;

		// 模块型号
		Constant.Tcp_Out_Buffer[67] = Constant.GPRS_MODEL;

		TcpModel.build(50, 68);

	}

	/**
	 * 登录响应
	 */
	public static void loginResponse() {

		for (int i = 9; i < 16; i++) {

			Constant.Server_Mac[i - 9] = Constant.Tcp_In_Buffer[i];
		}

		ControlCenter.heartBeat();
	}

}
