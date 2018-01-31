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

		String smsValue = Constant.Tcp_Address_Ip + SmsConstant.Sms_Split_Value_Symbol + Constant.TcP_Address_Port;
		SmsModel.buildMessage(SmsConstant.Sms_Type_Serv, smsValue);
	}

	/**
	 * 服务器 设置 GPRS 域名、IP，端口 解析短信
	 * 
	 */
	private static void servSetReceive() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		int start = 0;
		int end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start);

		if (end < smsValue.length()) {

			Constant.Tcp_Address_Ip = smsValue.substring(start, end);

			start = end + 1;
			end = smsValue.length();

			Constant.TcP_Address_Port = smsValue.substring(start, end);
			
			FileModel.setSmsServ(Constant.Tcp_Address_Ip, Constant.TcP_Address_Port);

		}

		servSetSend();
	}

	/**
	 * 服务器 设置 GPRS 域名、IP，端口 回复服务器短信
	 */
	private static void servSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Serv);

	}
}
