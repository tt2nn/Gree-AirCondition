package com.gree.air.condition.variable;

public class Variable {

	public static boolean Change_Vpn = false;

	public static String Tcp_Address_Ip = "";
	public static String Tcp_Address_Port = "";

	public static boolean Tcp_Address_Private = false;

	/**
	 * set private ip
	 */
	public static void tcpAddressPrivate() {

		Tcp_Address_Private = true;

		Tcp_Address_Ip = ConfigureVariable.Tcp_Address_Ip_Private;
		Tcp_Address_Port = ConfigureVariable.Tcp_Address_Port_Public;
	}

	/**
	 * set public ip
	 */
	public static void tcpAddressPublic() {

		Tcp_Address_Private = false;

		Tcp_Address_Ip = ConfigureVariable.Tcp_Address_Ip_Private;
		Tcp_Address_Port = ConfigureVariable.Tcp_Address_Port_Public;
	}

}
