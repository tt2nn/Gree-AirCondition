package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 心跳间隔
 * 
 * @author lihaotian
 *
 */
public class HbModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(Constant.Sms_Query_Symbol)) {

			hbQueryReceive();

		} else {

			hbSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 心跳间隔 解析短信
	 * 
	 */
	public static void hbQueryReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

	}

	/**
	 * 服务器 查询 GPRS 心跳间隔 回复服务器短信
	 * 
	 */
	public static void hbQuerySend() {
		int second = 0;
		String value1 = "heart";
		String value2 = "0";
		String smsValue = value1 + Constant.Sms_Split_Value_Symbol + value2 + Constant.Sms_Split_Value_Symbol + second;

		SmsModel.buildMessage(Constant.Sms_Type_Hb, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 心跳间隔 解析短信
	 * 
	 */
	public static void hbSetReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		String split = "heart,0,";
		int start = smsValue.indexOf(split) + split.length();
		int end = smsValue.length();
		String second = smsValue.substring(start, end);

	}

	/**
	 * 服务器 设置 GPRS 心跳间隔 回复服务器短信
	 */
	public static void hbSetSend() {

		SmsModel.buildMessageOk(Constant.Sms_Type_Hb);

	}
}
