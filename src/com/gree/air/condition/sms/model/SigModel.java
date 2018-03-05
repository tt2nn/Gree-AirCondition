package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;

/**
 * 信号信息周期
 * 
 * @author zhangzhuang
 *
 */
public class SigModel extends SmsBaseModel {

	void queryParams() {

		String smsValue = Constant.Tcp_Sig_Period / 60 + "";

		SmsModel.buildMessage(SmsConstant.Sms_Type_SIG, smsValue);
	}

	void setParams() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		FileWriteModel.setSmsSig(Integer.parseInt(smsValue) * 60);
		SmsModel.buildMessageOk(SmsConstant.Sms_Type_SIG);
	}

}
