package com.gree.air.condition.constant;

/**
 * 常量类
 * 
 * @author lihaotian
 *
 */
public class Constant {

	public static final String APP_VERSION = "V0.3";
	public static final byte APP_VERSION_FIRST = (byte) 0x00;
	public static final byte APP_VERSKON_SECOND = (byte) 0x03;

	public static final int BAUD_RATE = 9600;
	public static final byte GPRS_MODEL = (byte) 0x02;

	// =================== about uart constant ===================
	// 记录收Buffer的长度
	public static int Uart_In_Buffer_Length = 0;
	// 串口通讯收Buffer
	public static byte[] Uart_In_Buffer = new byte[512];
	// 串口通讯写Buffer
	public static byte[] Uart_Out_Buffer = new byte[512];

	// 选举
	public static final byte TYPE_CHOOSE = (byte) 0XF0;
	// 点名
	public static final byte TYPE_CALL = (byte) 0X0F;

	// =================== about tcp constant ===================
	// TCP通讯收Buffer
	public static byte[] Tcp_In_Buffer = new byte[1024];
	// TCP通讯发Buffer
	public static byte[] Tcp_Out_Buffer = new byte[1024];
	// TCP 上传数据 Buffer
	public static byte[] Tcp_Out_Data_Buffer = new byte[2048];

	// 用于存储服务器的机组数据
	public static byte[] Data_Cache_Buffer = new byte[3096];

	public static byte[] Data_Buffer = new byte[1792];
	public static byte[] Data_SPI_Buffer = new byte[2048];

	// 实时监控上报
	public static final byte TRANSMIT_TYPE_ALWAYS = (byte) 0x00;
	// 调试上报
	public static final byte TRANSMIT_TYPE_DEBUG = (byte) 0x01;
	// 按键上报
	public static final byte TRANSMIT_TYPE_PUSHKEY = (byte) 0x02;
	// 故障上报
	public static final byte TRANSMIT_TYPE_ERROR = (byte) 0x03;
	// 亚健康（异常）上报
	public static final byte TRANSMIT_TYPE_WARNING = (byte) 0x04;
	// 厂家参数变化上报
	public static final byte TRANSMIT_TYPE_CHANGE = (byte) 0x05;
	// 关机上报
	public static final byte TRANSMIT_TYPE_CLOSE = (byte) 0x06;
	// 选举上报
	public static final byte TRANSMIT_TYPE_CHOOSE = (byte) 0x07;
	// 上电上报
	public static final byte TRANSMIT_TYPE_POWER = (byte) 0x08;
	// 打卡上报
	public static final byte TRANSMIT_TYPE_CHECK = (byte) 0x80;
	// 开机上报
	public static final byte TRANSMIT_TYPE_OPEN = (byte) 0X0A;
	// 停止上报
	public static final byte TRANSMIT_TYPE_STOP = (byte) 0xFF;
	// 缓存 上报数据状态 默认无
	public static byte Transmit_Type = TRANSMIT_TYPE_STOP;

	// =============== about sms constant =====================
	public static String Sms_Receive = "";

	// ================= about file constant ================
	public static byte[] File_Buffer = new byte[256];
	public static int File_Buffer_Length = 0;

	// =============== about cache constant ===================
	// 服务器下发的数据
	public static byte[] Server_Data_Word_Buffer = new byte[1024];
	public static byte[] Server_Data_Byte_Buffer = new byte[256];
	// 系统时间
	public static long System_Time = 0L;
	public static long System_Delta_Time = 0L;
	// 静默时间戳
	public static long Stop_Time = 0;

	// =================== about params ===================
	// 普通手机号白名单
	public static String[] Sms_User_List = { "18926932769", "13128540406", "13113444079", "13128541143", "18666911714",
			"07568663110", "07568522593", "07568668938", "07568669703", "07568668717" };
	// 管理员手机号白名单
	public static String[] Sms_Admin_List = { "18023036958", "13128553002", "1069800006512610", "18926932781",
			"15992681809" };

	// 手机短信
	public static String Sms_Pwd = "123456";

	// 心跳间隔
	public static int Tcp_Heart_Beat_Period = 5 * 60;
	// 故障点前传输时间
	public static int Transmit_Error_Start_Time = 30 * 60;
	// 故障点后传输时间
	public static int Transmit_Error_End_Time = 5 * 60;
	// 厂家参数改变前传输结束时间
	public static int Transmit_Change_End_Time = 1 * 60;
	// 按键调试周期
	public static int Transmit_Pushkey_End_Time = 240 * 60;
	// 信号信息周期
	public static int Tcp_Sig_Period = 10 * 60;
	// 打卡周期
	public static int Transmit_Check_Period = 60 * 60;
	// 打卡时长
	public static int Transmit_Check_End_Time = 1 * 60;
	// 开机上报时间
	public static int Transmit_Open_Start_Time = 10 * 60;
	public static int Transmit_Open_End_Time = 30 * 60;
	// 关机上报时间
	public static int Transmit_Close_Start_Time = 30 * 60;
	public static int Transmit_Close_End_Time = 10 * 60;

	// APN信息
	// 联通
	public static String Apn_Cucc = "greeac.gd";
	// 移动
	public static String Apn_Cmcc = "cmiotgree.gd";
	public static String Apn_Name = "GPRS";
	public static String Apn_Pwd = "GPRS";

	// 记录GPRS模块是否被选中
	public static boolean Gprs_Choosed = false;

	// ================== about gprs info =================================
	// Mac地址
	public static byte[] Gprs_Mac = new byte[7];
	public static byte[] Server_Mac = new byte[7];

	// ================= about interim params =======================
	public static long Heart_Beat_Time = 0L;

	public static boolean Init_Success = false;

	// =============== gprs error type ===================
	public static final int GPRS_ERROR_TYPE_NO = 0;
	public static final int GPRS_ERROR_TYPE_SIM = 1;
	public static final int GPRS_ERROR_TYPE_NETWORK = 2;
	public static final int GPRS_ERROR_TYPE_SERVER = 3;

	public static int GPRS_ERROR_TYPE = GPRS_ERROR_TYPE_NO;

	// 是否有改变的数据
	public static boolean Data_Word_Change = false;

}
