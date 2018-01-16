package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
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
		String userName = "";
		String password = "";
		String smsValue = apn + SmsConstant.Sms_Split_Value_Symbol + userName + SmsConstant.Sms_Split_Value_Symbol
				+ password;

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
		String userName = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.length();
		String password = smsValue.substring(start, end);

		apnSetSend();
	}

	/**
	 * 服务器 设置 GPRS 接入点 回复服务器短信
	 */
	private static void apnSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Apn);

	}
}
