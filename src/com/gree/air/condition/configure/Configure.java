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

			Constant.Transfer_Power_Type = Constant.File_Buffer[0];

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

		FileConnection.readFile(FileConstant.FILE_NAME_TCP_HB);

		if (Constant.File_Buffer_Length > 0) {

			String second = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);

			if (Utils.isNotEmpty(second)) {

				Constant.Tcp_Hb = Integer.parseInt(second);
			}
		}

	}
	
	/**
	 * 获取存储的故障点前传输时间
	 */
	private static void getTcpErrorBefore() {
		
		FileConnection.readFile(FileConstant.FILE_NAME_TCP_ERROR_BEFORE);
		
		if (Constant.File_Buffer_Length > 0) {
			
			String errorBefore = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);
			
			if (Utils.isNotEmpty(errorBefore)) {
				
				Constant.Tcp_Error_Before_Second = Integer.parseInt(errorBefore);
			}
		}
		
	}
	
	/**
	 * 获取存储的故障点后传输时间
	 */
	private static void getTcpErrorAfter() {
		
		FileConnection.readFile(FileConstant.FILE_NAME_TCP_ERROR_AFTER);
		
		if (Constant.File_Buffer_Length > 0) {
			
			String errorAfter = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);
			
			if (Utils.isNotEmpty(errorAfter)) {
				
				Constant.Tcp_Error_After_Second = Integer.parseInt(errorAfter);
			}
		}
		
	}
	
	/**
	 * 厂家参数改变前传输结束时间
	 */
	private static void getTcpChangeBefore() {
		
		FileConnection.readFile(FileConstant.FILE_NAME_TCP_CHANGE_BEFORE);
		
		if (Constant.File_Buffer_Length > 0) {
			
			String changeBefore = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);
			
			if (Utils.isNotEmpty(changeBefore)) {
				
				Constant.Tcp_Change_Before_Second = Integer.parseInt(changeBefore);
			}
		}
		
	}
	
	/**
	 * 信号信息周期
	 */
	private static void getTcpSig() {
		
		FileConnection.readFile(FileConstant.FILE_NAME_TCP_SIG);
		
		if (Constant.File_Buffer_Length > 0) {
			
			String sig = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);
			
			if (Utils.isNotEmpty(sig)) {
				
				Constant.Tcp_Sig_Second = Integer.parseInt(sig);
			}
		}
		
	}
	
	/**
	 * 打卡周期
	 */
	private static void getTcpCheckPeriod() {
		
		FileConnection.readFile(FileConstant.FILE_NAME_TCP_CHECK_PERIOD);
		
		if (Constant.File_Buffer_Length > 0) {
			
			String checkPeriod = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);
			
			if (Utils.isNotEmpty(checkPeriod)) {
				
				Constant.Tcp_Check_Period = Integer.parseInt(checkPeriod);
			}
		}
		
	}
	/**
	 * 打卡时长
	 */
	private static void getTcpCheckTime() {
		
		FileConnection.readFile(FileConstant.FILE_NAME_TCP_CHECK_TIME);
		
		if (Constant.File_Buffer_Length > 0) {
			
			String checkTime = new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length);
			
			if (Utils.isNotEmpty(checkTime)) {
				
				Constant.Tcp_Check_Time= Integer.parseInt(checkTime);
			}
		}
		
	}

}
