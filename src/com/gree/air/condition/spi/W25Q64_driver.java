package com.gree.air.condition.spi;

import org.joshvm.j2me.dio.Device;
import org.joshvm.j2me.dio.spibus.SPIDeviceConfig;

import com.gree.air.condition.spi.jedi.SPIFlash;
import com.gree.air.condition.spi.jedi.SPIFlashDeviceDescriptor;
import com.joshvm.util.ByteBuffer;

public class W25Q64_driver extends SPIFlash {
	private static final int INSTRUCTION_ERASE_SECTOR = 0x20;
	private static final int INSTRUCTION_ERASE_BLOCK_32K = 0x52;
	private static final int INSTRUCTION_ERASE_BLOCK_64K = 0xD8;
	private static final int INSTRUCTION_ERASE_CHIP = 0x60;
	private static final int INSTRUCTION_WRITE_ENABLE = 0x06;
	private static final int INSTRUCTION_READ_DATA = 0x03;
	private static final int INSTRUCTION_READ_STATUS_1 = 0x05;
	private static final int INSTRUCTION_READ_STATUS_2 = 0x35;
	private static final int INSTRUCTION_READ_STATUS_3 = 0x15;
	private static final int INSTRUCTION_READ_DEVICE_INFO = 0x90;

	public static final int PAGE_SIZE = 256;
	public static final int SIZE_32K = 32 * 1024;
	public static final int SIZE_64K = 64 * 1024;

	private static final int DEFAULT_CLOCK_FREQUENCY = 20 * 1024 * 1024;

	public int getPageSize() {
		return PAGE_SIZE;
	}

	public byte[] readManufacturInfo() throws Exception {

		byte[] command = new byte[4];
		command[0] = (byte) INSTRUCTION_READ_DEVICE_INFO;
		command[1] = (byte) 0;
		command[2] = (byte) 0;
		command[3] = (byte) 0;
		ByteBuffer command_buf = new ByteBuffer(command);

		ByteBuffer result_buf = ByteBuffer.allocate(2);
		spi.writeAndRead(command_buf, 4, result_buf);
		result_buf.rewind();
		byte[] result = new byte[1];
		result[0] = result_buf.get();
		return result;
	}

	public byte[] readDeviceInfo() throws Exception {
		byte[] command = new byte[4];
		command[0] = (byte) INSTRUCTION_READ_DEVICE_INFO;
		command[1] = (byte) 0;
		command[2] = (byte) 0;
		command[3] = (byte) 1;
		ByteBuffer command_buf = new ByteBuffer(command);

		ByteBuffer result_buf = ByteBuffer.allocate(2);
		spi.writeAndRead(command_buf, 4, result_buf);
		result_buf.rewind();
		byte[] result = new byte[1];
		result[0] = result_buf.get();
		return result;
	}

	public byte readStatusByte(int offset) throws Exception {
		byte[] command = new byte[1];
		switch (offset) {
		case 0:
			command[0] = (byte) INSTRUCTION_READ_STATUS_1;
			break;
		case 1:
			command[0] = (byte) INSTRUCTION_READ_STATUS_2;
			break;
		case 2:
			command[0] = (byte) INSTRUCTION_READ_STATUS_3;
			break;
		default:
			throw new Exception();
		}
		ByteBuffer command_buf = new ByteBuffer(command);
		ByteBuffer result_buf = ByteBuffer.allocate(1);
		spi.writeAndRead(command_buf, 1, result_buf);
		result_buf.rewind();
		return result_buf.get();
	}

	public byte readStatusByte() throws Exception {
		return readStatusByte(0);
	}

	public byte[] readStatusBytes() throws Exception {
		byte[] result = new byte[3];
		result[0] = readStatusByte(0);
		result[1] = readStatusByte(1);
		result[2] = readStatusByte(2);
		return result;
	}

	public void writeEnable() throws Exception {
		byte[] command = new byte[1];
		command[0] = (byte) INSTRUCTION_WRITE_ENABLE;
		ByteBuffer command_buf = new ByteBuffer(command);
		spi.write(command_buf);
	}

