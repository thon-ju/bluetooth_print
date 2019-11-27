import 'dart:async';

import 'package:flutter/services.dart';

import 'bluetooth_device.dart';
import 'line_text.dart';

class BluetoothPrint {
  static const String namespace = 'bluetooth_print';
  static const int CONNECTED = 1;
  static const int DISCONNECTED = 0;

  static const MethodChannel _channel = const MethodChannel('$namespace/methods');
  static const EventChannel _readChannel = const EventChannel('$namespace/read');
  static const EventChannel _stateChannel = const EventChannel('$namespace/state');

  final StreamController<MethodCall> _methodStreamController = new StreamController.broadcast();

  BluetoothPrint._() {
    _channel.setMethodCallHandler((MethodCall call) {
      _methodStreamController.add(call);
    });
  }

  static BluetoothPrint _instance = new BluetoothPrint._();

  static BluetoothPrint get instance => _instance;

  Stream<int> onStateChanged() =>  _stateChannel.receiveBroadcastStream().map((buffer) => buffer);

  Stream<String> onRead() => _readChannel.receiveBroadcastStream().map((buffer) => buffer.toString());


  Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  Future<List<BluetoothDevice>> getBondedDevices() async {
    final List list = await _channel.invokeMethod('getDevices');
    return list.map((map) => BluetoothDevice.fromMap(map)).toList();
  }

  Future<bool> get isAvailable async => await _channel.invokeMethod('isAvailable');

  Future<bool> get isConnected async => await _channel.invokeMethod('isConnected');

  Future<dynamic> connect(BluetoothDevice device) => _channel.invokeMethod('connect', device.toMap());

  Future<dynamic> disconnect() => _channel.invokeMethod('disconnect');

  Future<dynamic> destroy() => _channel.invokeMethod('destroy');

  Future<dynamic> print(List<LineText> list) {
    Map<String, Object> args = Map();
    args['datas'] = list.map((m){return m.toMap();}).toList();
    _channel.invokeMethod('print', args);
  }

  Future<dynamic> printTest() => _channel.invokeMethod('printTest');


}
