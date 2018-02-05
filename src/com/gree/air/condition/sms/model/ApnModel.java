package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 接入点
 * 
 * @author lihaotian
 *
 */
public class ApnModel {

	/**
	 * 解析收到的短信
	 * 
	 * @param sms
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			apnQueryReceive();

		} else {

			apnSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 接入点 解析短信
	 * 
	 * @param sms
	 */
	private static void apnQueryReceive() {

		apnQuerySend();
	}

	/**
	 * 服务器 查询 GPRS 接入点 回复服务器短信
	 * 
	 */
	private static void apnQuerySend() {

		String apn = "";

		if (Constant.device.getMnc() == 1) {

			apn = Constant.Apn_Cucc;

		} else {

			apn = Constant.Apn_Cmcc;
		}

		String smsValue = apn + SmsConstant.Sms_Split_Value_Symbol + Constant.Apn_Name
				+ SmsConstant.Sms_Split_Value_Symbol + Constant.Apn_Pwd;

		SmsModel.buildMessage(SmsConstant.Sms_Type_Apn, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 接入点 解析短信
	 * 
	 * @param sms
	 */
	private static void apnSetReceive() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		int start = 0;
		int end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start);
		String apn = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start);
		Constant.Apn_Name = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.length();
		Constant.Apn_Pwd = smsValue.substring(start, end);

		FileWriteModel.setApn(apn, Constant.Apn_Name, Constant.Apn_Pwd);

		apnSetSend();
	}

	/**
	 * 服务器 设置 GPRS 接入点 回复服务器短信
	 */
	private static void apnSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Apn);

	}
}
