package com.gree.air.condition.configure;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.FileConstant;
import com.gree.air.condition.file.FileReadModel;
import com.gree.air.condition.utils.Utils;

/**
 * 配置
 * 
 * @author lihaotian
 *
 */
public class Configure extends FileReadModel {

	/**
	 * 初始化
	 */
	public static void init() {

		getGprsChoosed();

		getSmsPassword();

		getTransmType();

		getTcpAddress();

		getTcpHb();

		getTcpErrorBefore();

		getTcpErrorAfter();

		getTcpChangeBefore();

		getTcpPushkey();

		getTcpSig();

		getTcpCheckPeriod();

		getTcpCheckTime();

		getApnCucc();

		getApnCmcc();

		getSmsUser();

		getSmsAdmin();

		getOpenStartTime();

		getOpenEndTime();

		getCloseStartTime();

		getCloseEndTime();

	}

	/**
	 * 获取选中状态
	 */
	private static void getGprsChoosed() {

		Constant.Gprs_Choosed = readFileBool(FileConstant.FILE_NAME_GPRS_CHOOSED);
	}

	/**
	 * 获取短信密码
	 */
	private static void getSmsPassword() {

		String pwd = readFileString(FileConstant.FILE_NAME_SMS_PASSWORD);

		if (Utils.isNotEmpty(pwd)) {

			Constant.Sms_Pwd = pwd;
		}
	}

	/**
	 * 获取存储的上报模式
	 */
	private static void getTransmType() {

		int length = readFileLength(FileConstant.FILE_NAME_DATA_TRANSM);

		if (length == 1) {

			Constant.Transmit_Power_Type = Constant.File_Buffer[2];
		}
	}

	/**
	 * 获取存储的TCP地址
	 */
	private static void getTcpAddress() {

		String address = readFileString(FileConstant.FILE_NAME_TCP_ADDRESS);

		if (Utils.isNotEmpty(address)) {

			int start = 0;
			int end = address.indexOf(FileConstant.FILE_STRING_SPLIP_SYMBOL, start);

			if (end < address.length()) {

				Constant.Tcp_Address_Ip = address.substring(start, end);

				start = end + 1;
				end = address.length();

				Constant.Tcp_Address_Port = address.substring(start, end);

			}
		}
	}

	/**
	 * 获取存储的心跳间隔
	 */
	private static void getTcpHb() {

		int second = readFileInt(FileConstant.FILE_NAME_TCP_HEART_BEAT_PERIOD);

		if (second > 0) {

			Constant.Tcp_Heart_Beat_Period = second;
		}

	}

	/**
	 * 获取存储的故障点前传输时间
	 */
	private static void getTcpErrorBefore() {

		int second = readFileInt(FileConstant.FILE_NAME_TRANSMIT_ERROR_START_TIME);

		if (second > 0) {

			Constant.Transmit_Error_Start_Time = second;
		}

	}

	/**
	 * 获取存储的故障点后传输时间
	 */
	private static void getTcpErrorAfter() {

		int second = readFileInt(FileConstant.FILE_NAME_TRANSMIT_ERROR_END_TIME);

		if (second > 0) {

			Constant.Transmit_Error_End_Time = second;
		}

	}

	/**
	 * 厂家参数改变前传输结束时间
	 */
	private static void getTcpChangeBefore() {

		int second = readFileInt(FileConstant.FILE_NAME_TRANSMIT_CHANGE_END_TIME);

		if (second > 0) {

			Constant.Transmit_Change_End_Time = second;
		}

	}

	/**
	 * 按键调试周期
	 */
	private static void getTcpPushkey() {

		int second = readFileInt(FileConstant.FILE_NAME_TRANSMIT_PUSHKEY_END_TIME);

		if (second > 0) {

			Constant.Transmit_Pushkey_End_Time = second;
		}

	}

	/**
	 * 信号信息周期
	 */
	private static void getTcpSig() {

		int second = readFileInt(FileConstant.FILE_NAME_TCP_SIG_PERIOD);

		if (second > 0) {

			Constant.Tcp_Sig_Period = second;
		}

	}

	/**
	 * 打卡周期
	 */
	private static void getTcpCheckPeriod() {

		int second = readFileInt(FileConstant.FILE_NAME_TRANSMIT_CHECK_PERIOD);

		if (second > 0) {

			Constant.Transmit_Check_Period = second;
		}

	}

