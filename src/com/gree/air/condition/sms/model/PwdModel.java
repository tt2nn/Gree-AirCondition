package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 短信密码
 * 
 * @author lihaotian
 *
 */
public class PwdModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			pwdQueryReceive();

		} else {

			pwdSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 短信密码 解析短信
	 * 
	 */
	public static void pwdQueryReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

	}

	/**
	 * 服务器 查询 GPRS 短信密码 回复服务器短信
	 * 
	 */
	public static void pwdQuerySend() {
		String pwd = "";
		SmsModel.buildMessage(SmsConstant.Sms_Type_Pwd, pwd);
	}

	/**
	 * 服务器 设置 GPRS 短信密码 解析短信
	 * 
	 */
	public static void pwdSetReceive() {

		String smsPwd = SmsModel.smsGetPwd(Constant.Sms_Receive);

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		String pwd = smsValue;

	}

	/**
	 * 服务器 设置 GPRS 短信密码 回复服务器短信
	 */
	public static void pwdSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Pwd);

	}
}
