
import 'dart:async';

import 'package:flutter/services.dart';

class MileageTencent {
  static const MethodChannel _channel = MethodChannel('com.mileage.map.mileage_tencent/mileage_tencent');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}
