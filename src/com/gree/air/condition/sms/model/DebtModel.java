package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 故障点后传输时间
 * 
 * @author lihaotian
 *
 */
public class DebtModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			debtQueryReceive();

		} else {

			debtSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 故障点后传输时间 解析短信
	 * 
	 */
	public static void debtQueryReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

	}

	/**
	 * 服务器 查询 GPRS 故障点后传输时间 回复服务器短信
	 * 
	 */
	public static void debtQuerySend() {

		int minute = 0;
		String smsValue = minute + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Debt, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 故障点后传输时间 解析短信
	 * 
	 */
	public static void debtSetReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		String min = smsValue;

	}

	/**
	 * 服务器 设置 GPRS 故障点后传输时间 回复服务器短信
	 */
	public static void debtSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Debt);

	}
}
