package com.gree.air.condition.tcp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.gree.air.condition.Run;
import com.gree.air.condition.center.ControlCenter;
import com.gree.air.condition.configure.DeviceConfigure;
import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.entity.Apn;
import com.gree.air.condition.utils.Logger;
import com.gree.air.condition.utils.Utils;
import com.gree.air.condition.variable.Variable;

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

	private static Thread tcpThread;

	/**
	 * 启动服务器
	 */
	public static void startServer() {

		if (Variable.Change_Vpn) {

			Variable.Change_Vpn = false;

			Apn apn = Utils.getApn();
			DeviceConfigure.setApn(apn);
		}

		serverWorking = true;
		Server_ReConnect_Num = 0;

		TcpServer tcpServer = new TcpServer();

		tcpThread = new Thread(tcpServer);
		tcpThread.start();
	}

	public void run() {

		while (Run.Running_State && serverWorking) {

			try {

				String host = "socket://" + Variable.Tcp_Address_Ip + ":" + Variable.Tcp_Address_Port;

				streamConnect = (StreamConnection) Connector.open(host);

				outputStream = streamConnect.openOutputStream();
				inputStream = streamConnect.openInputStream();

				Logger.log("Tcp Server", "Start Tcp Server");

				serverNormal = true;

				if (!ControlCenter.Gprs_Login) {

					ControlCenter.login();
				}

				receiveData();

			} catch (IOException ioe) {

				ioe.printStackTrace();

			} finally {

				if (serverWorking) {

					Logger.log("Tcp Server", "Tcp Server Error");

					try {

						if (Constant.GPRS_ERROR_TYPE == Constant.GPRS_ERROR_TYPE_NO) {

							Constant.GPRS_ERROR_TYPE = Constant.GPRS_ERROR_TYPE_SERVER;
						}
						ControlCenter.pauseUploadData();

						closeStream();

						Thread.sleep(10 * 1000);

						if (Server_ReConnect_Num == 5) {

							Server_ReConnect_Num = 0;
							ControlCenter.stopUploadData();

						} else {

							Server_ReConnect_Num++;
						}

					} catch (InterruptedException e) {

						e.printStackTrace();
					}

				} else {
					Logger.log("Tcp Server", "Tcp Server Stop");
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

					Logger.log("Tcp Get Message", Constant.Tcp_In_Buffer, 0, total);

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

	public static Thread getTcpThread() {
		return tcpThread;
	}

}
