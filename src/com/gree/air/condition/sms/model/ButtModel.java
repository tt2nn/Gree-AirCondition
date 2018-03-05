package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 按键调试周期
 * 
 * @author lihaotian
 *
 */
public class ButtModel extends SmsBaseModel {

	void queryParams() {

		String smsValue = Constant.Transmit_Pushkey_End_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Butt, smsValue);
	}

	void setParams() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileWriteModel.setSmsButt(Integer.parseInt(smsValue) * 60);

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Butt);
	}

}
