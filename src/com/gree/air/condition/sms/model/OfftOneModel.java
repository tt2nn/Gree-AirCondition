package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;

public class OfftOneModel extends SmsBaseModel {

	void queryParams() {

		String smsValue = (Constant.Transmit_Close_Start_Time / 60) + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Close_Start, smsValue);
	}

	void setParams() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileWriteModel.setCloseStartTime(Integer.parseInt(smsValue) * 60);

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Close_Start);
	}

}
