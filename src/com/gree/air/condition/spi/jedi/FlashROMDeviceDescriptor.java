package com.gree.air.condition.spi.jedi;

public interface FlashROMDeviceDescriptor {
	public FlashROMDeviceDescriptor getDescriptor();

	public Class getFlashROMDeviceClass() throws ClassNotFoundException;
}
