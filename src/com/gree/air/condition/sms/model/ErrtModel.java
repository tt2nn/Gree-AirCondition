package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 故障点前传输时间
 * 
 * @author lihaotian
 *
 */
public class ErrtModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			errtQueryReceive();

		} else {

			errtSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 故障点前传输时间 解析短信
	 * 
	 */
	private static void errtQueryReceive() {

		errtQuerySend();

	}

	/**
	 * 服务器 查询 GPRS 故障点前传输时间 回复服务器短信
	 * 
	 */
	private static void errtQuerySend() {

		String smsValue = Constant.Transmit_Error_Start_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Errt, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 故障点前传输时间 解析短信
	 * 
	 */
	private static void errtSetReceive() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileWriteModel.setSmsErrt(Integer.parseInt(smsValue) * 60);

		errtSetSend();
	}

	/**
	 * 服务器 设置 GPRS 故障点前传输时间 回复服务器短信
	 */
	private static void errtSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Errt);

	}
}
