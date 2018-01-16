package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 开机上报
 * 
 * @author zhangzhuang
 *
 */
public class RunStartModel {
	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		runStartReceive();
	}

	/**
	 * 解析 开机上报
	 * 
	 */
	private static void runStartReceive() {

		runStartSend();
	}

	/**
	 * 开机上报 回复短信
	 */
	private static void runStartSend() {

		SmsModel.buildMessage(SmsConstant.Sms_Type_Run_Start, "ok");

	}
}
