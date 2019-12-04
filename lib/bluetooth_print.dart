import 'dart:async';

import 'package:flutter/services.dart';

import 'bluetooth_device.dart';
import 'line_text.dart';

class BluetoothPrint {
  static const String NAMESPACE = 'bluetooth_print';
  static const int CONNECTED = 1;
  static const int DISCONNECTED = 0;

  static const MethodChannel _channel = const MethodChannel('$NAMESPACE/methods');
  static const EventChannel _stateChannel = const EventChannel('$NAMESPACE/state');

  final StreamController<MethodCall> _methodStreamController = new StreamController.broadcast();
  Stream<MethodCall> get _methodStream => _methodStreamController.stream;

  BluetoothPrint._() {
    _channel.setMethodCallHandler((MethodCall call) {
      _methodStreamController.add(call);
    });
  }

  static BluetoothPrint _instance = new BluetoothPrint._();

  static BluetoothPrint get instance => _instance;

  Future<bool> get isAvailable async => await _channel.invokeMethod('isAvailable').then<bool>((d) => d);

  Future<bool> get isOn async => await _channel.invokeMethod('isOn').then<bool>((d) => d);

  Future<bool> get isConnected async => await _channel.invokeMethod('isConnected');

  /// Gets the current state of the Bluetooth module
  Stream<int> get state async* {
    yield await _channel
        .invokeMethod('state')
        .then((s) => s);

    yield* _stateChannel
        .receiveBroadcastStream()
        .map((s) => s);
  }

  Future<List<BluetoothDevice>> getBondedDevices() async {
    final List list = await _channel.invokeMethod('getDevices');
    return list.map((map) => BluetoothDevice.fromMap(map)).toList();
  }



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
