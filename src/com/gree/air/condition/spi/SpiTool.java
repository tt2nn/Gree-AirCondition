package com.gree.air.condition.spi;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.FileConstant;
import com.gree.air.condition.file.FileConnection;
import com.gree.air.condition.file.FileModel;
import com.gree.air.condition.spi.jedi.FlashROM;
import com.gree.air.condition.spi.jedi.FlashROMDeviceFactory;
import com.joshvm.util.ByteBuffer;

public class SpiTool {

	private static final int ERASE_SIZE = W25Q64_driver.SIZE_32K; // Size of erasing test

	private static int RW_Size;

	private static FlashROM flashROM;
	private static int Page_Size; // Page size which get from specific ROM device

	private static ByteBuffer readBuffer;

	private static byte[] Byte_Array = new byte[256];
	private static int Write_Address;
	private static int Write_Page = -1;
	private static final int SPI_PAGE_SIZE = 256;

	/**
	 * 初始化
	 * 
	 * @param rwSize
	 */
	public static void init(int rwSize) {

		try {

			// 初始化FlashRom 端口、读写的频率
			flashROM = FlashROMDeviceFactory.getDevice(W25Q64_driver.getDeviceDescriptor(0, 0, 20 * 1024 * 1024));
			Page_Size = flashROM.getPageSize();

			RW_Size = rwSize;

			getSpiWritePage();
			getSpiWriteAddress();

			/**
			 * Read Manufacture/Device information
			 */
			/*
			 * byte[] manufacture_info = flashROM.readManufacturInfo(); byte[] device_info =
			 * flashROM.readDeviceInfo(); System.out.println("Manufacture ID: " +
			 * manufacture_info[0]); System.out.println("Device ID: " + device_info[0]);
			 */

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 写入数据
	 */
	public static void writeData() {

		try {

			// 当spi写入新的一页时，需要先擦除下一页
			int res = Write_Address % (ERASE_SIZE * 1024);

			if (res == 0) {

				Write_Page++;

				if (Write_Page == SPI_PAGE_SIZE) {

					Write_Page = 0;
					Write_Address = 0;
				}

				FileModel.setSpiPage(Write_Page);

				erase(Write_Address);
			}

			for (int i = 0; i < 7; i++) {

				for (int j = 0; j < Byte_Array.length; j++) {

					Byte_Array[j] = Constant.Data_Buffer[i * Page_Size + j];
				}

				flashROM.pageProgram(Write_Address, Byte_Array);
				Write_Address += Page_Size;

			}

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	public static void readData(int readAddress) {

		try {

			readBuffer = flashROM.read(readAddress, RW_Size);
			Constant.Data_SPI_Buffer = readBuffer.array();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * 获取存储的页码
	 */
	private static void getSpiWritePage() {

		FileConnection.readFile(FileConstant.FILE_NAME_SPI_WRITE_PAGE);

		if (Constant.File_Buffer_Length > 0) {

			Write_Page = Constant.File_Buffer[0] & 0xff;
		}
	}

	/**
	 * 获取当前写到的位置
	 */
	private static void getSpiWriteAddress() {

		try {

			int ads = Write_Page * ERASE_SIZE * 1024;

			readBuffer = flashROM.read(ads, ERASE_SIZE * 1024);

			Write_Address += readBuffer.remaining();

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * 全部擦除
	 */
	public static void chipErase() {

		try {

			flashROM.chipErase();

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 擦除
	 * 
	 * @param address
	 */
	private static void erase(int address) {

		try {

			flashROM.erase(address, ERASE_SIZE);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 写入
	 * 
	 * @param address
	 * @param data
	 */
	private static void write(int address, byte[] data) {

		try {

			int originalAddr = address;
			int pageSize = data.length / Page_Size;

			for (int i = 0; i < pageSize; i++) {

				flashROM.pageProgram(originalAddr, data);

				originalAddr += Page_Size;
			}

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * 读取
	 * 
	 * @param address
	 */
	private static void read(int address) {

		try {

			readBuffer = flashROM.read(address, RW_Size);
			readBuffer.flip();

			/**
			 * Verify
			 */
			/*
			 * if (readBuffer.remaining() != rw_size) {
			 * System.out.println("Read bytes number wrong."); return; }
			 * 
			 * int wrongByteNum = 0; for (int j = 0; j < rw_size; j++) { int k = ((int)
			 * readBuffer.get() & 0xff); if (k != (j & 0xff)) { //
			 * System.out.println("Wrong data read at "+j+" : "+k); wrongByteNum++; } }
			 * 
			 * if (wrongByteNum == 0) { System.out.println("Verify OK."); } else {
			 * System.out.println(wrongByteNum + " bytes wrong."); }
			 * 
			 * // System.out.println("Erase chip time (milliseconds):"+(time0-time));
			 * System.out.println("Erase " + erase_size + "bytes time (milliseconds):" +
			 * (time2 - time1)); System.out.println("Write " + rw_size +
			 * "bytes time (milliseconds):" + (time4 - time3)); System.out.println("Read " +
			 * rw_size + "bytes time (milliseconds):" + (time6 - time5));
			 */

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

}
