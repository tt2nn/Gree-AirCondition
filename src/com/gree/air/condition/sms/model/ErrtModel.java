package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 故障点前传输时间
 * 
 * @author lihaotian
 *
 */
public class ErrtModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(Constant.Sms_Query_Symbol)) {

			errtQueryReceive();

		} else {

			errtSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 故障点前传输时间 解析短信
	 * 
	 */
	public static void errtQueryReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

	}

	/**
	 * 服务器 查询 GPRS 故障点前传输时间 回复服务器短信
	 * 
	 */
	public static void errtQuerySend() {

		int minute = 0;
		String smsValue = minute + "";

		SmsModel.buildMessage(Constant.Sms_Type_Errt, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 故障点前传输时间 解析短信
	 * 
	 */
	public static void errtSetReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		String min = smsValue;

	}

	/**
	 * 服务器 设置 GPRS 故障点前传输时间 回复服务器短信
	 */
	public static void errtSetSend() {

		SmsModel.buildMessageOk(Constant.Sms_Type_Errt);

	}
}
