package com.gree.air.condition.tcp.model;

import com.gree.air.condition.configure.DeviceConfigure;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.tcp.TcpModel;
import com.gree.air.condition.utils.Utils;

/**
 * 参数
 * 
 * @author lihaotian
 *
 */
public class ParamModel {

	/**
	 * 服务器查询GPRS模块参数，GPRS回复
	 */
	public static void query() {

		Constant.Tcp_Out_Buffer[18] = (byte) 0x95;

		int poi = 19;

		String apn = "APN:";
		if (Constant.device.getMnc() == 1) {

			apn += Constant.Apn_Cucc;
		} else {

			apn += Constant.Apn_Cmcc;
		}

		String[] res = { "PWD:" + Constant.Sms_Pwd, apn, "APNU:" + Constant.Apn_Name, "APNP:" + Constant.Apn_Pwd,
				"IP:" + Constant.Tcp_Address_Ip, "PORT:" + Constant.Tcp_Address_Port, "IPR:" + Constant.BAUD_RATE,
				"WT:" + Constant.Tcp_Heart_Beat_Period, "ADM*1:" + Constant.Sms_Admin_List[0],
				"ADM*2:" + Constant.Sms_Admin_List[1], "ADM*3:" + Constant.Sms_Admin_List[2],
				"ADM*4:" + Constant.Sms_Admin_List[3], "ADM*5:" + Constant.Sms_Admin_List[4],
				"USRON*1:" + Constant.Sms_User_List[0], "USRON*2:" + Constant.Sms_User_List[1],
				"USRON*3:" + Constant.Sms_User_List[2], "USRON*4:" + Constant.Sms_User_List[3],
				"USRON*5:" + Constant.Sms_User_List[4], "USRON*6:" + Constant.Sms_User_List[5],
				"USRON*7:" + Constant.Sms_User_List[6], "USRON*8:" + Constant.Sms_User_List[7],
				"USRON*9:" + Constant.Sms_User_List[8], "USRON*10:" + Constant.Sms_User_List[9],
				"ERRT:" + (Constant.Transmit_Error_Start_Time / 60), "DEBT:" + (Constant.Transmit_Error_End_Time / 60),
				"BUTT:" + (Constant.Transmit_Pushkey_End_Time / 60),
				"HEALT:" + (Constant.Transmit_Change_End_Time / 60),
				"FTP:" + Constant.Tcp_Address_Ip + ":" + Constant.Tcp_Address_Port,
				"SIG:" + (Constant.Tcp_Sig_Period / 60), "ONT1:" + (Constant.Transmit_Open_Start_Time / 60),
				"ONT2:" + (Constant.Transmit_Open_End_Time / 60), "OFFT1:" + (Constant.Transmit_Close_Start_Time / 60),
				"OFFT2:" + (Constant.Transmit_Close_End_Time / 60),
				"CHECKPERIOD:" + (Constant.Transmit_Check_Period / 60),
				"CHECKTIME:" + (Constant.Transmit_Check_End_Time / 60) };

		for (int i = 0; i < res.length; i++) {

			byte[] b = res[i].getBytes();

			for (int j = 0; j < b.length; j++) {

				Constant.Tcp_Out_Buffer[poi] = (byte) b[j];
				poi++;
			}

			Constant.Tcp_Out_Buffer[poi] = (byte) 0x00;
			poi++;
		}

		TcpModel.build(poi - 18, poi);

	}

	/**
	 * 修改GPRS模块参数 结果响应
	 */
	public static void update() {

		Constant.Tcp_Out_Buffer[18] = (byte) 0x95;
		Constant.Tcp_Out_Buffer[19] = (byte) 0x00;

		// String[] s = new String[] { "PWD", "APN" };
		// int poi = 20;
		// for (int i = 0; i < s.length; i++) {
		//
		// byte[] b = s[i].getBytes();
		//
		// for (int j = 0; j < b.length; j++) {
		//
		// Constant.Tcp_Out_Buffer[poi] = (byte) b[j];
		// poi++;
		// }
		//
		// Constant.Tcp_Out_Buffer[poi] = (byte) 0x00;
		// poi++;
		// }

		TcpModel.build(2, 20);

	}

