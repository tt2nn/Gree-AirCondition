package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
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
	public static void startReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);
	}

	/**
	 * 开始GPRS连接服务器 回复短信
	 */
	public static void startSend() {

		SmsModel.buildMessage(Constant.Sms_Type_Start, "ok");

	}

}
