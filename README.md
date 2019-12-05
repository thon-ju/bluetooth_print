[![pub package](https://img.shields.io/pub/v/bluetooth_print.svg)](https://pub.dartlang.org/packages/bluetooth_print)


## Introduction

BluetoothPrint is a bluetooth plugin for [Flutter](http://www.flutter.io), a new mobile SDK to help developers build bluetooth thermal printer apps for iOS and Android.(for example, Gprinter pt-380 eg.)



## Features
|                         |      Android       |         iOS          |             Description            |
| :---------------        | :----------------: | :------------------: |  :-------------------------------- |
| select devices          | :white_check_mark: |                      | scan for Bluetooth Low Energy devices and select. |
| connect                 | :white_check_mark: |                      | Establishes a connection to the device. |
| disconnect              | :white_check_mark: |                      | Cancels an active or pending connection to the device. |
| print test message      | :white_check_mark: |                      | print device test message. |
| print text              | :white_check_mark: |                      | print custom text, support layout. |
| print image             | :white_check_mark: |                      | print image. |
| print qrcode            | :white_check_mark: |                      | print qrcode,support change size. |
| display connect status  | :white_check_mark: |                      | Stream of state changes for the Bluetooth Device. |

## Usage

[Example](https://github.com/thon-ju/bluetooth_print/blob/master/example/lib/main.dart)

[Example](https://github.com/dlutton/flutter_tts/blob/master/example/lib/main.dart)

To use this plugin :

- add the dependency to your [pubspec.yaml](https://github.com/thon-ju/bluetooth_print/blob/master/example/pubspec.yaml) file.

```yaml
  dependencies:
    flutter:
      sdk: flutter
    bluetooth_print:
```

- instantiate a BluetoothPrint instance

```dart
import 'package:bluetooth_print/bluetooth_print.dart';
import 'package:bluetooth_print/bluetooth_print_model.dart';


BluetoothPrint bluetoothPrint = BluetoothPrint.instance;
```

### getDevices
```dart
List<BluetoothDevice> list = await bluetoothPrint.getBondedDevices();
```

### connect
```dart
await bluetoothPrint.connect(_device);
```

### disconnect
```dart
await bluetoothPrint.disconnect();
```

### listen state
```dart
      bluetoothPrint.state.listen((state) {
      print('cur device status: $state');

      switch (state) {
        case BluetoothPrint.CONNECTED:
          setState(() {
            _connected = true;
          });
          break;
        case BluetoothPrint.DISCONNECTED:
          setState(() {
            _connected = false;
          });
          break;
        default:
          break;
      }
    });
```

