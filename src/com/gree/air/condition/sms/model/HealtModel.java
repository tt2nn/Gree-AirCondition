package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 厂家参数改变前传输结束时间
 * 
 * @author lihaotian
 *
 */
public class HealtModel extends SmsBaseModel {

	void queryParams() {

		String smsValue = Constant.Transmit_Change_End_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Healt, smsValue);
	}

	void setParams() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileWriteModel.setSmsHealt(Integer.parseInt(smsValue) * 60);

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Healt);
	}

}
