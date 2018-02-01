package com.gree.air.condition.tcp.model;

import com.gree.air.condition.configure.DeviceConfigure;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.file.FileModel;
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
		if (Constant.device.getMcc() == 1) {

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
				"HEALT:" + (Constant.Transmit_Change_End_Time / 60) };

		// "FTP:" + Constant.Tcp_Address_Ip + ":" + Constant.Tcp_Address_Port,
		// "SIG:" + (Constant.Tcp_Sig_Period / 60),
		// "CHECKPERIOD:" + (Constant.Transmit_Check_Period / 60),
		// "CHECKTIME:" + Constant.Transmit_Check_End_Time

		for (int i = 0; i < res.length; i++) {

			byte[] b = res[i].getBytes();

			for (int j = 0; j < b.length; j++) {

				Constant.Tcp_Out_Buffer[poi] = (byte) b[j];
				poi++;
			}

			if (i < res.length - 1) {

				Constant.Tcp_Out_Buffer[poi] = (byte) 0x00;
				poi++;
			}
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

		for (int i = 19; i < length; i++) {

			// 查看所有的参数
			if (Constant.Tcp_In_Buffer[i] == (byte) 0x00) {

				String param = new String(Constant.Tcp_In_Buffer, poi, (i - poi));
				int start = param.indexOf(":", 0) + 1;

				if (start > 0) {

					String value = param.substring(start, param.length());

					if (Utils.isNotEmpty(value)) {

						if (Utils.stringContains(param, "PWD") && !Constant.Sms_Pwd.equals(value)) { // sms pwd

							FileModel.setSmsPassword(value);

						} else if (Utils.stringContains(param, "APN")) { // apn

							apn = value;

						} else if (Utils.stringContains(param, "APNU")) { // apn name

							apnu = value;

						} else if (Utils.stringContains(param, "APNP")) { // apn pwd

							apnp = value;

						} else if (Utils.stringContains(param, "IP")) { // tcp ip

							ip = value;

						} else if (Utils.stringContains(param, "PORT")) { // tcp port

							port = value;

						} else if (Utils.stringContains(param, "WT")) { // heart beat period

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Tcp_Heart_Beat_Period) {

								FileModel.setSmsHb(time);
							}

						} else if (Utils.stringContains(param, "ERRT")) { // error transmit start time

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Error_Start_Time) {

								FileModel.setSmsErrt(time * 60);
							}

						} else if (Utils.stringContains(param, "DEBT")) { // error transmit end time

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Error_End_Time) {

								FileModel.setSmsDebt(time * 60);
							}

						} else if (Utils.stringContains(param, "BUTT")) { // push key transmit end time

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Pushkey_End_Time) {

								FileModel.setSmsButt(time * 60);
							}

						} else if (Utils.stringContains(param, "HEALT")) { // change transmit end time

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Change_End_Time) {

								FileModel.setSmsHealt(time * 60);
							}

						} else if (Utils.stringContains(param, "SIG")) { // signal period time

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Tcp_Sig_Period) {

								FileModel.setSmsSig(time * 60);
							}

						} else if (Utils.stringContains(param, "CHECKPERIOD")) { // check transmit period time

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Check_Period) {

								FileModel.setSmsCheckPeriod(time * 60);
							}

						} else if (Utils.stringContains(param, "CHECKTIME")) { // check transmit end time

							int time = Utils.stringToInt(value);
							if (time != 0 && time != Constant.Transmit_Check_End_Time) {

								FileModel.setSmsCheckTime(time);
							}
						}
					}
				}

				poi = i;
			}

		}

		// save apn
		if (Utils.isNotEmpty(apn) && Utils.isNotEmpty(apnu) && Utils.isNotEmpty(apnp)) {

			FileModel.setApn(apn, apnu, apnp);
		}

		// save ip
		if (Utils.isNotEmpty(ip) && Utils.isNotEmpty(port)) {

			if (!ip.equals(Constant.Tcp_Address_Ip) || !port.equals(Constant.Tcp_Address_Port)) {

				FileModel.setSmsServ(ip, port);
			}
		}

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
