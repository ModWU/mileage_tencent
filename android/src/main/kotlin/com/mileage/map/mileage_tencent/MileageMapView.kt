package com.mileage.map.mileage_tencent

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.platform.PlatformView
import io.flutter.embedding.engine.plugins.lifecycle.FlutterLifecycleAdapter
import com.gv.livechat.flutter_live_chat.PluginContext
import com.tencent.map.geolocation.TencentLocation
import com.tencent.map.geolocation.TencentLocationListener
import com.tencent.map.geolocation.TencentLocationManager
import com.tencent.map.geolocation.TencentLocationRequest
import com.tencent.tencentmap.mapsdk.maps.*
import com.tencent.tencentmap.mapsdk.maps.model.*
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

class MileageMapView(context: Context, messenger: BinaryMessenger, viewId: Int, args: Any?) :
        PlatformView, MethodChannel.MethodCallHandler, DefaultLifecycleObserver, TencentMap.OnMapLoadedCallback, TencentLocationListener, LocationSource {

    private val context: Context = context

    private val activityPluginBinding: ActivityPluginBinding = PluginContext.activityPluginBinding as ActivityPluginBinding

    private val viewId: Int = viewId

    private var methodChannel: MethodChannel? = null

    //private var notificationManager: NotificationManager? = null

    //private var isCreateChannel: Boolean = false

    private var mapView: MapView? = null
    private var tencentMap: TencentMap

    private var tencentLocation: TencentLocation? = null
    private var locationRequest: TencentLocationRequest? = null

    private var locationManager: TencentLocationManager? = null

    private var locationChangedListener: LocationSource.OnLocationChangedListener? = null


    companion object{
        const val TAG: String = "mileage_tencent"
    }

    init {
        Log.d(TAG, "id##$viewId => MileageMapView -> init")

        methodChannel = MethodChannel(messenger, "com.mileage.map.mileage_tencent/map_view_$viewId")
        methodChannel?.setMethodCallHandler(this)

        mapView = MapView(context)
        tencentMap = mapView!!.map

        tencentMap.isMyLocationEnabled = true;

        val uiSettings: UiSettings  = tencentMap.uiSettings;
        uiSettings.isCompassEnabled = true
        uiSettings.isMyLocationButtonEnabled = true
        //uiSettings.isZoomGesturesEnabled = true
        //uiSettings.isRotateGesturesEnabled = true
        uiSettings.setAllGesturesEnabled(true)

        val myLocationStyle  = MyLocationStyle()
        myLocationStyle.anchor(0.5f, 0.5f)
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
        tencentMap.setMyLocationStyle(myLocationStyle)

        tencentMap.mapType = TencentMap.MAP_TYPE_NORMAL

        tencentMap.setBuilding3dEffectEnable(false)

       /* val cameraSigma = CameraUpdateFactory.newCameraPosition(CameraPosition(
                LatLng(39.977290, 116.337000),  //中心点坐标，地图目标经纬度
                19F,  //目标缩放级别
                15f,  //目标倾斜角[0.0 ~ 45.0] (垂直地图时为0)
                45f)) //目标旋转角 0~360° (正北方为0)

        tencentMap.moveCamera(cameraSigma)*/

        initLocation()

        val lifecycle: Lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(activityPluginBinding)
        lifecycle.addObserver(this)

        tencentMap.addOnMapLoadedCallback(this)
        tencentMap.setLocationSource(this)

        //val cameraUpdate = CameraUpdateFactory.zoomTo(9f)
        //tencentMap.moveCamera(cameraUpdate)
    }

    /**
     * 定位的一些初始化设置
     */
    private fun initLocation() {
        //用于访问腾讯定位服务的类, 周期性向客户端提供位置更新
        locationManager = TencentLocationManager.getInstance(context)
        //创建定位请求
        locationRequest = TencentLocationRequest.create()
        locationRequest!!.isAllowDirection = true
        //设置定位周期（位置监听器回调周期）为3s
        locationRequest!!.interval = 1000 * 60
    }

    /*private fun buildNotification(): Notification? {
        var builder: Notification.Builder? = null
        var notification: Notification? = null
        if (Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (notificationManager == null) {
                notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
            }
            val channelId: String = context.packageName
            if (!isCreateChannel) {
                val notificationChannel = NotificationChannel(channelId,
                        "Location", NotificationManager.IMPORTANCE_DEFAULT)
                notificationChannel.enableLights(true) //是否在桌面icon右上角展示小圆点
                notificationChannel.lightColor = Color.BLUE //小圆点颜色
                notificationChannel.setShowBadge(true) //是否在久按桌面图标时显示此渠道的通知
                notificationManager!!.createNotificationChannel(notificationChannel)
                isCreateChannel = true
            }
            builder = Notification.Builder(context.applicationContext, channelId)
        } else {
            builder = Notification.Builder(context.applicationContext)
        }
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("LocationDemo")
                .setContentText("正在后台运行")
                .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.mipmap.ic_launcher))
                .setWhen(System.currentTimeMillis())
        notification = if (Build.VERSION.SDK_INT >= 16) {
            builder.build()
        } else {
            builder.notification
        }
        return notification
    }*/

    override fun getView(): View {
        return mapView as MapView
    }

    override fun dispose() {
        locationManager?.removeUpdates(this)
        tencentMap.setLocationSource(null)
        tencentMap.removeOnMapLoadedCallback(this)
        mapView?.onDestroy()

        methodChannel?.setMethodCallHandler(null)

        val lifecycle = FlutterLifecycleAdapter.getActivityLifecycle(activityPluginBinding)
        lifecycle.removeObserver(this)

        methodChannel = null
        mapView = null

        locationManager = null
        locationRequest = null
        locationChangedListener = null
    }

    override fun onStart(owner: LifecycleOwner) {
        Log.d(TAG, "id##$viewId => MileageMapView -> onStart")
        mapView?.onStart()
        super.onStart(owner)
    }

    override fun onResume(owner: LifecycleOwner) {
        Log.d(TAG, "id##$viewId => MileageMapView -> onResume")
        mapView?.onResume()
        super.onResume(owner)
    }

    override fun onPause(owner: LifecycleOwner) {
        Log.d(TAG, "id##$viewId => MileageMapView -> onPause")
        mapView?.onPause()
        super.onPause(owner)
    }

    override fun onStop(owner: LifecycleOwner) {
        Log.d(TAG, "id##$viewId => MileageMapView -> onStop")
        mapView?.onStop()
        super.onStop(owner)
    }

    override fun onDestroy(owner: LifecycleOwner) {
        Log.d(TAG, "id##$viewId => MileageMapView -> onDestroy")
        mapView?.onDestroy()
        super.onDestroy(owner)
    }

   /* @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun onAny(source: LifecycleOwner?, event: Lifecycle.Event?) {
        when(event) {
            Lifecycle.Event.ON_START -> mapView?.onStart()
            Lifecycle.Event.ON_RESUME -> mapView?.onResume()
            Lifecycle.Event.ON_STOP -> mapView?.onStop()
            Lifecycle.Event.ON_PAUSE -> mapView?.onPause()
            Lifecycle.Event.ON_DESTROY -> mapView?.onDestroy()
        }
    }*/

    override fun onMapLoaded() {
        Log.d(TAG, "id##$viewId => MileageMapView -> onMapLoaded")
    }

    override fun onLocationChanged(tencentLocation: TencentLocation?, error: Int, reason: String?) {
        Log.d(TAG, "id##$viewId => MileageMapView -> onLocationChanged -> location: $tencentLocation, error: $error, reason: $reason")
        if (error === TencentLocation.ERROR_OK && locationChangedListener != null) {
            val location = Location(tencentLocation!!.provider)
            //设置经纬度
            location.latitude = tencentLocation.latitude
            location.longitude = tencentLocation.longitude
            //设置精度，这个值会被设置为定位点上表示精度的圆形半径
            location.accuracy = tencentLocation.accuracy
            //设置定位标的旋转角度，注意 tencentLocation.getBearing() 只有在 gps 时才有可能获取
            location.bearing = tencentLocation.bearing as Float
            //将位置信息返回给地图
            locationChangedListener!!.onLocationChanged(location)
            methodChannel!!.invokeMethod("onLocationChanged", mapOf(
                    "latitude" to tencentLocation.latitude,
                    "longitude" to tencentLocation.longitude
                    ))
        }
    }

    override fun onStatusUpdate(name: String?, status: Int, desc: String?) {
        Log.d(TAG, "id##$viewId => MileageMapView -> onStatusUpdate -> name: $name, status: $status, desc: $desc")
    }

    override fun activate(onLocationChangedListener: LocationSource.OnLocationChangedListener?) {
        //这里我们将地图返回的位置监听保存为当前 Activity 的成员变量
        locationChangedListener = onLocationChangedListener
        //开启定位
        val err = locationManager!!.requestLocationUpdates(
                locationRequest, this, Looper.myLooper())
        Log.d(TAG, "id##$viewId => MileageMapView -> activate -> err: $err")
        when (err) {
            1 -> Toast.makeText(context,
                    "设备缺少使用腾讯定位服务需要的基本条件",
                    Toast.LENGTH_SHORT).show()
            2 -> Toast.makeText(context,
                    "manifest 中配置的 key 不正确", Toast.LENGTH_SHORT).show()
            3 -> Toast.makeText(context,
                    "自动加载libtencentloc.so失败", Toast.LENGTH_SHORT).show()
            else -> {
            }
        }

        if (err == 1 || err == 2 || err == 3) {
            methodChannel!!.invokeMethod("onActivate", mapOf(
                    "isSuccess" to false))
        } else {
            methodChannel!!.invokeMethod("onActivate", mapOf(
                    "isSuccess" to true))
        }

    }

    override fun deactivate() {
        //当不需要展示定位点时，需要停止定位并释放相关资源
        locationManager?.removeUpdates(this)
        locationManager = null
        locationRequest = null
        locationChangedListener = null
    }

    private fun jumpToMapByIntent(uri: String): Boolean {
        try {
            val intent: Intent = Intent.parseUri(uri, 0)
            context.startActivity(intent)
            return true
        } catch(e: Exception) {
            Log.d(TAG, "_jumpToMapByIntent error: $e")
            return false
        }
    }

    private fun jumpToMapByIntent(call: MethodCall, result: MethodChannel.Result) {
        if (call.arguments is String) {
            val uri: String = call.arguments as String
            if (jumpToMapByIntent(uri)) {
                result.success(true)
            } else {
                result.error("-2", "跳转失败", "jumpToMapByIntent error: $uri")
            }
        } else {
            result.error("-1", "参数类型不正确", "expect type String, received type ${call.arguments.javaClass}")
        }

    }

    private fun jumpToMapByData(uri: String): Boolean {
        try {
            val intent = Intent()
            intent.action = Intent.ACTION_VIEW
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            intent.data = Uri.parse(uri)
            context.startActivity(intent)
            return true
        } catch(e: Exception) {
            Log.d(TAG, "_jumpToMapByData error: $e")
            return false
        }
    }

    private fun jumpToMapByData(call: MethodCall, result: MethodChannel.Result) {
        Log.d(TAG, "jumpToMapByData => arguments => ${call.arguments}")
        if (call.arguments is String) {
            val uri: String = call.arguments as String
            if (jumpToMapByData(uri)) {
                result.success(true)
            } else {
                result.error("-2", "跳转失败", "jumpToMapByData error: $uri")
            }
        } else {
            result.error("-1", "参数类型不正确", "expect type String, received type ${call.arguments.javaClass}")
        }

    }

    private fun isAvailable(packageName: String): Boolean {
        val packageManager: PackageManager = context.packageManager
        val packagesInfoList: List<PackageInfo> = packageManager.getInstalledPackages(0)

        val packageNames: MutableList<String> = arrayListOf()
        if (packagesInfoList.isNotEmpty()) {
            for (packageInfo in packagesInfoList) {
                val packageName: String = packageInfo.packageName
                packageNames += packageName
            }
        }
        return packageNames.contains(packageName)
    }

    private fun isAvailable(call: MethodCall, result: MethodChannel.Result) {
        if (call.arguments is String) {
            result.success(isAvailable(call.arguments as String))
        } else {
            result.error("-1", "参数类型不正确", "expect type String, received type ${call.arguments.javaClass}")
        }

    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "isAvailable" -> {
                isAvailable(call, result)
            }
            "jumpToMapByData" -> {
                jumpToMapByData(call, result)
            }
            "jumpToMapByIntent" -> {
                jumpToMapByIntent(call, result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }


}