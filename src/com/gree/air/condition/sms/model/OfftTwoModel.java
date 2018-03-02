package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;

public class OfftTwoModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			queryReceive();

		} else {

			setReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 关机上报后置时间 解析短信
	 * 
	 */
	private static void queryReceive() {

		querySend();
	}

	/**
	 * 服务器 查询 GPRS 关机上报后置时间 回复服务器短信
	 * 
	 */
	private static void querySend() {

		String smsValue = (Constant.Transmit_Close_End_Time / 60) + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Close_End, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 关机上报后置时间 解析短信
	 * 
	 */
	private static void setReceive() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileWriteModel.setCloseEndTime(Integer.parseInt(smsValue) * 60);

		setSend();
	}

	/**
	 * 服务器 设置 GPRS 关机上报后置时间 回复服务器短信
	 */
	private static void setSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Close_End);
	}

}
