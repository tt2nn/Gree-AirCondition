package com.gree.air.condition.file;

import com.gree.air.condition.constant.FileConstant;
import com.gree.air.condition.utils.LogUtils;

public class FileReadModel extends FileModel {

	/**
	 * 获取SPI Address
	 * 
	 * @return
	 */
	public static int getSpiAddress() {

		int address = readFileInt(FileConstant.FILE_NAME_SPI_WRITE_ADDRESS);

		LogUtils.showLog("FileReadModel", "SpiAddres : " + address);
		return address;

	}

}
