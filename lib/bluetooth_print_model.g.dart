// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'bluetooth_print_model.dart';

// **************************************************************************
// JsonSerializableGenerator
// **************************************************************************

BluetoothDevice _$BluetoothDeviceFromJson(Map<String, dynamic> json) {
  return BluetoothDevice()
    ..name = json['name'] as String?
    ..address = json['address'] as String?
    ..type = json['type'] as int?
    ..connected = json['connected'] as bool?;
}

Map<String, dynamic> _$BluetoothDeviceToJson(BluetoothDevice instance) {
  final val = <String, dynamic>{};

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('name', instance.name);
  writeNotNull('address', instance.address);
  writeNotNull('type', instance.type);
  writeNotNull('connected', instance.connected);
  return val;
}

LineText _$LineTextFromJson(Map<String, dynamic> json) {
  return LineText(
    type: json['type'] as String?,
    content: json['content'] as String?,
    size: json['size'] as int?,
    align: json['align'] as int?,
    weight: json['weight'] as int?,
    width: json['width'] as int?,
    height: json['height'] as int?,
    absolutePos: json['absolutePos'] as int?,
    relativePos: json['relativePos'] as int?,
    fontZoom: json['fontZoom'] as int?,
    underline: json['underline'] as int?,
    linefeed: json['linefeed'] as int?,
    x: json['x'] as int?,
    y: json['y'] as int?,
  );
}

Map<String, dynamic> _$LineTextToJson(LineText instance) {
  final val = <String, dynamic>{};

  void writeNotNull(String key, dynamic value) {
    if (value != null) {
      val[key] = value;
    }
  }

  writeNotNull('type', instance.type);
  writeNotNull('content', instance.content);
  writeNotNull('size', instance.size);
  writeNotNull('align', instance.align);
  writeNotNull('weight', instance.weight);
  writeNotNull('width', instance.width);
  writeNotNull('height', instance.height);
  writeNotNull('absolutePos', instance.absolutePos);
  writeNotNull('relativePos', instance.relativePos);
  writeNotNull('fontZoom', instance.fontZoom);
  writeNotNull('underline', instance.underline);
  writeNotNull('linefeed', instance.linefeed);
  writeNotNull('x', instance.x);
  writeNotNull('y', instance.y);
  return val;
}
