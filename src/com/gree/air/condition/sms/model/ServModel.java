package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 域名、IP，端口
 * 
 * @author lihaotian
 *
 */
public class ServModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			servQueryReceive();

		} else {

			servSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 域名、IP，端口 解析短信
	 * 
	 */
	public static void servQueryReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

	}

	/**
	 * 服务器 查询 GPRS 域名、IP，端口 回复服务器短信
	 * 
	 */
	public static void servQuerySend() {
		String serv = "";
		String port = "";

		String smsValue = serv + SmsConstant.Sms_Split_Value_Symbol + port;
		SmsModel.buildMessage(SmsConstant.Sms_Type_Serv, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 域名、IP，端口 解析短信
	 * 
	 */
	public static void servSetReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		int start = 0;
		int end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start);
		String serv = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start);
		String port = smsValue.substring(start, end);

	}

	/**
	 * 服务器 设置 GPRS 域名、IP，端口 回复服务器短信
	 */
	public static void servSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Serv);

	}
}
