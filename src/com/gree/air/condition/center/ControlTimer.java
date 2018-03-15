package com.gree.air.condition.center;

import com.gree.air.condition.Run;
import com.gree.air.condition.configure.DeviceConfigure;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.gpio.GpioPin;
import com.gree.air.condition.gpio.GpioTool;
import com.gree.air.condition.tcp.TcpPin;
import com.gree.air.condition.tcp.TcpServer;
import com.gree.air.condition.utils.Logger;
import com.gree.air.condition.variable.Variable;

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
	public static long signTime = 0L;

	private final long Sleep_Time = 1000L;
	private long sleepTime = 1000L;
	private long workTime = 0L;
	private long pinTime = 0L;

	private static int systemResetTime = 0;
	private static int systemResetPushTime = 0;

	private static Thread controlTimerThread;

	public static boolean chooseTransmit = false;

	public static int Choose_Prower_Time = 0;

	/**
	 * 启动Timer
	 */
	public static void startTimer() {

		controlTimerThread = new Thread(new ControlTimer());
		controlTimerThread.start();
	}

	public void run() {

		packageTime = Constant.System_Time;
		signTime = Constant.System_Time;
		pinTime = Constant.System_Time;
		systemResetTime = 0;
		systemResetPushTime = 0;

		while (Run.Running_State) {

			try {

				Thread.sleep(sleepTime);
				workTime = Constant.System_Time;

				if (systemResetTime < 100) {

					systemResetTime++;
				}

				if (Choose_Prower_Time < 100) {

					Choose_Prower_Time++;
				}

				// 上电后前一分钟，响应重置操作
				if (systemResetTime < 60) {

					if (systemResetTime - systemResetPushTime >= 15 && listensePush) {

						listensePush = false;
						ControlCenter.resetSystem();
						return;
					}
				}

				if (systemResetTime > 35) {

					if (!DeviceConfigure.hasSim()) {

						Constant.GPRS_ERROR_TYPE = Constant.GPRS_ERROR_TYPE_SIM;

					} else if ((systemResetTime >= 60 && !Constant.Init_Success) || !DeviceConfigure.hasNetwork()) {

						Constant.GPRS_ERROR_TYPE = Constant.GPRS_ERROR_TYPE_NETWORK;

					} else if (Choose_Prower_Time > 90 && Constant.Gprs_Choosed && !Variable.Transmit_Choose_Or_Power) {

						Constant.GPRS_ERROR_TYPE = Constant.GPRS_ERROR_TYPE_NETWORK;

					} else if (Constant.GPRS_ERROR_TYPE != Constant.GPRS_ERROR_TYPE_SERVER) {

						Constant.GPRS_ERROR_TYPE = Constant.GPRS_ERROR_TYPE_NO;

					}
				}

				if (!Constant.Init_Success && pinTime + 90 * 1000 <= Constant.System_Time) {

					pinTime = Constant.System_Time;

					new TcpPin().startPin(true);
					new Thread(new Runnable() {

						public void run() {

							try {
								Thread.sleep(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}

							new TcpPin().startPin(false);
						}
					}).start();
				}

				// 异常状态下 异常灯亮 通讯灯灭
				if (Constant.GPRS_ERROR_TYPE != Constant.GPRS_ERROR_TYPE_NO) {

					if (!GpioTool.getErrorValue()) {

						GpioPin.errorLight();
					}

					if (GpioTool.getCommunicationValue()) {

						GpioPin.communicationDark();
					}

				} else {

					if (GpioTool.getErrorValue()) {

						GpioPin.errorDark();
					}
				}

				// 3秒更新信号灯
				if (Constant.System_Time - signTime >= 1 * 1000) {

					signTime = Constant.System_Time;
					GpioTool.setSignLevel(DeviceConfigure.getNetworkSignalLevel());
				}

				/**
				 * 90s选举上报
				 */
				if (systemResetTime >= 90 && Constant.Gprs_Choosed && !chooseTransmit
						&& !Variable.Transmit_Choose_Or_Power) {

					chooseTransmit = true;
					ControlCenter.chooseTransmit();
				}

				if (ControlCenter.canWorking()) {

					if (Constant.GPRS_ERROR_TYPE == Constant.GPRS_ERROR_TYPE_NO) {

						// 上传数据时灯闪烁
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
					}

					// 每三秒打包一次数据
					if (Constant.System_Time - packageTime >= 3 * 1000) {

						Logger.log("ControlTimer",
								Constant.Transmit_Type + "==========" + DeviceConfigure.getNetworkSignalLevel());

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

					if (systemResetTime >= 60 && Constant.GPRS_ERROR_TYPE == Constant.GPRS_ERROR_TYPE_NO) {

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

		if (systemResetTime < 60) {

			systemResetPushTime = systemResetTime;
		}

		Push_Time = Constant.System_Time;
		listensePush = true;
	}

	/**
	 * 抬起
	 */
	public static void pushUp() {

		listensePush = false;
	}

	public static Thread getControlTimerThread() {
		return controlTimerThread;
	}

}
