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
	 * @param serv
	 */
	public static void setSmsServ(String serv) {

		Constant.Tcp_Serv = serv;

		byte[] servBytes = serv.getBytes();

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

		Constant.Tcp_Hb = hb;

		byte[] hbBytes = String.valueOf(hb).getBytes();

		for (int i = 0; i < hbBytes.length; i++) {

			Constant.File_Buffer[i] = hbBytes[i];
		}

		FileConnection.writeFile(FileConstant.FILE_NAME_TCP_HB);

	}
	/**
	 * 设置故障点前传输时间
	 * 
	 * @param errt
	 */
	public static void setSmsErrt(int errt) {
		
		Constant.Tcp_Error_Before_Second = errt;
		
		byte[] errtBytes = String.valueOf(errt).getBytes();
		
		for (int i = 0; i < errtBytes.length; i++) {
			
			Constant.File_Buffer[i] = errtBytes[i];
		}
		
		FileConnection.writeFile(FileConstant.FILE_NAME_TCP_ERROR_BEFORE);
		
	}
	
	/**
	 * 设置故障点后传输时间
	 * 
	 * @param debt
	 */
	public static void setSmsDebt(int debt) {
		
		Constant.Tcp_Error_After_Second = debt;
		
		byte[] debtBytes = String.valueOf(debt).getBytes();
		
		for (int i = 0; i < debtBytes.length; i++) {
			
			Constant.File_Buffer[i] = debtBytes[i];
		}
		
		FileConnection.writeFile(FileConstant.FILE_NAME_TCP_ERROR_AFTER);
		
	}
	
	/**
	 * 厂家参数改变前传输结束时间
	 * 
	 * @param healt
	 */
	public static void setSmsHealt(int healt) {
		
		Constant.Tcp_Change_Before_Second = healt;
		
		byte[] healtBytes = String.valueOf(healt).getBytes();
		
		for (int i = 0; i < healtBytes.length; i++) {
			
			Constant.File_Buffer[i] = healtBytes[i];
		}
		
		FileConnection.writeFile(FileConstant.FILE_NAME_TCP_CHANGE_BEFORE);
		
	}
	
	/**
	 * 信号信息周期
	 * 
	 * @param sig
	 */
	public static void setSmsSig(int sig) {
		
		Constant.Tcp_Sig_Second = sig;
		
		byte[] sigBytes = String.valueOf(sig).getBytes();
		
		for (int i = 0; i < sigBytes.length; i++) {
			
			Constant.File_Buffer[i] = sigBytes[i];
		}
		
		FileConnection.writeFile(FileConstant.FILE_NAME_TCP_SIG);
		
	}
	
	/**
	 * 打卡周期
	 * 
	 * @param checkPeriod
	 */
	public static void setSmsCheckPeriod(int checkPeriod) {
		
		Constant.Tcp_Check_Period = checkPeriod;
		
		byte[] checkPeriodBytes = String.valueOf(checkPeriod).getBytes();
		
		for (int i = 0; i < checkPeriodBytes.length; i++) {
			
			Constant.File_Buffer[i] = checkPeriodBytes[i];
		}
		
		FileConnection.writeFile(FileConstant.FILE_NAME_TCP_CHECK_PERIOD);
		
	}
	
	/**
	 * 打卡时长
	 * 
	 * @param checkTime
	 */
	public static void setSmsCheckTime(int checkTime) {
		
		Constant.Tcp_Check_Time = checkTime;
		
		byte[] checkTimeBytes = String.valueOf(checkTime).getBytes();
		
		for (int i = 0; i < checkTimeBytes.length; i++) {
			
			Constant.File_Buffer[i] = checkTimeBytes[i];
		}
		
		FileConnection.writeFile(FileConstant.FILE_NAME_TCP_CHECK_TIME);
		
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
