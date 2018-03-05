package com.gree.air.condition.sms.model;

import com.gree.air.condition.center.ControlCenter;
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
	private static void rstReceive() {

		rstSend();
		ControlCenter.resetSystem();
	}

	/**
	 * 复位DTU 回复短信
	 */
	private static void rstSend() {

		String smsValue = "ok";
		SmsModel.buildMessage(SmsConstant.Sms_Type_Rst, smsValue);
	}

}
