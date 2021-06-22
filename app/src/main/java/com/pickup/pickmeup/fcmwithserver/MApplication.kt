package com.pickup.pickmeup.fcmwithserver

import android.app.Application
import android.content.res.Configuration

import com.biz.stream.fcm.gofast.XamiFcm


class MApplication : Application() {

    companion object {
        private var myApp: MApplication? = null
        var test_Class: XamiFcm? = null
    }


    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)

    }

    override fun onCreate() {
        super.onCreate()

        test_Class =
                XamiFcm(this)

        myApp = this
          }


    }