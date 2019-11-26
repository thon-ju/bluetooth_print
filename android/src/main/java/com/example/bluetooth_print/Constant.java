package com.example.bluetooth_print;

public class Constant {
    public static final String SERIALPORTPATH = "SerialPortPath";
    public static final String SERIALPORTBAUDRATE = "SerialPortBaudrate";
    public static final String WIFI_CONFIG_IP = "wifi config ip";
    public static final String WIFI_CONFIG_PORT = "wifi config port";
    public static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    public static final int BLUETOOTH_REQUEST_CODE = 0x001;
    public static final int USB_REQUEST_CODE = 0x002;
    public static final int WIFI_REQUEST_CODE = 0x003;
    public static final int SERIALPORT_REQUEST_CODE = 0x006;
    public static final int CONN_STATE_DISCONN = 0x007;
    public static final int MESSAGE_UPDATE_PARAMETER = 0x009;
    public static final int tip=0x010;
    public static final int abnormal_Disconnection=0x011;//异常断开

    /**
     * wifi 默认ip
     */
    public static final String WIFI_DEFAULT_IP = "192.168.123.100";

    /**
     * wifi 默认端口号
     */
    public static final int WIFI_DEFAULT_PORT = 9100;
}