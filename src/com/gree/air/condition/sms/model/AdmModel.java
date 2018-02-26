package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.sms.SmsModel;

/**
 * 管理员号码
 * 
 * @author lihaotian
 *
 */
public class AdmModel {

	/**
	 * 解析收到的短信
	 */
	public static void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			admQueryReceive();

		} else {

			admSetReceive();
		}
	}

	/**
	 * 服务器 查询 GPRS 管理员号码 解析短信
	 */
	private static void admQueryReceive() {
		admQuerySend();
	}

	/**
	 * 服务器 查询 GPRS 管理员号码 回复服务器短信
	 */
	private static void admQuerySend() {

		StringBuffer stringBuffer = new StringBuffer();

		for (int i = 0; i < Constant.Sms_Admin_List.length; i++) {

			stringBuffer.append(i + 1);
			stringBuffer.append(SmsConstant.Sms_Split_Value_Symbol);
			stringBuffer.append(Constant.Sms_Admin_List[i]);

			if (i < Constant.Sms_Admin_List.length - 1) {

				stringBuffer.append(SmsConstant.Sms_Split_Value_Symbol);
			}
		}

		SmsModel.buildMessage(SmsConstant.Sms_Type_Adm, stringBuffer.toString());
	}

	/**
	 * 服务器 设置 GPRS 管理员号码 解析短信
	 */
	private static void admSetReceive() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		int start = 0;
		int end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start);
		String number = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.length();
		String phone = smsValue.substring(start, end);

		admSetSend();
	}

	/**
	 * 服务器 设置 GPRS 管理员号码 回复服务器短信
	 */
	private static void admSetSend() {

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Adm);

	}
}
