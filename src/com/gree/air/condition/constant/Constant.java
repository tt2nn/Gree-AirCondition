package com.gree.air.condition.constant;

/**
 * 常量类
 * 
 * @author lihaotian
 *
 */
public class Constant {

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
	public static byte[] Tcp_In_Buffer = new byte[512];
	// TCP通讯发Buffer
	public static byte[] Tcp_Out_Buffer = new byte[4096];
	// TCP 上传数据 Buffer
	public static byte[] Tcp_Out_Data_Buffer = new byte[4096];

	// 用于存储服务器的机组数据
	public static byte[] Data_Buffer = new byte[4096];
	public static byte[] Data_Cache_Buffer = new byte[4096];

	// 实时监控上报
	public static final byte TRANSM_TYPE_ALWAYS = (byte) 0x00;
	// 调试上报
	public static final byte TRANSM_TYPE_DEBUG = (byte) 0x01;
	// 按键上报
	public static final byte TRANSM_TYPE_PUSHKEY = (byte) 0x02;
	// 故障上报
	public static final byte TRANSM_TYPE_ERROR = (byte) 0x03;
	// 亚健康（异常）上报
	public static final byte TRANSM_TYPE_WARNING = (byte) 0x04;
	// 厂家参数变化上报
	public static final byte TRANSM_TYPE_CHANGE = (byte) 0x05;
	// 选举上报
	public static final byte TRANSM_TYPE_CHOOSE = (byte) 0x07;
	// 上电上报
	public static final byte TRANSM_TYPE_BOOT = (byte) 0x08;
	// 停止上报
	public static final byte TRANSM_TYPE_STOP = (byte) 0x10;
	// 缓存 上报数据状态 默认无
	public static byte Transm_Type = TRANSM_TYPE_STOP;

	// =================== about transm constant ===================
	// 传输级别
	public static final int TRANSM_TYPE_STOP_LEVEL = 0;
	public static final int TRANSM_TYPE_BOOT_LEVEL = 1;
	public static final int TRANSM_TYPE_CHOOSE_LEVEL = 2;
	public static final int TRANSM_TYPE_WARNING_LEVEL = 3;
	public static final int TRANSM_TYPE_PUSHKEY_LEVEL = 4;
	public static final int TRANSM_TYPE_CHANGE_LEVEL = 5;
	public static final int TRANSM_TYPE_ERROR_LEVEL = 6;
	public static final int TRANSM_TYPE_DEBUG_LEVEL = 7;
	public static final int TRANSM_TYPE_ALWAYS_LEVEL = 8;
	public static int Transm_Level = TRANSM_TYPE_STOP_LEVEL;

	// 上报优先级 实时监控-工程调试-故障-厂家参数-按键触发-亚健康-选举-上电
	// 故障标志位
	public static int Transm_Error_Mark = 0;
	// 异常标志位
	public static int Transm_Warning_Mark = 0;
	// 参数变化标志位
	public static int Transm_Change_Mark = 0;

	// =============== about sms constant =====================

	public static String Sms_Receive = "";
	public static String Sms_Send = "";

	public static final String Sms_Type_Apn = "*apn*";
	public static final String Sms_Type_Serv = "*serv*";
	public static final String Sms_Type_Hb = "*hb*";
	public static final String Sms_Type_Pwd = "*pwd*";
	public static final String Sms_Type_Start = "*start*";
	public static final String Sms_Type_Stop = "*stop*";
	public static final String Sms_Type_Ver = "*ver*";
	public static final String Sms_Type_Adm = "*adm*";
	public static final String Sms_Type_Usron = "*usron*";
	public static final String Sms_Type_Rst = "*rst*";
	public static final String Sms_Type_Errt = "*errt*";
	public static final String Sms_Type_Debt = "*debt*";
	public static final String Sms_Type_Healt = "*healt*";
	public static final String Sms_Type_Butt = "*butt*";

	public static final String Sms_Query_Symbol = "*?*";
	public static final String Sms_Split_Key_Symbol = "*";
	public static final String Sms_Split_Value_Symbol = ",";
	public static final String Sms_Set_Ok = "set ok";
	public static final String Sms_Message_Error = "error";
	public static final String Sms_Message_Empty = "empty";

	// 普通手机号白名单
	public static String Sms_User_List = "18926932769 13128540406 13113444079 13128541143 18666911714 07568663110 07568522593 07568668938 07568669703 07568668717";
	// 管理员手机号白名单
	public static String Sms_Admin_List = "18023036958 13128553002 1069800006512610 18926932781 15992681809";
	// 手机短信
	public static String Sms_Pwd = "123456";

	// =================== about other constant ===================
	// 记录GPRS模块是否被选中
	public static boolean GPRS_IS_CHOOSE = false;
	// 数据使用游标 由 0-2047
	public static int Data_Buffer_Mark = 0;
	public static int Data_Buffer_Out_Mark = 0;
	public static int Data_Buffer_Out_End_Mark = -1;
	// 服务器下发的数据
	public static byte[] Server_Data_Buffer = new byte[1024];
	// 系统时间
	public static long System_Time = 0;
	// 静默时间戳
	public static long Stop_Time = 0;

	// ================== about gprs info =================================
	// 模块Imei
	public static String Gprs_Imei = "861390030083946";
	
	// Mac地址
	public static byte[] Gprs_Mac = { (byte) 0x61, (byte) 0x39, (byte) 0x00, (byte) 0x30, (byte) 0x08, (byte) 0x39,
			(byte) 0x46 };
	public static byte[] Server_Mac = new byte[7];

}