	/**
	 * 服务器修改GPRS模块参数 解析
	 */
	public static void updateResponse() {

		String apn = "";
		String apnu = "";
		String apnp = "";

		String ip = "";
		String port = "";

		int poi = 19;

		int length = Utils.bytesToInt(Constant.Tcp_In_Buffer, 16, 17);

		for (int i = 19; i < 19 + length; i++) {

			// 查看所有的参数
			if (Constant.Tcp_In_Buffer[i] == (byte) 0x00) {

				String param = new String(Constant.Tcp_In_Buffer, poi, (i - poi));
				int start = param.indexOf(":", 0) + 1;

				if (start > 0) {

					String key = param.substring(0, start - 1);
					String value = param.substring(start, param.length());

					if (Utils.isNotEmpty(value)) {

						if (key.equals("PWD") && !Constant.Sms_Pwd.equals(value)) { // sms pwd

							FileWriteModel.setSmsPassword(value);

						} else if (key.equals("APN")) { // apn

							apn = value;

						} else if (key.equals("APNU")) { // apn name

							apnu = value;

						} else if (key.equals("APNP")) { // apn pwd

							apnp = value;

						} else if (key.equals("IP")) { // tcp ip

							ip = value;

						} else if (key.equals("PORT")) { // tcp port

							port = value;

						} else if (key.equals("WT")) { // heart beat period

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Tcp_Heart_Beat_Period) {

								FileWriteModel.setSmsHb(time);
							}

						} else if (key.equals("ERRT")) { // error transmit start time

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Error_Start_Time) {

								FileWriteModel.setSmsErrt(time * 60);
							}

						} else if (key.equals("DEBT")) { // error transmit end time

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Error_End_Time) {

								FileWriteModel.setSmsDebt(time * 60);
							}

						} else if (key.equals("BUTT")) { // push key transmit end time

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Pushkey_End_Time) {

								FileWriteModel.setSmsButt(time * 60);
							}

						} else if (key.equals("HEALT")) { // change transmit end time

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Change_End_Time) {

								FileWriteModel.setSmsHealt(time * 60);
							}

						} else if (key.equals("SIG")) { // signal period time

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Tcp_Sig_Period) {

								FileWriteModel.setSmsSig(time * 60);
							}

						} else if (key.equals("ONT1")) {

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Open_Start_Time) {

								FileWriteModel.setOpenStartTime(time * 60);
							}

						} else if (key.equals("ONT2")) {

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Open_End_Time) {

								FileWriteModel.setOpenStartTime(time * 60);
							}

						} else if (key.equals("OFFT1")) {

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Close_Start_Time) {

								FileWriteModel.setCloseStartTime(time * 60);
							}

						} else if (key.equals("OFFT2")) {

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Close_End_Time) {

								FileWriteModel.setCloseEndTime(time * 60);
							}

						} else if (key.equals("CHECKPERIOD")) { // check transmit period time

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Check_Period) {

								FileWriteModel.setSmsCheckPeriod(time * 60);
							}

						} else if (key.equals("CHECKTIME")) { // check transmit end time

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Check_End_Time) {

								FileWriteModel.setSmsCheckTime(time * 60);
							}

						} else if (key.equals("ADM*1")) {

							Constant.Sms_Admin_List[0] = value;

						} else if (key.equals("ADM*2")) {

							Constant.Sms_Admin_List[1] = value;

						} else if (key.equals("ADM*3")) {

							Constant.Sms_Admin_List[2] = value;

						} else if (key.equals("ADM*4")) {

							Constant.Sms_Admin_List[3] = value;

						} else if (key.equals("ADM*5")) {

							Constant.Sms_Admin_List[4] = value;

						} else if (key.equals("USRON*1")) {

							Constant.Sms_User_List[0] = value;

						} else if (key.equals("USRON*2")) {

							Constant.Sms_User_List[1] = value;

						} else if (key.equals("USRON*3")) {

							Constant.Sms_User_List[2] = value;

						} else if (key.equals("USRON*4")) {

							Constant.Sms_User_List[3] = value;

						} else if (key.equals("USRON*5")) {

							Constant.Sms_User_List[4] = value;

						} else if (key.equals("USRON*6")) {

							Constant.Sms_User_List[5] = value;

						} else if (key.equals("USRON*7")) {

							Constant.Sms_User_List[6] = value;

						} else if (key.equals("USRON*8")) {

							Constant.Sms_User_List[7] = value;

						} else if (key.equals("USRON*9")) {

							Constant.Sms_User_List[8] = value;

						} else if (key.equals("USRON*10")) {

							Constant.Sms_User_List[9] = value;
						}
					}
				}

				poi = i + 1;
			}

		}

		// save apn
		if (Utils.isNotEmpty(apn) && Utils.isNotEmpty(apnu) && Utils.isNotEmpty(apnp)) {

			FileWriteModel.setApn(apn, apnu, apnp);
		}

		// save ip
		if (Utils.isNotEmpty(ip) && Utils.isNotEmpty(port)) {

			if (!ip.equals(Constant.Tcp_Address_Ip) || !port.equals(Constant.Tcp_Address_Port)) {

				FileWriteModel.setSmsServ(ip, port);
			}
		}

		FileWriteModel.setSmsAdmin();
		FileWriteModel.setSmsUser();

		update();
	}

	/**
	 * 发送GPRS信号
	 */
	public static void gprsSignal() {

		Constant.Tcp_Out_Buffer[18] = (byte) 0xF4;

		// 模块型号
		Constant.Tcp_Out_Buffer[19] = (byte) 0x02;

		// 模块序列号
		byte[] imeiBytes = Constant.device.getImei().getBytes();
		for (int i = 20; i < 20 + imeiBytes.length; i++) {

			Constant.Tcp_Out_Buffer[i] = imeiBytes[i - 20];

		}
		Constant.Tcp_Out_Buffer[35] = (byte) 0x00;

		// 状态标记
		Constant.Tcp_Out_Buffer[36] = (byte) 0x00;
		// 故障代码
		Constant.Tcp_Out_Buffer[37] = (byte) 0x00;
		// 信号强度
		Constant.Tcp_Out_Buffer[38] = (byte) DeviceConfigure.getNetworkSignalLevel();

		TcpModel.build(21, 39);

	}

}
