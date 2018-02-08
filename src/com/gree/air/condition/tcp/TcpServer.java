package com.gree.air.condition.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.gree.air.condition.center.ControlCenter;
import com.gree.air.condition.constant.Constant;

/**
 * TCP服务
 * 
 * @author lihaotian
 *
 */
public class TcpServer implements Runnable {

	// 157.122.146.133:7000
	// 192.13.182.157:7000
	// 118.190.93.145:8888

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

		// 启动服务 延迟3s进行登录
		new Thread(new Runnable() {

			public void run() {

				try {

					Thread.sleep(3 * 1000);

				} catch (InterruptedException e) {

					e.printStackTrace();
				}

				while (!TcpServer.isServerNormal()) {

					try {

						Thread.sleep(3 * 1000);

					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}

				ControlCenter.login();

			}

		}).start();

	}

	public void run() {

		while (serverWorking) {

			try {

				String host = "socket://" + Constant.Tcp_Address_Ip + ":" + Constant.Tcp_Address_Port;

				streamConnect = (StreamConnection) Connector.open(host);

				outputStream = streamConnect.openOutputStream();
				inputStream = streamConnect.openInputStream();

				System.out.println("=================== start tcp server =========================");
				serverNormal = true;

				receiveData();

			} catch (IOException ioe) {

				ioe.printStackTrace();

			} finally {

				if (serverWorking) {

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
				} else {

					System.out.println("=================== tcp server close =========================");
				}

				clearStream();
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
			}

			if (outputStream != null) {

				outputStream.close();
			}

			if (streamConnect != null) {

				streamConnect.close();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 清空流
	 */
	private static void clearStream() {

		inputStream = null;
		outputStream = null;
		streamConnect = null;
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
