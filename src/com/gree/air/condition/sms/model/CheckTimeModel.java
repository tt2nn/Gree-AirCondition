package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 打卡上报时长
 * 
 * @author zhangzhuang
 *
 */
public class CheckTimeModel {

	/**
	 * 解析收到的短信
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			checkTimeQueryReceive();

		} else {

			checkTimeSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 打卡上报时长 解析短信
	 */
	private static void checkTimeQueryReceive() {
		checkTimeQuerySend();
	}

	/**
	 * 服务器 查询 GPRS 打卡上报时长 回复服务器短信
	 */
	private static void checkTimeQuerySend() {

		String smsValue = (Constant.Transmit_Check_Period / 60) + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Check_Time, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 打卡上报时长 解析短信
	 */
	private static void checkTimeSetReceive() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileWriteModel.setSmsCheckTime(Integer.parseInt(smsValue) * 60);

		checkTimeSetSend();
	}

	/**
	 * 服务器 设置 GPRS 打卡上报时长 回复服务器短信
	 */
	private static void checkTimeSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Check_Time);

	}
}
