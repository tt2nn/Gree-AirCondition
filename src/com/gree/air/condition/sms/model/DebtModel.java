package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileModel;
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
	private static void debtQueryReceive() {
		debtQuerySend();
	}

	/**
	 * 服务器 查询 GPRS 故障点后传输时间 回复服务器短信
	 * 
	 */
	private static void debtQuerySend() {

		String smsValue = Constant.Transmit_Error_End_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Debt, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 故障点后传输时间 解析短信
	 * 
	 */
	private static void debtSetReceive() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileModel.setSmsDebt(Integer.parseInt(smsValue) * 60);

		debtSetSend();
	}

	/**
	 * 服务器 设置 GPRS 故障点后传输时间 回复服务器短信
	 */
	private static void debtSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Debt);

	}
}
