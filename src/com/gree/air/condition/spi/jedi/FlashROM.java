package com.gree.air.condition.spi.jedi;

import com.joshvm.util.ByteBuffer;

public interface FlashROM {
	public byte[] readManufacturInfo() throws Exception;
	
	public byte[] readDeviceInfo() throws Exception;
	
	public byte readStatusByte(int offset) throws Exception;
	
	public byte[] readStatusBytes() throws Exception;

	public void writeEnable() throws Exception;

	public void chipErase() throws Exception;

	public void pageProgram(int address, byte[] data) throws Exception;
	
	public ByteBuffer read(int address, int size) throws Exception;
	
	public void erase(int address, int size) throws Exception;	
	
	public void mount(FlashROMDeviceDescriptor desc) throws Exception;
	
	public int getPageSize();
}
