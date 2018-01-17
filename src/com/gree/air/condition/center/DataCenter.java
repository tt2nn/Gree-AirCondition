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

	// 上报优先级 开机实时-实时-工程调试-故障-厂家参数-按键触发-亚健康-选举-上电-开机周期-打卡
	private static final int TRANSMIT_LEVEL_STOP = 0;
	private static final int TRANSMIT_LEVEL_CHECK = 1;
	private static final int TRANSMIT_LEVEL_BOOT_CLOSE = 2;
	private static final int TRANSMIT_LEVEL_POWER = 3;
	private static final int TRANSMIT_LEVEL_CHOOSE = 4;
	private static final int TRANSMIT_LEVEL_WARNING = 5;
	private static final int TRANSMIT_LEVEL_PUSHKEY = 6;
	private static final int TRANSMIT_LEVEL_CHANGE = 7;
	private static final int TRANSMIT_LEVEL_ERROR = 8;
	private static final int TRANSMIT_LEVEL_DEBUG = 9;
	private static final int TRANSMIT_LEVEL_ALWAYS = 10;
	private static final int TRANSMIT_LEVEL_BOOT_OPEN = 11;
	private static int Transmit_Level = TRANSMIT_LEVEL_STOP;

	// 缓存数据传输模式
	public static boolean Transmit_Cache_Warning = false;
	public static boolean Transmit_Cache_Check = false;
	public static boolean Transmit_Cache_Boot = false;

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
	private static long Upload_Data_Interval_Time = 0L;

	// 是否可以上传数据
	private static boolean Can_Upload_Data = false;

	// lzo 压缩
	private static LzoCompressor1x_1 lzo = new LzoCompressor1x_1();
	private static lzo_uintp lzo_uintp = new lzo_uintp();
	private static byte[] Lzo_Buffer = new byte[2048];

	/**
	 * 将机组数据写入4k缓存数据中
	 */
	public static void saveDataBuffer() {

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
	public static void packageData() {

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

					if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_BOOT || Constant.Transmit_Type == Constant.TRANSMIT_TYPE_CHECK) {
						
						pauseUploadData();
						
					}else {
						
						stopUploadData();
					}
					
					continue;
				}

				// 判断服务器是否正常
				if (!TcpServer.isServerNormal()) {

					Thread.sleep(2000);
					continue;
				}

				// 每10分钟需要上传GPRS信号
				if (Constant.System_Time - Upload_Data_Interval_Time >= Constant.Tcp_Sig_Period * 1000) {

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

		if (Transmit_Level < TRANSMIT_LEVEL_ALWAYS || Constant.Transmit_Type == Constant.TRANSMIT_TYPE_BOOT) {

			// 重置静默时间
			Constant.Stop_Time = 0;

			stopUploadData();

			Constant.Transmit_Type = Constant.TRANSMIT_TYPE_ALWAYS;
			Transmit_Level = TRANSMIT_LEVEL_ALWAYS;

			FileModel.setAlwaysTransm();

			Data_Buffer_Out_End_Mark = Data_Buffer_Mark;
			ControlCenter.requestStartUpload();

		}
	}

	/**
	 * 故障上报
	 */
	public static void errorTransmit() {

		if (Transmit_Level < TRANSMIT_LEVEL_ERROR && Constant.System_Time > Constant.Stop_Time) {

			stopUploadData();

			// 重置发送游标，上报故障点前30分钟到后5分钟数据
			Data_Buffer_Out_Mark = Data_Buffer_Mark - (Constant.Transmit_Error_Start_Time / 3);
			if (Data_Buffer_Out_Mark < 0) {

				Data_Buffer_Out_Mark = Data_Buffer_Out_Mark + BUFFER_MARK_SIZE;
			}
			Data_Buffer_Out_End_Mark = Data_Buffer_Mark + (Constant.Transmit_Error_End_Time / 3);

			if (Can_Upload_Data) {

				Data_Buffer_Out_End_Mark = Data_Buffer_Out_End_Mark - BUFFER_MARK_SIZE;
			}

			Constant.Transmit_Type = Constant.TRANSMIT_TYPE_ERROR;
			Transmit_Level = TRANSMIT_LEVEL_ERROR;

			ControlCenter.requestStartUpload();
		}

	}

	/**
	 * 厂家参数变化上报
	 */
	public static void changeTransmit() {

		if (Transmit_Level < TRANSMIT_LEVEL_CHANGE && Constant.System_Time > Constant.Stop_Time) {

			stopUploadData();

			// 重置发送游标，上报变化点前5分钟到后1分钟数据
			Data_Buffer_Out_Mark = Data_Buffer_Mark - (5 * 60 / 3);
			if (Data_Buffer_Out_Mark < 0) {

				Data_Buffer_Out_Mark = Data_Buffer_Out_Mark + BUFFER_MARK_SIZE;
			}

			Data_Buffer_Out_End_Mark = Data_Buffer_Mark + (Constant.Transmit_Change_End_Time / 3);
			if (Can_Upload_Data) {

				Data_Buffer_Out_End_Mark = Data_Buffer_Out_End_Mark - BUFFER_MARK_SIZE;
			}

			Constant.Transmit_Type = Constant.TRANSMIT_TYPE_CHANGE;
			Transmit_Level = TRANSMIT_LEVEL_CHANGE;

			ControlCenter.requestStartUpload();
		}

	}

	/**
	 * 亚健康上报
	 */
	public static void warningTransmit() {

		if (Transmit_Level < TRANSMIT_LEVEL_CHANGE && Constant.System_Time > Constant.Stop_Time) {

			stopUploadData();

			Transmit_Cache_Warning = true;

			// 重置发送游标
			Data_Buffer_Out_Mark = Data_Buffer_Mark;

			Constant.Transmit_Type = Constant.TRANSMIT_TYPE_WARNING;
			Transmit_Level = TRANSMIT_LEVEL_WARNING;

			ControlCenter.requestStartUpload();
		}

	}

	/**
	 * 选举上报
	 */
	public static void chooseTransmit() {

		if (Transmit_Level < TRANSMIT_LEVEL_CHOOSE && Constant.System_Time > Constant.Stop_Time) {

			stopUploadData();

			// 重置发送游标，选举上报持续发送5分钟数据
			Data_Buffer_Out_Mark = Data_Buffer_Mark;
			Data_Buffer_Out_End_Mark = Data_Buffer_Mark + (5 * 60 / 3);
			if (Data_Buffer_Out_End_Mark > BUFFER_MARK_SIZE) {

				Data_Buffer_Out_End_Mark = Data_Buffer_Out_End_Mark - BUFFER_MARK_SIZE;
			}

			Constant.Transmit_Type = Constant.TRANSMIT_TYPE_CHOOSE;
			Transmit_Level = TRANSMIT_LEVEL_CHOOSE;

			ControlCenter.requestStartUpload();

		}
	}

	/**
	 * 上电上报
	 */
	public static void powerTransmit() {

		Constant.Stop_Time = 0L;

		stopUploadData();

		// 重置发送游标，选举上报持续发送5分钟数据
		Data_Buffer_Out_Mark = Data_Buffer_Mark;
		Data_Buffer_Out_End_Mark = Data_Buffer_Mark + (5 * 60 / 3);
		if (Data_Buffer_Out_End_Mark > BUFFER_MARK_SIZE) {

			Data_Buffer_Out_End_Mark = Data_Buffer_Out_End_Mark - BUFFER_MARK_SIZE;
		}

		Constant.Transmit_Type = Constant.TRANSMIT_TYPE_POWER;
		Transmit_Level = TRANSMIT_LEVEL_POWER;

		ControlCenter.requestStartUpload();

	}

	/**
	 * 注册开机上报
	 */
	public static void registerBootTransmit() {

		if (Constant.System_Time < Constant.Stop_Time) {

			return;
		}

		if (ControlCenter.getTransmit_Mark_Boot() == 0 && (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_STOP
				|| Constant.Transmit_Type == Constant.TRANSMIT_TYPE_ALWAYS
				|| Constant.Transmit_Type == Constant.TRANSMIT_TYPE_BOOT)) {

			stopUploadData();

			Transmit_Cache_Boot = true;
			Transmit_Cache_Check = false;

			Constant.Transmit_Type = Constant.TRANSMIT_TYPE_BOOT;
			Transmit_Level = TRANSMIT_LEVEL_BOOT_CLOSE;

		} else {

			stopUploadData();

			Transmit_Cache_Boot = true;
			Transmit_Cache_Check = false;

			Constant.Transmit_Type = Constant.TRANSMIT_TYPE_BOOT;
			Transmit_Level = TRANSMIT_LEVEL_BOOT_OPEN;

			Data_Buffer_Out_End_Mark = Data_Buffer_Mark;
			ControlCenter.requestStartUpload();
		}

	}

	/**
	 * 开机上报
	 */
	public static void bootTransmit() {

		// 判断静默时间
		if (Constant.System_Time < Constant.Stop_Time) {

			return;
		}

		// 判断上报优先级
		if (Transmit_Level > TRANSMIT_LEVEL_BOOT_CLOSE) {

			return;
		}

		// 判断缓存上报状态
		if (!Transmit_Cache_Boot || Transmit_Cache_Warning) {

			return;
		}

		// 如果没有进行上报 需要缓存上报类型
		if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

			Constant.Transmit_Type = Constant.TRANSMIT_TYPE_BOOT;
			Transmit_Level = TRANSMIT_LEVEL_BOOT_CLOSE;
		}

		// 重置发送游标
		Data_Buffer_Out_Mark = Data_Buffer_Mark;
		Data_Buffer_Out_End_Mark = Data_Buffer_Mark + (Constant.Transmit_Check_End_Time / 3);
		if (Data_Buffer_Out_End_Mark > BUFFER_MARK_SIZE) {

			Data_Buffer_Out_End_Mark = Data_Buffer_Out_End_Mark - BUFFER_MARK_SIZE;
		}

		ControlCenter.requestStartUpload();
	}

	/**
	 * 注册打卡上报
	 */
	public static void registerCheckTransmit() {

		if (Constant.System_Time < Constant.Stop_Time) {

			return;
		}

		if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_STOP
				|| Constant.Transmit_Type == Constant.TRANSMIT_TYPE_ALWAYS
				|| Constant.Transmit_Type == Constant.TRANSMIT_TYPE_BOOT) {

			stopUploadData();

			Transmit_Cache_Boot = false;
			Transmit_Cache_Check = true;

			Constant.Transmit_Type = Constant.TRANSMIT_TYPE_CHECK;
			Transmit_Level = TRANSMIT_LEVEL_CHECK;
		}

	}

	/**
	 * 进行打卡上报
	 */
	public static void checkTransmit() {

		// 判断静默时间
		if (Constant.System_Time < Constant.Stop_Time) {

			return;
		}

		// 判断上报优先级
		if (Transmit_Level > TRANSMIT_LEVEL_CHECK) {

			return;
		}

		// 判断缓存上报状态
		if (Transmit_Cache_Boot || Transmit_Cache_Warning || !Transmit_Cache_Check) {

			return;
		}

		// 如果没有进行上报 需要注册打卡上报
		if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_STOP) {

			Constant.Transmit_Type = Constant.TRANSMIT_TYPE_CHECK;
			Transmit_Level = TRANSMIT_LEVEL_CHECK;
		}

		// 重置发送游标
		Data_Buffer_Out_Mark = Data_Buffer_Mark;
		Data_Buffer_Out_End_Mark = Data_Buffer_Mark + (Constant.Transmit_Check_End_Time / 3);
		if (Data_Buffer_Out_End_Mark > BUFFER_MARK_SIZE) {

			Data_Buffer_Out_End_Mark = Data_Buffer_Out_End_Mark - BUFFER_MARK_SIZE;
		}

		ControlCenter.requestStartUpload();
	}

	/**
	 * 暂停数据上报
	 */
	public static void pauseUploadData() {

		Can_Upload_Data = false;
		Data_Buffer_Out_End_Mark = -1;

		ControlCenter.stopTcpServer();

	}

	/**
	 * 停止数据上报
	 */
	public static void stopUploadData() {

		Can_Upload_Data = false;
		Data_Buffer_Out_End_Mark = -1;

		Constant.Transmit_Type = Constant.TRANSMIT_TYPE_STOP;
		Transmit_Level = TRANSMIT_LEVEL_STOP;

		ControlCenter.stopTcpServer();

	}

	/**
	 * 销毁数据上报
	 */
	public static void destoryUploadData() {

		Can_Upload_Data = false;
		Data_Buffer_Out_End_Mark = -1;

		Constant.Transmit_Type = Constant.TRANSMIT_TYPE_STOP;
		Transmit_Level = TRANSMIT_LEVEL_STOP;

		Transmit_Cache_Boot = false;
		Transmit_Cache_Check = false;
		Transmit_Cache_Warning = false;

		FileModel.setStopTransm();

		ControlCenter.stopTcpServer();
	}

}
