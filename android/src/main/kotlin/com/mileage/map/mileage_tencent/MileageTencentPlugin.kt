package com.mileage.map.mileage_tencent

import android.os.Build
import androidx.annotation.NonNull

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import com.gv.livechat.flutter_live_chat.PluginContext
import io.flutter.plugin.common.PluginRegistry
import pub.devrel.easypermissions.EasyPermissions


/** MileageTencentPlugin */
class MileageTencentPlugin: FlutterPlugin, MethodCallHandler, ActivityAware/*, PluginRegistry.RequestPermissionsResultListener*/ {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "com.mileage.map.mileage_tencent/mileage_tencent")
    channel.setMethodCallHandler(this)

    flutterPluginBinding.platformViewRegistry.registerViewFactory("com.mileage.map.mileage_tencent/map_view", MapViewFactory(flutterPluginBinding.binaryMessenger))
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    if (call.method == "getPlatformVersion") {
      result.success("Android ${android.os.Build.VERSION.RELEASE}")
    } else {
      result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }

  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    PluginContext.activityPluginBinding = binding

//    binding.addRequestPermissionsResultListener(this)

//    /*if (Build.VERSION.SDK_INT >= 23) {
//      PermissionUtils.requirePermission(binding.activity)
//    }*/
  }

  override fun onDetachedFromActivityForConfigChanges() {
    TODO("Not yet implemented")
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    TODO("Not yet implemented")
  }

  override fun onDetachedFromActivity() {
    //PluginContext.activityPluginBinding?.removeRequestPermissionsResultListener(this)
    PluginContext.activityPluginBinding = null
  }

  /*override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?): Boolean {
    var isHandled = false
    if (grantResults != null && permissions != null) {
      EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
      isHandled = true
    }
    return isHandled
  }*/
}
