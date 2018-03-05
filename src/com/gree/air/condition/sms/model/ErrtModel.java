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
public class ErrtModel extends SmsBaseModel {

	void queryParams() {

		String smsValue = Constant.Transmit_Error_Start_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Errt, smsValue);
	}

	void setParams() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileWriteModel.setSmsErrt(Integer.parseInt(smsValue) * 60);

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Errt);
	}

}
