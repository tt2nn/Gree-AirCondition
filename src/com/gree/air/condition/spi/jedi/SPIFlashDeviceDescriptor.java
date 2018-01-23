package com.gree.air.condition.spi.jedi;

public class SPIFlashDeviceDescriptor implements FlashROMDeviceDescriptor {
	protected int SPIControllerNumber;
	protected int CSAddress;
	protected int ClockFrequency;
	protected int ClockMode;
	protected int WordLength;
	protected int BitOrdering;
	protected int CSActiveLevel;
	protected String driverName;

	public SPIFlashDeviceDescriptor(String classNameOfFlashROMDriver, int SPIControllerNumber, int CSAddress,
			int clockFrequency, int clockMode, int wordLength, int bitOrdering, int csActiveLevel) {
		this.SPIControllerNumber = SPIControllerNumber;
		this.CSAddress = CSAddress;
		this.ClockFrequency = clockFrequency;
		this.ClockMode = clockMode;
		this.WordLength = wordLength;
		this.BitOrdering = bitOrdering;
		this.CSActiveLevel = csActiveLevel;
		this.driverName = classNameOfFlashROMDriver;
	}

	public int getSPIControllerNumber() {
		return SPIControllerNumber;
	}

	public int getCSAddress() {
		return CSAddress;
	}

	public int getClockFrequency() {
		return ClockFrequency;
	}

	public int getClockMode() {
		return ClockMode;
	}

	public int getWordLength() {
		return WordLength;
	}

	public int getBitOrdering() {
		return BitOrdering;
	}

	public int getCSActiveLevel() {
		return CSActiveLevel;
	}

	public FlashROMDeviceDescriptor getDescriptor() {
		return this;
	}

	public Class getFlashROMDeviceClass() throws ClassNotFoundException {
		return Class.forName(driverName);
	}
}
