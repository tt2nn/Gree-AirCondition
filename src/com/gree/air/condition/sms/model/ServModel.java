package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 域名、IP，端口
 * 
 * @author lihaotian
 *
 */
public class ServModel {

	/**
	 * 解析收到的短信
	 * 
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			servQueryReceive();

		} else {

			servSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 域名、IP，端口 解析短信
	 * 
	 */
	private static void servQueryReceive() {

		servQuerySend();
	}

	/**
	 * 服务器 查询 GPRS 域名、IP，端口 回复服务器短信
	 * 
	 */
	private static void servQuerySend() {

		String smsValue = Constant.Tcp_Serv.replace(SmsConstant.Tcp_Char_Value_Symbol, SmsConstant.Sms_Char_Value_Symbol);
		SmsModel.buildMessage(SmsConstant.Sms_Type_Serv, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 域名、IP，端口 解析短信
	 * 
	 */
	private static void servSetReceive() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		String address = smsValue.replace(SmsConstant.Sms_Char_Value_Symbol,SmsConstant.Tcp_Char_Value_Symbol);
		FileModel.setSmsServ(address);
		
		servSetSend();
	}

	/**
	 * 服务器 设置 GPRS 域名、IP，端口 回复服务器短信
	 */
	private static void servSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Serv);

	}
}
