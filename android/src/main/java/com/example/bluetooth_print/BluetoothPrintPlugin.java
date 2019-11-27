package com.example.bluetooth_print;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import com.gprinter.command.EscCommand;
import com.gprinter.command.FactoryCommand;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import java.util.*;

/** BluetoothPrintPlugin */
public class BluetoothPrintPlugin implements MethodCallHandler {

  private int id = 0;
  private ThreadPool threadPool;

  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "bluetooth_print");
    channel.setMethodCallHandler(new BluetoothPrintPlugin());
  }

  BluetoothPrintPlugin(){

  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    final Map<String, Object> args = call.arguments();

    switch (call.method){
      case "getPlatformVersion":
        result.success("Android " + android.os.Build.VERSION.RELEASE);
        break;
      case "getDevices":
        result.success(getDevices());
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
      case "printTest":
        printTest(result);
        break;
      default:
        result.notImplemented();
        break;
    }

  }

  private List<Map<String, Object>> getDevices(){
    List<Map<String, Object>> devices = new ArrayList<>();
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
      Map<String, Object> ret = new HashMap<>();
      ret.put("address", device.getAddress());
      ret.put("name", device.getName());
      ret.put("type", device.getType());
      devices.add(ret);
    }

    return  devices;
  }

  /**
   * 连接
   */
  private Result connect(Result result, Map<String, Object> args){
    if (args.containsKey("address")) {
      String address = (String) args.get("address");
      disconnect();

      new DeviceConnFactoryManager.Build()
              .setId(id)
              //设置连接方式
              .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.BLUETOOTH)
              //设置连接的蓝牙mac地址
              .setMacAddress(address)
              .build();
      //打开端口
      threadPool = ThreadPool.getInstantiation();
      threadPool.addSerialTask(new Runnable() {
        @Override
        public void run() {
          DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort();
        }
      });

      result.success(true);
    } else {
      result.error("invalid_argument", "argument 'address' not found", null);
    }

    return result;
  }

  /**
   * 重新连接回收上次连接的对象，避免内存泄漏
   */
  private boolean disconnect(){
    if(DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id]!=null&&DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort!=null) {
      DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].reader.cancel();
      DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort.closePort();
      DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort=null;
    }
    return true;
  }

  private boolean destroy() {
    DeviceConnFactoryManager.closeAllPort();
    if (threadPool != null) {
      threadPool.stopThreadPool();
    }

    return true;
  }

  private Result printTest(Result result) {
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
        }
      }
    });

    return result;
  }

  private Result print(Result result, Map<String, Object> args) {
    if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null ||
            !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState()) {

      result.error("not connect", "state not right", null);
    }

    if (args.containsKey("datas")) {
      final List<Map<String,Object>> list = (List)args.get("datas");

      threadPool = ThreadPool.getInstantiation();
      threadPool.addSerialTask(new Runnable() {
        @Override
        public void run() {
          if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.ESC) {
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(PrintContent.mapToReceipt(list));
          }
        }
      });
    }else{
      result.error("no datas", "", null);
    }

    return result;
  }

}
