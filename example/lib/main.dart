import 'package:bluetooth_print/bluetooth_device.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:bluetooth_print/bluetooth_print.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  List<BluetoothDevice> _list;

  @override
  void initState() {
    super.initState();
    initPlatformState();

    initBluetooth();
  }

  Future<void> initPlatformState() async {
    String platformVersion;
    try {
      platformVersion = await BluetoothPrint.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initBluetooth() async {
    List<BluetoothDevice> list = await BluetoothPrint.getBondedDevices();
    list.forEach((e){
      print('${e.name} ${e.address}');
    });

    if (!mounted) return;

    setState(() {
      _list = list;
    });

  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Container(
          height: 500.0,
          child: ListView(
            children: <Widget>[
              Center(
                child: Text('Running on: $_platformVersion\n'),
              ),
              Container(
                height: 300.0,
                child: ListView.builder(
                    itemCount: _list.length,
                    itemBuilder: (context, index){
                      return ListTile(
                        title: Text('${_list[index].name}'),
                        subtitle: Text('${_list[index].address}'),
                        onTap: () async {
                          await BluetoothPrint.connect(_list[index]);
                        },
                      );
                    }
                ),
              ),
              FlatButton(
                child: Text('打印'),
                onPressed:  () async {
                  await BluetoothPrint.print();
                },
              ),
              FlatButton(
                child: Text('打印自检'),
                onPressed:  () async {
                  await BluetoothPrint.printTest();
                },
              )
            ],
          ),
        )
      ),
    );
  }
}
