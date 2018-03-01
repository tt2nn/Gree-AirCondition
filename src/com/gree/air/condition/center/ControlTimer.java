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

				// 3秒更新信号灯
				if (Constant.System_Time - packageTime >= 3 * 1000) {

					GpioTool.setSignLevel(DeviceConfigure.getNetworkSignalLevel());
				}

				if (ControlCenter.canWorking()) {

					// 上传数据时灯闪烁
					if (Constant.GPRS_ERROR_TYPE == Constant.GPRS_ERROR_TYPE_NO
							&& Constant.Transmit_Type != Constant.TRANSMIT_TYPE_STOP) {

						if (GpioTool.getErrorValue()) {

							GpioPin.errorDark();
						}

						if (GpioTool.getCommunicationValue()) {

							GpioPin.communicationDark();

						} else {

							GpioPin.communicationLight();
						}

					} else if (Constant.GPRS_ERROR_TYPE != Constant.GPRS_ERROR_TYPE_NO) {

						// 异常状态下 异常灯亮 通讯灯灭
						if (!GpioTool.getErrorValue()) {

							GpioPin.errorLight();
						}

						if (GpioTool.getCommunicationValue()) {

							GpioPin.communicationDark();
						}

					} else {

						// 空闲时 异常灯灭 通讯灯常亮
						if (GpioTool.getErrorValue()) {

							GpioPin.errorDark();
						}

						if (!GpioTool.getCommunicationValue()) {

							GpioPin.communicationLight();
						}
					}

					// 每三秒打包一次数据
					if (Constant.System_Time - packageTime >= 3 * 1000) {

						ControlCenter.packageData();
					}

					// 周期性心跳
					if (Constant.System_Time - Constant.Heart_Beat_Time >= Constant.Tcp_Heart_Beat_Period * 1000) {

						if (Constant.GPRS_ERROR_TYPE != Constant.GPRS_ERROR_TYPE_NO) {

							Constant.Heart_Beat_Time += 10 * 1000;

						} else {

							ControlCenter.heartBeat();
						}
					}

					// 周期性开机或者打卡上报
					if (Constant.System_Time - ControlCenter.Transmit_Period_Time >= Constant.Transmit_Check_Period
							* 1000) {

						if (Constant.GPRS_ERROR_TYPE != Constant.GPRS_ERROR_TYPE_NO) {

							ControlCenter.Transmit_Period_Time = Constant.System_Time;

						} else {

							ControlCenter.periodCheckTransmit();
						}
					}

					if (Constant.GPRS_ERROR_TYPE == Constant.GPRS_ERROR_TYPE_NO) {

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
					}

					// 恢复数据上报
					if (TcpServer.isServerNormal() && Constant.GPRS_ERROR_TYPE != Constant.GPRS_ERROR_TYPE_NO
							&& Constant.Transmit_Type != Constant.TRANSMIT_TYPE_STOP) {

						ControlCenter.recoverUpload();
					}

				}

				sleepTime = Sleep_Time - (Constant.System_Time - workTime);
				sleepTime = sleepTime < 0 ? 0 : sleepTime;

			} catch (Exception e) {

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
