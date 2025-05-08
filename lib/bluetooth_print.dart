import 'dart:async';

import 'package:flutter/services.dart';
import 'package:rxdart/rxdart.dart';

import 'bluetooth_print_model.dart';

class BluetoothPrint {
  static const String NAMESPACE = 'bluetooth_print';
  static const int CONNECTED = 1;
  static const int DISCONNECTED = 0;

  static const MethodChannel _channel = MethodChannel('$NAMESPACE/methods');
  static const EventChannel _stateChannel = EventChannel('$NAMESPACE/state');

  Stream<MethodCall> get _methodStream => _methodStreamController.stream;
  final StreamController<MethodCall> _methodStreamController = StreamController.broadcast();

  BluetoothPrint._() {
    _channel.setMethodCallHandler((MethodCall call) async {
      _methodStreamController.add(call);
      return;
    });
  }

  static final BluetoothPrint _instance = BluetoothPrint._();
  static BluetoothPrint get instance => _instance;

  Future<bool> get isAvailable async {
    try {
      return await _channel.invokeMethod<bool>('isAvailable') ?? false;
    } on PlatformException catch (e) {
      throw BluetoothException('Failed to check availability: ${e.message}');
    }
  }

  Future<bool> get isOn async {
    try {
      return await _channel.invokeMethod<bool>('isOn') ?? false;
    } on PlatformException catch (e) {
      throw BluetoothException('Failed to check bluetooth state: ${e.message}');
    }
  }

  Future<bool> get isConnected async {
    try {
      return await _channel.invokeMethod<bool>('isConnected') ?? false;
    } on PlatformException catch (e) {
      throw BluetoothException('Failed to check connection: ${e.message}');
    }
  }

  final BehaviorSubject<bool> _isScanning = BehaviorSubject.seeded(false);
  Stream<bool> get isScanning => _isScanning.stream;

  final BehaviorSubject<List<BluetoothDevice>> _scanResults = BehaviorSubject.seeded([]);
  Stream<List<BluetoothDevice>> get scanResults => _scanResults.stream;

  final PublishSubject<void> _stopScanPill = PublishSubject();

  Stream<int> get state async* {
    try {
      final initialState = await _channel.invokeMethod<int>('state') ?? DISCONNECTED;
      yield initialState;

      yield* _stateChannel.receiveBroadcastStream()
          .map((dynamic s) => s as int)
          .handleError((error) {
            throw BluetoothException('Error receiving state updates: $error');
          });
    } on PlatformException catch (e) {
      throw BluetoothException('Failed to get bluetooth state: ${e.message}');
    }
  }

  Stream<BluetoothDevice> scan({Duration? timeout}) async* {
    if (_isScanning.value) {
      throw BluetoothException('Another scan is already in progress.');
    }

    _isScanning.add(true);

    final killStreams = <Stream>[_stopScanPill];
    if (timeout != null) {
      killStreams.add(Rx.timer<void>(null, timeout));
    }

    _scanResults.add([]);

    try {
      await _channel.invokeMethod('startScan');
    } on PlatformException catch (e) {
      _stopScanPill.add(null);
      _isScanning.add(false);
      throw BluetoothException('Failed to start scan: ${e.message}');
    }

    yield* BluetoothPrint.instance._methodStream
        .where((m) => m.method == "ScanResult")
        .map((m) => m.arguments as Map<dynamic, dynamic>)
        .takeUntil(Rx.merge(killStreams))
        .doOnDone(stopScan)
        .map((map) {
          try {
            final device = BluetoothDevice.fromJson(Map<String, dynamic>.from(map));
            final List<BluetoothDevice> list = _scanResults.value;
            final existingIndex = list.indexWhere((e) => e.address == device.address);

            if (existingIndex != -1) {
              list[existingIndex] = device;
            } else {
              list.add(device);
            }
            _scanResults.add(list);
            return device;
          } catch (e) {
            throw BluetoothException('Failed to parse scan result: $e');
          }
        });
  }

  Future<List<BluetoothDevice>> startScan({Duration? timeout}) async {
    try {
      await scan(timeout: timeout).drain();
      return _scanResults.value;
    } catch (e) {
      throw BluetoothException('Scan failed: $e');
    }
  }

  Future<void> stopScan() async {
    try {
      await _channel.invokeMethod('stopScan');
    } on PlatformException catch (e) {
      throw BluetoothException('Failed to stop scan: ${e.message}');
    } finally {
      _stopScanPill.add(null);
      _isScanning.add(false);
    }
  }

  Future<void> connect(BluetoothDevice device) async {
    try {
      await _channel.invokeMethod('connect', device.toJson());
    } on PlatformException catch (e) {
      throw BluetoothException('Failed to connect: ${e.message}');
    }
  }

  Future<void> disconnect() async {
    try {
      await _channel.invokeMethod('disconnect');
    } on PlatformException catch (e) {
      throw BluetoothException('Failed to disconnect: ${e.message}');
    }
  }

  Future<void> destroy() async {
    try {
      await _channel.invokeMethod('destroy');
    } on PlatformException catch (e) {
      throw BluetoothException('Failed to destroy: ${e.message}');
    }
  }

  Future<bool> printReceipt(Map<String, dynamic> config, List<LineText> data) async {
    try {
      final Map<String, Object> args = {
        'config': config,
        'data': data.map((m) => m.toJson()).toList(),
      };

      await _channel.invokeMethod('printReceipt', args);
      return true;
    } catch (e) {
      throw BluetoothException('Failed to print receipt: $e');
    }
  }

  Future<bool> printLabel(Map<String, dynamic> config, List<LineText> data) async {
    try {
      final Map<String, Object> args = {
        'config': config,
        'data': data.map((m) => m.toJson()).toList(),
      };

      await _channel.invokeMethod('printLabel', args);
      return true;
    } on PlatformException catch (e) {
      throw BluetoothException('Failed to print label: ${e.message}');
    }
  }

  Future<void> printTest() async {
    try {
      await _channel.invokeMethod('printTest');
    } on PlatformException catch (e) {
      throw BluetoothException('Failed to print test: ${e.message}');
    }
  }
}

class BluetoothException implements Exception {
  final String message;
  BluetoothException(this.message);

  @override
  String toString() => 'BluetoothException: $message';
}
