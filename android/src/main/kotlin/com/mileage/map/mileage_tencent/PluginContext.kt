package com.gv.livechat.flutter_live_chat

import android.app.Activity
import android.content.Context
import androidx.lifecycle.Lifecycle
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.PluginRegistry
import java.lang.ref.WeakReference

internal object PluginContext {

    private var weakReferenceActivityPluginBinding: WeakReference<ActivityPluginBinding?> = WeakReference(null)
    var activityPluginBinding: ActivityPluginBinding?
        get() = weakReferenceActivityPluginBinding.get()
        set(value) {
            weakReferenceActivityPluginBinding = WeakReference(value)
        }
}