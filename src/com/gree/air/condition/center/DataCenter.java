package com.gree.air.condition.center;

import com.gree.air.condition.Run;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.file.FileReadModel;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.lzo.LzoCompressor1x_1;
import com.gree.air.condition.lzo.lzo_uintp;
import com.gree.air.condition.spi.SpiTool;
import com.gree.air.condition.tcp.TcpServer;
import com.gree.air.condition.utils.Utils;
import com.gree.air.condition.variable.Variable;

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
	private static final int TRANSMIT_LEVEL_POWER = 2;
	private static final int TRANSMIT_LEVEL_CHOOSE = 3;
	private static final int TRANSMIT_LEVEL_WARNING = 4;
	private static final int TRANSMIT_LEVEL_PUSHKEY = 5;
	private static final int TRANSMIT_LEVEL_CHANGE = 6;
	private static final int TRANSMIT_LEVEL_OPEN_CLOSE = 7;
	private static final int TRANSMIT_LEVEL_ERROR = 8;
	private static final int TRANSMIT_LEVEL_DEBUG = 9;
	private static final int TRANSMIT_LEVEL_ALWAYS = 10;
	private static int Transmit_Level = TRANSMIT_LEVEL_STOP;
	private static int Transmit_Cache_Level = TRANSMIT_LEVEL_STOP;

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
	private static byte[] Lzo_Buffer = new byte[1792];

	private static byte[] Data_Out_Success_Array = new byte[256];

	private static Thread dataCenterThread;

	/**
	 * 初始化
	 */
	public static void init() {

		int spiAddress = FileReadModel.getSpiAddress();

		Data_Buffer_Mark = spiAddress / 2048;

		Data_Out_Success_Array[0] = (byte) 0x01;
	}

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

			if (Write_Data_Buffer_Poi + Constant.Uart_In_Buffer_Length >= Constant.Data_Cache_Buffer.length) {

				ControlCenter.packageData();
			}

			for (int i = 0; i < Constant.Uart_In_Buffer_Length; i++) {

				Constant.Data_Cache_Buffer[i + Write_Data_Buffer_Poi] = Constant.Uart_In_Buffer[i];
			}

			Write_Data_Buffer_Poi = Constant.Uart_In_Buffer_Length + Write_Data_Buffer_Poi;
		}
	}

	/**
	 * 将缓存数据打包
	 */
	public static void packageData() {

		if (Write_Data_Buffer_Poi == 0) {

			return;
		}

		synchronized (lock) {

			lzo.compress(Constant.Data_Cache_Buffer, 0, Write_Data_Buffer_Poi, Lzo_Buffer, 0, lzo_uintp);

			if (lzo_uintp.value > Constant.Data_Buffer.length - 12) {

				Write_Data_Buffer_Poi = 0;
				return;
			}

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

			Data_Buffer_Mark++;
			if (Data_Buffer_Mark == BUFFER_MARK_SIZE) {

				Data_Buffer_Mark = 0;
			}

			SpiTool.writeData();

			Write_Data_Buffer_Poi = 0;
		}
	}

	/**
	 * 开始上传数据
	 */
	public static void startUploadData() {

		dataCenterThread = new Thread(dataCenter);
		dataCenterThread.start();
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

		while (Run.Running_State) {

			try {

				// 如果停止上传，阻塞
				if (!Can_Upload_Data) {

					synchronized (dataCenter) {

						dataCenter.wait();
					}
				}

				// 达到上报标志位
				if (Data_Buffer_Out_Mark == Data_Buffer_Out_End_Mark) {

					if ((Constant.Transmit_Type == Constant.TRANSMIT_TYPE_CHANGE
							&& ControlCenter.getTransmit_Mark_Change())
							|| (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_OPEN
									&& ControlCenter.getTransmit_Mark_Open())
							|| (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_CLOSE
									&& ControlCenter.getTransmit_Mark_Close())) {

						ControlCenter.Arrive_Stop_Mark = true;
						Data_Buffer_Out_End_Mark = -1;

					} else {

						ControlCenter.stopUploadData();
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

				// 正在进行上电上报，如果缓存了实时上报，切换为实时上报
				if (!Variable.Transmit_Choose_Or_Power && (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_CHOOSE
						|| Constant.Transmit_Type == Constant.TRANSMIT_TYPE_POWER)) {

					if (Transmit_Cache_Level > Transmit_Level) {

						switch (Variable.Transmit_Cache_Type) {
						case Constant.TRANSMIT_TYPE_ALWAYS:

							alwaysTransmit();
							break;
						}
					}

					Variable.Transmit_Choose_Or_Power = true;

					continue;
				}

				int length = 0;
				long time = 0L;

				if (SpiTool.readData(Data_Buffer_Out_Mark * 2 * 1024)) {

					// 验证数据没有发过
					if (Constant.Data_SPI_Buffer[1792] != (byte) 0x01) {

						length = Utils.bytesToInt(Constant.Data_SPI_Buffer, 2, 3);

						if (length > 0 && length < 1792) { // 验证数据是否正确

							time = Utils.bytesToLong(Constant.Data_SPI_Buffer, 4);

							for (int i = 12; i < 12 + length; i++) {

								Constant.Tcp_Out_Data_Buffer[i - 12 + 25] = Constant.Data_SPI_Buffer[i];
							}

						} else {

							outMarkAdd();
							continue;
						}

					} else {

						outMarkAdd();
						continue;
					}
				}

				// 如果数据长度大于0，进行上传
				if (length > 0) {

					ControlCenter.transmitData(length, time);

					SpiTool.writeData((Data_Buffer_Out_Mark * 2 * 1024 + 1792), Data_Out_Success_Array);

					outMarkAdd();
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
	 * Out Mark Add
	 */
	private void outMarkAdd() {

		Data_Buffer_Out_Mark++;

		if (Data_Buffer_Out_Mark == BUFFER_MARK_SIZE) {

			Data_Buffer_Out_Mark = 0;
		}
	}

	/**
	 * 实时传输
	 */
	public static void alwaysTransmit() {

		if (!Variable.Transmit_Choose_Or_Power && Transmit_Cache_Level < TRANSMIT_LEVEL_ALWAYS) {

			Transmit_Cache_Level = TRANSMIT_LEVEL_ALWAYS;
			Variable.Transmit_Cache_Type = Constant.TRANSMIT_TYPE_ALWAYS;

			return;
		}

		if (Transmit_Level < TRANSMIT_LEVEL_ALWAYS) {

			// 重置静默时间
			Constant.Stop_Time = 0;
			stopUploadData();

			Constant.Transmit_Type = Constant.TRANSMIT_TYPE_ALWAYS;
			Transmit_Level = TRANSMIT_LEVEL_ALWAYS;

			FileWriteModel.setAlwaysTransm();

			Data_Buffer_Out_Mark = Data_Buffer_Mark;
			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 实时传输
	 */
	public static void debugTransmit() {

		if (Constant.System_Time > Constant.Stop_Time && Transmit_Level < TRANSMIT_LEVEL_DEBUG) {

			stopUploadData();

			Constant.Transmit_Type = Constant.TRANSMIT_TYPE_DEBUG;
			Transmit_Level = TRANSMIT_LEVEL_DEBUG;

			Data_Buffer_Out_Mark = Data_Buffer_Mark;
			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 故障上报
	 */
	public static void errorTransmit() {

		if (Constant.System_Time > Constant.Stop_Time) {

			// 如果正在故障上报，向后延时
			if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_ERROR) {

				checkOutEndMark(Constant.Transmit_Error_End_Time);
				return;
			}

			if (Transmit_Level < TRANSMIT_LEVEL_ERROR) {

				stopUploadData();

				// 重置发送游标，上报故障点前30分钟到后5分钟数据
				checkOutStartMark(Constant.Transmit_Error_Start_Time);
				checkOutEndMark(Constant.Transmit_Error_End_Time);

				Constant.Transmit_Type = Constant.TRANSMIT_TYPE_ERROR;
				Transmit_Level = TRANSMIT_LEVEL_ERROR;

				ControlCenter.requestStartUpload();
			}
		}
	}

	/**
	 * 开机上报
	 */
	public static void openTransmit() {

		if (Constant.System_Time > Constant.Stop_Time) {

			// 正在开机上报，向后延时
			if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_OPEN) {

				checkOutEndMark(Constant.Transmit_Open_End_Time);
				return;
			}

			if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_CLOSE || Transmit_Level < TRANSMIT_LEVEL_OPEN_CLOSE) {

				stopUploadData();

				// 重置发送游标
				checkOutStartMark(Constant.Transmit_Open_Start_Time);
				checkOutEndMark(Constant.Transmit_Open_End_Time);

				Constant.Transmit_Type = Constant.TRANSMIT_TYPE_OPEN;
				Transmit_Level = TRANSMIT_LEVEL_OPEN_CLOSE;

				ControlCenter.requestStartUpload();
			}
		}
	}

	/**
	 * 关机上报
	 */
	public static void closeTransmit() {

		if (Constant.System_Time > Constant.Stop_Time) {

			// 正在开机上报，向后延时
			if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_CLOSE) {

				checkOutEndMark(Constant.Transmit_Close_End_Time);
				return;
			}

			if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_OPEN || Transmit_Level < TRANSMIT_LEVEL_OPEN_CLOSE) {

				stopUploadData();

				// 重置发送游标
				checkOutStartMark(Constant.Transmit_Close_Start_Time);
				checkOutEndMark(Constant.Transmit_Close_End_Time);

				Constant.Transmit_Type = Constant.TRANSMIT_TYPE_CLOSE;
				Transmit_Level = TRANSMIT_LEVEL_OPEN_CLOSE;

				ControlCenter.requestStartUpload();
			}
		}
	}

	/**
	 * 厂家参数变化上报
	 */
	public static void changeTransmit() {

		if (Transmit_Level < TRANSMIT_LEVEL_CHANGE && Constant.System_Time > Constant.Stop_Time) {

			stopUploadData();

			// 重置发送游标，上报变化点前5分钟到后1分钟数据
			checkOutStartMark(5 * 60);
			checkOutEndMark(Constant.Transmit_Change_End_Time);

			Constant.Transmit_Type = Constant.TRANSMIT_TYPE_CHANGE;
			Transmit_Level = TRANSMIT_LEVEL_CHANGE;

			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 按键上报
	 */
	public static void pushKeyTransmit() {

		if (Transmit_Level < TRANSMIT_LEVEL_PUSHKEY && Constant.System_Time > Constant.Stop_Time) {

			stopUploadData();

			Data_Buffer_Out_Mark = Data_Buffer_Mark;
			checkOutEndMark(Constant.Transmit_Pushkey_End_Time);

			Constant.Transmit_Type = Constant.TRANSMIT_TYPE_PUSHKEY;
			Transmit_Level = TRANSMIT_LEVEL_PUSHKEY;

			ControlCenter.requestStartUpload();
		}
	}

	/**
	 * 亚健康上报
	 */
	public static void warningTransmit() {

		if (Transmit_Level < TRANSMIT_LEVEL_WARNING && Constant.System_Time > Constant.Stop_Time) {

			stopUploadData();

			Variable.Transmit_Cache_Type = Constant.TRANSMIT_TYPE_WARNING;

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
			checkOutEndMark(5 * 60);

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
		checkOutEndMark(5 * 60);

		Constant.Transmit_Type = Constant.TRANSMIT_TYPE_POWER;
		Transmit_Level = TRANSMIT_LEVEL_POWER;

		ControlCenter.requestStartUpload();
	}

	/**
	 * 注册打卡上报
	 */
	public static void registerCheckTransmit() {

		if (Constant.Transmit_Type == Constant.TRANSMIT_TYPE_STOP
				|| Constant.Transmit_Type == Constant.TRANSMIT_TYPE_ALWAYS) {

			stopUploadData();
		}

		Constant.Stop_Time = 0;
		FileWriteModel.setCheckTransm();
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
		if (ControlCenter.getTransmit_Mark_Warning() || Variable.Transmit_Cache_Type != Constant.TRANSMIT_TYPE_CHECK) {

			return;
		}

		Constant.Transmit_Type = Constant.TRANSMIT_TYPE_CHECK;
		Transmit_Level = TRANSMIT_LEVEL_CHECK;

		Data_Buffer_Out_Mark = Data_Buffer_Mark;
		checkOutEndMark(Constant.Transmit_Check_End_Time);

		ControlCenter.requestStartUpload();
	}

	/**
	 * 验证起始Mark
	 * 
	 * @param beforeTime
	 */
	private static void checkOutStartMark(int beforeTime) {

		long localTime = Constant.System_Time;

		int localMark = Data_Buffer_Mark;
		int startMark = localMark - (beforeTime / 3);

		if (startMark < 0) {

			startMark += BUFFER_MARK_SIZE;
		}

		// 验证SPI中数据的时间是否满足条件
		while (true) {

			SpiTool.readData(startMark * 2 * 1024);
			long spiTimeStamp = Utils.bytesToLong(Constant.Data_SPI_Buffer, 4);

			if (localTime - spiTimeStamp > beforeTime * 1000) {

				startMark++;

				if (startMark == localMark) {

					Data_Buffer_Out_Mark = startMark;
					break;
				}

				if (startMark > BUFFER_MARK_SIZE) {

					startMark = 0;
				}

			} else {

				Data_Buffer_Out_Mark = startMark;
				break;
			}
		}
	}

	/**
	 * 验证结束Mark
	 * 
	 * @param endTime
	 */
	private static void checkOutEndMark(int endTime) {

		Data_Buffer_Out_End_Mark = Data_Buffer_Mark + (endTime / 3);

		if (Data_Buffer_Out_End_Mark > BUFFER_MARK_SIZE) {

			Data_Buffer_Out_End_Mark = Data_Buffer_Out_End_Mark - BUFFER_MARK_SIZE;
		}
	}

	/**
	 * 暂停上报
	 */
	public static void pauseUploadData() {

		Can_Upload_Data = false;
	}

	/**
	 * 停止数据上报
	 */
	public static void stopUploadData() {

		Can_Upload_Data = false;
		Data_Buffer_Out_End_Mark = -1;

		Constant.Transmit_Type = Constant.TRANSMIT_TYPE_STOP;
		Transmit_Level = TRANSMIT_LEVEL_STOP;
	}

	public static Thread getDataCenterThread() {
		return dataCenterThread;
	}

}
