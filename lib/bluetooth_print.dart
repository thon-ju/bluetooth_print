import 'dart:async';

import 'package:flutter/services.dart';

class BluetoothPrint {
  static const MethodChannel _channel =
      const MethodChannel('bluetooth_print');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
