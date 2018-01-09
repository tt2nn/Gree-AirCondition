package com.gree.air.condition.file;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.microedition.io.Connector;

public class FileConnection {

	private static OutputStream outputStream;
	private static InputStream inputStream;

	private static javax.microedition.io.file.FileConnection fileConn;

	/**
	 * 创建通信
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	private static void openFile(String fileName) throws IOException {

		fileConn = (javax.microedition.io.file.FileConnection) Connector.open("file:///Phone/" + fileName);

		if (!fileConn.exists()) {

			fileConn.create();
		}

	}

	/**
	 * 读文件
	 * 
	 * @param fileName
	 */
	public static void readFile(String fileName, FileInterface fileInterface) {

		try {

			openFile(fileName);

			inputStream = fileConn.openInputStream();

			fileInterface.readFile(inputStream);

			closeFile();

		} catch (IOException e) {

			e.printStackTrace();
		}

	}

	public static void readFile(String fileName) {
		
	}

	/**
	 * 写文件
	 * 
	 * @param fileName
	 */
	public static void writeFile(String fileName, int start, int length) {

		try {

			openFile(fileName);

			outputStream = fileConn.openOutputStream();
			
			closeFile();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 关闭File
	 * 
	 * @throws IOException
	 */
	private static void closeFile() throws IOException {

		if (outputStream != null) {

			outputStream.close();
			outputStream = null;
		}

		if (inputStream != null) {

			inputStream.close();
			inputStream = null;
		}

		if (fileConn != null) {

			fileConn.close();
			fileConn = null;
		}

	}

	public interface FileInterface {

		void readFile(InputStream inputStream);
	}

}
