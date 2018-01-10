package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 厂家参数改变前传输结束时间
 * 
 * @author lihaotian
 *
 */
public class HealtModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			healtQueryReceive();

		} else {

			healtSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 厂家参数改变前传输结束时间 解析短信
	 * 
	 */
	public static void healtQueryReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

	}

	/**
	 * 服务器 查询 GPRS 厂家参数改变前传输结束时间 回复服务器短信
	 * 
	 */
	public static void healtQuerySend() {
		int minute = 0;
		String smsValue = minute + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Healt, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 厂家参数改变前传输结束时间 解析短信
	 * 
	 */
	public static void healtSetReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		String min = smsValue;

	}

	/**
	 * 服务器 设置 GPRS 厂家参数改变前传输结束时间 回复服务器短信
	 */
	public static void healtSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Healt);

	}
}
