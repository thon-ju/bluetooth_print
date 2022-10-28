package com.example.bluetooth_print;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.gprinter.io.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author thon
 */
public class DeviceConnFactoryManager {
    private static final String TAG = DeviceConnFactoryManager.class.getSimpleName();

    public PortManager mPort;

    public CONN_METHOD connMethod;

    private final String macAddress;

    private final Context mContext;

    private static Map<String, DeviceConnFactoryManager> deviceConnFactoryManagers = new HashMap<>();

    private boolean isOpenPort;
    /**
     * ESC查询打印机实时状态指令
     */
    private final byte[] esc = {0x10, 0x04, 0x02};

    /**
     * ESC查询打印机实时状态 缺纸状态
     */
    private static final int ESC_STATE_PAPER_ERR = 0x20;

    /**
     * ESC指令查询打印机实时状态 打印机开盖状态
     */
    private static final int ESC_STATE_COVER_OPEN = 0x04;

    /**
     * ESC指令查询打印机实时状态 打印机报错状态
     */
    private static final int ESC_STATE_ERR_OCCURS = 0x40;

    /**
     * TSC查询打印机状态指令
     */
    private final byte[] tsc = {0x1b, '!', '?'};

    /**
     * TSC指令查询打印机实时状态 打印机缺纸状态
     */
    private static final int TSC_STATE_PAPER_ERR = 0x04;

    /**
     * TSC指令查询打印机实时状态 打印机开盖状态
     */
    private static final int TSC_STATE_COVER_OPEN = 0x01;

    /**
     * TSC指令查询打印机实时状态 打印机出错状态
     */
    private static final int TSC_STATE_ERR_OCCURS = 0x80;

    private final byte[] cpcl={0x1b,0x68};

    /**
     * CPCL指令查询打印机实时状态 打印机缺纸状态
     */
    private static final int CPCL_STATE_PAPER_ERR = 0x01;
    /**
     * CPCL指令查询打印机实时状态 打印机开盖状态
     */
    private static final int CPCL_STATE_COVER_OPEN = 0x02;

    private byte[] sendCommand;

    /**
     * 判断打印机所使用指令是否是ESC指令
     */
    private PrinterCommand currentPrinterCommand;
    public static final byte FLAG = 0x10;
    private static final int READ_DATA = 10000;
    private static final int DEFAUIT_COMMAND=20000;
    private static final String READ_DATA_CNT = "read_data_cnt";
    private static final String READ_BUFFER_ARRAY = "read_buffer_array";
    public static final String ACTION_CONN_STATE = "action_connect_state";
    public static final String ACTION_QUERY_PRINTER_STATE = "action_query_printer_state";
    public static final String STATE = "state";
    public static final String DEVICE_ID = "id";
    public static final int CONN_STATE_DISCONNECT = 0x90;
    public static final int CONN_STATE_CONNECTED = CONN_STATE_DISCONNECT << 3;
    public PrinterReader reader;
    private int queryPrinterCommandFlag;
    private final int ESC = 1;
    private final int TSC = 3;
    private final int CPCL = 2;

    public enum CONN_METHOD {
        //蓝牙连接
        BLUETOOTH("BLUETOOTH"),
        //USB连接
        USB("USB"),
        //wifi连接
        WIFI("WIFI"),
        //串口连接
        SERIAL_PORT("SERIAL_PORT");

        private final String name;

        private CONN_METHOD(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }

    public static Map<String, DeviceConnFactoryManager> getDeviceConnFactoryManagers() {
        return deviceConnFactoryManagers;
    }

    /**
     * 打开端口
     */
    public void openPort() {
        DeviceConnFactoryManager deviceConnFactoryManager = deviceConnFactoryManagers.get(macAddress);
        if(deviceConnFactoryManager == null){
            return;
        }

        deviceConnFactoryManager.isOpenPort = false;
        if (deviceConnFactoryManager.connMethod == CONN_METHOD.BLUETOOTH) {
            mPort = new BluetoothPort(macAddress);
            isOpenPort = deviceConnFactoryManager.mPort.openPort();
        }

        //端口打开成功后，检查连接打印机所使用的打印机指令ESC、TSC
        if (isOpenPort) {
            queryCommand();
        } else {
            if (this.mPort != null) {
                this.mPort=null;
            }

        }
    }

