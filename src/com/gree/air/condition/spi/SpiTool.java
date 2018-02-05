package com.gree.air.condition.spi;

import com.gree.air.condition.constant.Constant;
import com.gree.air.condition.constant.FileConstant;
import com.gree.air.condition.file.FileConnection;
import com.gree.air.condition.file.FileWriteModel;
import com.gree.air.condition.spi.jedi.FlashROM;
import com.gree.air.condition.spi.jedi.FlashROMDeviceFactory;
import com.gree.air.condition.utils.Utils;

public class SpiTool {

	private static final int ERASE_SIZE = W25Q64_driver.SIZE_32K; // Size of erasing test

	private static int RW_Size;

	private static FlashROM flashROM;
	private static int Page_Size; // Page size which get from specific ROM device

	private static int Write_Address;
	private static final int ROM_SIZE = 8 * 1024 * 1024;

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
			int res = Write_Address % ERASE_SIZE;

			if (res == 0) {

				if (Write_Address == ROM_SIZE) {

					Write_Address = 0;
				}

				erase(Write_Address);
			}

			for (int i = 0; i < 7; i++) {

				flashROM.pageProgram(Write_Address, Constant.Data_Buffer, Page_Size * i, Page_Size);
				Write_Address += Page_Size;
			}

			Write_Address += Page_Size;
			FileWriteModel.setSpiAddress(Write_Address);

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	/**
	 * 读数据
	 * 
	 * @param readAddress
	 * @return 是否读取到数据
	 */
	public static boolean readData(int readAddress) {

		if (readAddress == Write_Address) {

			return false;
		}

		try {

			Constant.Data_SPI_Buffer = flashROM.read(readAddress, RW_Size);

		} catch (Exception e) {

			e.printStackTrace();
		}

		return true;
	}

	/**
	 * 获取写到的address
	 */
	private static void getSpiWriteAddress() {

		FileConnection.readFile(FileConstant.FILE_NAME_SPI_WRITE_ADDRESS);

		if (Constant.File_Buffer_Length > 0) {

			Write_Address = Utils.stringToInt(new String(Constant.File_Buffer, 0, Constant.File_Buffer_Length));
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

}
