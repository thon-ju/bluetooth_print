//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.qs.helper.printer.bt;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.qs.helper.printer.BlueToothService;
import com.qs.helper.printer.BlueToothService.OnReceiveDataHandleEvent;
import com.qs.helper.printer.Device;
import com.qs.helper.printer.PrintService;
import com.qs.helper.printer.PrinterClass;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BtService extends PrintService implements PrinterClass {
    Context context;
    Handler mhandler;
    Handler handler;
    public static BlueToothService mBTService = null;

    public BtService(Context _context, Handler _mhandler, Handler _handler) {
        this.context = _context;
        this.mhandler = _mhandler;
        this.handler = _handler;
        mBTService = new BlueToothService(this.context, this.mhandler);
        mBTService.setOnReceive(new OnReceiveDataHandleEvent() {
            @Override
            public void OnReceive(BluetoothDevice device) {
                if (device != null) {
                    Device d = new Device();
                    d.deviceName = device.getName();
                    d.deviceAddress = device.getAddress();
                    Message msg = new Message();
                    msg.what = 1;
                    msg.obj = d;
                    BtService.this.handler.sendMessage(msg);
                    BtService.this.setState(7);
                } else {
                    Message msgx = new Message();
                    msgx.what = 8;
                    BtService.this.handler.sendMessage(msgx);
                    Log.e("", "发送的消息：" + msgx);
                }

            }
        });
    }

    @Override
    public boolean open(Context context) {
        mBTService.OpenDevice();
        return true;
    }

    @Override
    public boolean close(Context context) {
        mBTService.CloseDevice();
        return false;
    }

    @Override
    public void scan() {
        if (!mBTService.IsOpen()) {
            mBTService.OpenDevice();
        } else if (mBTService.getState() != 7) {
            (new Thread() {
                public void run() {
                    BtService.mBTService.ScanDevice();
                }
            }).start();
        }
    }

    @Override
    public boolean connect(String device) {
        if (mBTService.getState() == 7) {
            this.stopScan();
        }

        if (mBTService.getState() == 2) {
            return false;
        } else {
            if (mBTService.getState() == 3) {
                mBTService.DisConnected();
            }

            mBTService.ConnectToDevice(device);
            return true;
        }
    }

    @Override
    public boolean disconnect() {
        mBTService.DisConnected();
        return true;
    }

    @Override
    public int getState() {
        return mBTService.getState();
    }

    @Override
    public boolean write(byte[] bt) {
        if (this.getState() != 3) {
            Toast toast = Toast.makeText(this.context, "LOST", 0);
            toast.show();
            return false;
        } else {
            mBTService.write(bt);
            return true;
        }
    }

    @Override
    public boolean printText(String textStr) {
        byte[] buffer = this.getText(textStr);
        if (buffer.length <= 100) {
            return this.write(buffer);
        } else {
            int sendSize = 100;
            boolean issendfull = false;

            for(int j = 0; j < buffer.length; j += sendSize) {
                byte[] btPackage = new byte[sendSize];
                if (buffer.length - j < sendSize) {
                    btPackage = new byte[buffer.length - j];
                }

                System.arraycopy(buffer, j, btPackage, 0, btPackage.length);
                this.write(btPackage);

                try {
                    Thread.sleep(86L);
                } catch (InterruptedException var8) {
                    var8.printStackTrace();
                }
            }

            return true;
        }
    }

    @Override
    public boolean printImage(Bitmap bitmap) {
        return this.write(this.getImage(bitmap));
    }

    @Override
    public boolean printUnicode(String textStr) {
        return this.write(this.getTextUnicode(textStr));
    }

    @Override
    public boolean IsOpen() {
        return mBTService.IsOpen();
    }

    @Override
    public void stopScan() {
    }

    @Override
    public void setState(int state) {
        mBTService.setState(state);
    }

    @Override
    public List<Device> getDeviceList() {
        List<Device> devList = new ArrayList();
        Set<BluetoothDevice> devices = mBTService.GetBondedDevice();
        Iterator var4 = devices.iterator();

        while(var4.hasNext()) {
            BluetoothDevice bluetoothDevice = (BluetoothDevice)var4.next();
            Device d = new Device();
            d.deviceName = bluetoothDevice.getName();
            d.deviceAddress = bluetoothDevice.getAddress();
            devList.add(d);
        }

        return devList;
    }
}
