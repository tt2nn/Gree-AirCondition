package com.gree.air.condition.tcp;

import com.gree.air.condition.center.ControlCenter;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.tcp.model.LoginModel;
import com.gree.air.condition.tcp.model.ParamModel;
import com.gree.air.condition.tcp.model.TimeModel;
import com.gree.air.condition.tcp.model.TransmitModel;
import com.gree.air.condition.utils.CRC;
import com.gree.air.condition.utils.Utils;

/**
 * TCP 业务类
 * 
 * @author lihaotian
 *
 */
public class TcpModel {

	/**
	 * 解析 TCP协议 判断 功能码
	 */
	public static void analyze() {

		// 检验引导码
		if (Constant.Tcp_In_Buffer[0] != (byte) 0x7E || Constant.Tcp_In_Buffer[1] != (byte) 0x7E) {

			System.out.println("TCP 7E7E Error");
			return;
		}

		// 检验校验码
		int dataLength = Utils.bytesToInt(Constant.Tcp_In_Buffer, 16, 17);

		if (Constant.Tcp_In_Buffer[18 + dataLength] != CRC.crc8(Constant.Tcp_In_Buffer, 2, 18 + dataLength)) {

			System.out.println("TCP CRC8 Error");

			return;
		}

		// 判断功能码
		switch (Constant.Tcp_In_Buffer[18]) {

		case (byte) 0x72:

			ControlCenter.resetSystem();
			break;

		case (byte) 0x90: // 登录 响应

			LoginModel.loginResponse();

			break;

		case (byte) 0x91: // 开始传输 响应

			TransmitModel.startResponse();

			break;

		case (byte) 0x92: // 停止发送数据 请求

			TransmitModel.stopResponse();

			break;

		case (byte) 0x93: // 进行实时监控 请求

			TransmitModel.monitorResponse();

			break;

		case (byte) 0x94: // 修改GPRS模块 请求

			ParamModel.updateResponse();

			break;

		case (byte) 0x95: // 查询GPRS模块 请求

			ParamModel.query();

			break;

		case (byte) 0x96: // 服务器发送机组数据至GPRS模块 请求

			TransmitModel.dataTransmResponse();

			break;

		case (byte) 0x98: // 设置静默时间 请求

			TimeModel.stopTimeResponse();

			break;

		case (byte) 0xF3: // 心跳命令 响应

			TimeModel.heartResponse();

			break;

		}

	}

	/**
	 * TCP 输出流 构建
	 * 
	 * @param dataLength
	 *            有效数据长度
	 * @param crcPosition
	 *            crc校验码位置
	 */
	public static void build(int dataLength, int crcPosition) {

		if (!TcpServer.isServerWorking()) {

			return;
		}

		while (!TcpServer.isServerNormal()) {

			try {

				Thread.sleep(1000);

			} catch (InterruptedException e) {

				e.printStackTrace();
			}

		}

		// 引导码
		Constant.Tcp_Out_Buffer[0] = (byte) 0x7E;
		Constant.Tcp_Out_Buffer[1] = (byte) 0x7E;

		// 目标地址
		for (int i = 0; i < Constant.Server_Mac.length; i++) {

			Constant.Tcp_Out_Buffer[i + 2] = Constant.Server_Mac[i];
		}

		// 源地址
		for (int i = 0; i < Constant.Gprs_Mac.length; i++) {

			Constant.Tcp_Out_Buffer[i + 9] = Constant.Gprs_Mac[i];
		}

		// 数据长度
		byte[] lengthBytes = Utils.intToBytes(dataLength);
		Constant.Tcp_Out_Buffer[16] = lengthBytes[0];
		Constant.Tcp_Out_Buffer[17] = lengthBytes[1];

		// crc8校验码
		Constant.Tcp_Out_Buffer[crcPosition] = CRC.crc8(Constant.Tcp_Out_Buffer, 2, crcPosition);

		String s = "";
		for (int i = 0; i < crcPosition + 1; i++) {

			s = s + " " + Integer.toHexString(Constant.Tcp_Out_Buffer[i] & 0xFF);
		}

		System.out.println("tcp send message ---" + s);

		TcpServer.sendData(crcPosition + 1);
	}

	/**
	 * TCP 输出流 构建 至 上传机组数据使用
	 * 
	 * @param dataLength
	 *            有效数据长度
	 * @param crcPosition
	 *            crc校验码位置
	 */
	public static void buildForTransm(int dataLength, int crcPosition) {

		if (!TcpServer.isServerWorking()) {

			return;
		}

		while (!TcpServer.isServerNormal()) {

			try {

				Thread.sleep(1000);

			} catch (InterruptedException e) {

				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		// 引导码
		Constant.Tcp_Out_Data_Buffer[0] = (byte) 0x7E;
		Constant.Tcp_Out_Data_Buffer[1] = (byte) 0x7E;

		// 目标地址
		for (int i = 0; i < Constant.Server_Mac.length; i++) {

			Constant.Tcp_Out_Data_Buffer[i + 2] = Constant.Server_Mac[i];
		}

		// 源地址
		for (int i = 0; i < Constant.Gprs_Mac.length; i++) {

			Constant.Tcp_Out_Data_Buffer[i + 9] = Constant.Gprs_Mac[i];
		}

		// 数据长度
		byte[] lengthBytes = Utils.intToBytes(dataLength);
		Constant.Tcp_Out_Data_Buffer[16] = lengthBytes[0];
		Constant.Tcp_Out_Data_Buffer[17] = lengthBytes[1];

		// crc8校验码
		Constant.Tcp_Out_Data_Buffer[crcPosition] = CRC.crc8(Constant.Tcp_Out_Data_Buffer, 2, crcPosition);

		String s = "";
		for (int i = 0; i < crcPosition + 1; i++) {

			s = s + " " + Integer.toHexString(Constant.Tcp_Out_Data_Buffer[i] & 0xFF);
		}

		System.out.println("tcp send transm message ---" + s);

		TcpServer.sendDataForTransm(crcPosition + 1);
	}

}
