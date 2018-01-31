package com.gree.air.condition.file;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.FileConstant;

public class FileModel {

	/**
	 * 设置短信密码
	 * 
	 * @param password
	 */
	public static void setSmsPassword(String password) {

		Constant.Sms_Pwd = password;

		byte[] pwdBytes = password.getBytes();

		for (int i = 0; i < pwdBytes.length; i++) {

			Constant.File_Buffer[i] = pwdBytes[i];
		}

		FileConnection.writeFile(FileConstant.FILE_NAME_SMS_PASSWORD);

	}

	/**
	 * 设置域名IP、Port
	 * 
	 * @param ip
	 * @param port
	 */
	public static void setSmsServ(String ip, String port) {

		Constant.Tcp_Address_Ip = ip;
		Constant.Tcp_Address_Port = port;

		byte[] servBytes = (ip + FileConstant.FILE_STRING_SPLIP_SYMBOL + port).getBytes();

		for (int i = 0; i < servBytes.length; i++) {

			Constant.File_Buffer[i] = servBytes[i];
		}

		FileConnection.writeFile(FileConstant.FILE_NAME_TCP_ADDRESS);

	}

	/**
	 * 设置心跳间隔
	 * 
	 * @param hb
	 */
	public static void setSmsHb(int hb) {

		Constant.Tcp_Heart_Beat_Period = hb;

		byte[] hbBytes = String.valueOf(hb).getBytes();

		for (int i = 0; i < hbBytes.length; i++) {

			Constant.File_Buffer[i] = hbBytes[i];
		}

		FileConnection.writeFile(FileConstant.FILE_NAME_TCP_HEART_BEAT_PERIOD);

	}

	/**
	 * 设置故障点前传输时间
	 * 
	 * @param errt
	 */
	public static void setSmsErrt(int errt) {

		Constant.Transmit_Error_Start_Time = errt;

		byte[] errtBytes = String.valueOf(errt).getBytes();

		for (int i = 0; i < errtBytes.length; i++) {

			Constant.File_Buffer[i] = errtBytes[i];
		}

		FileConnection.writeFile(FileConstant.FILE_NAME_TRANSMIT_ERROR_START_TIME);

	}

	/**
	 * 设置故障点后传输时间
	 * 
	 * @param debt
	 */
	public static void setSmsDebt(int debt) {

		Constant.Transmit_Error_End_Time = debt;

		byte[] debtBytes = String.valueOf(debt).getBytes();

		for (int i = 0; i < debtBytes.length; i++) {

			Constant.File_Buffer[i] = debtBytes[i];
		}

		FileConnection.writeFile(FileConstant.FILE_NAME_TRANSMIT_ERROR_END_TIME);

	}

	/**
	 * 厂家参数改变前传输结束时间
	 * 
	 * @param healt
	 */
	public static void setSmsHealt(int healt) {

		Constant.Transmit_Change_End_Time = healt;

		byte[] healtBytes = String.valueOf(healt).getBytes();

		for (int i = 0; i < healtBytes.length; i++) {

			Constant.File_Buffer[i] = healtBytes[i];
		}

		FileConnection.writeFile(FileConstant.FILE_NAME_TRANSMIT_CHANGE_END_TIME);

	}

	/**
	 * 按键调试周期
	 * 
	 * @param butt
	 */
	public static void setSmsButt(int butt) {

		Constant.Transmit_Pushkey_End_Time = butt;

		byte[] buttBytes = String.valueOf(butt).getBytes();

		for (int i = 0; i < buttBytes.length; i++) {

			Constant.File_Buffer[i] = buttBytes[i];
		}

		FileConnection.writeFile(FileConstant.FILE_NAME_TRANSMIT_PUSHKEY_END_TIME);

	}

	/**
	 * 信号信息周期
	 * 
	 * @param sig
	 */
	public static void setSmsSig(int sig) {

		Constant.Tcp_Sig_Period = sig;

		byte[] sigBytes = String.valueOf(sig).getBytes();

		for (int i = 0; i < sigBytes.length; i++) {

			Constant.File_Buffer[i] = sigBytes[i];
		}

		FileConnection.writeFile(FileConstant.FILE_NAME_TCP_SIG_PERIOD);

	}

