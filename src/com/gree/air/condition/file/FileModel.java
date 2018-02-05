package com.gree.air.condition.file;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.utils.Utils;

public class FileModel {

	/**
	 * write file with String
	 * 
	 * @param FileName
	 * @param value
	 */
	protected static synchronized void writeFile(String FileName, String value) {

		byte[] valueBytes = value.getBytes();

		writeFile(FileName, valueBytes);

	}

	/**
	 * write file with int
	 * 
	 * @param FileName
	 * @param value
	 */
	protected static void writeFile(String FileName, int value) {

		byte[] valueBytes = Utils.intToBytes(value);

		writeFile(FileName, valueBytes);

	}

	/**
	 * write file with byte
	 * 
	 * @param FileName
	 * @param value
	 */
	protected static void writeFile(String FileName, byte value) {

		byte[] valueBytes = { value };

		writeFile(FileName, valueBytes);

	}

	/**
	 * write file with byte[]
	 * 
	 * @param FileName
	 * @param value
	 */
	protected static void writeFile(String FileName, byte[] value) {

		byte[] dataLength = Utils.intToBytes(value.length);

		Constant.File_Buffer[0] = dataLength[0];
		Constant.File_Buffer[1] = dataLength[1];

		for (int i = 0; i < value.length; i++) {

			Constant.File_Buffer[i + 2] = value[i];
		}

		FileConnection.writeFile(FileName);

	}

}
