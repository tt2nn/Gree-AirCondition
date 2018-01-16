package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 管理员号码
 * 
 * @author lihaotian
 *
 */
public class AdmModel {

	/**
	 * 解析收到的短信
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			admQueryReceive();

		} else {

			admSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 管理员号码 解析短信
	 */
	private static void admQueryReceive() {
		admQuerySend();
	}

	/**
	 * 服务器 查询 GPRS 管理员号码 回复服务器短信
	 */
	private static void admQuerySend() {
		String value1 = "1";
		String admPhone = "";
		String smsValue = value1 + SmsConstant.Sms_Split_Value_Symbol + admPhone;

		SmsModel.buildMessage(SmsConstant.Sms_Type_Adm, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 管理员号码 解析短信
	 */
	private static void admSetReceive() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		int start = 0;
		int end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start);
		String number = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.length();
		String phone = smsValue.substring(start, end);

		admSetSend();
	}

	/**
	 * 服务器 设置 GPRS 管理员号码 回复服务器短信
	 */
	private static void admSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Adm);

	}
}
