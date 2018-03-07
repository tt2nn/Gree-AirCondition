package com.gree.air.condition.utils;

import com.gree.air.condition.constant.Constant;

public class Logger {

	/**
	 * logger with string message
	 * 
	 * @param title
	 * @param message
	 */
	public static void log(String title, String message) {

		System.out.println("Logger Title ==== : " + title + " ;  Logger Message ==== : " + message);
	}

	/**
	 * logger with byte[] message
	 * 
	 * @param title
	 * @param message
	 * @param start
	 * @param length
	 */
	public static void log(String title, byte[] message, int start, int length) {

		StringBuffer stringBuffer = new StringBuffer();
		for (int i = start; i < length; i++) {

			stringBuffer.append(" " + Integer.toHexString(Constant.Tcp_In_Buffer[i] & 0xFF));
		}

		System.out.println("Logger Title ==== : " + title + " ;  Logger Message ==== : " + stringBuffer.toString());
	}

}