	/**
	 * 打卡周期
	 * 
	 * @param checkPeriod
	 */
	public static void setSmsCheckPeriod(int checkPeriod) {

		Constant.Transmit_Check_Period = checkPeriod;

		byte[] checkPeriodBytes = String.valueOf(checkPeriod).getBytes();

		for (int i = 0; i < checkPeriodBytes.length; i++) {

			Constant.File_Buffer[i] = checkPeriodBytes[i];
		}

		FileConnection.writeFile(FileConstant.FILE_NAME_TRANSMIT_CHECK_PERIOD);

	}

	/**
	 * 打卡时长
	 * 
	 * @param checkTime
	 */
	public static void setSmsCheckTime(int checkTime) {

		Constant.Transmit_Check_End_Time = checkTime;

		byte[] checkTimeBytes = String.valueOf(checkTime).getBytes();

		for (int i = 0; i < checkTimeBytes.length; i++) {

			Constant.File_Buffer[i] = checkTimeBytes[i];
		}

		FileConnection.writeFile(FileConstant.FILE_NAME_TRANSMIT_CHECK_END_TIME);

	}

	/**
	 * 存储实时上报模式
	 */
	public static void setAlwaysTransm() {

		Constant.File_Buffer[0] = Constant.TRANSMIT_TYPE_ALWAYS;

		FileConnection.writeFile(FileConstant.FILE_NAME_DATA_TRANSM);

	}

	/**
	 * 清空上报模式
	 */
	public static void setStopTransm() {

		Constant.File_Buffer[0] = Constant.TRANSMIT_TYPE_STOP;

		FileConnection.writeFile(FileConstant.FILE_NAME_DATA_TRANSM);

	}

	/**
	 * 记录用户被选中
	 */
	public static void setIsChoosed() {

		Constant.File_Buffer[0] = (byte) 0x01;

		FileConnection.writeFile(FileConstant.FILE_NAME_GPRS_CHOOSED);

	}

	/**
	 * 清空用户选中状态
	 */
	public static void setNotChoosed() {

		Constant.File_Buffer[0] = (byte) 0x00;

		FileConnection.writeFile(FileConstant.FILE_NAME_GPRS_CHOOSED);
	}

	/**
	 * 存储spi写入的地址
	 * 
	 * @param page
	 */
	public static void setSpiAddress(int address) {

		byte[] res = (address + "").getBytes();

		for (int i = 0; i < res.length; i++) {

			Constant.File_Buffer[i] = res[i];
		}

		FileConnection.writeFile(FileConstant.FILE_NAME_SPI_WRITE_ADDRESS);

	}

	/**
	 * 设置APN
	 * 
	 * @param serv
	 */
	public static void setApn(String apn, String name, String pwd) {

		Constant.Apn_Name = name;
		Constant.Apn_Pwd = pwd;

		if (Constant.device.getMnc() == 1) {

			Constant.Apn_Cucc = apn;

			byte[] res = (Constant.Apn_Cucc + FileConstant.FILE_STRING_SPLIP_SYMBOL + Constant.Apn_Name
					+ FileConstant.FILE_STRING_SPLIP_SYMBOL + Constant.Apn_Pwd).getBytes();

			for (int i = 0; i < res.length; i++) {

				Constant.File_Buffer[i] = res[i];
			}

			FileConnection.writeFile(FileConstant.FILE_NAME_APN_CUCC);

		} else {

			Constant.Apn_Cmcc = apn;

			byte[] res = (Constant.Apn_Cmcc + FileConstant.FILE_STRING_SPLIP_SYMBOL + Constant.Apn_Name
					+ FileConstant.FILE_STRING_SPLIP_SYMBOL + Constant.Apn_Pwd).getBytes();

			for (int i = 0; i < res.length; i++) {

				Constant.File_Buffer[i] = res[i];
			}

			FileConnection.writeFile(FileConstant.FILE_NAME_APN_CMCC);
		}

	}
}
