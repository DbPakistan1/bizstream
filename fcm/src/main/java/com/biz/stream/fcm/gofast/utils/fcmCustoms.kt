package com.biz.stream.fcm.gofast.utils

import android.content.Context
import android.widget.Toast
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

import com.biz.stream.fcm.gofast.R

class fcmCustoms(private val context: Context) {


    fun SubscribeSpecficTopic(context: Context, topicName: String){
//        Toast.makeText(context, "" + context.packageName, Toast.LENGTH_SHORT).show()
        var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
        FirebaseApp.initializeApp(context)
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        mFirebaseRemoteConfig!!.setDefaultsAsync(R.xml.remote_config_defaults)
        FirebaseMessaging.getInstance().subscribeToTopic(topicName)
        mFirebaseRemoteConfig!!.fetchAndActivate()
    }
    fun intentKeyword(context: Context, activity: String,keyword:String){
      Toast.makeText(context, "" +activity, Toast.LENGTH_SHORT).show()
        val tinydb: TinyDB = TinyDB(context.applicationContext);
        tinydb.putString(keyword,activity)

    }
}