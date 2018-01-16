package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 厂家参数改变前传输结束时间
 * 
 * @author lihaotian
 *
 */
public class HealtModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			healtQueryReceive();

		} else {

			healtSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 厂家参数改变前传输结束时间 解析短信
	 * 
	 */
	private static void healtQueryReceive() {

		healtQuerySend();
	}

	/**
	 * 服务器 查询 GPRS 厂家参数改变前传输结束时间 回复服务器短信
	 * 
	 */
	private static void healtQuerySend() {

		String smsValue = Constant.Transfer_Change_End_Time / 60000 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Healt, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 厂家参数改变前传输结束时间 解析短信
	 * 
	 */
	private static void healtSetReceive() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileModel.setSmsHealt(Integer.parseInt(smsValue) * 60000);
		
		healtSetSend();
	}

	/**
	 * 服务器 设置 GPRS 厂家参数改变前传输结束时间 回复服务器短信
	 */
	private static void healtSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Healt);

	}
}
