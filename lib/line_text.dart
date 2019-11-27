
class LineText {
  static const String TYPE_TEXT='text';
  static const String TYPE_BARCODE='barcode';
  static const String TYPE_QRCODE='qrcode';
  static const String ALIGN_LEFT='left';
  static const String ALIGN_CENTER='center';
  static const String ALIGN_RIGHT='right';

  final String type;
  final String content;
  final int size;
  final String align;
  final int weight;
  final int width;
  final int height;
  final int underline;
  final int linefeed;

  LineText({
    this.type, //text,barcode,qrcode
    this.content,
    this.size=0,
    this.align=ALIGN_LEFT,
    this.weight=0, //0,1
    this.width=0, //0,1
    this.height=0, //0,1
    this.underline=0, //0,1
    this.linefeed=0, //0,1
  });

  LineText.fromMap(Map map)
      : type = map['type'],
        content = map['content'],
        size = map['size'],
        align = map['align'],
        weight = map['weight'],
        width = map['width'],
        height = map['height'],
        underline = map['underline'],
        linefeed = map['linefeed'];

  Map<String, dynamic> toMap() => {
    'type': this.type,
    'content': this.content,
    'size': this.size,
    'align': this.align,
    'weight': this.weight,
    'width': this.width,
    'height': this.height,
    'underline': this.underline,
    'linefeed': this.linefeed,
  };

}
