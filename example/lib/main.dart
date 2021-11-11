import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:mileage_tencent/mileage_map_view.dart';
import 'package:mileage_tencent/mileage_tencent.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  FlutterMileageTencent? _flutterMileageTencent;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    // We also handle the message potentially returning null.
    try {
      platformVersion =
          await MileageTencent.platformVersion ?? 'Unknown platform version';
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  void _onPlatformViewCreated(FlutterMileageTencent flutterMileageTencent) {
    flutterMileageTencent.setListener((CallbackType type, dynamic arguments) {
      if (type == CallbackType.activate) {
        print("_onPlatformViewCreated CallbackType.activate => ${arguments}");
        if (arguments['isSuccess'] == true) {
          _flutterMileageTencent = flutterMileageTencent;
        }
      } else if (type == CallbackType.onLocationChanged) {
        print(
            "_onPlatformViewCreated CallbackType.onLocationChanged => $arguments");
      }
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: Text(_platformVersion),
        ),
        body: Center(
          child: Column(
            children: [
              Expanded(
                  child: MileageMapView(
                      onPlatformViewCreated: _onPlatformViewCreated)),
              GestureDetector(
                onTap: () async {
                  if (_flutterMileageTencent == null) {
                    print(
                        "_onPlatformViewCreated 没有激活");
                    return;
                  }
                  final bool isHasMap = await _flutterMileageTencent!.isAvailable('com.tencent.map');
                  print("腾讯地图: $isHasMap");
                },
                child: Text("腾讯地图"),
              ),
              GestureDetector(
                onTap: () async {
                  if (_flutterMileageTencent == null) {
                    print(
                        "_onPlatformViewCreated 没有激活");
                    return;
                  }
                  final bool isHasMap = await _flutterMileageTencent!.isAvailable('com.autonavi.minimap');
                  print("高德地图: $isHasMap");
                },
                child: Text("高德地图"),
              ),
              GestureDetector(
                onTap: () async {
                  if (_flutterMileageTencent == null) {
                    print(
                        "_onPlatformViewCreated 没有激活");
                    return;
                  }
                  final bool isHasMap = await _flutterMileageTencent!.isAvailable('com.baidu.BaiduMap');
                  print("百度地图：$isHasMap");
                },
                child: Text("百度地图"),
              ),
              GestureDetector(
                onTap: () async {
                  if (_flutterMileageTencent == null) {
                    print(
                        "_onPlatformViewCreated 没有激活");
                    return;
                  }
                  print("跳到腾讯地图");//qqmap://map/routeplan?type=drive&to=我的终点&tocoord=" + mLatitude + "," + mLongitude
                  _flutterMileageTencent!.jumpToMapByData('qqmap://map/routeplan?type=drive&to=我的终点&tocoord=31.307215,121.541367');
                },
                child: Text("跳到腾讯地图"),
              ),
              GestureDetector(
                onTap: () async {
                  if (_flutterMileageTencent == null) {
                    print(
                        "_onPlatformViewCreated 没有激活");
                    return;
                  }
                  print("跳到百度地图");
                  _flutterMileageTencent!.jumpToMapByIntent(
                      'intent://map/direction?'
                          'destination=latlng:31.307215,121.541367'
                          '|name:我的终点'
                          '&mode=driving&'
                          '&src=appname#Intent;scheme=bdapp;package=com.baidu.BaiduMap;end'
                  );
                },
                child: Text("跳到百度地图"),
              ),
              GestureDetector(
                onTap: () async {
                  if (_flutterMileageTencent == null) {
                    print(
                        "_onPlatformViewCreated 没有激活");
                    return;
                  }/*androidamap://navi?sourceApplication=appname&poiname=fangheng&lat=" + mLatitude + "&lon=" + mLongitude + "&dev=1&style=2*/
                  print("跳到高德地图");
                  _flutterMileageTencent!.jumpToMapByData('androidamap://navi?sourceApplication=appname&poiname=fangheng&lat=31.307215&lon=121.541367&dev=1&style=2');
                },
                child: Text("跳到高德地图"),
              ),
            ],
          ),
        ),
      ),
    );
  }
}
