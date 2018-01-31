package com.gree.air.condition.center;

import com.gree.air.condition.constant.Constant;

/**
 * 定时器
 * 
 * @author lihaotian
 *
 */
public class Timer implements Runnable {

	private long synchronizedTime = 0L;

	/**
	 * 启动Timer
	 */
	public static void startTimer() {

		new Thread(new Timer()).start();
	}

	public void run() {

		synchronizedTime = Constant.System_Time;

		while (true) {

			try {

				Thread.sleep(500);

				if (Constant.System_Delta_Time != 0 && Constant.System_Time - synchronizedTime >= 60 * 1000) {

					Constant.System_Time = System.currentTimeMillis() + Constant.System_Delta_Time;
					synchronizedTime = Constant.System_Time;

				} else {

					Constant.System_Time += 500;
				}

			} catch (InterruptedException e) {

				e.printStackTrace();
			}

		}
	}
}
