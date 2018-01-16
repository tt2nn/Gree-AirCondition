package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.sms.SmsModel;
/**
 *   打卡上报
 * @author zhangzhuang
 *
 */
public class CheckStartModel {
	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		checkStartReceive();
	}

	/**
	 * 解析 打卡上报
	 * 
	 */
	private static void checkStartReceive() {

		checkStartSend();
	}

	/**
	 * 打卡上报 回复短信
	 */
	private static void checkStartSend() {

		SmsModel.buildMessage(SmsConstant.Sms_Type_Check_Start, "ok");

	}
}
