package com.gree.air.condition.sms.model;

import com.gree.air.condition.center.ControlCenter;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 服务器下发短信，开始GPRS连接服务器，并主动上传数据
 * 
 * @author lihaotian
 *
 */
public class StartModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		startReceive();
	}

	/**
	 * 解析 开始GPRS连接服务器短信
	 * 
	 */
	private static void startReceive() {

		ControlCenter.alwaysTransmit();
		startSend();
	}

	/**
	 * 开始GPRS连接服务器 回复短信
	 */
	private static void startSend() {

		SmsModel.buildMessage(SmsConstant.Sms_Type_Start, "ok");
	}

}
