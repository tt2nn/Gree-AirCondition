package com.gree.air.condition.uart.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.uart.UartModel;
import com.gree.air.condition.utils.CRC;
import com.gree.air.condition.utils.Utils;

/**
 * modbus 02 读bit 协议
 * 
 * @author lihaotian
 *
 */
public class MbReadBitModel {

	/**
	 * 处理
	 */
	public static void analyze() {

		if (!Constant.Gprs_Choosed && !DoChoose.isChooseResp()) {

			return;
		}

		Constant.Uart_Out_Buffer[2] = Constant.Uart_In_Buffer[0];
		Constant.Uart_Out_Buffer[3] = Constant.Uart_In_Buffer[1];

		// 读 bit长度 转化byte长度
		int dataLength = Utils.bytesToInt(Constant.Uart_In_Buffer, 4, 5) / 8;
		Constant.Uart_Out_Buffer[4] = (byte) dataLength;

		// 数据内容
		for (int i = 5; i < dataLength + 5; i++) {

			Constant.Uart_Out_Buffer[i] = Constant.Server_Data_Byte_Buffer[i - 5];
		}

		// crc16
		byte[] crc16 = CRC.crc16(Constant.Uart_Out_Buffer, 2, dataLength + 5);
		Constant.Uart_Out_Buffer[dataLength + 5] = crc16[1];
		Constant.Uart_Out_Buffer[dataLength + 6] = crc16[0];

		Utils.resetData(Constant.Server_Data_Byte_Buffer);
		
		UartModel.build(dataLength + 7);
	}

}
