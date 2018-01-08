package com.gree.air.condition.center;

import java.util.Calendar;
import java.util.Date;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.lzo.LzoCompressor1x_1;
import com.gree.air.condition.lzo.lzo_uintp;
import com.gree.air.condition.tcp.TcpServer;
import com.gree.air.condition.utils.Utils;

/**
 * 数据中心
 * 
 * @author lihaotian
 *
 */
public class DataCenter implements Runnable {

	private static Object lock = new Object();
	private static DataCenter dataCenter = new DataCenter();
	private static int Write_Data_Buffer_Poi = 0;

	// 数据上传间隔时间
	private static long Upload_Data_Interval_Time = 0;

	private static boolean Can_Upload_Data = false;

	private static LzoCompressor1x_1 lzo = new LzoCompressor1x_1();
	private static lzo_uintp lzo_uintp = new lzo_uintp();
	private static byte[] Lzo_Buffer = new byte[4096];

	private static Calendar calendar = Calendar.getInstance();
	private static Date date = new Date();

	/**
	 * 将机组数据写入4k缓存数据中
	 */
	public static void writeDataBuffer() {

		if (!Constant.GPRS_IS_CHOOSE) {

			return;
		}

		if (Constant.Uart_In_Buffer_Length == 0) {

			return;
		}

		synchronized (lock) {

			for (int i = 0; i < Constant.Uart_In_Buffer_Length; i++) {

				Constant.Data_Cache_Buffer[i + Write_Data_Buffer_Poi] = Constant.Uart_In_Buffer[i];
			}

			Write_Data_Buffer_Poi = Constant.Uart_In_Buffer_Length + Write_Data_Buffer_Poi;
		}

	}

	/**
	 * 将缓存数据写入SPI
	 */
	public static void writeSpi() {

		if (Write_Data_Buffer_Poi == 0) {

			return;
		}

		synchronized (lock) {

			lzo.compress(Constant.Data_Cache_Buffer, 0, Write_Data_Buffer_Poi, Lzo_Buffer, 0, lzo_uintp);

			for (int i = 0; i < lzo_uintp.value; i++) {

				Constant.Data_Buffer[i + 10] = Lzo_Buffer[i];
			}

			// 获取年月日时分秒
			date.setTime(Constant.System_Time);
			calendar.setTime(date);
			Constant.Data_Buffer[4] = (byte) (calendar.get(Calendar.YEAR) - 2000);
			Constant.Data_Buffer[5] = (byte) (calendar.get(Calendar.MONTH) + 1);

			int localDay = calendar.get(Calendar.DATE);
			int localHour = calendar.get(Calendar.HOUR_OF_DAY) + 8;

			if (localHour > 23) {

				localDay++;
				localHour -= 24;

			}

			Constant.Data_Buffer[6] = (byte) localDay;
			Constant.Data_Buffer[7] = (byte) localHour;
			Constant.Data_Buffer[8] = (byte) calendar.get(Calendar.MINUTE);
			Constant.Data_Buffer[9] = (byte) calendar.get(Calendar.SECOND);

			// 游标位
			byte[] mark = Utils.intToBytes(Constant.Data_Buffer_Mark);
			Constant.Data_Buffer[0] = mark[0];
			Constant.Data_Buffer[1] = mark[1];

			// 长度位
			byte[] length = Utils.intToBytes(lzo_uintp.value + 6);
			Constant.Data_Buffer[2] = length[0];
			Constant.Data_Buffer[3] = length[1];

			if (Constant.Data_Buffer_Mark == 2047) {

				Constant.Data_Buffer_Mark = 0;

			} else {

				Constant.Data_Buffer_Mark++;
			}

			Write_Data_Buffer_Poi = 0;
		}
	}

	/**
	 * 开始上传数据
	 */
	public static void startUploadData() {

		new Thread(dataCenter).start();
	}

	/**
	 * 通知上传数据
	 */
	public static void notifyUploadData() {

		Can_Upload_Data = true;

		synchronized (dataCenter) {

			dataCenter.notify();
		}
	}

