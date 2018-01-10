package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 普通手机账号
 * 
 * @author lihaotian
 *
 */
public class UsronModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			usronQueryReceive();

		} else {

			usronSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 普通手机账号 解析短信
	 * 
	 */
	public static void usronQueryReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

	}

	/**
	 * 服务器 查询 GPRS 普通手机账号 回复服务器短信
	 * 
	 */
	public static void usronQuerySend() {
		String value1 = "1";
		String usronPhone = "";
		String smsValue = value1 + SmsConstant.Sms_Split_Value_Symbol + usronPhone;

		SmsModel.buildMessage(SmsConstant.Sms_Type_Usron, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 普通手机账号 解析短信
	 * 
	 */
	public static void usronSetReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		int start = 0;
		int end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start);
		String number = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.length();
		String phone = smsValue.substring(start, end);

	}

	/**
	 * 服务器 设置 GPRS 普通手机账号 回复服务器短信
	 */
	public static void usronSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Usron);

	}
}
