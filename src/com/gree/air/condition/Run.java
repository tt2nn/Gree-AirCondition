package com.gree.air.condition;

import com.gree.air.condition.center.DataCenter;
import com.gree.air.condition.center.Timer;
import com.gree.air.condition.configure.Configure;
import com.gree.air.condition.configure.DeviceConfigure;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.entity.Apn;
import com.gree.air.condition.gpio.GpioPin;
import com.gree.air.condition.gpio.GpioTool;
import com.gree.air.condition.sms.SmsServer;
import com.gree.air.condition.spi.SpiTool;
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

		DeviceConfigure.deviceInit();
		DeviceConfigure.setApn(new Apn());
		SpiTool.init(2048);
		GpioPin.gpioInit();
		GpioPin.closeAllLight();
		DataCenter.init();

		try {

			Thread.sleep(30 * 1000);

			while (!DeviceConfigure.deviceInit()) {

				Thread.sleep(10 * 1000);
			}

			DeviceConfigure.deviceInfo();
			GpioTool.setSignLevel(DeviceConfigure.getNetworkSignalLevel());

			Constant.System_Time = 60000;

			Constant.Gprs_Mac[0] = (byte) Integer.parseInt(Constant.device.getImei().substring(1, 3));
			Constant.Gprs_Mac[1] = (byte) Integer.parseInt(Constant.device.getImei().substring(3, 5));
			Constant.Gprs_Mac[2] = (byte) Integer.parseInt(Constant.device.getImei().substring(5, 7));
			Constant.Gprs_Mac[3] = (byte) Integer.parseInt(Constant.device.getImei().substring(7, 9));
			Constant.Gprs_Mac[4] = (byte) Integer.parseInt(Constant.device.getImei().substring(9, 11));
			Constant.Gprs_Mac[5] = (byte) Integer.parseInt(Constant.device.getImei().substring(11, 13));
			Constant.Gprs_Mac[6] = (byte) Integer.parseInt(Constant.device.getImei().substring(13, 15));

			SmsServer.startServer();

			DataCenter.startUploadData();

		} catch (InterruptedException e) {

			e.printStackTrace();
		}

	}

}
