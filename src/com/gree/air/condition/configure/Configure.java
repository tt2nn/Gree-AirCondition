package com.gree.air.condition.configure;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.FileConstant;
import com.gree.air.condition.file.FileConnection;
import com.gree.air.condition.utils.Utils;

/**
 * 配置
 * 
 * @author lihaotian
 *
 */
public class Configure {

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
	}

	/**
	 * 获取选中状态
	 */
	private static void getGprsChoosed() {

		FileConnection.readFile(FileConstant.FILE_NAME_GPRS_CHOOSED);

		if (Constant.File_Buffer_Length > 0) {

			if (Constant.File_Buffer[0] == (byte) 0x01) {

				Constant.Gprs_Choosed = true;
			}
		}
	}

	/**
	 * 获取短信密码
	 */
	private static void getSmsPassword() {

		FileConnection.readFile(FileConstant.FILE_NAME_SMS_PASSWORD);

		if (Constant.File_Buffer_Length > 0) {

			String pwd = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);

			if (Utils.isNotEmpty(pwd)) {

				Constant.Sms_Pwd = pwd;
			}
		}
	}

	/**
	 * 获取存储的上报模式
	 */
	private static void getTransmType() {

		FileConnection.readFile(FileConstant.FILE_NAME_DATA_TRANSM);

		if (Constant.File_Buffer_Length > 0) {

			Constant.Transmit_Power_Type = Constant.File_Buffer[0];

		}

	}

	/**
	 * 获取存储的TCP地址
	 */
	private static void getTcpAddress() {

		FileConnection.readFile(FileConstant.FILE_NAME_TCP_ADDRESS);

		if (Constant.File_Buffer_Length > 0) {

			String address = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);

			if (Utils.isNotEmpty(address)) {

				Constant.Tcp_Serv = address;
			}
		}
	}

	/**
	 * 获取存储的心跳间隔
	 */
	private static void getTcpHb() {

		FileConnection.readFile(FileConstant.FILE_NAME_TCP_HEART_BEAT_PERIOD);

		if (Constant.File_Buffer_Length > 0) {

			String second = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);

			if (Utils.isNotEmpty(second)) {

				Constant.Tcp_Heart_Beat_Period = Integer.parseInt(second);
			}
		}

	}

	/**
	 * 获取存储的故障点前传输时间
	 */
	private static void getTcpErrorBefore() {

		FileConnection.readFile(FileConstant.FILE_NAME_TRANSMIT_ERROR_START_TIME);

		if (Constant.File_Buffer_Length > 0) {

			String errorBefore = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);

			if (Utils.isNotEmpty(errorBefore)) {

				Constant.Transmit_Error_Start_Time = Integer.parseInt(errorBefore);
			}
		}

	}

	/**
	 * 获取存储的故障点后传输时间
	 */
	private static void getTcpErrorAfter() {

		FileConnection.readFile(FileConstant.FILE_NAME_TRANSMIT_ERROR_END_TIME);

		if (Constant.File_Buffer_Length > 0) {

			String errorAfter = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);

			if (Utils.isNotEmpty(errorAfter)) {

				Constant.Transmit_Error_End_Time = Integer.parseInt(errorAfter);
			}
		}

	}

	/**
	 * 厂家参数改变前传输结束时间
	 */
	private static void getTcpChangeBefore() {

		FileConnection.readFile(FileConstant.FILE_NAME_TRANSMIT_CHANGE_END_TIME);

		if (Constant.File_Buffer_Length > 0) {

			String changeBefore = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);

			if (Utils.isNotEmpty(changeBefore)) {

				Constant.Transmit_Change_End_Time = Integer.parseInt(changeBefore);
			}
		}

	}

	/**
	 * 按键调试周期
	 */
	private static void getTcpPushkey() {

		FileConnection.readFile(FileConstant.FILE_NAME_TRANSMIT_PUSHKEY_END_TIME);

		if (Constant.File_Buffer_Length > 0) {

			String pushkey = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);

			if (Utils.isNotEmpty(pushkey)) {

				Constant.Transmit_Pushkey_End_Time = Integer.parseInt(pushkey);
			}
		}

	}

	/**
	 * 信号信息周期
	 */
	private static void getTcpSig() {

		FileConnection.readFile(FileConstant.FILE_NAME_TCP_SIG_PERIOD);

		if (Constant.File_Buffer_Length > 0) {

			String sig = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);

			if (Utils.isNotEmpty(sig)) {

				Constant.Tcp_Sig_Period = Integer.parseInt(sig);
			}
		}

	}

	/**
	 * 打卡周期
	 */
	private static void getTcpCheckPeriod() {

		FileConnection.readFile(FileConstant.FILE_NAME_TRANSMIT_CHECK_PERIOD);

		if (Constant.File_Buffer_Length > 0) {

			String checkPeriod = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);

			if (Utils.isNotEmpty(checkPeriod)) {

				Constant.Transmit_Check_Period = Integer.parseInt(checkPeriod);
			}
		}

	}

	/**
	 * 打卡时长
	 */
	private static void getTcpCheckTime() {

		FileConnection.readFile(FileConstant.FILE_NAME_TRANSMIT_CHECK_END_TIME);

		if (Constant.File_Buffer_Length > 0) {

			String checkTime = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);

			if (Utils.isNotEmpty(checkTime)) {

				Constant.Transmit_Check_End_Time = Integer.parseInt(checkTime);
			}
		}

	}

}
