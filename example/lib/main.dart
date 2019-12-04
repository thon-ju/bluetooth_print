
import 'dart:convert';
import 'dart:io';

import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:bluetooth_print/bluetooth_print.dart';
import 'package:bluetooth_print/bluetooth_print_model.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  BluetoothPrint bluetoothPrint = BluetoothPrint.instance;

  List<BluetoothDevice> _list = [];
  bool _connected = false;
  BluetoothDevice _device;

  @override
  void initState() {
    super.initState();

    initBluetooth();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initBluetooth() async {
    bool isConnected=await bluetoothPrint.isConnected;
    List<BluetoothDevice> list = await bluetoothPrint.getBondedDevices();
    list.forEach((e){
      print('${e.name} ${e.address}');
    });

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

    if (!mounted) return;

    setState(() {
      _list = list;
    });

    if(isConnected) {
      setState(() {
        _connected=true;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Container(
          child: ListView(
            children: <Widget>[
              Container(
                height: 300.0,
                child: ListView.builder(
                    itemCount: _list.length,
                    itemBuilder: (context, index){
                      return ListTile(
                        title: Text('${_list[index].name}'),
                        subtitle: Text('${_list[index].address}'),
                        onTap: () async {
                          setState(() {
                            _device = _list[index];
                          });
                        },
                        trailing: _device!=null && _device.address == _list[index].address?Icon(
                          Icons.check,
                          color: Colors.green,
                        ):null,
                      );
                    }
                ),
              ),
              Container(
                padding: EdgeInsets.fromLTRB(20, 20, 20, 10),
                child: Column(
                  children: <Widget>[
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: <Widget>[
                        OutlineButton(
                          child: Text('连接'),
                          onPressed:  _connected?null:() async {
                            if(_device!=null && _device.address !=null){
                              await bluetoothPrint.connect(_device);
                            }else{
                              print('请选择设备');
                            }
                          },
                        ),
                        SizedBox(width: 10.0),
                        OutlineButton(
                          child: Text('断开'),
                          onPressed:  _connected?() async {
                            await bluetoothPrint.disconnect();
                          }:null,
                        ),
                      ],
                    ),
                    Row(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: <Widget>[
                        OutlineButton(
                          child: Text('打印'),
                          onPressed:  _connected?() async {
                            List<LineText> list = List();
                            list.add(LineText(type: LineText.TYPE_TEXT, content: '我是一个测试i', align: LineText.ALIGN_LEFT,linefeed: 0));
                            list.add(LineText(type: LineText.TYPE_TEXT, content: '在右边', align: LineText.ALIGN_RIGHT,linefeed: 0));
                            list.add(LineText(linefeed: 1));
                            list.add(LineText(type: LineText.TYPE_QRCODE, content: '我是一个测试i\n', size:10, align: LineText.ALIGN_CENTER, linefeed: 1));
                            list.add(LineText(linefeed: 1));

                            ByteData data = await rootBundle.load("assets/images/guide3.png");
                            List<int> imageBytes = data.buffer.asUint8List(data.offsetInBytes, data.lengthInBytes);
                            String base64Image = base64Encode(imageBytes);
                            list.add(LineText(type: LineText.TYPE_IMAGE, content: base64Image, align: LineText.ALIGN_CENTER, linefeed: 1));
                            await bluetoothPrint.print(list);
                          }:null,
                        ),
                        SizedBox(width: 10.0),
                        OutlineButton(
                          child: Text('打印自检'),
                          onPressed:  _connected?() async {
                            await bluetoothPrint.printTest();
                          }:null,
                        )
                      ],
                    )
                  ],
                ),
              )
            ],
          ),
        )
      ),
    );
  }
}
