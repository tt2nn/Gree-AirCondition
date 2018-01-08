package com.gree.air.condition.tcp.model;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.tcp.TcpModel;
import com.gree.air.condition.utils.Utils;

/**
 * 参数
 * 
 * @author lihaotian
 *
 */
public class ParamModel {

	/**
	 * 服务器查询GPRS模块参数，GPRS回复
	 */
	public static void query() {

		Constant.Tcp_Out_Buffer[18] = (byte) 0x95;

		String[] s = new String[] { "PWD:123456", "APN:1234" };
		int poi = 19;

		for (int i = 0; i < s.length; i++) {

			byte[] b = s[i].getBytes();

			for (int j = 0; j < b.length; j++) {

				Constant.Tcp_Out_Buffer[poi] = (byte) b[j];
				poi++;
			}

			Constant.Tcp_Out_Buffer[poi] = (byte) 0x00;
			poi++;
		}

		TcpModel.build(poi - 18, poi);

	}

	/**
	 * 修改GPRS模块参数 结果响应
	 */
	public static void update() {

		Constant.Tcp_Out_Buffer[18] = (byte) 0x95;
		Constant.Tcp_Out_Buffer[19] = (byte) 0x00;

		String[] s = new String[] { "PWD", "APN" };
		int poi = 20;
		for (int i = 0; i < s.length; i++) {

			byte[] b = s[i].getBytes();

			for (int j = 0; j < b.length; j++) {

				Constant.Tcp_Out_Buffer[poi] = (byte) b[j];
				poi++;
			}

			Constant.Tcp_Out_Buffer[poi] = (byte) 0x00;
			poi++;
		}

		TcpModel.build(poi - 18, poi);

	}

	/**
	 * 服务器修改GPRS模块参数 解析
	 */
	public static void updateResponse() {

		byte[][] params = new byte[50][50];
		int paramsPoi = 0;
		int poi = 19;

		int length = Utils.bytesToInt(Constant.Tcp_In_Buffer, 16, 17);

		for (int i = 19; i < length; i++) {

			// 查看所有的参数
			if (Constant.Tcp_In_Buffer[i] == 0x00) {

				for (int j = poi; j < i; j++) {

					params[paramsPoi][j] = Constant.Tcp_In_Buffer[i];
				}

				poi = i;
				paramsPoi++;

			}
		}
	}

	/**
	 * 发送GPRS信号
	 */
	public static void gprsSignal() {

		Constant.Tcp_Out_Buffer[18] = (byte) 0xF4;

		// 模块型号
		Constant.Tcp_Out_Buffer[19] = (byte) 0x02;

		// 模块序列号
		byte[] imeiBytes = Constant.Gprs_Imei.getBytes();
		for (int i = 20; i < 20 + imeiBytes.length; i++) {

			Constant.Tcp_Out_Buffer[i] = imeiBytes[i - 20];

		}
		Constant.Tcp_Out_Buffer[35] = (byte) 0x00;

		// 状态标记
		Constant.Tcp_Out_Buffer[36] = (byte) 0x00;
		// 故障代码
		Constant.Tcp_Out_Buffer[37] = (byte) 0x00;
		// 信号强度
		Constant.Tcp_Out_Buffer[38] = (byte) 12;

		TcpModel.build(21, 39);

	}

}
