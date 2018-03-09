package com.gree.air.condition.utils;

public class Logger {

	/**
	 * logger with string message
	 * 
	 * @param title
	 * @param message
	 */
	public static void log(String title, String message) {

		System.out.println("====== Title == : " + title + " ;  ====== Message == : " + message);
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

			stringBuffer.append(" " + Integer.toHexString(message[i] & 0xFF));
		}

		System.out.println("====== Title == : " + title + " ;  ====== Message == : " + stringBuffer.toString());
	}

}
