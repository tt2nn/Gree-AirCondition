package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 服务器下发短信 GPRS模块端口连接
 * 
 * @author lihaotian
 *
 */
public class StopModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		stopReceive();
	}

	/**
	 * 解析 断开GPRS连接服务器短信
	 * 
	 */
	public static void stopReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);
	}

	/**
	 * 断开GPRS连接服务器 回复短信
	 */
	public static void stopSend() {

		SmsModel.buildMessage(SmsConstant.Sms_Type_Stop, "ok");

	}
}
