package com.gree.air.condition.utils;

import com.gree.air.condition.Run;

/**
 * Log输出类
 * 
 * @author Administrator
 *
 */
public class Logger implements Runnable {

	private static final int BUFFER_SIZE = 4 * 1024;
	private static byte[] Log_Buffer = new byte[4 * 1024];

	private static int Write_Mark = 0;
	private static int Read_Mark = 0;

	private static Logger logger = new Logger();
	private static Thread logTimerThread;

	/**
	 * 启动Timer
	 */
	public static void startLogTimer() {

		logTimerThread = new Thread(logger);
		logTimerThread.start();
	}

	/**
	 * logger with string message
	 * 
	 * @param title
	 * @param message
	 */
	public static synchronized void log(String title, String message) {

		System.out.println("======T== : " + title + " ;  ======M== : " + message);
	}

	/**
	 * logger with byte[] message
	 * 
	 * @param title
	 * @param message
	 * @param start
	 * @param length
	 */
	public static synchronized void log(String title, byte[] message, int start, int length) {

		StringBuffer stringBuffer = new StringBuffer();
		for (int i = start; i < start + length; i++) {

			stringBuffer.append(" " + Integer.toHexString(message[i] & 0xFF));
		}

		System.out.println("======T== : " + title + " ;  ======M== : " + stringBuffer.toString());
	}

	public void run() {

		while (Run.Running_State) {

			try {

				Thread.sleep(500);

				if (Write_Mark == Read_Mark) {

					synchronized (logger) {

						logger.wait();
					}
				}

				if (Write_Mark == Read_Mark) {

					continue;
				}

				if (Read_Mark > BUFFER_SIZE - 4) {

					Read_Mark = 0;

				} else if (Log_Buffer[Read_Mark] == (byte) 0xFF && Log_Buffer[Read_Mark + 1] == (byte) 0xFF) {

					Read_Mark = 0;
				}

				int length = Utils.bytesToInt(Log_Buffer, Read_Mark + 1, Read_Mark + 2);

				if (Log_Buffer[Read_Mark] == (byte) 0x01) {

					log("UartMessage", Log_Buffer, Read_Mark + 3, length);

				} else {

					log("UartMessage", new String(Log_Buffer, Read_Mark + 3, length));
				}

				Read_Mark = Read_Mark + length + 3;

			} catch (InterruptedException e) {

				e.printStackTrace();
			}
		}
	}

	/**
	 * log输出 byte
	 * 
	 * @param res
	 */
	public static void log(byte[] res, int length) {

		logBase((byte) 0x01, res, length);
	}

	/**
	 * log输出 string
	 * 
	 * @param res
	 */
	public static void log(String res) {

		byte[] bs = res.getBytes();

		logBase((byte) 0x02, bs, bs.length);
	}

	private static void logBase(byte b, byte[] res, int length) {

		if (Write_Mark >= BUFFER_SIZE - 4) {

			Write_Mark = 0;

		} else if (Write_Mark + length + 3 > BUFFER_SIZE) {

			Log_Buffer[Write_Mark] = (byte) 0xFF;
			Log_Buffer[Write_Mark + 1] = (byte) 0xFF;
			Write_Mark = 0;
		}

		int poi = Write_Mark;

		Log_Buffer[poi] = b;
		poi++;

		Log_Buffer[poi] = Utils.intToBytes(length)[0];
		poi++;

		Log_Buffer[poi] = Utils.intToBytes(length)[1];
		poi++;

		for (int i = 0; i < length; i++) {

			Log_Buffer[poi] = res[i];
			poi++;
		}

		Write_Mark = poi;

		synchronized (logger) {

			logger.notify();
		}
	}

}
