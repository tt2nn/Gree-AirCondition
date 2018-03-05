package com.gree.air.condition.file;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.FileConstant;
import com.gree.air.condition.entity.Device;
import com.gree.air.condition.utils.Utils;
import com.gree.air.condition.variable.Variable;

public class FileWriteModel extends FileModel {

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

		Constant.Transmit_Power_Type = Constant.TRANSMIT_TYPE_ALWAYS;
		writeFile(FileConstant.FILE_NAME_DATA_TRANSM, Constant.TRANSMIT_TYPE_ALWAYS);
	}

	/**
	 * 存储实时上报模式
	 */
	public static void setCheckTransm() {

		Constant.Transmit_Power_Type = Constant.TRANSMIT_TYPE_CHECK;
		writeFile(FileConstant.FILE_NAME_DATA_TRANSM, Constant.TRANSMIT_TYPE_CHECK);
	}

	/**
	 * 清空上报模式
	 */
	public static void setStopTransm() {

		// Constant.Transmit_Power_Type = Constant.TRANSMIT_TYPE_STOP;
		// writeFile(FileConstant.FILE_NAME_DATA_TRANSM, Constant.TRANSMIT_TYPE_STOP);
		setCheckTransm();
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

		Variable.Change_Vpn = true;

		Constant.Apn_Name = name;
		Constant.Apn_Pwd = pwd;

		if (Device.getInstance().getMnc() == 1) {

			Constant.Apn_Cucc = apn;

			writeFile(FileConstant.FILE_NAME_APN_CUCC, Constant.Apn_Cucc + FileConstant.FILE_STRING_SPLIP_SYMBOL
					+ Constant.Apn_Name + FileConstant.FILE_STRING_SPLIP_SYMBOL + Constant.Apn_Pwd);

		} else {

			Constant.Apn_Cmcc = apn;

			writeFile(FileConstant.FILE_NAME_APN_CMCC, Constant.Apn_Cmcc + FileConstant.FILE_STRING_SPLIP_SYMBOL
					+ Constant.Apn_Name + FileConstant.FILE_STRING_SPLIP_SYMBOL + Constant.Apn_Pwd);
		}
	}

	/**
	 * 存储Sms User
	 */
	public static void setSmsUser() {

		StringBuffer stringBuffer = new StringBuffer();

		for (int i = 0; i < Constant.Sms_User_List.length; i++) {

			stringBuffer.append(Constant.Sms_User_List[i]);

			if (i < Constant.Sms_User_List.length - 1) {

				stringBuffer.append(FileConstant.FILE_STRING_SPLIP_SYMBOL);
			}
		}

		writeFile(FileConstant.FILE_NAME_SMS_USER, stringBuffer.toString());
	}

	/**
	 * 存储Sms Admin
	 */
	public static void setSmsAdmin() {

		StringBuffer stringBuffer = new StringBuffer();

		for (int i = 0; i < Constant.Sms_Admin_List.length; i++) {

			stringBuffer.append(Constant.Sms_Admin_List[i]);

			if (i < Constant.Sms_Admin_List.length - 1) {

				stringBuffer.append(FileConstant.FILE_STRING_SPLIP_SYMBOL);
			}
		}

		writeFile(FileConstant.FILE_NAME_SMS_ADMIN, stringBuffer.toString());

	}

	/**
	 * 存储波特率
	 * 
	 * @param value
	 */
	public static void setBaudRate(int value) {

		writeFile(FileConstant.FILE_NAME_BAUD_RATE, value);
	}

	/**
	 * 存储开机上报前置时间
	 * 
	 * @param value
	 */
	public static void setOpenStartTime(int value) {

		Constant.Transmit_Open_Start_Time = value;
		writeFile(FileConstant.FILE_NAME_OPEN_START_TIME, value);
	}

	/**
	 * 存储开机上报后置时间
	 * 
	 * @param value
	 */
	public static void setOpenEndTime(int value) {

		Constant.Transmit_Open_End_Time = value;
		writeFile(FileConstant.FILE_NAME_OPEN_END_TIME, value);
	}

	/**
	 * 存储关机上报前置时间
	 * 
	 * @param value
	 */
	public static void setCloseStartTime(int value) {

		Constant.Transmit_Close_Start_Time = value;
		writeFile(FileConstant.FILE_NAME_CLOSE_START_TIME, value);
	}

	/**
	 * 存储关机上报后置时间
	 * 
	 * @param value
	 */
	public static void setCloseEndTime(int value) {

		Constant.Transmit_Close_End_Time = value;
		writeFile(FileConstant.FILE_NAME_CLOSE_END_TIME, value);
	}

}
