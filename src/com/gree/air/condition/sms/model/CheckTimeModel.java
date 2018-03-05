package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 打卡上报时长
 * 
 * @author zhangzhuang
 *
 */
public class CheckTimeModel extends SmsBaseModel {

	void queryParams() {

		String smsValue = (Constant.Transmit_Check_Period / 60) + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_Check_Time, smsValue);
	}

	void setParams() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileWriteModel.setSmsCheckTime(Integer.parseInt(smsValue) * 60);

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Check_Time);
	}

}
