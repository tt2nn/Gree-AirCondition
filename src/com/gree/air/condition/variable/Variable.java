package com.gree.air.condition.variable;

import com.gree.air.condition.constant.Constant;

public class Variable {

	public static boolean Change_Vpn = false;

	public static String Tcp_Address_Ip = "";
	public static String Tcp_Address_Port = "";

	public static boolean Tcp_Address_Private = false;

	public static boolean Transmit_Choose_Or_Power = false;

	public static byte Transmit_Cache_Type = Constant.TRANSMIT_TYPE_CHECK;

	/**
	 * set private ip
	 */
	public static void tcpAddressPrivate() {

		Tcp_Address_Private = true;

		Tcp_Address_Ip = ConfigureVariable.Tcp_Address_Ip_Private;
		Tcp_Address_Port = ConfigureVariable.Tcp_Address_Port_Private;
	}

	/**
	 * set public ip
	 */
	public static void tcpAddressPublic() {

		Tcp_Address_Private = false;

		Tcp_Address_Ip = ConfigureVariable.Tcp_Address_Ip_Public;
		Tcp_Address_Port = ConfigureVariable.Tcp_Address_Port_Public;
	}

}
