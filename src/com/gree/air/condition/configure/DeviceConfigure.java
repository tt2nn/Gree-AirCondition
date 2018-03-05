package com.gree.air.condition.configure;

import java.io.IOException;

import org.joshvm.j2me.cellular.AccessPoint;
import org.joshvm.j2me.cellular.CellInfo;
import org.joshvm.j2me.cellular.CellularDeviceInfo;
import org.joshvm.j2me.cellular.NetworkInfo;

import com.gree.air.condition.entity.Apn;
import com.gree.air.condition.entity.Device;

/**
 * 
 * 
 * @author lihaotian
 *
 */
public class DeviceConfigure {

	private static CellularDeviceInfo[] devices;

	/**
	 * 查看设备初始化是否成功，主要是网络 <br>
	 * 
	 * mcc 460 中国 <br>
	 * mnc 0移动 1联通 <br>
	 * 
	 * @return
	 */
	public static boolean deviceInit() {

		try {

			devices = CellularDeviceInfo.listCellularDevices();

			if (devices != null && devices.length > 0) {

				NetworkInfo networkInfo = devices[0].getNetworkInfo();

				if (networkInfo.getMCC() == 460 && devices[0].getIMSI().length() > 1) {

					return true;
				}
			}

		} catch (IOException e) {

			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 获取设备信息
	 */
	public static void deviceInfo() {

		try {

			Device.getInstance().setImei(devices[0].getIMEI());
			Device.getInstance().setImsi(devices[0].getIMSI());
			Device.getInstance().setIccid(devices[0].getICCID());

			NetworkInfo networkInfo = devices[0].getNetworkInfo();
			if (networkInfo != null) {

				Device.getInstance().setMnc(networkInfo.getMNC());
				Device.getInstance().setMcc(networkInfo.getMCC());
			}

			CellInfo cellInfo = devices[0].getCellInfo();
			if (cellInfo != null) {

				Device.getInstance().setLac(cellInfo.getLAC());
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 获取网络信号级别
	 * 
	 * @return
	 */
	public static int getNetworkSignalLevel() {

		try {

			return devices[0].getNetworkSignalLevel();

		} catch (IOException e) {

			e.printStackTrace();
		}

		return 0;
	}

	/**
	 * 设置APN
	 * 
	 * @param apn
	 */
	public static void setApn(Apn apn) {

		try {

			AccessPoint accessPoint = new AccessPoint(apn.getApnName(), apn.getUserName(), apn.getPassword());
			devices[0].setAPN(accessPoint);

		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	/**
	 * 获取APN信息
	 * 
	 * @return
	 */
	public static Apn getApn() {

		Apn apn = new Apn();

		try {

			AccessPoint accessPoint = devices[0].getCurrentAPNSetting();
			apn.setApnName(accessPoint.getName());
			apn.setUserName(accessPoint.getUserName());
			apn.setPassword(accessPoint.getPassword());

		} catch (IOException e) {

			e.printStackTrace();
		}

		return apn;
	}

}
