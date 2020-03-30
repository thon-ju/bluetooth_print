package com.example.bluetooth_print;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.gprinter.command.FactoryCommand;
import com.qs.helper.printer.Device;
import com.qs.helper.printer.PrintService;
import com.qs.helper.printer.PrinterClass;
import com.qs.helper.printer.bt.BtService;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.EventChannel.EventSink;
import io.flutter.plugin.common.EventChannel.StreamHandler;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.flutter.plugin.common.PluginRegistry.RequestPermissionsResultListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** BluetoothPrintPlugin */
public class BluetoothPrintPlugin implements MethodCallHandler, RequestPermissionsResultListener {
  private static final String TAG = "BluetoothPrintPlugin";
  private int id = 0;
  private ThreadPool threadPool;
  private static final int REQUEST_COARSE_LOCATION_PERMISSIONS = 1451;
  private static final String NAMESPACE = "bluetooth_print";
  private final Registrar registrar;
  private final Activity activity;
  private final MethodChannel channel;
  private final EventChannel stateChannel;
  private final BluetoothManager mBluetoothManager;
  private BluetoothAdapter mBluetoothAdapter;

  // 打印机操作类
  public static PrinterClass pl = null;
  public static final int MESSAGE_STATE_CHANGE = 1;
  public static final int MESSAGE_READ = 2;
  public static final int MESSAGE_WRITE = 3;
  public static final int MESSAGE_DEVICE_NAME = 4;
  public static final int MESSAGE_TOAST = 5;
  Handler mhandler = null;
  Handler handler = null;

  private MethodCall pendingCall;
  private Result pendingResult;

  public static void registerWith(Registrar registrar) {
    final BluetoothPrintPlugin instance = new BluetoothPrintPlugin(registrar);
    registrar.addRequestPermissionsResultListener(instance);
  }

  BluetoothPrintPlugin(Registrar r){

    mhandler = new Handler() {
      public void handleMessage(Message msg) {
        switch (msg.what) {
          case MESSAGE_READ:
            byte[] readBuf = (byte[]) msg.obj;
            Log.e(TAG, "readBuf:" + readBuf[0]);
            if (readBuf[0] == 0x13) {
              PrintService.isFUll = true;

            } else if (readBuf[0] == 0x11) {
              PrintService.isFUll = false;

            } else if (readBuf[0] == 0x08) {

            } else if (readBuf[0] == 0x01) {

            }  else if (readBuf[0] == 0x04) {

            } else if (readBuf[0] == 0x02) {

            }else {
              String readMessage = new String(readBuf, 0, msg.arg1);
              Log.e("", "readMessage"+readMessage);
              if (readMessage.contains("800"))// 80mm paper
              {
                PrintService.imageWidth = 72;

                Log.e(TAG, "imageWidth:"+"80mm");
              } else if (readMessage.contains("580")) {
                // 58mm paper
                PrintService.imageWidth = 48;
                Log.e("", "imageWidth:"+"58mm");
              }
            }
            break;
          case MESSAGE_STATE_CHANGE:// 蓝牙连接状态
            switch (msg.arg1) {
              case PrinterClass.STATE_CONNECTED:// 已经连接
                break;
              case PrinterClass.STATE_CONNECTING:// 正在连接
                break;
              case PrinterClass.STATE_LISTEN:
              case PrinterClass.STATE_NONE:
                break;
              case PrinterClass.SUCCESS_CONNECT://连接成功
                pl.write(new byte[] { 0x1b, 0x2b });// 检测打印机型号
                break;
              case PrinterClass.FAILED_CONNECT://连接失败
                break;
              case PrinterClass.LOSE_CONNECT://连接丢失
            }
            break;
          case MESSAGE_WRITE:

            break;
        }
        super.handleMessage(msg);
      }
    };

    handler = new Handler() {
      @Override
      public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch (msg.what) {
          case 0:
            break;
          case 1:// 获取蓝牙端口号
            Device d = (Device) msg.obj;
            if (d != null) {

            }
            break;
          case 2:
            break;
        }
      }
    };

