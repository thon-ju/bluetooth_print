// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bluetooth_print_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BluetoothDevice _$BluetoothDeviceFromJson(Map<String, dynamic> json) =>
    BluetoothDevice(
      name: json['name'] as String?,
      address: json['address'] as String?,
      type: (json['type'] as num?)?.toInt() ?? 0,
      connected: json['connected'] as bool? ?? false,
    );

Map<String, dynamic> _$BluetoothDeviceToJson(BluetoothDevice instance) =>
    <String, dynamic>{
      if (instance.name case final value?) 'name': value,
      if (instance.address case final value?) 'address': value,
      if (instance.type case final value?) 'type': value,
      if (instance.connected case final value?) 'connected': value,
    };

LineText _$LineTextFromJson(Map<String, dynamic> json) => LineText(
      type: json['type'] as String?,
      content: json['content'] as String?,
      size: (json['size'] as num?)?.toInt() ?? 0,
      align: (json['align'] as num?)?.toInt() ?? 0,
      weight: (json['weight'] as num?)?.toInt() ?? 0,
      width: (json['width'] as num?)?.toInt() ?? 0,
      height: (json['height'] as num?)?.toInt() ?? 0,
      absolutePos: (json['absolutePos'] as num?)?.toInt() ?? 0,
      relativePos: (json['relativePos'] as num?)?.toInt() ?? 0,
      fontZoom: (json['fontZoom'] as num?)?.toInt() ?? 1,
      underline: (json['underline'] as num?)?.toInt() ?? 0,
      linefeed: (json['linefeed'] as num?)?.toInt() ?? 0,
      x: (json['x'] as num?)?.toInt() ?? 0,
      y: (json['y'] as num?)?.toInt() ?? 0,
    );

Map<String, dynamic> _$LineTextToJson(LineText instance) => <String, dynamic>{
      if (instance.type case final value?) 'type': value,
      if (instance.content case final value?) 'content': value,
      if (instance.size case final value?) 'size': value,
      if (instance.align case final value?) 'align': value,
      if (instance.weight case final value?) 'weight': value,
      if (instance.width case final value?) 'width': value,
      if (instance.height case final value?) 'height': value,
      if (instance.absolutePos case final value?) 'absolutePos': value,
      if (instance.relativePos case final value?) 'relativePos': value,
      if (instance.fontZoom case final value?) 'fontZoom': value,
      if (instance.underline case final value?) 'underline': value,
      if (instance.linefeed case final value?) 'linefeed': value,
      if (instance.x case final value?) 'x': value,
      if (instance.y case final value?) 'y': value,
    };
