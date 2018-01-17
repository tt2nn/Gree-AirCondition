package com.gree.air.condition.center;

import com.gree.air.condition.constant.Constant;

/**
 * Timer
 * 
 * @author lihaotian
 *
 */
public class Timer implements Runnable {

	private int packageNum = 1;

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

						packageNum = 1;
						ControlCenter.packageData();

					} else {

						packageNum++;
					}

					// 周期性心跳
					if (Constant.System_Time - Constant.Heart_Beat_Time > Constant.Tcp_Heart_Beat_Period) {

						ControlCenter.heartBeat();
					}

					// 周期性开机或者打开上报
					if (Constant.System_Time - ControlCenter.Transmit_Period_Time > Constant.Transmit_Check_Period) {

						ControlCenter.periodBootTransmit();
						ControlCenter.periodCheckTransmit();
					}
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

}
