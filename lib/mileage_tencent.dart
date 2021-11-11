import 'dart:async';

import 'package:flutter/services.dart';

class MileageTencent {
  static const MethodChannel _channel =
      MethodChannel('com.mileage.map.mileage_tencent/mileage_tencent');

  static Future<String?> get platformVersion async {
    final String? version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }
}

enum CallbackType { activate, onLocationChanged }

typedef LiveChatCallback = void Function(CallbackType type, dynamic arguments);

class FlutterMileageTencent {
  FlutterMileageTencent(this.viewId);

  final int viewId;

  static const String viewType = 'com.mileage.map.mileage_tencent/map_view';

  late final MethodChannel channel =
      MethodChannel('com.mileage.map.mileage_tencent/map_view_$viewId');

  Future<bool> isAvailable(String packageName) async {
    return await channel.invokeMethod('isAvailable', packageName);
  }

  Future<bool> jumpToMapByData(String uri) async {
    return await channel.invokeMethod('jumpToMapByData', uri);
  }

  Future<bool> jumpToMapByIntent(String uri) async {
    return await channel.invokeMethod('jumpToMapByIntent', uri);
  }

  void setListener(LiveChatCallback callback) {
    channel.setMethodCallHandler((MethodCall call) async {
      switch (call.method) {
        case 'onActivate':
          callback(CallbackType.activate, call.arguments);
          break;
        case 'onLocationChanged':
          callback(CallbackType.onLocationChanged, call.arguments);
          break;
      }
    });
  }
}
