package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;
import com.gree.air.condition.utils.Utils;

/**
 * 普通手机账号
 * 
 * @author lihaotian
 *
 */
public class UsronModel extends SmsBaseModel {

	void queryParams() {

		StringBuffer stringBuffer = new StringBuffer();

		for (int i = 0; i < Constant.Sms_User_List.length; i++) {

			stringBuffer.append(i + 1);
			stringBuffer.append(SmsConstant.Sms_Split_Value_Symbol);
			stringBuffer.append(Constant.Sms_User_List[i]);

			if (i < Constant.Sms_User_List.length - 1) {

				stringBuffer.append(SmsConstant.Sms_Split_Value_Symbol);
			}
		}

		SmsModel.buildMessage(SmsConstant.Sms_Type_Usron, stringBuffer.toString());
	}

	void setParams() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		int start = 0;
		int end = 0;
		boolean isPhone = false;
		int num = 0;
		boolean isChange = false;

		while ((end = smsValue.indexOf(SmsConstant.Sms_Split_Value_Symbol, start)) != -1) {

			if (!isPhone) {

				String numString = smsValue.substring(start, end);
				num = Utils.stringToInt(numString) - 1;

				start = end + 1;
				isPhone = true;

			} else {

				String phone = smsValue.substring(start, end);

				if (Utils.isNotEmpty(phone) && num > 0 && num < Constant.Sms_User_List.length) {

					Constant.Sms_User_List[num] = phone;

					start = end + 1;
					isPhone = false;
					isChange = true;

				}
			}
		}

		if (isChange) {

			FileWriteModel.setSmsUser();
		}

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Usron);
	}

}
