package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileModel;
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

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			hbQueryReceive();

		} else {

			hbSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 心跳间隔 解析短信
	 * 
	 */
	private static void hbQueryReceive() {

		hbQuerySend();
	}

	/**
	 * 服务器 查询 GPRS 心跳间隔 回复服务器短信
	 * 
	 */
	private static void hbQuerySend() {

		String value1 = "heart,0,";
		String smsValue = value1 + Constant.Tcp_Heart_Beat_Period / 1000;

		SmsModel.buildMessage(SmsConstant.Sms_Type_Hb, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 心跳间隔 解析短信
	 * 
	 */
	private static void hbSetReceive() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		String split = "heart,0,";
		int start = smsValue.indexOf(split) + split.length();
		int end = smsValue.length();
		String second = smsValue.substring(start, end);

		FileModel.setSmsHb(Integer.parseInt(second) * 1000);

		hbSetSend();
	}

	/**
	 * 服务器 设置 GPRS 心跳间隔 回复服务器短信
	 */
	private static void hbSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Hb);

	}
}
