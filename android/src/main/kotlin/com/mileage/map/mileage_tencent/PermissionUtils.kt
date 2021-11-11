package com.mileage.map.mileage_tencent

import android.Manifest
import android.app.Activity
import android.content.Context
import android.os.Build
import android.widget.Toast
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.AfterPermissionGranted

object PermissionUtils {

    @AfterPermissionGranted(1)
    fun requirePermission(activity: Activity) {
        val permissions: Array<String> = arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permissionsForQ: Array<String> = arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION,  //target为Q时，动态请求后台定位权限
                Manifest.permission.READ_PHONE_STATE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        if (if (Build.VERSION.SDK_INT >= 29) EasyPermissions.hasPermissions(activity, *permissionsForQ) else EasyPermissions.hasPermissions(activity, *permissions)) {
            //Toast.makeText(activity, "权限OK", Toast.LENGTH_LONG).show()
        } else {
            if (Build.VERSION.SDK_INT >= 29) {
                EasyPermissions.requestPermissions(activity, "需要权限",
                        1, *permissionsForQ)
            } else {
                EasyPermissions.requestPermissions(activity, "需要权限",
                        1, *permissions)
            }
        }
    }

}