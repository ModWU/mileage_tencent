import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class MileageMapView extends StatefulWidget {
  const MileageMapView({Key? key}) : super(key: key);

  @override
  State<StatefulWidget> createState() => _MileageMapViewState();

}

class _MileageMapViewState extends State<MileageMapView> {
  @override
  Widget build(BuildContext context) {
    if (Platform.isAndroid) {
      return AndroidView(
        viewType: 'com.mileage.map.mileage_tencent/map_view',
        layoutDirection: TextDirection.ltr,
          onPlatformViewCreated: _onPlatformViewCreated,
        creationParamsCodec: const StandardMessageCodec());
    }

    return const SizedBox();
  }

  void _onPlatformViewCreated(int id) {

  }

}