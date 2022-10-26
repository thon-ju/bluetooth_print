[![pub package](https://img.shields.io/pub/v/bluetooth_print.svg)](https://pub.dartlang.org/packages/bluetooth_print)


## Introduction

BluetoothPrint is a bluetooth plugin for [Flutter](https://flutter.dev), a new mobile SDK to help developers build bluetooth thermal printer apps for both iOS and Android.(for example, Gprinter pt-280、pt-380、gp-1324、gp-2120 eg.)

### Underway(please suggest)
[ ] print x,y positions  
[ ] set paper size  
[ ] more print examples

### verison
4.0.0（flutter 3.x）    
3.0.0（flutter 2.x）      
2.0.0（flutter 1.12）       
1.2.0（flutter 1.9） 

## Features

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

### To use this plugin :

- add the dependency to your [pubspec.yaml](https://github.com/thon-ju/bluetooth_print/blob/master/example/pubspec.yaml) file.

```yaml
  dependencies:
    flutter:
      sdk: flutter
    bluetooth_print:
```

### Add permissions for Bluetooth
We need to add the permission to use Bluetooth and access location:

#### **Android**
In the **android/app/src/main/AndroidManifest.xml** let’s add:

```xml 
	 <uses-permission android:name="android.permission.BLUETOOTH" />  
	 <uses-permission android:name="android.permission.BLUETOOTH_SCAN" />
     <uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
	 <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />  
	 <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"/>  
 <application
```
#### **IOS**
In the **ios/Runner/Info.plist** let’s add:

```dart 
	<dict>  
	    <key>NSBluetoothAlwaysUsageDescription</key>  
	    <string>Need BLE permission</string>  
	    <key>NSBluetoothPeripheralUsageDescription</key>  
	    <string>Need BLE permission</string>  
	    <key>NSLocationAlwaysAndWhenInUseUsageDescription</key>  
	    <string>Need Location permission</string>  
	    <key>NSLocationAlwaysUsageDescription</key>  
	    <string>Need Location permission</string>  
	    <key>NSLocationWhenInUseUsageDescription</key>  
	    <string>Need Location permission</string>
```

For location permissions on iOS see more at: [https://developer.apple.com/documentation/corelocation/requesting_authorization_for_location_services](https://developer.apple.com/documentation/corelocation/requesting_authorization_for_location_services)

### init a BluetoothPrint instance

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
# bluetooth_print

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
A new flutter plugin project.

### print (esc command, receipt mode)
```dart
    Map<String, dynamic> config = Map();
    List<LineText> list = List();
    list.add(LineText(type: LineText.TYPE_TEXT, content: 'A Title', weight: 1, align: LineText.ALIGN_CENTER,linefeed: 1));
    list.add(LineText(type: LineText.TYPE_TEXT, content: 'this is conent left', weight: 0, align: LineText.ALIGN_LEFT,linefeed: 1));
    list.add(LineText(type: LineText.TYPE_TEXT, content: 'this is conent right', align: LineText.ALIGN_RIGHT,linefeed: 1));
    list.add(LineText(linefeed: 1));
    list.add(LineText(type: LineText.TYPE_BARCODE, content: 'A12312112', size:10, align: LineText.ALIGN_CENTER, linefeed: 1));
    list.add(LineText(linefeed: 1));
    list.add(LineText(type: LineText.TYPE_QRCODE, content: 'qrcode i', size:10, align: LineText.ALIGN_CENTER, linefeed: 1));
    list.add(LineText(linefeed: 1));
## Getting Started

    ByteData data = await rootBundle.load("assets/images/guide3.png");
    List<int> imageBytes = data.buffer.asUint8List(data.offsetInBytes, data.lengthInBytes);
    String base64Image = base64Encode(imageBytes);
    list.add(LineText(type: LineText.TYPE_IMAGE, content: base64Image, align: LineText.ALIGN_CENTER, linefeed: 1));
This project is a starting point for a Flutter
[plug-in package](https://flutter.dev/developing-packages/),
a specialized package that includes platform-specific implementation code for
Android and/or iOS.

    await bluetoothPrint.printReceipt(config, list);
```
For help getting started with Flutter, view our 
[online documentation](https://flutter.dev/docs), which offers tutorials, 
samples, guidance on mobile development, and a full API reference.

### print (tsc command, label mode)
```dart
    Map<String, dynamic> config = Map();
    config['width'] = 40; // 标签宽度，单位mm
    config['height'] = 70; // 标签高度，单位mm
    config['gap'] = 2; // 标签间隔，单位mm

    // x、y坐标位置，单位dpi，1mm=8dpi
    List<LineText> list = List();
    list.add(LineText(type: LineText.TYPE_TEXT, x:10, y:10, content: 'A Title'));
    list.add(LineText(type: LineText.TYPE_TEXT, x:10, y:40, content: 'this is content'));
    list.add(LineText(type: LineText.TYPE_QRCODE, x:10, y:70, content: 'qrcode i\n'));
    list.add(LineText(type: LineText.TYPE_BARCODE, x:10, y:190, content: 'qrcode i\n'));

    List<LineText> list1 = List();
    ByteData data = await rootBundle.load("assets/images/guide3.png");
    List<int> imageBytes = data.buffer.asUint8List(data.offsetInBytes, data.lengthInBytes);
    String base64Image = base64Encode(imageBytes);
    list1.add(LineText(type: LineText.TYPE_IMAGE, x:10, y:10, content: base64Image,));

    await bluetoothPrint.printLabel(config, list);
    await bluetoothPrint.printLabel(config, list1);
```


## Troubleshooting
#### ios import third party library
[Please Read link: https://stackoverflow.com/questions/19189463/cocoapods-podspec-issue)         
*.podspec add:
```
# .a filename must begin with lib, eg. 'libXXX.a'
s.vendored_libraries = '**/*.a'
```

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

## FAQ Support
you can join this [QQ](https://im.qq.com/index.shtml) group, feedback your problem  
<img src="assets/bluetooth_print.png">

## Thanks For
- [flutter_blue](https://github.com/pauldemarco/flutter_blue)
