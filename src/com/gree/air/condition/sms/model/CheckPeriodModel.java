package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 打卡上报周期
 * 
 * @author zhangzhuang
 *
 */
public class CheckPeriodModel {

	/**
	 * 解析收到的短信
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			checkPeriodQueryReceive();

		} else {

			checkPeriodSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 打卡上报周期 解析短信
	 */
	private static void checkPeriodQueryReceive() {
		checkPeriodQuerySend();
	}

	/**
	 * 服务器 查询 GPRS 打卡上报周期 回复服务器短信
	 */
	private static void checkPeriodQuerySend() {

		String smsValue = Constant.Transmit_Check_Period / 60 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Check_Period, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 打卡上报周期 解析短信
	 */
	private static void checkPeriodSetReceive() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileModel.setSmsCheckPeriod(Integer.parseInt(smsValue) * 60);

		checkPeriodSetSend();
	}

	/**
	 * 服务器 设置 GPRS 打卡上报周期 回复服务器短信
	 */
	private static void checkPeriodSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Check_Period);

	}
}