	public void chipErase() throws Exception {
		writeEnable();
		byte[] command = new byte[1];
		command[0] = (byte) INSTRUCTION_ERASE_CHIP;
		ByteBuffer command_buf = new ByteBuffer(command);
		spi.write(command_buf);
		waitWIPClear();
	}

	public void pageProgram(int address, byte[] data) throws Exception {
		if (data.length != 256) {
			System.out.println("Can not program: Page size must be 256!");
			return;
		}

		writeEnable();

		byte[] command = new byte[4];
		address = address & 0x00ffffff;
		command[0] = (byte) 0x02;
		command[1] = (byte) ((address >> 16) & 0xff);
		command[2] = (byte) ((address >> 8) & 0xff);
		command[3] = (byte) (address & 0xff);
		ByteBuffer command_buf = ByteBuffer.allocate(4 + data.length);
		command_buf.put(command);
		command_buf.put(data);
		command_buf.flip();
		spi.write(command_buf);
		waitWIPClear();
	}

	public ByteBuffer read(int address, int size) throws Exception {
		byte[] command = new byte[4];
		address = address & 0x00ffffff;
		command[0] = (byte) INSTRUCTION_READ_DATA;
		command[1] = (byte) ((address >> 16) & 0xff);
		command[2] = (byte) ((address >> 8) & 0xff);
		command[3] = (byte) (address & 0xff);
		ByteBuffer command_buf = new ByteBuffer(command);

		ByteBuffer result_buf = ByteBuffer.allocate(size);
		spi.writeAndRead(command_buf, 4, result_buf);
		return result_buf;
	}

	public void erase(int address, int size) throws Exception {
		switch (size) {
		case PAGE_SIZE:
			eraseSector(address);
			break;
		case SIZE_32K:
			erase32KBlock(address);
			break;
		case SIZE_64K:
			erase64KBlock(address);
			break;
		default:
			// TODO: Create specific exception for this
			throw new Exception();
		}
	}

	private void eraseSector(int address) throws Exception {
		eraseCommonInternal(address, (byte) INSTRUCTION_ERASE_SECTOR);
	}

	private void erase32KBlock(int address) throws Exception {
		eraseCommonInternal(address, (byte) INSTRUCTION_ERASE_BLOCK_32K);
	}

	private void erase64KBlock(int address) throws Exception {
		eraseCommonInternal(address, (byte) INSTRUCTION_ERASE_BLOCK_64K);
	}

	private void eraseCommonInternal(int address, byte inst) throws Exception {
		writeEnable();

		byte[] command = new byte[4];
		address = address & 0x00ffffff;
		command[0] = inst;
		command[1] = (byte) ((address >> 16) & 0xff);
		command[2] = (byte) ((address >> 8) & 0xff);
		command[3] = (byte) (address & 0xff);
		ByteBuffer command_buf = new ByteBuffer(command);

		spi.write(command_buf);
		waitWIPClear(0);
	}

	public void waitWIPClear() throws Exception {
		waitWIPClear(0);
	}

	private void waitWIPClear(int interval) throws Exception {
		byte byteStatus;
		do {
			byteStatus = readStatusByte();
			Thread.sleep(interval);
		} while ((byteStatus & 0x01) == 0x01);
	}

	public static SPIFlashDeviceDescriptor getDeviceDescriptor(int SPIContollerNumber, int CSAddress) {
		return getDeviceDescriptor(SPIContollerNumber, CSAddress, DEFAULT_CLOCK_FREQUENCY);
	}

	public static SPIFlashDeviceDescriptor getDeviceDescriptor(int SPIContollerNumber, int CSAddress,
			int clockFrequency) {
		return new SPIFlashDeviceDescriptor("com.joshvm.JEDI.FlashROM.W25Q64.W25Q64_driver", SPIContollerNumber,
				CSAddress, clockFrequency, 0, /* Clock mode 0 */
				8, /* Word length 8 */
				Device.BIG_ENDIAN, SPIDeviceConfig.CS_ACTIVE_LOW);
	}
}
