package com.hirno.explorer.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.hardware.usb.UsbManager.ACTION_USB_DEVICE_ATTACHED
import android.hardware.usb.UsbManager.ACTION_USB_DEVICE_DETACHED
import android.util.Log
import com.hirno.explorer.storage.StorageObserver
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class StorageChangeReceiver : BroadcastReceiver(), KoinComponent {

    private val storageObserver: StorageObserver by inject()

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "Received usb change intent: $intent")
        val action = intent?.action
        if (action == ACTION_USB_DEVICE_ATTACHED || action == ACTION_USB_DEVICE_DETACHED) {
            storageObserver.notifyVolumeChange()
        }
    }

    companion object {
        var TAG = "StorageChangeReceiver"
    }
}
