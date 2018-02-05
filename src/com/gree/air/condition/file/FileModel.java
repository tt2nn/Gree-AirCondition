package com.gree.air.condition.file;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.FileConstant;
import com.gree.air.condition.utils.Utils;

public class FileModel {

	/**
	 * write file with String
	 * 
	 * @param FileName
	 * @param value
	 */
	private static synchronized void writeFile(String FileName, String value) {

		byte[] valueBytes = value.getBytes();

		writeFile(FileName, valueBytes);

	}

	/**
	 * write file with int
	 * 
	 * @param FileName
	 * @param value
	 */
	private static void writeFile(String FileName, int value) {

		byte[] valueBytes = Utils.intToBytes(value);

		writeFile(FileName, valueBytes);

	}

	/**
	 * write file with byte
	 * 
	 * @param FileName
	 * @param value
	 */
	private static void writeFile(String FileName, byte value) {

		byte[] valueBytes = { value };

		writeFile(FileName, valueBytes);

	}

	/**
	 * write file with byte[]
	 * 
	 * @param FileName
	 * @param value
	 */
	private static void writeFile(String FileName, byte[] value) {

		byte[] dataLength = Utils.intToBytes(value.length);

		Constant.File_Buffer[0] = dataLength[0];
		Constant.File_Buffer[1] = dataLength[1];

		for (int i = 0; i < value.length; i++) {

			Constant.File_Buffer[i + 2] = value[i];
		}

		FileConnection.writeFile(FileName);

	}

	/**
	 * 设置短信密码
	 * 
	 * @param password
	 */
	public static void setSmsPassword(String password) {

		Constant.Sms_Pwd = password;

		writeFile(FileConstant.FILE_NAME_SMS_PASSWORD, password);

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

		writeFile(FileConstant.FILE_NAME_TCP_ADDRESS, ip + FileConstant.FILE_STRING_SPLIP_SYMBOL + port);

	}

	/**
	 * 设置心跳间隔
	 * 
	 * @param hb
	 */
	public static void setSmsHb(int hb) {

		Constant.Tcp_Heart_Beat_Period = hb;

		writeFile(FileConstant.FILE_NAME_TCP_HEART_BEAT_PERIOD, hb);

	}

	/**
	 * 设置故障点前传输时间
	 * 
	 * @param errt
	 */
	public static void setSmsErrt(int errt) {

		Constant.Transmit_Error_Start_Time = errt;

		writeFile(FileConstant.FILE_NAME_TRANSMIT_ERROR_START_TIME, errt);

	}

	/**
	 * 设置故障点后传输时间
	 * 
	 * @param debt
	 */
	public static void setSmsDebt(int debt) {

		Constant.Transmit_Error_End_Time = debt;

		writeFile(FileConstant.FILE_NAME_TRANSMIT_ERROR_END_TIME, debt);

	}

	/**
	 * 厂家参数改变前传输结束时间
	 * 
	 * @param healt
	 */
	public static void setSmsHealt(int healt) {

		Constant.Transmit_Change_End_Time = healt;

		writeFile(FileConstant.FILE_NAME_TRANSMIT_CHANGE_END_TIME, healt);

	}

	/**
	 * 按键调试周期
	 * 
	 * @param butt
	 */
	public static void setSmsButt(int butt) {

		Constant.Transmit_Pushkey_End_Time = butt;

		writeFile(FileConstant.FILE_NAME_TRANSMIT_PUSHKEY_END_TIME, butt);

	}

	/**
	 * 信号信息周期
	 * 
	 * @param sig
	 */
	public static void setSmsSig(int sig) {

		Constant.Tcp_Sig_Period = sig;

		writeFile(FileConstant.FILE_NAME_TCP_SIG_PERIOD, sig);

	}

	/**
	 * 打卡周期
	 * 
	 * @param checkPeriod
	 */
	public static void setSmsCheckPeriod(int checkPeriod) {

		Constant.Transmit_Check_Period = checkPeriod;

		writeFile(FileConstant.FILE_NAME_TRANSMIT_CHECK_PERIOD, checkPeriod);

	}

	/**
	 * 打卡时长
	 * 
	 * @param checkTime
	 */
	public static void setSmsCheckTime(int checkTime) {

		Constant.Transmit_Check_End_Time = checkTime;

		writeFile(FileConstant.FILE_NAME_TRANSMIT_CHECK_END_TIME, checkTime);

	}

	/**
	 * 存储实时上报模式
	 */
	public static void setAlwaysTransm() {

		writeFile(FileConstant.FILE_NAME_DATA_TRANSM, Constant.TRANSMIT_TYPE_ALWAYS);

	}

	/**
	 * 清空上报模式
	 */
	public static void setStopTransm() {

		writeFile(FileConstant.FILE_NAME_DATA_TRANSM, Constant.TRANSMIT_TYPE_STOP);

	}

	/**
	 * 记录用户被选中
	 */
	public static void setIsChoosed() {

		writeFile(FileConstant.FILE_NAME_GPRS_CHOOSED, (byte) 0x01);

	}

	/**
	 * 清空用户选中状态
	 */
	public static void setNotChoosed() {

		writeFile(FileConstant.FILE_NAME_GPRS_CHOOSED, (byte) 0x00);

	}

	/**
	 * 存储spi写入的地址
	 * 
	 * @param page
	 */
	public static void setSpiAddress(int address) {

		writeFile(FileConstant.FILE_NAME_SPI_WRITE_ADDRESS, Utils.intToBytes(address, 3));

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

			writeFile(FileConstant.FILE_NAME_APN_CUCC, Constant.Apn_Cucc + FileConstant.FILE_STRING_SPLIP_SYMBOL
					+ Constant.Apn_Name + FileConstant.FILE_STRING_SPLIP_SYMBOL + Constant.Apn_Pwd);

		} else {

			Constant.Apn_Cmcc = apn;

			writeFile(FileConstant.FILE_NAME_APN_CMCC, Constant.Apn_Cmcc + FileConstant.FILE_STRING_SPLIP_SYMBOL
					+ Constant.Apn_Name + FileConstant.FILE_STRING_SPLIP_SYMBOL + Constant.Apn_Pwd);

		}

	}
}
