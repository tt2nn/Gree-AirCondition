package com.gree.air.condition.gpio;

/**
 * Gpio工具类
 * 
 * @author lihaotian
 *
 */
public class GpioTool {

	/**
	 * 设置信号灯
	 * 
	 * @param level
	 */
	public static void setSignLevel(int level) {

		GpioPin.signalAllDark();

		if (level >= 12 && level <= 14) {

			GpioPin.signalLowLight();

		} else if (level >= 15 && level <= 17) {

			GpioPin.signalLowLight();
			GpioPin.signalMindleLight();

		} else if (level >= 18) {

			GpioPin.signalLowLight();
			GpioPin.signalMindleLight();
			GpioPin.signalHighLight();

		}
	}

}
