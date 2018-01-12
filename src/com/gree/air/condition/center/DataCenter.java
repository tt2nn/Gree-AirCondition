package com.gree.air.condition.center;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.file.FileModel;
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

	// 传输级别
	private static final int TRANSM_TYPE_STOP_LEVEL = 0;
	private static final int TRANSM_TYPE_POWER_LEVEL = 1;
	private static final int TRANSM_TYPE_CHOOSE_LEVEL = 2;
	private static final int TRANSM_TYPE_WARNING_LEVEL = 3;
	private static final int TRANSM_TYPE_PUSHKEY_LEVEL = 4;
	private static final int TRANSM_TYPE_CHANGE_LEVEL = 5;
	private static final int TRANSM_TYPE_ERROR_LEVEL = 6;
	private static final int TRANSM_TYPE_DEBUG_LEVEL = 7;
	private static final int TRANSM_TYPE_ALWAYS_LEVEL = 8;
	private static int Transm_Level = TRANSM_TYPE_STOP_LEVEL;

	// 上报优先级 实时监控-工程调试-故障-厂家参数-按键触发-亚健康-选举-上电
	// 故障标志位
	private static int Transm_Error_Mark = 0;
	// 异常标志位
	private static int Transm_Warning_Mark = 0;
	// 参数变化标志位
	private static int Transm_Change_Mark = 0;

	// 数据使用游标 由 0-2047
	private static int Data_Buffer_Mark = 0;
	private static int Data_Buffer_Out_Mark = 0;
	private static int Data_Buffer_Out_End_Mark = -1;
	private static final int BUFFER_MARK_SIZE = 4096;

	// lock
	private static Object lock = new Object();
	private static DataCenter dataCenter = new DataCenter();
	// 缓存数据长度
	private static int Write_Data_Buffer_Poi = 0;

	// 数据上传间隔时间 用于出发F4
	private static long Upload_Data_Interval_Time = 0;

	// 是否可以上传数据
	private static boolean Can_Upload_Data = false;

	// lzo 压缩
	private static LzoCompressor1x_1 lzo = new LzoCompressor1x_1();
	private static lzo_uintp lzo_uintp = new lzo_uintp();
	private static byte[] Lzo_Buffer = new byte[2048];

	/**
	 * 将机组数据写入4k缓存数据中
	 */
	public static void writeDataBuffer() {

		if (!ControlCenter.canWorking()) {

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

				Constant.Data_Buffer[i + 12] = Lzo_Buffer[i];
			}

			// 游标位
			byte[] mark = Utils.intToBytes(Data_Buffer_Mark);
			Constant.Data_Buffer[0] = mark[0];
			Constant.Data_Buffer[1] = mark[1];

			// 长度位
			byte[] length = Utils.intToBytes(lzo_uintp.value);
			Constant.Data_Buffer[2] = length[0];
			Constant.Data_Buffer[3] = length[1];

			// 时间
			byte[] time = Utils.longToBytes(Constant.System_Time);
			for (int i = 0; i < time.length; i++) {

				Constant.Data_Buffer[i + 4] = time[i];
			}

			if (Data_Buffer_Mark == BUFFER_MARK_SIZE - 1) {

				Data_Buffer_Mark = 0;

			} else {

				Data_Buffer_Mark++;
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

				if (Data_Buffer_Out_Mark == Data_Buffer_Out_End_Mark) {

					stopUploadData();
					continue;
				}

				// 判断服务器是否正常
				if (!TcpServer.isServerNormal()) {

					Thread.sleep(1000);
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

				int length = 0;
				long time = 0L;
				// 缓存数据符合取值要求
				synchronized (lock) {

					System.out.println(Data_Buffer_Mark + "==============" + Data_Buffer_Out_Mark);

					if (Data_Buffer_Mark != Data_Buffer_Out_Mark) {

						length = Utils.bytesToInt(Constant.Data_Buffer, 2, 3);

						if (length > 0) {

							time = Utils.bytesToLong(Constant.Data_Buffer, 4);

							for (int i = 12; i < 12 + length; i++) {

								Constant.Tcp_Out_Data_Buffer[i - 12 + 25] = Constant.Data_Buffer[i];
							}
						}
					}
				}

				// 如果数据长度大于0，进行上传
				if (length > 0) {

					ControlCenter.transmitData(length, time);

					if (Data_Buffer_Out_Mark == BUFFER_MARK_SIZE - 1) {

						Data_Buffer_Out_Mark = 0;

					} else {

						Data_Buffer_Out_Mark++;
					}

					Thread.sleep(500);

				} else {

					Thread.sleep(2000);
				}

			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}

	}

	/**
	 * 实时传输
	 */
	public static void alwaysTransmit() {

		if (Transm_Level < TRANSM_TYPE_ALWAYS_LEVEL) {

			pauseUploadData();

			Constant.Transm_Type = Constant.TRANSM_TYPE_ALWAYS;
			Transm_Level = TRANSM_TYPE_ALWAYS_LEVEL;

			// 重置静默时间
			Constant.Stop_Time = 0;

			FileModel.setAlwaysTransm();

			ControlCenter.requestStartUpload();

		}
	}

	/**
	 * 故障上报
	 */
	public static void errorTransmit() {

		pauseUploadData();

		// 重置发送游标，上报故障点前30分钟到后5分钟数据
		Data_Buffer_Out_Mark = Data_Buffer_Mark - (30 * 60 / 3);
		if (Data_Buffer_Out_Mark < 0) {

			Data_Buffer_Out_Mark = Data_Buffer_Out_Mark + BUFFER_MARK_SIZE;
		}
		Data_Buffer_Out_End_Mark = Data_Buffer_Mark + (5 * 60 / 3);

		if (Can_Upload_Data) {

			Data_Buffer_Out_End_Mark = Data_Buffer_Out_End_Mark - BUFFER_MARK_SIZE;
		}

		Constant.Transm_Type = Constant.TRANSM_TYPE_ERROR;
		Transm_Level = TRANSM_TYPE_ERROR_LEVEL;

		ControlCenter.requestStartUpload();

	}

	/**
	 * 厂家参数变化上报
	 */
	public static void changeTransmit() {

		pauseUploadData();

		// 重置发送游标，上报变化点前5分钟到后1分钟数据
		Data_Buffer_Out_Mark = Data_Buffer_Mark - (5 * 60 / 3);
		if (Data_Buffer_Out_Mark < 0) {

			Data_Buffer_Out_Mark = Data_Buffer_Out_Mark + BUFFER_MARK_SIZE;
		}
		Data_Buffer_Out_End_Mark = Data_Buffer_Mark + (60 / 3);

		if (Can_Upload_Data) {

			Data_Buffer_Out_End_Mark = Data_Buffer_Out_End_Mark - BUFFER_MARK_SIZE;
		}

		Constant.Transm_Type = Constant.TRANSM_TYPE_CHANGE;
		Transm_Level = TRANSM_TYPE_CHANGE_LEVEL;

		ControlCenter.requestStartUpload();

	}

	/**
	 * 选举上报
	 */
	public static void chooseTransmit() {

		if (Transm_Level < TRANSM_TYPE_CHOOSE_LEVEL && Constant.System_Time > Constant.Stop_Time) {

			pauseUploadData();

			// 重置发送游标，选举上报持续发送5分钟数据
			Data_Buffer_Out_Mark = Data_Buffer_Mark;
			Data_Buffer_Out_End_Mark = Data_Buffer_Mark + (5 * 60 / 3);
			if (Data_Buffer_Out_End_Mark > BUFFER_MARK_SIZE) {

				Data_Buffer_Out_End_Mark = Data_Buffer_Out_End_Mark - BUFFER_MARK_SIZE;
			}

			Constant.Transm_Type = Constant.TRANSM_TYPE_CHOOSE;
			Transm_Level = TRANSM_TYPE_CHOOSE_LEVEL;

			ControlCenter.requestStartUpload();

		}
	}

	/**
	 * 上电上报
	 */
	public static void powerTransmit() {

		if (Transm_Level < TRANSM_TYPE_POWER_LEVEL && Constant.System_Time > Constant.Stop_Time) {

			pauseUploadData();

			Constant.Transm_Type = Constant.TRANSM_TYPE_POWER;
			Transm_Level = TRANSM_TYPE_POWER_LEVEL;

			ControlCenter.requestStartUpload();

			// 判断缓存的上传类型
			switch (Constant.Transfer_Power_Type) {

			case Constant.TRANSM_TYPE_ALWAYS:

				chooseTransmit();

				break;
			}
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
		if (Transm_Error_Mark == 1 && error == 0) {

			Transm_Error_Mark = 0;
		}

		// 厂家参数变化标志位1-0，记录。
		if (Transm_Change_Mark == 1 && change == 0) {

			Transm_Change_Mark = 0;

		}

		// 亚健康标志位1-0，记录。
		if (Transm_Warning_Mark == 1 && warning == 0) {

			Transm_Warning_Mark = 0;

			// 如果正在亚健康上报，停止上报。

			if (Constant.Transm_Type == Constant.TRANSM_TYPE_WARNING) {

				DataCenter.stopUploadData();
				return;
			}

		}

		// 如果现在的 上传级别 小于 故障上传 则 判断故障上传
		if (Transm_Level < TRANSM_TYPE_ERROR_LEVEL) {

			if (Transm_Error_Mark == 0 && error == 1) {
				// 如果标志位 由 0 变为1；启动故障上报

				Transm_Error_Mark = 1;
				Constant.Stop_Time = 0;

				errorTransmit();

				return;
			}

			if (Constant.System_Time <= Constant.Stop_Time) {

				return;
			}

			// 如果现在的 上传级别 小于 参数变化上传 则判断参数变化上传
			if (Transm_Level < TRANSM_TYPE_CHANGE_LEVEL) {

				if (Transm_Change_Mark == 0 && change == 1) {

					// 如果标志位由0-1，启动厂家参数变化上传

					Transm_Change_Mark = 1;

					return;

				}

				// 如果上报级别小于 异常上报，则判断异常上报标志位
				if (Transm_Level < TRANSM_TYPE_WARNING_LEVEL && Transm_Warning_Mark == 0 && warning == 1) {
					// 如果标志位0-1 启动亚健康（异常）上报

					Transm_Warning_Mark = 1;

					return;

				}
			}
		}

	}

	/**
	 * 暂停数据传输
	 */
	public static void pauseUploadData() {

		Can_Upload_Data = false;
		Data_Buffer_Out_End_Mark = -1;

		Constant.Transm_Type = Constant.TRANSM_TYPE_STOP;
		Transm_Level = TRANSM_TYPE_STOP_LEVEL;

		FileModel.setStopTransm();
	}

	/**
	 * 停止上传数据
	 */
	public static void stopUploadData() {

		pauseUploadData();

		Constant.Transm_Type_Cache = Constant.TRANSM_TYPE_STOP;

		ControlCenter.stopTcpServer();
	}

}
