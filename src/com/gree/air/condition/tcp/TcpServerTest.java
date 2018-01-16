package com.gree.air.condition.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.gree.air.condition.center.ControlCenter;
import com.gree.air.condition.constant.Constant;

public class TcpServerTest implements Runnable {

	private static StreamConnection streamConnect;
	private static InputStream inputStream;
	private static OutputStream outputStream;

	private static boolean serverWorking = false;
	private static boolean serverNormal = false;

	private static int Server_ReConnect_Num = 0;

	/**
	 * 启动服务器
	 */
	public static void startServer() {

		serverWorking = true;
		Server_ReConnect_Num = 0;

		TcpServer tcpServer = new TcpServer();

		new Thread(tcpServer).start();

	}

	public void run() {

		while (serverWorking) {

			try {

				String host = "socket://192.168.1.19:20006";

				streamConnect = (StreamConnection) Connector.open(host);

				outputStream = streamConnect.openOutputStream();
				inputStream = streamConnect.openInputStream();

				System.out.println("=================== start tcp server =========================");
				serverNormal = true;

				receiveData();

			} catch (IOException ioe) {

				ioe.printStackTrace();

			} finally {

				System.out.println("=================== tcp server error =========================");

				try {

					closeStream();

					Thread.sleep(3000);

					if (Server_ReConnect_Num == 5) {

						stopServer();
					} else {

						Server_ReConnect_Num++;
					}

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 数据获取
	 */
	private static void receiveData() {

		try {

			if (inputStream != null) {

				int total = 0;
				while ((total = inputStream.read(Constant.Tcp_In_Buffer)) != -1) {

					StringBuffer stringBuffer = new StringBuffer();
					for (int i = 0; i < total; i++) {

						stringBuffer.append(" " + Integer.toHexString(Constant.Tcp_In_Buffer[i] & 0xFF));
					}

					System.out.println("new message tcp ---" + stringBuffer.toString());

					TcpModel.analyze();
				}
			}

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	/**
	 * 发送数据
	 */
	public static synchronized void sendData(int length) {

		try {

			if (outputStream != null) {

				outputStream.write(Constant.Tcp_Out_Buffer, 0, length);
			}

		} catch (IOException e) {

			e.printStackTrace();

			closeStream();
		}
	}

	/**
	 * 发送数据
	 */
	public static synchronized void sendDataForTransm(int length) {

		try {

			if (outputStream != null) {

				outputStream.write(Constant.Tcp_Out_Data_Buffer, 0, length);
			}

		} catch (IOException e) {

			e.printStackTrace();

			closeStream();
		}
	}

	/**
	 * 关闭流
	 */
	private static void closeStream() {

		serverNormal = false;

		try {

			if (inputStream != null) {

				inputStream.close();
				inputStream = null;
			}

			if (outputStream != null) {

				outputStream.close();
				outputStream = null;
			}

			if (streamConnect != null) {

				streamConnect.close();
				streamConnect = null;
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 停止服务
	 */
	public static void stopServer() {

		serverWorking = false;

		closeStream();

	}

	public static boolean isServerNormal() {
		return serverNormal;
	}

	public static boolean isServerWorking() {
		return serverWorking;
	}

}
