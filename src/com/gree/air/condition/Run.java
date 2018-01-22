package com.gree.air.condition;

import com.gree.air.condition.center.DataCenter;
import com.gree.air.condition.center.Timer;
import com.gree.air.condition.configure.Configure;
import com.gree.air.condition.configure.DeviceConfigure;
import com.gree.air.condition.entity.Apn;
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

		System.out.println("============================ start run ===============================");

		Configure.init();

		UartServer.startServer();

		Timer.startTimer();

		try {

			while (DeviceConfigure.deviceInit()) {

				Thread.sleep(10 * 1000);
			}

			DeviceConfigure.deviceInfo();

			DeviceConfigure.setApn(new Apn());

			SmsServer.startServer();
			DataCenter.startUploadData();

		} catch (InterruptedException e) {

			e.printStackTrace();
		}

	}

}
