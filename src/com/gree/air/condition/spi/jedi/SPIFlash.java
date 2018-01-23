package com.gree.air.condition.spi.jedi;

import org.joshvm.j2me.dio.DeviceManager;
import org.joshvm.j2me.dio.spibus.SPIDevice;
import org.joshvm.j2me.dio.spibus.SPIDeviceConfig;

public abstract class SPIFlash implements FlashROM {

	protected SPIDevice spi = null;

	protected SPIDevice getSPIDevice(int SPIControllerNumber, int CSAddress, int clockFrequency, int clockMode,
			int wordLength, int bitOrdering, int csActiveLevel) {
		SPIDevice spi = null;
		try {
			SPIDeviceConfig config = new SPIDeviceConfig.Builder().setControllerNumber(SPIControllerNumber)
					.setAddress(CSAddress).setClockFrequency(clockFrequency).setClockMode(clockMode)
					.setWordLength(wordLength).setBitOrdering(bitOrdering).setCSActiveLevel(csActiveLevel).build();
			spi = (SPIDevice) DeviceManager.open(config, DeviceManager.EXCLUSIVE);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return spi;
	}

	public void mount(FlashROMDeviceDescriptor desc) throws Exception {

		if (desc instanceof SPIFlashDeviceDescriptor) {
			if (spi != null) {
				return; // Do nothing if already mounted
			}

			SPIFlashDeviceDescriptor spi_flash_desc = (SPIFlashDeviceDescriptor) desc;
			SPIDevice spi = getSPIDevice(spi_flash_desc.getSPIControllerNumber(), spi_flash_desc.getCSAddress(),
					spi_flash_desc.getClockFrequency(), spi_flash_desc.getClockMode(), spi_flash_desc.getWordLength(),
					spi_flash_desc.getBitOrdering(), spi_flash_desc.getCSActiveLevel());
			this.spi = spi;
		} else {
			// TODO: Create specific exception for this
			throw new Exception();
		}
	}
}