    /**
     * 查询当前连接打印机所使用打印机指令（ESC（EscCommand.java）、TSC（LabelCommand.java））
     */
    private void queryCommand() {
        //开启读取打印机返回数据线程
        reader = new PrinterReader();
        reader.start(); //读取数据线程
        //查询打印机所使用指令
        queryPrinterCommand(); //小票机连接不上  注释这行，添加下面那三行代码。使用ESC指令

    }

    /**
     * 获取端口连接方式
     */
    public CONN_METHOD getConnMethod() {
        return connMethod;
    }

    /**
     * 获取端口打开状态（true 打开，false 未打开）
     */
    public boolean getConnState() {
        return isOpenPort;
    }

    /**
     * 获取连接蓝牙的物理地址
     */
    public String getMacAddress() {
        return macAddress;
    }

    /**
     * 关闭端口
     */
    public void closePort() {
        if (this.mPort != null) {
            if(reader!=null) {
                reader.cancel();
                reader = null;
            }
            boolean b= this.mPort.closePort();
            if(b) {
                this.mPort=null;
                isOpenPort = false;
                currentPrinterCommand = null;
            }

            Log.e(TAG, "******************* close Port macAddress -> " + macAddress);
        }
    }

    public static void closeAllPort() {
        for (DeviceConnFactoryManager deviceConnFactoryManager : deviceConnFactoryManagers.values()) {
            if (deviceConnFactoryManager != null) {
                Log.e(TAG, "******************* close All Port macAddress -> " + deviceConnFactoryManager.macAddress);

                deviceConnFactoryManager.closePort();
                deviceConnFactoryManagers.put(deviceConnFactoryManager.macAddress, null);
            }
        }
    }

    private DeviceConnFactoryManager(Build build) {
        this.connMethod = build.connMethod;
        this.macAddress = build.macAddress;
        this.mContext = build.context;
        deviceConnFactoryManagers.put(build.macAddress, this);
    }

    /**
     * 获取当前打印机指令
     *
     * @return PrinterCommand
     */
    public PrinterCommand getCurrentPrinterCommand() {
        return Objects.requireNonNull(deviceConnFactoryManagers.get(macAddress)).currentPrinterCommand;
    }

    public static final class Build {
        private String macAddress;
        private CONN_METHOD connMethod;
        private Context context;

        public DeviceConnFactoryManager.Build setMacAddress(String macAddress) {
            this.macAddress = macAddress;
            return this;
        }

        public DeviceConnFactoryManager.Build setConnMethod(CONN_METHOD connMethod) {
            this.connMethod = connMethod;
            return this;
        }

        public DeviceConnFactoryManager.Build setContext(Context context) {
            this.context = context;
            return this;
        }

        public DeviceConnFactoryManager build() {
            return new DeviceConnFactoryManager(this);
        }
    }

    public void sendDataImmediately(final Vector<Byte> data) {
        if (this.mPort == null) {
            return;
        }
        try {
            this.mPort.writeDataImmediately(data, 0, data.size());
        } catch (Exception e) {//异常中断发送
            mHandler.obtainMessage(Constant.abnormal_Disconnection).sendToTarget();
//            e.printStackTrace();

        }
    }
    public void sendByteDataImmediately(final byte [] data) {
        if (this.mPort != null) {
            Vector<Byte> datas = new Vector<Byte>();
            for (byte datum : data) {
                datas.add(Byte.valueOf(datum));
            }
            try {
                this.mPort.writeDataImmediately(datas, 0, datas.size());
            } catch (IOException e) {//异常中断
                mHandler.obtainMessage(Constant.abnormal_Disconnection).sendToTarget();
            }
        }
    }
    public int readDataImmediately(byte[] buffer){
        int r = 0;
        if (this.mPort == null) {
            return r;
        }

        try {
            r =  this.mPort.readData(buffer);
        } catch (IOException e) {
            closePort();
        }

        return  r;
    }

