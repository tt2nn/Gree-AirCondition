package com.gree.air.condition.sms.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.SmsConstant;
import com.gree.air.condition.sms.SmsModel;

public abstract class SmsBaseModel {

	/**
	 * 解析收到的短信
	 */
	public void smsAnalyze() {

		if (Constant.Sms_Receive.endsWith(SmsConstant.Sms_Query_Symbol)) {

			queryParams();

		} else {

			if (!SmsModel.isAdmin()) {

				setParams();
			}
		}
	}

	/**
	 * 服务器查询GPRS信息
	 */
	abstract void queryParams();

	/**
	 * 服务器设置GPRS信息
	 */
	abstract void setParams();

}
