package com.gree.air.condition.uart.model;

import com.gree.air.condition.center.ControlCenter;
import com.gree.air.condition.configure.DeviceConfigure;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.uart.UartModel;
import com.gree.air.condition.utils.CRC;
import com.gree.air.condition.utils.Utils;

/**
 * 7E7E协议 model
 * 
 * @author lihaotian
 *
 */
public class SeveneModel {

	/**
	 * 解析协议
	 */
	public static void analyze() {

		if (Constant.Uart_In_Buffer[4] != (byte) 0x70 && Constant.Uart_In_Buffer[5] != (byte) 0xFF) {

			System.out.println("from or to address is error");

			return;
		}

		switch (Constant.Uart_In_Buffer[8]) {

		case (byte) Constant.TYPE_CALL:// 点名

			call();

			break;

		case (byte) Constant.TYPE_CHOOSE:// 选举

			choose();

			break;
		}

	}

	/**
	 * 选举
	 */
	private static void choose() {

		if (Constant.Gprs_Choosed) {

			ControlCenter.chooseRest();
		}

		if (!DoChoose.choose()) {

			return;
		}

		buildSendBufferHeader();
		buildSendDataHeader();

		for (int i = 30; i < 94; i++) {

			Constant.Uart_Out_Buffer[i] = Constant.Server_Data_Byte_Buffer[i - 30];
		}

		Constant.Uart_Out_Buffer[94] = CRC.crc8(Constant.Uart_Out_Buffer, 2, 94);

		UartModel.build(95);

	}

	/**
	 * 点名
	 */
	private static void call() {

		if (!Constant.Gprs_Choosed && !DoChoose.isChooseResp()) {

			return;
		}

		if (!Constant.Gprs_Choosed && DoChoose.isChooseResp()) {

			ControlCenter.chooseGprs();
		}

		// 判断是否是 上电是状态为选中
		if (!DoChoose.isChooseResp() && Constant.Init_Success) {

			ControlCenter.powerCall();
		}

		buildSendBufferHeader();
		buildSendDataHeader();

		for (int i = 29; i < 94; i++) {

			Constant.Uart_Out_Buffer[i] = Constant.Server_Data_Byte_Buffer[i - 29];
		}

		Constant.Uart_Out_Buffer[94] = CRC.crc8(Constant.Uart_Out_Buffer, 2, 94);

		UartModel.build(95);

		ControlCenter.setMarker(Utils.byteGetBit(Constant.Uart_In_Buffer[11], 0),
				Utils.byteGetBit(Constant.Uart_In_Buffer[11], 1), Utils.byteGetBit(Constant.Uart_In_Buffer[11], 2),
				Utils.byteGetBit(Constant.Uart_In_Buffer[10], 3));

	}

	/**
	 * GPRS模块回复显示板 <br>
	 * 引导码 A5 A7 <br>
	 * 机型 00 00 <br>
	 * 目的地址 FF <br>
	 * 源地址 70 <br>
	 * 有效数据长度 固定 55
	 */
	private static void buildSendBufferHeader() {

		Constant.Uart_Out_Buffer[2] = (byte) 0xA5;
		Constant.Uart_Out_Buffer[3] = (byte) 0xA7;
		Constant.Uart_Out_Buffer[4] = (byte) 0x00;
		Constant.Uart_Out_Buffer[5] = (byte) 0x00;
		Constant.Uart_Out_Buffer[6] = (byte) 0xFF;
		Constant.Uart_Out_Buffer[7] = (byte) 0x70;
		Constant.Uart_Out_Buffer[8] = (byte) 0x55;
	}

	/**
	 * GPRS模块回复显示板，机组数据前面固定字节 <br>
	 * p0默认02 <br>
	 * p1-p16 模块序列号 现在没有实现 <br>
	 * p17 状态标记 <br>
	 * p18 故障代码 <br>
	 * p19 信号强度 0-31 99 表示无网络 <br>
	 * 
	 */
	private static void buildSendDataHeader() {

		// 机组数据从第6位开始
		Constant.Uart_Out_Buffer[9] = (byte) 0x02;

		byte[] imeiBytes = Constant.device.getImei().getBytes();
		for (int i = 0; i < imeiBytes.length; i++) {

			Constant.Uart_Out_Buffer[i + 10] = imeiBytes[i];
		}
		Constant.Uart_Out_Buffer[25] = (byte) 0x00;

		// 状态标记
		if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

			Constant.Uart_Out_Buffer[26] = (byte) 0x00;

		} else {

			Constant.Uart_Out_Buffer[26] = (byte) 0x01;
		}

		// 故障代码
		Constant.Uart_Out_Buffer[27] = (byte) 0x00;

		// 信号强度
		Constant.Uart_Out_Buffer[28] = (byte) DeviceConfigure.getNetworkSignalLevel();

	}

}
