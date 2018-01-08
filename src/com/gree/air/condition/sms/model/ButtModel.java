package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 按键调试周期
 * 
 * @author lihaotian
 *
 */
public class ButtModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(Constant.Sms_Query_Symbol)) {

			buttQueryReceive();

		} else {

			buttSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 按键调试周期 解析短信
	 * 
	 */
	public static void buttQueryReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

	}

	/**
	 * 服务器 查询 GPRS 按键调试周期 回复服务器短信
	 * 
	 */
	public static void buttQuerySend() {
		
		int minute = 0;
		String smsValue = minute + "";

		SmsModel.buildMessage(Constant.Sms_Type_Butt, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 按键调试周期 解析短信
	 * 
	 */
	public static void buttSetReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		String min = smsValue;

	}

	/**
	 * 服务器 设置 GPRS 按键调试周期 回复服务器短信
	 */
	public static void buttSetSend() {

		SmsModel.buildMessageOk(Constant.Sms_Type_Butt);

	}
}