    /**
     * 查询打印机当前使用的指令（ESC、CPCL、TSC、）
     */
    private void queryPrinterCommand() {
        queryPrinterCommandFlag = ESC;
        ThreadPool.getInstantiation().addSerialTask(new Runnable() {
            @Override
            public void run() {
                //开启计时器，隔2000毫秒没有没返回值时发送查询打印机状态指令，先发票据，面单，标签
                final ThreadFactoryBuilder threadFactoryBuilder = new ThreadFactoryBuilder("Timer");
                final ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, threadFactoryBuilder);
                scheduledExecutorService.scheduleAtFixedRate(threadFactoryBuilder.newThread(new Runnable() {
                    @Override
                    public void run() {
                        if (currentPrinterCommand == null && queryPrinterCommandFlag > TSC) {
                            if (reader != null) {//三种状态，查询无返回值，发送连接失败广播
                                reader.cancel();
                                mPort.closePort();
                                isOpenPort = false;

                                scheduledExecutorService.shutdown();
                            }
                        }
                        if (currentPrinterCommand != null) {
                            if (!scheduledExecutorService.isShutdown()) {
                                scheduledExecutorService.shutdown();
                            }
                            return;
                        }
                        switch (queryPrinterCommandFlag) {
                            case ESC:
                                //发送ESC查询打印机状态指令
                                sendCommand = esc;
                                break;
                            case TSC:
                                //发送ESC查询打印机状态指令
                                sendCommand = tsc;
                                break;
                            case CPCL:
                                //发送CPCL查询打印机状态指令
                                sendCommand = cpcl;
                                break;
                            default:
                                break;
                        }
                        Vector<Byte> data = new Vector<>(sendCommand.length);
                        for (byte b : sendCommand) {
                            data.add(b);
                        }
                        sendDataImmediately(data);
                        queryPrinterCommandFlag++;
                    }
                }), 1500, 1500, TimeUnit.MILLISECONDS);
            }
        });
    }

    class PrinterReader extends Thread {
        private boolean isRun = false;
        private final byte[] buffer = new byte[100];

        public PrinterReader() {
            isRun = true;
        }

        @Override
        public void run() {
            try {
                while (isRun && mPort != null) {
                    //读取打印机返回信息,打印机没有返回纸返回-1
                    Log.e(TAG,"******************* wait read ");
                    int len = readDataImmediately(buffer);
                    Log.e(TAG,"******************* read "+len);
                    if (len > 0) {
                        Message message = Message.obtain();
                        message.what = READ_DATA;
                        Bundle bundle = new Bundle();
                        bundle.putInt(READ_DATA_CNT, len); //数据长度
                        bundle.putByteArray(READ_BUFFER_ARRAY, buffer); //数据
                        message.setData(bundle);
                        mHandler.sendMessage(message);
                    }
                }
            } catch (Exception e) {//异常断开
                if (deviceConnFactoryManagers.get(macAddress) != null) {
                    closePort();
                    mHandler.obtainMessage(Constant.abnormal_Disconnection).sendToTarget();
                }
            }
        }

