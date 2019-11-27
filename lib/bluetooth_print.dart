import 'dart:async';

import 'package:flutter/services.dart';

import 'bluetooth_device.dart';
import 'line_text.dart';

class BluetoothPrint {
  static const MethodChannel _channel = const MethodChannel('bluetooth_print');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }


  static Future<List<BluetoothDevice>> getBondedDevices() async {
    final List list = await _channel.invokeMethod('getDevices');
    return list.map((map) => BluetoothDevice.fromMap(map)).toList();
  }

  static Future<dynamic> connect(BluetoothDevice device) => _channel.invokeMethod('connect', device.toMap());

  static Future<dynamic> disconnect() => _channel.invokeMethod('disconnect');

  static Future<dynamic> destroy() => _channel.invokeMethod('destroy');

  static Future<dynamic> print(List<LineText> list) {
    Map<String, Object> args = Map();
    args['datas'] = list.map((m){return m.toMap();}).toList();
    _channel.invokeMethod('print', args);
  }

  static Future<dynamic> printTest() => _channel.invokeMethod('printTest');


}
