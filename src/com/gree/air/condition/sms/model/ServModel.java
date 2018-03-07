package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;
import com.gree.air.condition.variable.Variable;

/**
 * 域名、IP，端口
 * 
 * @author lihaotian
 *
 */
public class ServModel extends SmsBaseModel {

	void queryParams() {

		String smsValue = Variable.Tcp_Address_Ip + SmsConstant.Sms_Split_Value_Symbol + Variable.Tcp_Address_Port;
		SmsModel.buildMessage(SmsConstant.Sms_Type_Serv, smsValue);
	}

	void setParams() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		int start = 0;
		int end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start);

		if (end < smsValue.length()) {

			String ip = smsValue.substring(start, end);

			start = end + 1;
			end = smsValue.length();

			String port = smsValue.substring(start, end);

			FileWriteModel.setSmsServ(ip, port);
		}

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Serv);
	}

}
