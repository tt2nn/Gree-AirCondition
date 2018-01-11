package com.gree.air.condition;

import com.gree.air.condition.center.DataCenter;
import com.gree.air.condition.center.Timer;
import com.gree.air.condition.configure.Configure;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.sms.SmsServer;
import com.gree.air.condition.uart.UartServer;

/**
 * 主函数
 * 
 * @author lihaotian
 *
 */
public class Run {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		System.out.println("============================ start run ===============================");

		Configure.init();

		System.out.println("----------------" + Constant.Sms_Pwd);

		UartServer.startServer();

		Timer.startTimer();

		try {

			Thread.sleep(30 * 1000);

			SmsServer.startServer();

			// TcpServer.startServer();

			// DataCenter.chooseTransmit();

			DataCenter.startUploadData();

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
