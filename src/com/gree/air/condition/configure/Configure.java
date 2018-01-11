package com.gree.air.condition.configure;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.FileConstant;
import com.gree.air.condition.file.FileConnection;

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

			if (pwd != null && !pwd.equals("")) {

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

			Constant.Transfer_Power_Type = Constant.File_Buffer[0];

		}

	}

}
