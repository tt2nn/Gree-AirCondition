package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.sms.SmsModel;
import com.gree.air.condition.utils.Utils;

/**
 * 管理员号码
 * 
 * @author lihaotian
 *
 */
public class AdmModel extends SmsBaseModel {

	void queryParams() {

		StringBuffer stringBuffer = new StringBuffer();

		for (int i = 0; i < Constant.Sms_Admin_List.length; i++) {

			String string = (i + 1) + SmsConstant.Sms_Split_Value_Symbol + Constant.Sms_Admin_List[i];

			if (stringBuffer.length() + string.length() > 70) {

				stringBuffer.deleteCharAt(stringBuffer.length() - 1);

				SmsModel.buildMessage(SmsConstant.Sms_Type_Adm, stringBuffer.toString());

				stringBuffer = new StringBuffer();
			}

			stringBuffer.append(string);

			if (i < Constant.Sms_Admin_List.length - 1) {

				stringBuffer.append(SmsConstant.Sms_Split_Value_Symbol);
			}
		}

		SmsModel.buildMessage(SmsConstant.Sms_Type_Adm, stringBuffer.toString());
	}

	void setParams() {

		String smsValue = SmsModel.smsGetValue(Constant.Sms_Receive);

		if (!Utils.isNotEmpty(smsValue)) {

			SmsModel.buildMessageError(SmsConstant.Sms_Type_Adm);

			return;
		}

		smsValue = smsValue + SmsConstant.Sms_Split_Value_Symbol;

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

				if (Utils.isNotEmpty(phone) && num > 0 && num < Constant.Sms_Admin_List.length && phone.length() >= 5
						&& phone.length() <= 24) {

					Constant.Sms_Admin_List[num] = phone;
					isChange = true;
				}

				start = end + 1;
				isPhone = false;
			}
		}

		if (isChange) {

			FileWriteModel.setSmsAdmin();
		}

		SmsModel.buildMessageOk(SmsConstant.Sms_Type_Adm);
	}

}
