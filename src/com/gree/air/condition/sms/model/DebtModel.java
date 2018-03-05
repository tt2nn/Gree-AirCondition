package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 故障点后传输时间
 * 
 * @author lihaotian
 *
 */
public class DebtModel extends SmsBaseModel {

	void queryParams() {

		String smsValue = Constant.Transmit_Error_End_Time / 60 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Debt, smsValue);
	}

	void setParams() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileWriteModel.setSmsDebt(Integer.parseInt(smsValue) * 60);

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Debt);
	}

}
