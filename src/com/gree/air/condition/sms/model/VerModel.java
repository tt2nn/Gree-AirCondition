package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 服务器 查询DTU软件版本
 * 
 * @author lihaotian
 *
 */
public class VerModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		verQueryReceive();
	}

	/**
	 * 查询DTU版本 解析短信
	 * 
	 */
	public static void verQueryReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);
	}

	/**
	 * 查询DTU版本 回复短信
	 * 
	 */
	public static void verQuerySend() {

		SmsModel.buildMessage(SmsConstant.Sms_Type_Ver, "");
	}

}
