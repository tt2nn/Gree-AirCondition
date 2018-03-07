package com.gree.air.condition.tcp;

import java.io.IOException;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;

import com.gree.air.condition.variable.ConfigureVariable;
import com.gree.air.condition.variable.Variable;

public class TcpPin implements Runnable {

	private boolean privateIp = false;
	private String host;

	private StreamConnection streamConnect;

	public void startPin(boolean privateIp) {

		this.privateIp = privateIp;

		if (privateIp) {

			host = "socket://" + ConfigureVariable.Tcp_Address_Ip_Private + ":"
					+ ConfigureVariable.Tcp_Address_Port_Private;

		} else {

			host = "socket://" + ConfigureVariable.Tcp_Address_Ip_Public + ":"
					+ ConfigureVariable.Tcp_Address_Port_Public;
		}

		new Thread(this).start();

	}

	public void run() {

		try {

			streamConnect = (StreamConnection) Connector.open(host);

			if (privateIp) {

				Variable.tcpAddressPrivate();

			} else {

				Variable.tcpAddressPublic();
			}

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			if (streamConnect != null) {

				try {

					streamConnect.close();
					streamConnect = null;

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
