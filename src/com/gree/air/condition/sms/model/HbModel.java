package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 心跳间隔
 * 
 * @author lihaotian
 *
 */
public class HbModel extends SmsBaseModel {

	void queryParams() {

		String value1 = "heart,0,";
		String smsValue = value1 + Constant.Tcp_Heart_Beat_Period;

		SmsModel.buildMessage(SmsConstant.Sms_Type_Hb, smsValue);
	}

	void setParams() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		String split = "heart,0,";
		int start = smsValue.indexOf(split) + split.length();
		int end = smsValue.length();
		String second = smsValue.substring(start, end);

		FileWriteModel.setSmsHb(Integer.parseInt(second));

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Hb);
	}

}
