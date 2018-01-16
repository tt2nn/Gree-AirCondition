package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileModel;
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

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			buttQueryReceive();

		} else {

			buttSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 按键调试周期 解析短信
	 * 
	 */
	private static void buttQueryReceive() {
		buttQuerySend();
	}

	/**
	 * 服务器 查询 GPRS 按键调试周期 回复服务器短信
	 * 
	 */
	private static void buttQuerySend() {

		String smsValue = Constant.Transmit_Pushkey_End_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Butt, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 按键调试周期 解析短信
	 * 
	 */
	private static void buttSetReceive() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileModel.setSmsButt(Integer.parseInt(smsValue) * 60);

		buttSetSend();
	}

	/**
	 * 服务器 设置 GPRS 按键调试周期 回复服务器短信
	 */
	private static void buttSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Butt);

	}
}
