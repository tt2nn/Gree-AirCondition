package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 服务器 发送短信至 GPRS 复位DTU
 * 
 * @author lihaotian
 *
 */
public class RstModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		rstReceive();
	}

	/**
	 * 解析 复位DTU 短信
	 * 
	 */
	public static void rstReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);
	}

	/**
	 * 复位DTU 回复短信
	 */
	public static void rstSend() {

		String smsValue = "ok";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Rst, smsValue);

	}
}