    this.registrar = r;
    this.activity = r.activity();
    this.channel = new MethodChannel(registrar.messenger(), NAMESPACE + "/methods");
    this.stateChannel = new EventChannel(registrar.messenger(), NAMESPACE + "/state");
    this.mBluetoothManager = (BluetoothManager) registrar.activity().getSystemService(Context.BLUETOOTH_SERVICE);
    this.mBluetoothAdapter = mBluetoothManager.getAdapter();
    channel.setMethodCallHandler(this);
    stateChannel.setStreamHandler(stateStreamHandler);
    this.pl = new BtService(this.activity.getApplication().getBaseContext(), mhandler, handler);
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (mBluetoothAdapter == null && !"isAvailable".equals(call.method)) {
      result.error("bluetooth_unavailable", "the device does not have bluetooth", null);
      return;
    }

    final Map<String, Object> args = call.arguments();

    switch (call.method){
      case "state":
        state(result);
        break;
      case "isAvailable":
        result.success(mBluetoothAdapter != null);
        break;
      case "isOn":
        result.success(mBluetoothAdapter.isEnabled());
        break;
      case "isConnected":
        result.success(threadPool != null);
        break;
      case "startScan":
      {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
          ActivityCompat.requestPermissions(
                  activity,
                  new String[] {Manifest.permission.ACCESS_COARSE_LOCATION},
                  REQUEST_COARSE_LOCATION_PERMISSIONS);
          pendingCall = call;
          pendingResult = result;
          break;
        }
        startScan(call, result);
        break;
      }
      case "stopScan":
        stopScan();
        result.success(null);
        break;
      case "connect":
        connect(result, args);
        break;
      case "disconnect":
        result.success(disconnect());
        break;
      case "destroy":
        result.success(destroy());
        break;
      case "print":
        print(result, args);
        break;
      case "printReceipt":
        print(result, args);
        break;
      case "printLabel":
        print(result, args);
        break;
      case "printTest":
        printTest(result);
        break;
      default:
        result.notImplemented();
        break;
    }

  }

  private void getDevices(Result result){
    List<Map<String, Object>> devices = new ArrayList<>();
    for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
      Map<String, Object> ret = new HashMap<>();
      ret.put("address", device.getAddress());
      ret.put("name", device.getName());
      ret.put("type", device.getType());
      devices.add(ret);
    }

    result.success(devices);
  }

  /**
   * 获取状态
   */
  private void state(Result result){
    try {
      switch(mBluetoothAdapter.getState()) {
        case BluetoothAdapter.STATE_OFF:
          result.success(BluetoothAdapter.STATE_OFF);
          break;
        case BluetoothAdapter.STATE_ON:
          result.success(BluetoothAdapter.STATE_ON);
          break;
        case BluetoothAdapter.STATE_TURNING_OFF:
          result.success(BluetoothAdapter.STATE_TURNING_OFF);
          break;
        case BluetoothAdapter.STATE_TURNING_ON:
          result.success(BluetoothAdapter.STATE_TURNING_ON);
          break;
        default:
          result.success(0);
          break;
      }
    } catch (SecurityException e) {
      result.error("invalid_argument", "argument 'address' not found", null);
    }

  }


  private void startScan(MethodCall call, Result result) {
    Log.d(TAG,"start scan ");

    try {
      startScan();
      result.success(null);
    } catch (Exception e) {
      result.error("startScan", e.getMessage(), e);
    }
  }

  private void invokeMethodUIThread(final String name, final BluetoothDevice device)
  {
    final Map<String, Object> ret = new HashMap<>();
    ret.put("address", device.getAddress());
    ret.put("name", device.getName());
    ret.put("type", device.getType());

    activity.runOnUiThread(
            new Runnable() {
              @Override
              public void run() {
                channel.invokeMethod(name, ret);
              }
            });
  }

  private ScanCallback mScanCallback = new ScanCallback() {
    @Override
    public void onScanResult(int callbackType, ScanResult result) {
      BluetoothDevice device = result.getDevice();
      if(device != null && device.getName() != null){
        invokeMethodUIThread("ScanResult", device);
      }
    }
  };

  private void startScan() throws IllegalStateException {
    BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
    if(scanner == null) {
      throw new IllegalStateException("getBluetoothLeScanner() is null. Is the Adapter on?");
    }

    // 0:lowPower 1:balanced 2:lowLatency -1:opportunistic
    ScanSettings settings = new ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).build();
    scanner.startScan(null, settings, mScanCallback);
  }

  private void stopScan() {
    BluetoothLeScanner scanner = mBluetoothAdapter.getBluetoothLeScanner();
    if(scanner != null) {
      scanner.stopScan(mScanCallback);
    }
  }

  /**
   * 连接
   */
  private void connect(Result result, Map<String, Object> args){
    if (args.containsKey("address")) {
      stopScan();

      String address = (String) args.get("address");
      Log.d(TAG,"start connect " + address);

      disconnect();
      pl.connect(address);

      result.success(true);
    } else {
      result.error("invalid_argument", "argument 'address' not found", null);
    }

  }

  /**
   * 重新连接回收上次连接的对象，避免内存泄漏
   */
  private boolean disconnect(){
    pl.disconnect();

    return true;
  }

  private boolean destroy() {
    DeviceConnFactoryManager.closeAllPort();
    if (threadPool != null) {
      threadPool.stopThreadPool();
    }

    return true;
  }

  private void printTest(Result result) {
    if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null ||
            !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState()) {

      result.error("not connect", "state not right", null);
    }

    threadPool = ThreadPool.getInstantiation();
    threadPool.addSerialTask(new Runnable() {
      @Override
      public void run() {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.ESC) {
          DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendByteDataImmediately(FactoryCommand.printSelfTest(FactoryCommand.printerMode.ESC));
        }else if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.TSC) {
          DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendByteDataImmediately(FactoryCommand.printSelfTest(FactoryCommand.printerMode.TSC));
        }else if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.CPCL) {
          DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendByteDataImmediately(FactoryCommand.printSelfTest(FactoryCommand.printerMode.CPCL));
        }
      }
    });

  }

  @SuppressWarnings("unchecked")
  private void print(Result result, Map<String, Object> args) {

    if (args.containsKey("config") && args.containsKey("data")) {
      final Map<String,Object> config = (Map<String,Object>)args.get("config");
      final List<Map<String,Object>> list = (List<Map<String,Object>>)args.get("data");
      if(list == null){
        return;
      }

      Log.d(TAG, "print esc begin");

      pl.write(PrintContent.convertVectorByteToBytes(PrintContent.mapToReceipt(config, list)));
    }else{
      result.error("please add config or data", "", null);
    }

  }

  @Override
  public boolean onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

    if (requestCode == REQUEST_COARSE_LOCATION_PERMISSIONS) {
      if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        startScan(pendingCall, pendingResult);
      } else {
        pendingResult.error("no_permissions", "this plugin requires location permissions for scanning", null);
        pendingResult = null;
      }
      return true;
    }
    return false;

  }



  private final StreamHandler stateStreamHandler = new StreamHandler() {
    private EventSink sink;

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
        Log.d(TAG, "stateStreamHandler, current action: " + action);

        if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
          threadPool = null;
          sink.success(intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, -1));
        } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
          sink.success(1);
        } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
          threadPool = null;
          sink.success(0);
        }
      }
    };

    @Override
    public void onListen(Object o, EventSink eventSink) {
      sink = eventSink;
      IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
      filter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
      filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
      filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
      activity.registerReceiver(mReceiver, filter);
    }

    @Override
    public void onCancel(Object o) {
      sink = null;
      activity.unregisterReceiver(mReceiver);
    }
  };

}
