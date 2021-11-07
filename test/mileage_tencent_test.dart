import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:mileage_tencent/mileage_tencent.dart';

void main() {
  const MethodChannel channel = MethodChannel('mileage_tencent');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await MileageTencent.platformVersion, '42');
  });
}
