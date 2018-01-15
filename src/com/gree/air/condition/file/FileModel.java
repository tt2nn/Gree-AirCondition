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
	 * 设置域名IP、port
	 * 
	 * @param server
	 */
	public static void setSmsServer(String server) {
		
		Constant.Tcp_Serv = server;
		
		byte[] serverBytes = server.getBytes();
		
		for (int i = 0; i < serverBytes.length; i++) {
			
			Constant.File_Buffer[i] = serverBytes[i];
		}
		
		FileConnection.writeFile(FileConstant.FILE_NAME_TCP_ADDRESS);
		
	}

	/**
	 * 存储实时上报模式
	 */
	public static void setAlwaysTransm() {

		Constant.File_Buffer[0] = Constant.TRANSM_TYPE_ALWAYS;

		FileConnection.writeFile(FileConstant.FILE_NAME_DATA_TRANSM);

	}

	/**
	 * 清空上报模式
	 */
	public static void setStopTransm() {

		Constant.File_Buffer[0] = Constant.TRANSM_TYPE_STOP;

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
}
