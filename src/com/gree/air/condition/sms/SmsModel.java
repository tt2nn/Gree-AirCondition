package com.gree.air.condition.sms;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.sms.model.AdmModel;
import com.gree.air.condition.sms.model.ApnModel;
import com.gree.air.condition.sms.model.ButtModel;
import com.gree.air.condition.sms.model.DebtModel;
import com.gree.air.condition.sms.model.ErrtModel;
import com.gree.air.condition.sms.model.HbModel;
import com.gree.air.condition.sms.model.HealtModel;
import com.gree.air.condition.sms.model.PwdModel;
import com.gree.air.condition.sms.model.RstModel;
import com.gree.air.condition.sms.model.ServModel;
import com.gree.air.condition.sms.model.StartModel;
import com.gree.air.condition.sms.model.StopModel;
import com.gree.air.condition.sms.model.UsronModel;
import com.gree.air.condition.sms.model.VerModel;

public class SmsModel {

	private static final String SMS_GREE = "[格力信息]";

	/**
	 * 解析由服务器发送的短信，判断短信的功能，交给对应的模块进行处理
	 */
	public static void analyze() {

		// 判断是否是正确的短信格式
		if ((Constant.Sms_Receive.startsWith(SMS_GREE) && Constant.Sms_Receive.endsWith(Constant.Sms_Split_Key_Symbol))
				|| (Constant.Sms_Receive.startsWith(Constant.Sms_Split_Key_Symbol)
						&& Constant.Sms_Receive.endsWith(SMS_GREE))) {
			
			// TODO 验证白名单
			
			// 验证 短信密码
			if (!smsGetPwd(Constant.Sms_Receive).equals(Constant.Sms_Pwd)) {
				
				return;
			}

			// 判断短信类型
			if (Constant.Sms_Receive.indexOf(Constant.Sms_Type_Apn) != -1) {// 接入点

				ApnModel.smsAnalyze();

			} else if (Constant.Sms_Receive.indexOf(Constant.Sms_Type_Serv) != -1) { // 域名、IP，端口号

				ServModel.smsAnalyze();

			} else if (Constant.Sms_Receive.indexOf(Constant.Sms_Type_Hb) != -1) { // 心跳间隔

				HbModel.smsAnalyze();

			} else if (Constant.Sms_Receive.indexOf(Constant.Sms_Type_Pwd) != -1) { // 短信密码

				PwdModel.smsAnalyze();

			} else if (Constant.Sms_Receive.indexOf(Constant.Sms_Type_Start) != -1) { // 开始连接服务器

				StartModel.smsAnalyze();

			} else if (Constant.Sms_Receive.indexOf(Constant.Sms_Type_Stop) != -1) { // 断开连接服务器

				StopModel.smsAnalyze();

			} else if (Constant.Sms_Receive.indexOf(Constant.Sms_Type_Ver) != -1) { // DTU软件版本号

				VerModel.smsAnalyze();

			} else if (Constant.Sms_Receive.indexOf(Constant.Sms_Type_Adm) != -1) { // 管理员号码

				AdmModel.smsAnalyze();

			} else if (Constant.Sms_Receive.indexOf(Constant.Sms_Type_Usron) != -1) { // 普通手机账号

				UsronModel.smsAnalyze();

			} else if (Constant.Sms_Receive.indexOf(Constant.Sms_Type_Rst) != -1) { // 复位DTU

				RstModel.smsAnalyze();

			} else if (Constant.Sms_Receive.indexOf(Constant.Sms_Type_Errt) != -1) { // 故障点前传输时间

				ErrtModel.smsAnalyze();

			} else if (Constant.Sms_Receive.indexOf(Constant.Sms_Type_Debt) != -1) { // 故障点后传输时间

				DebtModel.smsAnalyze();

			} else if (Constant.Sms_Receive.indexOf(Constant.Sms_Type_Healt) != -1) { // 厂家参数改变前传输结束时间

				HealtModel.smsAnalyze();

			} else if (Constant.Sms_Receive.indexOf(Constant.Sms_Type_Butt) != -1) { // 按键调试周期

				ButtModel.smsAnalyze();
			}
		}
	}

	/**
	 * 获取Sms 短信密码
	 * 
	 * @param sms
	 * @return
	 */
	public static String smsGetPwd(String sms) {

		int start = sms.indexOf(Constant.Sms_Split_Key_Symbol, 0);
		int end = sms.indexOf(Constant.Sms_Split_Key_Symbol, start + 1);
		String smsPwd = sms.substring(start + 1, end);

		return smsPwd;
	}

	/**
	 * 获取短信的有效数据
	 * 
	 * @param sms
	 * @return
	 */
	public static String smsGetValue(String sms) {

		int start = sms.indexOf(Constant.Sms_Split_Key_Symbol, sms.indexOf(Constant.Sms_Split_Key_Symbol, 1) + 1);
		
		int end = 0;
		int poi = start;
		while ((poi = sms.indexOf(Constant.Sms_Split_Key_Symbol , poi + 1)) != -1) {
			
			end = poi;
		}

		String smsValue = sms.substring(start + 1, end);

		return smsValue;
	}

	/**
	 * GPRS 发送短信 至 服务器 组包消息
	 * 
	 * @param smsType
	 *            消息类型
	 * @param smsValue
	 *            消息内容
	 */
	public static void buildMessage(String smsType, String smsValue) {

		Constant.Sms_Send = smsType + smsValue + Constant.Sms_Split_Key_Symbol;
	}

	/**
	 * GPRF 发送成功短信 至 服务器 组包消息
	 * 
	 * @param smsType
	 *            消息类型
	 */
	public static void buildMessageOk(String smsType) {

		Constant.Sms_Send = smsType + Constant.Sms_Set_Ok + Constant.Sms_Split_Key_Symbol;
	}

	/**
	 * 短信异常 组包消息
	 * 
	 * @param smsType
	 *            消息类型
	 */
	protected static void buildMessageError(String smsType) {

		Constant.Sms_Send = smsType + Constant.Sms_Message_Error + Constant.Sms_Split_Key_Symbol;
	}

	/**
	 * 短信有效数据为空 组包消息
	 * 
	 * @param smsType
	 *            消息类型
	 */
	public static void buildMessageEmpty(String smsType) {

		Constant.Sms_Send = smsType + Constant.Sms_Message_Empty + Constant.Sms_Split_Key_Symbol;
	}

}
