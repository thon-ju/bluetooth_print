import 'package:bluetooth_print/bluetooth_print.dart';
import 'package:bluetooth_print/bluetooth_print_model.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  group('getChannelArguments', () {
    test('should return a json map of the passed-in arguments', () {
      // arrange
      final config = <String, dynamic>{'key1': 'value1'};
      final data = <LineText>[LineText(content: 'content1')];

      // act
      final actual = getChannelArguments(config, data);

      // assert
      expect(
        actual,
        <String, dynamic>{
          'config': {'key1': 'value1'},
          'data': [LineText(content: 'content1').toJson()],
        },
      );
    });
  });
}
