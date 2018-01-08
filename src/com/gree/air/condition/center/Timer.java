package com.gree.air.condition.center;

import com.gree.air.condition.constant.Constant;

/**
 * Timer
 * 
 * @author lihaotian
 *
 */
public class Timer implements Runnable {

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
				
				Thread.sleep(1000);
				Constant.System_Time = Constant.System_Time + 1000;
				
				Thread.sleep(1000);
				Constant.System_Time = Constant.System_Time + 1000;
				DataCenter.writeSpi();

			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
