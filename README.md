[![pub package](https://img.shields.io/pub/v/bluetooth_print.svg)](https://pub.dartlang.org/packages/bluetooth_print)


## Introduction

BluetoothPrint is a bluetooth plugin for [Flutter](http://www.flutter.io), a new mobile SDK to help developers build bluetooth thermal printer apps for both iOS and Android.(for example, Gprinter pt-280、pt-380、gp-1324、gp-2120 eg.)



## Features

ios features maybe finished before this month end(eg. 2019/12/30)       

|                         |      Android       |         iOS          |             Description            |
| :---------------        | :----------------: | :------------------: |  :-------------------------------- |
| scan                    | :white_check_mark: |  :white_check_mark:  | Starts a scan for Bluetooth Low Energy devices. |
| connect                 | :white_check_mark: |  :white_check_mark:  | Establishes a connection to the device. |
| disconnect              | :white_check_mark: |  :white_check_mark:  | Cancels an active or pending connection to the device. |
| state                   | :white_check_mark: |  :white_check_mark:  | Stream of state changes for the Bluetooth Device. |
| print test message      | :white_check_mark: |  :white_check_mark:  | print device test message. |
| print text              | :white_check_mark: |  :white_check_mark:  | print custom text, support layout. |
| print image             | :white_check_mark: |  :white_check_mark:  | print image. |
| print qrcode            | :white_check_mark: |  :white_check_mark:  | print qrcode,support change size. |
| print barcode           | :white_check_mark: |  :white_check_mark:  | print barcode |


## Usage

[Example](https://github.com/thon-ju/bluetooth_print/blob/master/example/lib/main.dart)

To use this plugin :

- add the dependency to your [pubspec.yaml](https://github.com/thon-ju/bluetooth_print/blob/master/example/pubspec.yaml) file.

```yaml
  dependencies:
    flutter:
      sdk: flutter
    bluetooth_print:
```

- init a BluetoothPrint instance

```dart
import 'package:bluetooth_print/bluetooth_print.dart';
import 'package:bluetooth_print/bluetooth_print_model.dart';


BluetoothPrint bluetoothPrint = BluetoothPrint.instance;
```

### scan
```dart
// begin scan
bluetoothPrint.startScan(timeout: Duration(seconds: 4));

// get devices
StreamBuilder<List<BluetoothDevice>>(
    stream: bluetoothPrint.scanResults,
    initialData: [],
    builder: (c, snapshot) => Column(
      children: snapshot.data.map((d) => ListTile(
        title: Text(d.name??''),
        subtitle: Text(d.address),
        onTap: () async {
          setState(() {
            _device = d;
          });
        },
        trailing: _device!=null && _device.address == d.address?Icon(
          Icons.check,
          color: Colors.green,
        ):null,
      )).toList(),
    ),
  ),
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

## Troubleshooting
#### ios import third party library
[Please Read link: https://www.jianshu.com/p/a8a05ab9b895](https://www.jianshu.com/p/a8a05ab9b895) 

#### error:'State restoration of CBCentralManager is only allowed for applications that have specified the "bluetooth-central" background mode'    
info.plist add:
```
<key>NSBluetoothAlwaysUsageDescription</key>
<string>Allow App use bluetooth?</string>
<key>NSBluetoothPeripheralUsageDescription</key>
<string>Allow App use bluetooth?</string>
<key>UIBackgroundModes</key>
<array>
    <string>bluetooth-central</string>
    <string>bluetooth-peripheral</string>
</array>
```

## Thanks For
- [flutter_blue](https://github.com/pauldemarco/flutter_blue)