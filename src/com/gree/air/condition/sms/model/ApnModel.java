package com.gree.air.condition.sms.model;

import com.gree.air.condition.configure.DeviceConfigure;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.entity.Apn;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 接入点
 * 
 * @author lihaotian
 *
 */
public class ApnModel extends SmsBaseModel {

	void queryParams() {

		Apn apn = DeviceConfigure.getApn();

		String smsValue = apn.getApnName() + SmsConstant.Sms_Split_Value_Symbol + apn.getUserName()
				+ SmsConstant.Sms_Split_Value_Symbol + apn.getPassword();

		SmsModel.buildMessage(SmsConstant.Sms_Type_Apn, smsValue);
	}

	void setParams() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		int start = 0;
		int end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start);
		String apn = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start);
		Constant.Apn_Name = smsValue.substring(start, end);

		start = end + 1;
		end = smsValue.length();
		Constant.Apn_Pwd = smsValue.substring(start, end);

		FileWriteModel.setApn(apn, Constant.Apn_Name, Constant.Apn_Pwd);

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Apn);
	}

}
