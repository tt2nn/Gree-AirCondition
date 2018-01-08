package com.gree.air.condition.uart;

import com.gree.air.condition.center.DataCenter;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.uart.model.MbReadBitModel;
import com.gree.air.condition.uart.model.MbReadWordModel;
import com.gree.air.condition.uart.model.MbWriteModel;
import com.gree.air.condition.uart.model.SeveneModel;
import com.gree.air.condition.utils.CRC;
import com.gree.air.condition.utils.Utils;

/**
 * Uart功能
 * 
 * @author lihaotian
 *
 */
public class UartModel {

	// private static long time;

	/**
	 * 判断串口通信协议类型（7E7E / modbus）
	 */
	public static void analyze() {

		// time = System.currentTimeMillis();

		if (Constant.Uart_In_Buffer_Length == 4 && Constant.Uart_In_Buffer[0] == (byte) 0xA5
				&& Constant.Uart_In_Buffer[1] == (byte) 0xA7 && Constant.Uart_In_Buffer[2] == (byte) 0xB6
				&& Constant.Uart_In_Buffer[3] == (byte) 0xB4) {

			// A5 A7 B6 B4 是一个机组帧

			DataCenter.writeDataBuffer();

			return;

		}

		// 如果是 A5A7 开头 则是 7E7E协议
		if (Constant.Uart_In_Buffer[0] == (byte) 0xA5 && Constant.Uart_In_Buffer[1] == (byte) 0xA7) {

			// 7E7E 下标5表示 有效数据长度
			int dataLength = Constant.Uart_In_Buffer[6] & 0xFF;

			// 有效数据长度是否符合条件，验证CRC8校验是否正确
			if (dataLength <= 85
					&& Constant.Uart_In_Buffer[7 + dataLength] == CRC.crc8(Constant.Uart_In_Buffer, 7 + dataLength)) {

				logBuffer();

				SeveneModel.analyze();
			}

			return;
		}

		// 判断是modbus协议
		if (Constant.Uart_In_Buffer[0] == (byte) 0xFF) {

			// System.out.println("message is modbus");

			switch (Constant.Uart_In_Buffer[1]) {

			case (byte) 0x10: // 10写功能

				// modbus 下标6表示 有效数据长度
				int dataLength = Constant.Uart_In_Buffer[6] & 0xFF;

				byte[] crc10 = CRC.crc16(Constant.Uart_In_Buffer, 7 + dataLength);

				// 判断CRC16校验是否正确
				if (dataLength <= 246 && Constant.Uart_In_Buffer[7 + dataLength] == crc10[1]
						&& Constant.Uart_In_Buffer[8 + dataLength] == crc10[0]) {

					logBuffer();

					MbWriteModel.analyze();
				}

				return;

			case (byte) 0x04: // 读word CRC16的校验位在6和7

				byte[] crc04 = CRC.crc16(Constant.Uart_In_Buffer, 6);

				if (Utils.bytesToInt(Constant.Uart_In_Buffer, 4, 5) <= 123 && Constant.Uart_In_Buffer[6] == crc04[1]
						&& Constant.Uart_In_Buffer[7] == crc04[0]) {

					// logBuffer();

					MbReadWordModel.analyze();
				}

				return;

			case (byte) 0x02:// 读bit

				byte[] crc02 = CRC.crc16(Constant.Uart_In_Buffer, 6);

				if (Utils.bytesToInt(Constant.Uart_In_Buffer, 4, 5) <= 48 && Constant.Uart_In_Buffer[6] == crc02[1]
						&& Constant.Uart_In_Buffer[7] == crc02[0]) {

					// logBuffer();

					MbReadBitModel.analyze();
				}

				return;

			}
		}

		// 如果GPRS被选中则缓存机组数据

		DataCenter.writeDataBuffer();
	}

	/**
	 * 建立 发出数据 并 调用 server 发送函数
	 */
	public static void build(int length) {

		Constant.Uart_Out_Buffer[0] = (byte) 0xFA;
		Constant.Uart_Out_Buffer[1] = (byte) 0xFB;

		// StringBuffer stringBuffer = new StringBuffer();
		// for (int i = 0; i < length; i++) {
		//
		// stringBuffer.append(" " + Integer.toHexString(Constant.Uart_Out_Buffer[i] &
		// 0xFF));
		// }
		//
		// System.out.println("send uart message ---" + stringBuffer.toString());

		UartServer.sendData(length);
	}

	/**
	 * 打log用于测试
	 */
	private static void logBuffer() {

		StringBuffer stringBuffer = new StringBuffer();
		for (int i = 0; i < Constant.Uart_In_Buffer_Length; i++) {

			stringBuffer.append(" " + Integer.toHexString(Constant.Uart_In_Buffer[i] & 0xFF));
		}

		System.out.println("new messsage ======== " + stringBuffer.toString());
	}

}