        public void cancel() {
            isRun = false;
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.abnormal_Disconnection://异常断开连接
                    Log.d(TAG, "******************* abnormal disconnection");
                    sendStateBroadcast(Constant.abnormal_Disconnection);
                    break;
                case DEFAUIT_COMMAND://默认模式

                    break;
                case READ_DATA:
                    int cnt = msg.getData().getInt(READ_DATA_CNT); //数据长度 >0;
                    byte[] buffer = msg.getData().getByteArray(READ_BUFFER_ARRAY);  //数据
                    //这里只对查询状态返回值做处理，其它返回值可参考编程手册来解析
                    if (buffer == null) {
                        return;
                    }
                    int result = judgeResponseType(buffer[0]); //数据右移
                    String status = "";
                    if (sendCommand == esc) {
                        //设置当前打印机模式为ESC模式
                        if (currentPrinterCommand == null) {
                            currentPrinterCommand = PrinterCommand.ESC;
                            sendStateBroadcast(CONN_STATE_CONNECTED);
                        } else {//查询打印机状态
                            if (result == 0) {//打印机状态查询
                                Intent intent = new Intent(ACTION_QUERY_PRINTER_STATE);
                                intent.putExtra(DEVICE_ID, macAddress);
                                if(mContext!=null){
                                    mContext.sendBroadcast(intent);
                                }
                            } else if (result == 1) {//查询打印机实时状态
                                if ((buffer[0] & ESC_STATE_PAPER_ERR) > 0) {
                                    status += "*******************  Printer out of paper";
                                }
                                if ((buffer[0] & ESC_STATE_COVER_OPEN) > 0) {
                                    status += "*******************  Printer open cover";
                                }
                                if ((buffer[0] & ESC_STATE_ERR_OCCURS) > 0) {
                                    status += "*******************  Printer error";
                                }
                                Log.d(TAG, status);
                            }
                        }
                    }else if (sendCommand == tsc) {
                        //设置当前打印机模式为TSC模式
                        if (currentPrinterCommand == null) {
                            currentPrinterCommand = PrinterCommand.TSC;
                            sendStateBroadcast(CONN_STATE_CONNECTED);
                        } else {
                            if (cnt == 1) {//查询打印机实时状态
                                if ((buffer[0] & TSC_STATE_PAPER_ERR) > 0) {
                                    //缺纸
                                    status += "*******************  Printer out of paper";
                                }
                                if ((buffer[0] & TSC_STATE_COVER_OPEN) > 0) {
                                    //开盖
                                    status += "*******************  Printer open cover";
                                }
                                if ((buffer[0] & TSC_STATE_ERR_OCCURS) > 0) {
                                    //打印机报错
                                    status += "*******************  Printer error";
                                }
                                Log.d(TAG, status);
                            } else {//打印机状态查询
                                Intent intent = new Intent(ACTION_QUERY_PRINTER_STATE);
                                intent.putExtra(DEVICE_ID, macAddress);
                                if(mContext!=null){
                                    mContext.sendBroadcast(intent);
                                }
                            }
                        }
                    }else if(sendCommand==cpcl){
                        if (currentPrinterCommand == null) {
                            currentPrinterCommand = PrinterCommand.CPCL;
                            sendStateBroadcast(CONN_STATE_CONNECTED);
                        }else {
                            if (cnt == 1) {

                                if ((buffer[0] ==CPCL_STATE_PAPER_ERR)) {//缺纸
                                    status += "*******************  Printer out of paper";
                                }
                                if ((buffer[0] ==CPCL_STATE_COVER_OPEN)) {//开盖
                                    status += "*******************  Printer open cover";
                                }
                                Log.d(TAG, status);
                            } else {//打印机状态查询
                                Intent intent = new Intent(ACTION_QUERY_PRINTER_STATE);
                                intent.putExtra(DEVICE_ID, macAddress);
                                if(mContext!=null){
                                    mContext.sendBroadcast(intent);
                                }
                            }
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    /**
     * 发送广播
     */
    private void sendStateBroadcast(int state) {
        Intent intent = new Intent(ACTION_CONN_STATE);
        intent.putExtra(STATE, state);
        intent.putExtra(DEVICE_ID, macAddress);
        if(mContext != null){
            mContext.sendBroadcast(intent);//此处若报空指针错误，需要在清单文件application标签里注册此类，参考demo
        }
    }

    /**
     * 判断是实时状态（10 04 02）还是查询状态（1D 72 01）
     */
    private int judgeResponseType(byte r) {
        return (byte) ((r & FLAG) >> 4);
    }

}