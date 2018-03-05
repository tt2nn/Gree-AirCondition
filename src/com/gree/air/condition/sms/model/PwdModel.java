package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 短信密码
 * 
 * @author lihaotian
 *
 */
public class PwdModel extends SmsBaseModel {

	void queryParams() {

		SmsModel.buildMessage(SmsConstant.Sms_Type_Pwd, Constant.Sms_Pwd);
	}

	void setParams() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileWriteModel.setSmsPassword(smsValue);

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Pwd);
	}

}