	/**
	 * 打卡时长
	 */
	private static void getTcpCheckTime() {

		int second = readFileInt(FileConstant.FILE_NAME_TRANSMIT_CHECK_END_TIME);

		if (second > 0) {

			Constant.Transmit_Check_End_Time = second;
		}

	}

	/**
	 * 联通APN
	 */
	private static void getApnCucc() {

		String apnString = readFileString(FileConstant.FILE_NAME_APN_CUCC);

		if (Utils.isNotEmpty(apnString)) {

			int start = 0;
			int end = apnString.indexOf(FileConstant.FILE_STRING_SPLIP_SYMBOL, start);
			Constant.Apn_Cucc = apnString.substring(start, end);

			start = end + 1;
			end = apnString.indexOf(FileConstant.FILE_STRING_SPLIP_SYMBOL, start);
			Constant.Apn_Name = apnString.substring(start, end);

			start = end + 1;
			end = apnString.length();
			Constant.Apn_Pwd = apnString.substring(start, end);
		}
	}

	/**
	 * 移动APN
	 */
	private static void getApnCmcc() {

		String apnString = readFileString(FileConstant.FILE_NAME_APN_CMCC);

		if (Utils.isNotEmpty(apnString)) {

			int start = 0;
			int end = apnString.indexOf(FileConstant.FILE_STRING_SPLIP_SYMBOL, start);
			Constant.Apn_Cmcc = apnString.substring(start, end);
		}
	}

	/**
	 * 获取Sms User
	 */
	private static void getSmsUser() {

		String userString = readFileString(FileConstant.FILE_NAME_SMS_USER);

		int symbolPoi = 0;
		int userPoi = 0;
		for (int i = 0; i < userString.length(); i++) {

			if (userString.substring(i, i + 1).equals(FileConstant.FILE_STRING_SPLIP_SYMBOL)) {

				String user = userString.substring(symbolPoi, i);
				Constant.Sms_User_List[userPoi] = user;
				userPoi++;
				symbolPoi = i + 1;

			} else if (i == userString.length() - 1) {

				String user = userString.substring(symbolPoi, userString.length());
				Constant.Sms_User_List[userPoi] = user;
			}
		}
	}

	/**
	 * 获取Sms Admin
	 */
	private static void getSmsAdmin() {

		String userString = readFileString(FileConstant.FILE_NAME_SMS_ADMIN);

		int symbolPoi = 0;
		int adminPoi = 0;
		for (int i = 0; i < userString.length(); i++) {

			if (userString.substring(i, i + 1).equals(FileConstant.FILE_STRING_SPLIP_SYMBOL)) {

				String user = userString.substring(symbolPoi, i);
				Constant.Sms_Admin_List[adminPoi] = user;
				adminPoi++;
				symbolPoi = i + 1;

			} else if (i == userString.length() - 1) {

				String user = userString.substring(symbolPoi, userString.length());
				Constant.Sms_Admin_List[adminPoi] = user;
			}
		}
	}

	/**
	 * 获取开机上报前置时间
	 */
	private static void getOpenStartTime() {

		int time = readFileInt(FileConstant.FILE_NAME_OPEN_START_TIME);

		if (time > 0) {

			Constant.Transmit_Open_Start_Time = time;
		}
	}

	/**
	 * 获取开机上报后置时间
	 */
	private static void getOpenEndTime() {

		int time = readFileInt(FileConstant.FILE_NAME_OPEN_END_TIME);

		if (time > 0) {

			Constant.Transmit_Open_End_Time = time;
		}
	}

	/**
	 * 获取开机上报前置时间
	 */
	private static void getCloseStartTime() {

		int time = readFileInt(FileConstant.FILE_NAME_CLOSE_START_TIME);

		if (time > 0) {

			Constant.Transmit_Close_Start_Time = time;
		}
	}

	/**
	 * 获取开机上报前置时间
	 */
	private static void getCloseEndTime() {

		int time = readFileInt(FileConstant.FILE_NAME_CLOSE_END_TIME);

		if (time > 0) {

			Constant.Transmit_Close_End_Time = time;
		}
	}

}
