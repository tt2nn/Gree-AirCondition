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

		FileConnection.writeFile(FileConstant.FIME_NAME_SMS_PASSWORD);

	}
}
