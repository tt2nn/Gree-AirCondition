package com.gree.air.condition;

import com.gree.air.condition.center.ControlTimer;
import com.gree.air.condition.center.DataCenter;
import com.gree.air.condition.center.Timer;
import com.gree.air.condition.configure.Configure;
import com.gree.air.condition.configure.DeviceConfigure;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.entity.Apn;
import com.gree.air.condition.entity.Device;
import com.gree.air.condition.gpio.GpioPin;
import com.gree.air.condition.gpio.GpioTool;
import com.gree.air.condition.sms.SmsServer;
import com.gree.air.condition.spi.SpiTool;
import com.gree.air.condition.tcp.TcpPin;
import com.gree.air.condition.tcp.TcpServer;
import com.gree.air.condition.uart.UartServer;
import com.gree.air.condition.utils.Logger;
import com.gree.air.condition.utils.Utils;

/**
 * 主函数
 * 
 * @author lihaotian
 *
 */
public class Run {

	public static boolean Running_State = true;

	public static void main(String[] args) {

		Logger.log("System Running", "Start Run");

		Running_State = true;

		// start timer
		Timer.startTimer();
		ControlTimer.startTimer();
		Logger.startLogTimer();

		DeviceConfigure.deviceInit();
		SpiTool.init(2048);
		GpioPin.gpioInit();
		GpioPin.closeAllLight();
		DataCenter.init();
		Configure.init();

		try {

			Thread.sleep(30 * 1000 - Constant.System_Time);

		} catch (InterruptedException e) {

			e.printStackTrace();
		}

		DeviceConfigure.deviceInfo();

		Apn apn = Utils.getApn();
		DeviceConfigure.setApn(apn);

		GpioTool.setSignLevel(DeviceConfigure.getNetworkSignalLevel());

		Constant.Gprs_Mac[0] = Utils.stringToByte(Device.getInstance().getImei().substring(1, 3));
		Constant.Gprs_Mac[1] = Utils.stringToByte(Device.getInstance().getImei().substring(3, 5));
		Constant.Gprs_Mac[2] = Utils.stringToByte(Device.getInstance().getImei().substring(5, 7));
		Constant.Gprs_Mac[3] = Utils.stringToByte(Device.getInstance().getImei().substring(7, 9));
		Constant.Gprs_Mac[4] = Utils.stringToByte(Device.getInstance().getImei().substring(9, 11));
		Constant.Gprs_Mac[5] = Utils.stringToByte(Device.getInstance().getImei().substring(11, 13));
		Constant.Gprs_Mac[6] = Utils.stringToByte(Device.getInstance().getImei().substring(13, 15));

		new TcpPin().startPin(true);

		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		new TcpPin().startPin(false);

		SmsServer.startServer();
		DataCenter.startUploadData();
		UartServer.startServer();

		// 等待所有线程销毁
		waitThread(Timer.getTimerThread());
		waitThread(ControlTimer.getControlTimerThread());
		waitThread(DataCenter.getDataCenterThread());
		waitThread(TcpServer.getTcpThread());
		waitThread(SmsServer.getSmsThread());
		waitThread(UartServer.getUartThread());
	}

	/**
	 * 验证线程销毁
	 * 
	 * @param runThread
	 */
	private static void waitThread(Thread runThread) {

		if (runThread != null) {

			try {
				runThread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

}
