package com.gree.air.condition.uart.model;

import com.gree.air.condition.center.ControlCenter;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.uart.UartModel;
import com.gree.air.condition.utils.CRC;

/**
 * modbus 10 写 协议 model
 * 
 * @author lihaotian
 *
 */
public class MbWriteModel {

	/**
	 * 解析
	 */
	public static void analyze() {

		switch (Constant.Uart_In_Buffer[10]) {

		case (byte) Constant.TYPE_CALL: // 点名

			call();

			break;

		case (byte) Constant.TYPE_CHOOSE: // 选举

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

		buildSendBuffer();

		UartModel.build(10);

	}

	/**
	 * 点名
	 */
	private static void call() {

		if (!Constant.Gprs_Choosed && !DoChoose.isChooseResp()) {

			return;
		}

		buildSendBuffer();

		UartModel.build(10);

		if (!Constant.Gprs_Choosed && DoChoose.isChooseResp()) {

			ControlCenter.chooseGprs();
			return;
		}

		// 判断是否是 上电是状态为选中
		if (!DoChoose.isChooseResp()) {

			ControlCenter.powerCall();
			return;
		}

		ControlCenter.setMarker(Constant.Uart_In_Buffer[30], Constant.Uart_In_Buffer[32], Constant.Uart_In_Buffer[34],
				Constant.Uart_In_Buffer[16], Constant.Uart_In_Buffer[18]);
	}

	/**
	 * 组装数据
	 */
	private static void buildSendBuffer() {

		Constant.Uart_Out_Buffer[2] = Constant.Uart_In_Buffer[0];
		Constant.Uart_Out_Buffer[3] = Constant.Uart_In_Buffer[1];
		Constant.Uart_Out_Buffer[4] = Constant.Uart_In_Buffer[2];
		Constant.Uart_Out_Buffer[5] = Constant.Uart_In_Buffer[3];
		Constant.Uart_Out_Buffer[6] = Constant.Uart_In_Buffer[4];
		Constant.Uart_Out_Buffer[7] = Constant.Uart_In_Buffer[5];

		byte[] crc16 = CRC.crc16(Constant.Uart_Out_Buffer, 2, 8);
		Constant.Uart_Out_Buffer[8] = crc16[1];
		Constant.Uart_Out_Buffer[9] = crc16[0];
	}

}
