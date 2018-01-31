package com.gree.air.condition;

import com.gree.air.condition.center.ControlTimer;
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
import com.gree.air.condition.utils.Utils;

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
		ControlTimer.startTimer();

		DeviceConfigure.deviceInit();
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

			Apn apn = new Apn();
			if (Constant.device.getMnc() == 1) {

				apn.setApnName(Constant.Apn_Cucc);

			} else if (Constant.device.getMnc() == 0) {

				apn.setApnName(Constant.Apn_Cmcc);
			}
			DeviceConfigure.setApn(apn);

			GpioTool.setSignLevel(DeviceConfigure.getNetworkSignalLevel());

			Constant.Gprs_Mac[0] = Utils.stringToByte(Constant.device.getImei().substring(1, 3));
			Constant.Gprs_Mac[1] = Utils.stringToByte(Constant.device.getImei().substring(3, 5));
			Constant.Gprs_Mac[2] = Utils.stringToByte(Constant.device.getImei().substring(5, 7));
			Constant.Gprs_Mac[3] = Utils.stringToByte(Constant.device.getImei().substring(7, 9));
			Constant.Gprs_Mac[4] = Utils.stringToByte(Constant.device.getImei().substring(9, 11));
			Constant.Gprs_Mac[5] = Utils.stringToByte(Constant.device.getImei().substring(11, 13));
			Constant.Gprs_Mac[6] = Utils.stringToByte(Constant.device.getImei().substring(13, 15));

			Constant.System_Time = 60000;

			SmsServer.startServer();

			DataCenter.startUploadData();

		} catch (InterruptedException e) {

			e.printStackTrace();
		}

	}

}
