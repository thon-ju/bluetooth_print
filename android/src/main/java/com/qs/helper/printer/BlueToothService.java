//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.qs.helper.printer;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.UUID;

public class BlueToothService {
    private BluetoothAdapter adapter;
    private Context context;
    private int mState;
    private Boolean D = true;
    private String TAG = "BlueToothService";
    private BlueToothService.AcceptThread mAcceptThread;
    private BlueToothService.ConnectThread mConnectThread;
    private BlueToothService.ConnectedThread mConnectedThread;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String NAME = "BTPrinter";
    private Handler mHandler;
    public BlueToothService.OnReceiveDataHandleEvent OnReceive = null;
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if ("android.bluetooth.device.action.FOUND".equals(action)) {
                BluetoothDevice device = (BluetoothDevice)intent.getParcelableExtra("android.bluetooth.device.extra.DEVICE");
                if (device.getBondState() != 12) {
                    BlueToothService.this.setState(7);
                    BlueToothService.this.OnReceive.OnReceive(device);
                }
            } else if ("android.bluetooth.adapter.action.DISCOVERY_FINISHED".equals(action)) {
                BlueToothService.this.setState(8);
                BlueToothService.this.OnReceive.OnReceive((BluetoothDevice)null);
            }

        }

        private void OnFinished() {
        }
    };
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    public BlueToothService(Context context, Handler handler) {
        this.context = context;
        this.mHandler = handler;
        this.mState = 0;
        this.adapter = BluetoothAdapter.getDefaultAdapter();
    }

    public boolean HasDevice() {
        return this.adapter != null;
    }

    public boolean IsOpen() {
        synchronized(this) {
            return this.adapter.isEnabled();
        }
    }

    public void OpenDevice() {
        Intent intent = new Intent("android.bluetooth.adapter.action.REQUEST_ENABLE");
        this.context.startActivity(intent);
    }

    public void CloseDevice() {
        this.adapter.disable();
    }

    public Set<BluetoothDevice> GetBondedDevice() {
        Set<BluetoothDevice> devices = this.adapter.getBondedDevices();
        return devices;
    }

    public void ScanDevice() {
        IntentFilter filter = new IntentFilter("android.bluetooth.device.action.FOUND");
        this.context.registerReceiver(this.mReceiver, filter);
        filter = new IntentFilter("android.bluetooth.adapter.action.DISCOVERY_FINISHED");
        this.context.registerReceiver(this.mReceiver, filter);
        if (this.adapter.isDiscovering()) {
            this.adapter.cancelDiscovery();
        }

        this.setState(7);
        this.adapter.startDiscovery();
    }

    public void StopScan() {
        this.context.unregisterReceiver(this.mReceiver);
        this.adapter.cancelDiscovery();
        this.setState(8);
    }

    public BlueToothService.OnReceiveDataHandleEvent getOnReceive() {
        return this.OnReceive;
    }

    public void setOnReceive(BlueToothService.OnReceiveDataHandleEvent onReceive) {
        this.OnReceive = onReceive;
    }

    public void ConnectToDevice(String address) {
        if (BluetoothAdapter.checkBluetoothAddress(address)) {
            BluetoothDevice device = this.adapter.getRemoteDevice(address);
            this.connect(device);
            this.setState(2);
        }

    }

    public void write(byte[] out) {
        BlueToothService.ConnectedThread r;
        synchronized(this) {
            if (this.mState != 3) {
                return;
            }

            r = this.mConnectedThread;
        }

        if (r != null) {
            r.write(out);
        } else {
            this.DisConnected();
            this.Nopointstart();
        }

    }

    public synchronized void start() {
        if (this.D) {
            Log.d(this.TAG, "start");
        }

        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        if (this.mAcceptThread == null) {
            this.mAcceptThread = new BlueToothService.AcceptThread();
            this.mAcceptThread.start();
        }

        this.setState(1);
    }

    public synchronized void setState(int state) {
        this.mState = state;
    }

    public synchronized int getState() {
        return this.mState;
    }

    public synchronized void connect(BluetoothDevice device) {
        if (this.mState == 2 && this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        try {
            Thread.sleep(1000L);
        } catch (InterruptedException var3) {
            var3.printStackTrace();
        }

        this.mConnectThread = new BlueToothService.ConnectThread(device);
        this.mConnectThread.start();
    }

    public synchronized void DisConnected() {
        if (this.mState == 3) {
            if (this.mConnectThread != null) {
                this.mConnectThread.cancel();
                this.mConnectThread = null;
            }

            if (this.mConnectedThread != null) {
                this.mConnectedThread.cancel();
                this.mConnectedThread = null;
            }

            if (this.mAcceptThread != null) {
                this.mAcceptThread.cancel();
                this.mAcceptThread = null;
            }

            this.setState(0);
        }

    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device) {
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        if (this.mAcceptThread != null) {
            this.mAcceptThread.cancel();
            this.mAcceptThread = null;
        }

        this.mConnectedThread = new BlueToothService.ConnectedThread(socket);
        this.mConnectedThread.start();
        this.setState(3);
    }

    public synchronized void stop() {
        if (this.D) {
            Log.d(this.TAG, "stop");
        }

        this.setState(0);
        if (this.mConnectThread != null) {
            this.mConnectThread.cancel();
            this.mConnectThread = null;
        }

        if (this.mConnectedThread != null) {
            this.mConnectedThread.cancel();
            this.mConnectedThread = null;
        }

        if (this.mAcceptThread != null) {
            this.mAcceptThread.cancel();
            this.mAcceptThread = null;
        }

    }

    private void connectionSuccess() {
        this.setState(3);
        this.mHandler.obtainMessage(1, 6, -1).sendToTarget();
    }

    private void connectionFailed() {
        this.setState(1);
        this.mHandler.obtainMessage(1, 5, -1).sendToTarget();
    }

    private void connectionLost() {
        this.setState(1);
        this.mHandler.obtainMessage(1, 4, -1).sendToTarget();
    }

    private void Nopointstart() {
        this.setState(1);
        this.mHandler.obtainMessage(1, 4, 0).sendToTarget();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            BluetoothServerSocket tmp = null;

            try {
                tmp = BlueToothService.this.adapter.listenUsingRfcommWithServiceRecord("BTPrinter", BlueToothService.MY_UUID);
            } catch (IOException var4) {
                Log.e(BlueToothService.this.TAG, "listen() failed", var4);
            }

            this.mmServerSocket = tmp;
        }

        public void run() {
            if (BlueToothService.this.D) {
                Log.d(BlueToothService.this.TAG, "BEGIN mAcceptThread" + this);
            }

            this.setName("AcceptThread");
            BluetoothSocket socket = null;

            while(true) {
                try {
                    if (this.mmServerSocket != null) {
                        socket = this.mmServerSocket.accept();
                    }
                } catch (IOException var4) {
                    Log.e(BlueToothService.this.TAG, var4.toString());
                    return;
                }

                if (socket != null) {
                    synchronized(BlueToothService.this) {
                        switch(BlueToothService.this.mState) {
                            case 0:
                            case 3:
                                try {
                                    socket.close();
                                } catch (IOException var5) {
                                }
                                break;
                            case 1:
                            case 2:
                                BlueToothService.this.connected(socket, socket.getRemoteDevice());
                        }
                    }
                }
            }
        }

        public void cancel() {
            if (BlueToothService.this.D) {
                Log.d(BlueToothService.this.TAG, "cancel " + this);
            }

            try {
                this.mmServerSocket.close();
            } catch (IOException var2) {
                Log.e(BlueToothService.this.TAG, "close() of server failed", var2);
            }

        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            this.mmDevice = device;
            BluetoothSocket tmp = null;

            try {
                tmp = device.createRfcommSocketToServiceRecord(BlueToothService.MY_UUID);
            } catch (IOException var5) {
                Log.e(BlueToothService.this.TAG, "create() failed", var5);
            }

            this.mmSocket = tmp;
        }

        public void run() {
            Log.i(BlueToothService.this.TAG, "BEGIN mConnectThread");
            this.setName("ConnectThread");
            BlueToothService.this.adapter.cancelDiscovery();
            BlueToothService.this.setState(8);

            try {
                this.mmSocket.connect();
                BlueToothService.this.connectionSuccess();
            } catch (IOException var5) {
                BlueToothService.this.connectionFailed();

                try {
                    this.mmSocket.close();
                } catch (IOException var3) {
                    Log.e(BlueToothService.this.TAG, "unable to close() socket during connection failure", var3);
                }

                BlueToothService.this.start();
                return;
            }

            synchronized(BlueToothService.this) {
                BlueToothService.this.mConnectThread = null;
            }

            BlueToothService.this.connected(this.mmSocket, this.mmDevice);
        }

        public void cancel() {
            try {
                this.mmSocket.close();
            } catch (IOException var2) {
                Log.e(BlueToothService.this.TAG, "close() of connect socket failed", var2);
            }

        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private boolean isCancle = false;

        public ConnectedThread(BluetoothSocket socket) {
            Log.d(BlueToothService.this.TAG, "create ConnectedThread");
            this.mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            this.isCancle = false;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException var6) {
                Log.e(BlueToothService.this.TAG, "temp sockets not created", var6);
            }

            this.mmInStream = tmpIn;
            this.mmOutStream = tmpOut;
        }

        public void run() {
            Log.i(BlueToothService.this.TAG, "BEGIN mConnectedThread");
            byte[] buffer = new byte[1024];

            while(true) {
                try {
                    while(true) {
                        while(true) {
                            int bytes = this.mmInStream.read(buffer);
                            String readMessage = new String(buffer, 0, bytes);
                            Log.e("", "读入数据：" + readMessage);
                            if (bytes <= 0) {
                                return;
                            }

                            if (buffer[0] != 19) {
                                if (buffer[0] != 17) {
                                    BlueToothService.this.mHandler.obtainMessage(2, bytes, -1, buffer).sendToTarget();
                                } else {
                                    PrintService.isFUll = false;
                                    Log.i(BlueToothService.this.TAG, "0x11:");
                                }
                            } else {
                                PrintService.isFUll = true;
                                Log.i(BlueToothService.this.TAG, "0x13:");
                            }
                        }
                    }
                } catch (IOException var5) {
                    Log.e(BlueToothService.this.TAG, "disconnected", var5);
                    BlueToothService.this.connectionLost();
                    return;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                this.mmOutStream.write(buffer);
                Log.w("BTPWRITE", new String(buffer));
                BlueToothService.this.mHandler.obtainMessage(3, -1, -1, buffer).sendToTarget();
            } catch (IOException var3) {
            }

        }

        public void cancel() {
            try {
                this.isCancle = true;
                this.mmSocket.close();
                Log.d(BlueToothService.this.TAG, "562cancel suc");
                BlueToothService.this.setState(1);
            } catch (IOException var2) {
                Log.d(BlueToothService.this.TAG, "565cancel failed");
            }

        }
    }

    public interface OnReceiveDataHandleEvent {
        void OnReceive(BluetoothDevice var1);
    }
}
