package com.gree.air.condition.center;

import com.gree.air.condition.configure.DeviceConfigure;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.gpio.GpioTool;

/**
 * Timer
 * 
 * @author lihaotian
 *
 */
public class Timer implements Runnable {

	private int packageNum = 1;

	private static boolean listensePush;
	private static long Push_Time;

	/**
	 * 启动Timer
	 */
	public static void startTimer() {

		new Thread(new Timer()).start();
	}

	public void run() {

		System.out.println("=================== start timer =========================");

		while (true) {

			try {

				Thread.sleep(1000);
				Constant.System_Time = Constant.System_Time + 1000;
				
				if (ControlCenter.canWorking()) {
					
					// 每三秒打包一次数据
					if (packageNum == 3) {
						
						GpioTool.setSignLevel(DeviceConfigure.getNetworkSignalLevel());

						packageNum = 1;
						ControlCenter.packageData();

					} else {

						packageNum++;
					}

					// 周期性心跳
					if (Constant.System_Time - Constant.Heart_Beat_Time > Constant.Tcp_Heart_Beat_Period * 1000) {

						ControlCenter.heartBeat();
					}

					// 周期性开机或者打卡上报
					if (Constant.System_Time - ControlCenter.Transmit_Period_Time > Constant.Transmit_Check_Period
							* 1000) {

						ControlCenter.periodBootTransmit();
						ControlCenter.periodCheckTransmit();
					}

					// 判断进行按键上报
					if (Constant.System_Time - Push_Time > 3 * 1000 && listensePush
							&& Constant.Transmit_Type != Constant.TRANSMIT_TYPE_PUSHKEY) {

						listensePush = false;
						ControlCenter.pushKeyTransmit();
					}

					// 判断停止按键上报
					if (Constant.System_Time - Push_Time > 5 * 1000 && listensePush
							&& Constant.Transmit_Type == Constant.TRANSMIT_TYPE_PUSHKEY) {

						listensePush = false;
						ControlCenter.stopUploadData();
					}
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 按下
	 */
	public static void pushDown() {

		Push_Time = Constant.System_Time;
		listensePush = true;
	}

	/**
	 * 抬起
	 */
	public static void pushUp() {

		listensePush = false;
	}

}
