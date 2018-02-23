package com.gree.air.condition.center;

import com.gree.air.condition.configure.DeviceConfigure;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.gpio.GpioPin;
import com.gree.air.condition.gpio.GpioTool;
import com.gree.air.condition.tcp.TcpServer;

/**
 * Timer
 * 
 * @author lihaotian
 *
 */
public class ControlTimer implements Runnable {

	private static boolean listensePush;
	private static long Push_Time;

	public static long packageTime = 0L;

	private final long Sleep_Time = 1000L;
	private long sleepTime = 1000L;
	private long workTime = 0L;

	/**
	 * 启动Timer
	 */
	public static void startTimer() {

		new Thread(new ControlTimer()).start();
	}

	public void run() {

		packageTime = Constant.System_Time;

		while (true) {

			try {

				Thread.sleep(sleepTime);
				workTime = Constant.System_Time;

				if (ControlCenter.canWorking()) {

					// 上传数据时灯闪烁、空闲时灯常亮
					if (Constant.Transmit_Type != Constant.TRANSMIT_TYPE_STOP) {

						if (GpioTool.getCommunicationValue()) {

							GpioPin.communicationDark();

						} else {

							GpioPin.communicationLight();
						}

					} else {

						if (!GpioTool.getCommunicationValue()) {

							GpioPin.communicationLight();
						}

					}

					// 每三秒打包一次数据
					if (Constant.System_Time - packageTime >= 3 * 1000) {

						GpioTool.setSignLevel(DeviceConfigure.getNetworkSignalLevel());
						ControlCenter.packageData();
					}

					// 周期性心跳
					if (Constant.System_Time - Constant.Heart_Beat_Time >= Constant.Tcp_Heart_Beat_Period * 1000) {

						ControlCenter.heartBeat();
					}

					// 周期性开机或者打卡上报
					if (Constant.System_Time - ControlCenter.Transmit_Period_Time >= Constant.Transmit_Check_Period
							* 1000) {

						ControlCenter.periodBootTransmit();
						ControlCenter.periodCheckTransmit();
					}

					// 判断进行按键上报
					if (Constant.System_Time - Push_Time >= 3 * 1000 && listensePush
							&& Constant.Transmit_Type != Constant.TRANSMIT_TYPE_PUSHKEY) {

						listensePush = false;
						ControlCenter.pushKeyTransmit();
					}

					// 判断停止按键上报
					if (Constant.System_Time - Push_Time >= 5 * 1000 && listensePush
							&& Constant.Transmit_Type == Constant.TRANSMIT_TYPE_PUSHKEY) {

						listensePush = false;
						ControlCenter.stopUploadData();
					}

					// 恢复数据上报
					if (TcpServer.isServerNormal() && ControlCenter.Tcp_Error_Pause_Upload
							&& Constant.Transmit_Type != Constant.TRANSMIT_TYPE_STOP) {

						ControlCenter.recoverUpload();
					}

				}

				sleepTime = Sleep_Time - (Constant.System_Time - workTime);
				sleepTime = sleepTime < 0 ? 0 : sleepTime;

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
