package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 信号信息周期
 * 
 * @author zhangzhuang
 *
 */
public class SigModel {
	/**
	 * 解析收到的短信
	 * 
	 * @param sms
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			sigQueryReceive();

		} else {

			sigSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 信号信息周期 解析短信
	 * 
	 * @param sms
	 */
	private static void sigQueryReceive() {
		sigQuerySend();
	}

	/**
	 * 服务器 查询 GPRS 信号信息周期 回复服务器短信
	 * 
	 */
	private static void sigQuerySend() {

		String smsValue = Constant.Tcp_Sig_Period / 60000 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_SIG, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 信号信息周期 解析短信
	 * 
	 * @param sms
	 */
	private static void sigSetReceive() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileModel.setSmsSig(Integer.parseInt(smsValue) * 60000);
		sigSetSend();
	}

	/**
	 * 服务器 设置 GPRS 信号信息周期 回复服务器短信
	 */
	private static void sigSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_SIG);

	}
}