	/**
	 * 数据传输
	 */
	public void run() {

		while (true) {

			try {

				// 如果停止上传，阻塞
				if (!Can_Upload_Data) {

					synchronized (dataCenter) {

						dataCenter.wait();

					}
				}

				if (Constant.Data_Buffer_Out_Mark == Constant.Data_Buffer_Out_End_Mark) {

					stopUploadData();
					continue;
				}

				// 每10分钟需要上传GPRS信号
				if (Constant.System_Time - Upload_Data_Interval_Time >= 10 * 60 * 1000) {

					Upload_Data_Interval_Time = Constant.System_Time;
					ControlCenter.sendGprsSignal();
				}

				// TODO 选举上报 需要在 90秒后启动
				// if (Constant.Transm_Type == Constant.TRANSM_TYPE_CHOOSE &&
				// Constant.System_Time <= 90 * 1000) {
				//
				// Thread.sleep(1000);
				// continue;
				// }

				// 判断服务器是否正常
				if (!TcpServer.isServerNormal()) {

					Thread.sleep(1000);
					continue;
				}

				int length = 0;
				// 缓存数据符合取值要求
				synchronized (lock) {

					System.out.println(Constant.Data_Buffer_Mark + "==============" + Constant.Data_Buffer_Out_Mark);

					if (Constant.Data_Buffer_Mark != Constant.Data_Buffer_Out_Mark) {

						length = Utils.bytesToInt(Constant.Data_Buffer, 2, 3);

						if (length > 0) {

							for (int i = 4; i < 4 + length; i++) {

								Constant.Tcp_Out_Buffer[i - 4 + 19] = Constant.Data_Buffer[i];
							}
						}
					}
				}

				if (length > 0) {

					ControlCenter.transmitData(length);

					if (Constant.Data_Buffer_Out_Mark == 2047) {

						Constant.Data_Buffer_Out_Mark = 0;

					} else {

						Constant.Data_Buffer_Out_Mark++;
					}

					Thread.sleep(500);

				} else {

					Thread.sleep(2000);
				}

				// if (true) {
				//
				// // spi数据符合取值要求
				//
				// TcpServer.sendData(1);
				// // continue;
				// }

			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

	}

	/**
	 * 实时传输
	 */
	public static void alwaysTransmit() {

		if (Constant.Transm_Level < Constant.TRANSM_TYPE_ALWAYS_LEVEL) {

			stopUploadData();

			Constant.Transm_Type = Constant.TRANSM_TYPE_ALWAYS;
			Constant.Transm_Level = Constant.TRANSM_TYPE_ALWAYS_LEVEL;

			// 重置静默时间
			Constant.Stop_Time = 0;

			ControlCenter.requestStartUpload();

		}
	}

	/**
	 * 上电传输
	 */
	public static void bootTransmit() {

		if (Constant.Transm_Level < Constant.TRANSM_TYPE_BOOT_LEVEL && Constant.System_Time > Constant.Stop_Time) {

			stopUploadData();

			Constant.Transm_Type = Constant.TRANSM_TYPE_BOOT;
			Constant.Transm_Level = Constant.TRANSM_TYPE_BOOT_LEVEL;

			ControlCenter.requestStartUpload();
		}

	}

	/**
	 * 选举上报
	 */
	public static void chooseTransmit() {

		if (Constant.Transm_Level < Constant.TRANSM_TYPE_CHOOSE_LEVEL && Constant.System_Time > Constant.Stop_Time) {

			stopUploadData();

			// 重置发送游标，选举上报持续发送5分钟数据
			Constant.Data_Buffer_Out_Mark = Constant.Data_Buffer_Mark;
			Constant.Data_Buffer_Out_End_Mark = Constant.Data_Buffer_Mark + (5 * 60 / 3);
			if (Constant.Data_Buffer_Out_End_Mark > 2047) {

				Constant.Data_Buffer_Out_End_Mark = Constant.Data_Buffer_Out_End_Mark - 2048;
			}

			Constant.Transm_Type = Constant.TRANSM_TYPE_CHOOSE;
			Constant.Transm_Level = Constant.TRANSM_TYPE_CHOOSE_LEVEL;

			ControlCenter.requestStartUpload();

		}

	}

	/**
	 * 设置上传标志位
	 * 
	 * @param error
	 *            故障标志位
	 * @param warning
	 *            亚健康标志位
	 * @param change
	 *            参数变化标志位
	 */
	public static void setUploadMarker(int error, int warning, int change) {

		// 故障标志位由1-0，记录。
		if (Constant.Transm_Error_Mark == 1 && error == 0) {

			Constant.Transm_Error_Mark = 0;
		}

		// 厂家参数变化标志位1-0，记录。
		if (Constant.Transm_Change_Mark == 1 && change == 0) {

			Constant.Transm_Change_Mark = 0;

			// 如果进行的是参数变化上报，则停止上报。
			/*
			 * if (Constant.Transm_Type == Constant.TRANSM_TYPE_CHANGE) {
			 * 
			 * DataCenter.stopUploadData(); return; }
			 */

		}

		// 亚健康标志位1-0，记录。
		if (Constant.Transm_Warning_Mark == 1 && warning == 0) {

			Constant.Transm_Warning_Mark = 0;

			// 如果正在亚健康上报，停止上报。
			/*
			 * if (Constant.Transm_Type == Constant.TRANSM_TYPE_WARNING) {
			 * 
			 * DataCenter.stopUploadData(); return; }
			 */

		}

		// 如果现在的 上传级别 小于 故障上传 则 判断故障上传
		if (Constant.Transm_Level < Constant.TRANSM_TYPE_ERROR_LEVEL) {

			if (Constant.Transm_Error_Mark == 0 && error == 1) {
				// 如果标志位 由 0 变为1；启动故障上报

				Constant.Transm_Error_Mark = 1;
				Constant.Stop_Time = 0;

				return;
			}

			if (Constant.System_Time <= Constant.Stop_Time) {

				return;
			}

			// 如果现在的 上传级别 小于 参数变化上传 则判断参数变化上传
			if (Constant.Transm_Level < Constant.TRANSM_TYPE_CHANGE_LEVEL) {

				if (Constant.Transm_Change_Mark == 0 && change == 1) {

					// 如果标志位由0-1，启动厂家参数变化上传

					Constant.Transm_Change_Mark = 1;

					return;

				}

				// 如果上报级别小于 异常上报，则判断异常上报标志位
				if (Constant.Transm_Level < Constant.TRANSM_TYPE_WARNING_LEVEL && Constant.Transm_Warning_Mark == 0
						&& warning == 1) {
					// 如果标志位0-1 启动亚健康（异常）上报

					Constant.Transm_Warning_Mark = 1;

					return;

				}
			}
		}

	}

	/**
	 * 停止上传数据
	 */
	public static void stopUploadData() {

		Can_Upload_Data = false;
		Constant.Data_Buffer_Out_End_Mark = -1;

		Constant.Transm_Type = Constant.TRANSM_TYPE_STOP;
		Constant.Transm_Level = Constant.TRANSM_TYPE_STOP_LEVEL;
		
		ControlCenter.stopTcpServer();
	}

}
