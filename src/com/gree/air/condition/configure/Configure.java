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

	public static void init() {

		getSmsPassword();
	}

	/**
	 * 获取短信密码
	 */
	private static void getSmsPassword() {

		FileConnection.readFile(FileConstant.FIME_NAME_SMS_PASSWORD);

		if (Constant.File_Buffer_Length > 0) {

			String pwd = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);

			if (pwd != null && !pwd.equals("")) {

				Constant.Sms_Pwd = pwd;
			}
		}
	}
	
}
