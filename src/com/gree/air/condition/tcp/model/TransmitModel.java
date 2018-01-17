package com.gree.air.condition.tcp.model;

import java.util.Calendar;
import java.util.Date;

import com.gree.air.condition.center.ControlCenter;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.tcp.TcpModel;
import com.gree.air.condition.utils.Utils;

/**
 * 传输
 * 
 * @author lihaotian
 *
 */
public class TransmitModel {

	private static Date date = new Date();
	private static Calendar calendar = Calendar.getInstance();

	/**
	 * 开始传输 上报传输模式
	 */
	public static void start() {

		Constant.Tcp_Out_Buffer[18] = (byte) 0x91;

		Constant.Tcp_Out_Buffer[19] = Constant.Transmit_Type;

		// 获取年月日时分秒
		date.setTime(Constant.System_Time);
		calendar.setTime(date);
		Constant.Tcp_Out_Buffer[20] = (byte) (calendar.get(Calendar.YEAR) - 2000);
		Constant.Tcp_Out_Buffer[21] = (byte) (calendar.get(Calendar.MONTH) + 1);

		int localDay = calendar.get(Calendar.DATE);
		int localHour = calendar.get(Calendar.HOUR_OF_DAY) + 8;

		if (localHour > 23) {

			localDay++;
			localHour -= 24;

		}

		Constant.Tcp_Out_Buffer[22] = (byte) localDay;
		Constant.Tcp_Out_Buffer[23] = (byte) localHour;
		Constant.Tcp_Out_Buffer[24] = (byte) calendar.get(Calendar.MINUTE);
		Constant.Tcp_Out_Buffer[25] = (byte) calendar.get(Calendar.SECOND);

		TcpModel.build(8, 26);

	}

	/**
	 * 服务器响应开始传输
	 */
	public static void startResponse() {

		if (Constant.Tcp_In_Buffer[19] == (byte) 0x00) {

			ControlCenter.uploadData();
		}

	}

	/**
	 * GPRS响应服务器停止传输
	 */
	public static void stop() {

		Constant.Tcp_Out_Buffer[18] = (byte) 0x92;
		Constant.Tcp_Out_Buffer[19] = (byte) 0x00;

		TcpModel.build(2, 20);

	}

	/**
	 * 解析服务器停止传输请求
	 */
	public static void stopResponse() {

		ControlCenter.stopUploadData();
		stop();
	}

	/**
	 * 实时监控
	 */
	public static void monitorResponse() {

		switch (Constant.Tcp_Out_Buffer[19]) {

		case (byte) 0x01:

			ControlCenter.alwaysTransmit();

			break;

		case Constant.TRANSMIT_TYPE_BOOT:

			ControlCenter.startBootTransmit();
			
			break;

		case Constant.TRANSMIT_TYPE_CHECK:

			ControlCenter.startCheckTransmit();
			
			break;
		}
	}

	/**
	 * GPRS模块上传机组数据
	 */
	public static void dataTransm(int dataLength, long time) {

		Constant.Tcp_Out_Data_Buffer[18] = (byte) 0x96;

		// 获取年月日时分秒
		date.setTime(Constant.System_Time);
		calendar.setTime(date);
		Constant.Tcp_Out_Data_Buffer[19] = (byte) (calendar.get(Calendar.YEAR) - 2000);
		Constant.Tcp_Out_Data_Buffer[20] = (byte) (calendar.get(Calendar.MONTH) + 1);

		int localDay = calendar.get(Calendar.DATE);
		int localHour = calendar.get(Calendar.HOUR_OF_DAY) + 8;

		if (localHour > 23) {

			localDay++;
			localHour -= 24;

		}

		Constant.Tcp_Out_Data_Buffer[21] = (byte) localDay;
		Constant.Tcp_Out_Data_Buffer[22] = (byte) localHour;
		Constant.Tcp_Out_Data_Buffer[23] = (byte) calendar.get(Calendar.MINUTE);
		Constant.Tcp_Out_Data_Buffer[24] = (byte) calendar.get(Calendar.SECOND);

		TcpModel.buildForTransm(dataLength + 7, dataLength + 25);
	}

	/**
	 * GPRS解析服务器下发机组数据
	 */
	public static void dataTransmResponse() {

		int length = Utils.bytesToInt(Constant.Tcp_In_Buffer, 16, 17);

		if (length <= 7) {

			// 有效数据长度不超过7 说明没有机组数据。
			return;
		}

		int dataLength = length - 7;

		// modbus模拟量第一组数据
		if (Constant.Tcp_In_Buffer[25] == (byte) 0x11 || Constant.Tcp_In_Buffer[25] == (byte) 0x21
				|| Constant.Tcp_In_Buffer[25] == (byte) 0x31) {

			for (int i = 0; i < dataLength; i++) {

				Constant.Server_Data_Buffer[i] = Constant.Tcp_In_Buffer[26 + i];
			}

			return;
		}

		// modbus模拟量第二组数据
		if (Constant.Tcp_In_Buffer[25] == (byte) 0x22 || Constant.Tcp_In_Buffer[25] == (byte) 0x32) {

			for (int i = 214; i < 214 + dataLength; i++) {

				Constant.Server_Data_Buffer[i] = Constant.Tcp_In_Buffer[i - 214 + 26];
			}

			return;
		}

		// modbus模拟量第三组数据
		if (Constant.Tcp_In_Buffer[25] == (byte) 0x33) {

			for (int i = 254; i < 254 + dataLength; i++) {

				Constant.Server_Data_Buffer[i] = Constant.Tcp_In_Buffer[i - 254 + 26];
			}

			return;
		}

		// modbus开关量、7E7E
		for (int i = 0; i < dataLength; i++) {

			Constant.Server_Data_Buffer[i] = Constant.Tcp_In_Buffer[i + 25];
		}

	}

}
