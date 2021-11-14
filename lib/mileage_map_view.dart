import 'dart:io';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:mileage_tencent/mileage_tencent.dart';

class MileageMapView extends StatefulWidget {
  const MileageMapView(
      {Key? key, this.onPlatformViewCreated, this.interval = 1000})
      : assert(interval >= 1000),
        super(key: key);

  @override
  State<StatefulWidget> createState() => _MileageMapViewState();

  final void Function(FlutterMileageTencent flutterMileageTencent)?
      onPlatformViewCreated;

  final int interval;
}

class _MileageMapViewState extends State<MileageMapView> {
  @override
  Widget build(BuildContext context) {
    if (Platform.isAndroid) {
      return AndroidView(
          viewType: 'com.mileage.map.mileage_tencent/map_view',
          layoutDirection: TextDirection.ltr,
          onPlatformViewCreated: _onPlatformViewCreated,
          creationParams: widget.interval,
          creationParamsCodec: const StandardMessageCodec());
    }

    return const SizedBox();
  }

  void _onPlatformViewCreated(int id) {
    if (widget.onPlatformViewCreated != null) {
      widget.onPlatformViewCreated!(FlutterMileageTencent(id));
    }
  }
}
