package com.gree.air.condition.gpio;

import org.joshvm.j2me.dio.DeviceManager;
import org.joshvm.j2me.dio.gpio.GPIOPin;
import org.joshvm.j2me.dio.gpio.GPIOPinConfig;
import org.joshvm.j2me.dio.gpio.PinEvent;
import org.joshvm.j2me.dio.gpio.PinListener;

/**
 * 控制灯 <br>
 * 响应按键 <br>
 * 
 * @author lihaotian
 *
 */
public class GpioPin {

	public static void in() {

		try {
			GPIOPinConfig cfg1 = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 86, // SPI0_1_DI
					GPIOPinConfig.DIR_INPUT_ONLY, GPIOPinConfig.MODE_INPUT_PULL_UP, GPIOPinConfig.TRIGGER_LOW_LEVEL,
					true);

			GPIOPin pinin = (GPIOPin) DeviceManager.open(cfg1, DeviceManager.EXCLUSIVE);

			pinin.setInputListener(new PinListener() {
				public void valueChanged(PinEvent event) {
					System.out.println("value changed:" + (event.getValue() ? "on" : "off"));
				}
			});

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}

	public static void out() {

		try {

			GPIOPinConfig cfg = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 89, GPIOPinConfig.DIR_OUTPUT_ONLY,
					GPIOPinConfig.MODE_OUTPUT_OPEN_DRAIN, GPIOPinConfig.TRIGGER_NONE, false);
			GPIOPin pinout = (GPIOPin) DeviceManager.open(cfg, DeviceManager.EXCLUSIVE);
			
			GPIOPinConfig cfg2 = new GPIOPinConfig(GPIOPinConfig.UNASSIGNED, 20, GPIOPinConfig.DIR_OUTPUT_ONLY,
					GPIOPinConfig.MODE_OUTPUT_OPEN_DRAIN, GPIOPinConfig.TRIGGER_NONE, false);
			GPIOPin pinled = (GPIOPin) DeviceManager.open(cfg2, DeviceManager.EXCLUSIVE);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
