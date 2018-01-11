package com.gree.air.condition.uart.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.uart.UartModel;
import com.gree.air.condition.utils.CRC;
import com.gree.air.condition.utils.Utils;

/**
 * modbus 04 读word 协议
 * 
 * @author lihaotian
 *
 */
public class MbReadWordModel {

	/**
	 * 处理
	 */
	public static void analyze() {
		
		if (!Constant.Gprs_Choosed && !DoChoose.isChooseResp()) {

			return;
		}

		Constant.Uart_Out_Buffer[2] = Constant.Uart_In_Buffer[0];
		Constant.Uart_Out_Buffer[3] = Constant.Uart_In_Buffer[1];

		// 获取读数据长度
		int dataLength = Utils.bytesToInt(Constant.Uart_In_Buffer, 4, 5) * 2;
		Constant.Uart_Out_Buffer[4] = (byte) dataLength;

		// word0
		Constant.Uart_Out_Buffer[5] = (byte) 0x00;
		Constant.Uart_Out_Buffer[6] = (byte) 0x02;

		// word1~8
		byte[] imeiBytes = "8613900300839460".getBytes();
		for (int i = 7; i < 7 + imeiBytes.length; i++) {

			Constant.Uart_Out_Buffer[i] = imeiBytes[i - 7];
		}

		// word9
		Constant.Uart_Out_Buffer[23] = (byte) 0x00;
		Constant.Uart_Out_Buffer[24] = (byte) 0x00;

		// word10
		Constant.Uart_Out_Buffer[25] = (byte) 0x00;
		Constant.Uart_Out_Buffer[26] = (byte) 0x00;

		// word11
		Constant.Uart_Out_Buffer[27] = (byte) 0x00;
		Constant.Uart_Out_Buffer[28] = (byte) 0x10;

		// word12
		Constant.Uart_Out_Buffer[29] = (byte) 0x00;
		Constant.Uart_Out_Buffer[30] = (byte) 0x00;

		int readStart = Utils.bytesToInt(Constant.Uart_In_Buffer, 2, 3);
		// 回复读数据内容
		for (int i = 31; i < dataLength + 5; i++) {

			Constant.Uart_Out_Buffer[i] = Constant.Server_Data_Buffer[i - 31 + readStart];
		}

		// crc16校验
		byte[] crc16 = CRC.crc16(Constant.Uart_Out_Buffer, 2, dataLength + 5);
		Constant.Uart_Out_Buffer[dataLength + 5] = crc16[1];
		Constant.Uart_Out_Buffer[dataLength + 6] = crc16[0];

		UartModel.build(dataLength + 7);

	}
}
