package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
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

		if (Constant.Sms_Receive.endsWith(Constant.Sms_Query_Symbol)) {

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
	public static void apnQueryReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

	}

	/**
	 * 服务器 查询 GPRS 接入点 回复服务器短信
	 * 
	 */
	public static void apnQuerySend() {

		String apn = "";
		String userName = "";
		String password = "";
		String smsValue = apn + Constant.Sms_Split_Value_Symbol + userName + Constant.Sms_Split_Value_Symbol + password;

		SmsModel.buildMessage(Constant.Sms_Type_Apn, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 接入点 解析短信
	 * 
	 * @param sms
	 */
	public static void apnSetReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		int start = 0;
		int end = smsValue.indexOf(Constant.Sms_Split_Value_Symbol, start);
		String apn = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.indexOf(Constant.Sms_Split_Value_Symbol, start);
		String userName = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.length();
		String password = smsValue.substring(start, end);

	}

	/**
	 * 服务器 设置 GPRS 接入点 回复服务器短信
	 */
	public static void apnSetSend() {

		SmsModel.buildMessageOk(Constant.Sms_Type_Apn);

	}
}
