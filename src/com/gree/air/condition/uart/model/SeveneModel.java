package com.gree.air.condition.uart.model;

import com.gree.air.condition.center.DataCenter;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.uart.UartModel;
import com.gree.air.condition.utils.CRC;

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

		if (!DoChoose.choose()) {

			return;
		}

		buildSendBufferHeader();
		buildSendDataHeader();

		for (int i = 30; i < 94; i++) {

			Constant.Uart_Out_Buffer[i] = Constant.Server_Data_Buffer[i - 30];
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

			Constant.Gprs_Choosed = true;
			DataCenter.chooseTransmit();
		}

		buildSendBufferHeader();
		buildSendDataHeader();

		for (int i = 30; i < 94; i++) {

			Constant.Uart_Out_Buffer[i] = (byte) 0x00;
		}

		Constant.Uart_Out_Buffer[94] = CRC.crc8(Constant.Uart_Out_Buffer, 2, 94);

		UartModel.build(95);

		// DataCenter.setUploadMarker(Utils.byteGetBit(Constant.Uart_In_Buffer[10], 0),
		// Utils.byteGetBit(Constant.Uart_In_Buffer[10], 3),
		// Utils.byteGetBit(Constant.Uart_In_Buffer[10], 2));
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
	 * p20 协议版本号
	 * 
	 */
	private static void buildSendDataHeader() {

		// 机组数据从第6位开始
		Constant.Uart_Out_Buffer[9] = (byte) 0x02;

		byte[] imeiBytes = "8613900300839460".getBytes();
		for (int i = 10; i < 10 + imeiBytes.length; i++) {

			Constant.Uart_Out_Buffer[i] = imeiBytes[i - 10];
		}

		// 状态标记
		Constant.Uart_Out_Buffer[26] = (byte) 0x00;

		// 故障代码
		Constant.Uart_Out_Buffer[27] = (byte) 0x00;

		// 信号强度
		Constant.Uart_Out_Buffer[28] = (byte) 16;

		// 协议版本
		Constant.Uart_Out_Buffer[29] = Constant.Uart_In_Buffer[7];

	}

}
