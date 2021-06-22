package com.biz.stream.fcm.gofast.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.biz.stream.fcm.gofast.BuildConfig
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import com.biz.stream.fcm.gofast.R
import com.biz.stream.fcm.gofast.utils.ApiRequest
import com.biz.stream.fcm.gofast.utils.Callback
import com.biz.stream.fcm.gofast.utils.Constant
import com.biz.stream.fcm.gofast.utils.broadcastReciever

import com.squareup.picasso.Picasso
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.atomic.AtomicInteger


/**
 *
 * Created by Xami on 21/06/2021.
 */
open class MyFcmService : FirebaseMessagingService() {

    companion object {
        var androidId = ""
        val NOTF_LAYOUT = "notf_layout"
        val ICON_KEY = "icon"
        val APP_TITLE_KEY = "title"
        val SHORT_DESC_KEY = "short_desc"
        val LONG_DESC_KEY = "long_desc"
        val APP_FEATURE_KEY = "feature"
        val APP_URL_KEY = "contentClickType"

        val NOTIF_ID = "notif_id"
        var notf_show=0
        const val IS_PREMIUM = "is_premium"

        private val seed = AtomicInteger()

        fun getNextInt(): Int {
            return seed.incrementAndGet()
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        androidId =  Settings.Secure.getString(
                contentResolver,
                Settings.Secure.ANDROID_ID
        )
        val data = remoteMessage!!.data
        if (data != null && !data.isEmpty()) {

          val notf_layout = data[NOTF_LAYOUT]
            val iconURL = data[ICON_KEY]

            val title = data[APP_TITLE_KEY]
            val shortDesc = data[SHORT_DESC_KEY]
            val longDesc = data[LONG_DESC_KEY]
            val feature = data[APP_FEATURE_KEY]

            val notif_id = data[NOTIF_ID]

            val notificationID = getNextInt()

            if (iconURL != null && title != null && shortDesc != null  ) {


                try {




                                Handler(this.mainLooper).post {
                                    when (notf_layout) {
                                        "1" -> {
                                            customNotification(
                                                    iconURL,
                                                    title, shortDesc, longDesc,
                                                  notif_id!!.toInt(), data!!
                                            )
                                        }
                                        "2" -> {
                                            customNotification2(
                                                    iconURL,
                                                    title, shortDesc, longDesc,
                                                feature,  notif_id!!.toInt(), data!!
                                            )
                                        }
                                        "3" -> {
                                            customNotification3(
                                                    iconURL,
                                                    title, shortDesc, longDesc,
                                                    feature,  notif_id!!.toInt(), data
                                            )
                                        }
                                        "4" -> {
                                            customNotification4(
                                                    iconURL,
                                                    title, shortDesc, longDesc,
                                                    feature,  notif_id!!.toInt(), data
                                            )
                                        }
                                    }
                                }

                    }




                catch (e: Exception) {
                    if (BuildConfig.DEBUG) Log.e("FcmFireBase", "package not valid")
                    responseIsNotificatonError("package not valid", data["notif_id"]!!, data["p_id"]!!, data["cus_id"]!!, 0, 0)

                }
            }
            responseIsNotificatonRecieve(data["notif_id"]!!, data["p_id"]!!, data["cus_id"]!!)
        }else{
            responseIsNotificatonError("Invalid Attributes", data["notif_id"]!!, data["p_id"]!!, data["cus_id"]!!, 0, 0)
        }

        if (BuildConfig.DEBUG) Log.e("From: ", remoteMessage.from!!)
        if (remoteMessage.notification != null) {
            if (BuildConfig.DEBUG) Log.e("Message  Body:", remoteMessage.notification!!.body!!)
        }

    }

    //    custom view
    private fun customNotification(
            iconURL: String, title: String, shortDesc: String, longDesc: String?,
             notificationID: Int, data: MutableMap<String, String>
    ) {


        val content_intent = Intent(this, broadcastReciever::class.java)
        content_intent.putExtra("notif_id", notificationID)
        content_intent.putExtra("action_Type", data["contentClickType"])
        content_intent.putExtra("action", data["contentClickAction"]!!)
        content_intent.putExtra("putExtraName", data["contentPutExtraName"])
        content_intent.putExtra("putExtra_value", data["contentPutExtraValue"])
        val  pendingcontent_Intent = PendingIntent.getBroadcast(this, 0, content_intent, 0)




        val remoteViews = RemoteViews(packageName, R.layout.sample_notification1)
        val remoteViews_large = RemoteViews(packageName, R.layout.sample_notification1_large)

        remoteViews.setTextViewText(R.id.tv_title, title)
        remoteViews_large.setTextViewText(R.id.tv_title, title)
        remoteViews.setTextViewText(R.id.tv_short_desc, shortDesc)
        remoteViews_large.setTextViewText(R.id.tv_short_desc, shortDesc)
        remoteViews.setTextViewText(R.id.tv_long_desc, longDesc)
        remoteViews_large.setTextViewText(R.id.tv_long_desc, longDesc)
        remoteViews.setViewVisibility(
                R.id.tv_long_desc,
                 View.GONE
        )
        remoteViews_large.setViewVisibility(
                R.id.tv_long_desc,
                if (longDesc != null && !longDesc.isEmpty() && longDesc != "") View.VISIBLE else View.GONE
        )

        val builder = NotificationCompat.Builder(this, title)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSmallIcon(R.drawable.ic_ad_small)
            .setContentIntent(pendingcontent_Intent)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews_large)
            .setOngoing(true)
            .setShowWhen(true)


        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    title,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            mNotificationManager.createNotificationChannel(channel)
        }

        mNotificationManager.notify(notificationID, builder.build())

        Picasso.get()
                .load(iconURL)
                .into(remoteViews, R.id.iv_icon, notificationID, builder.build())
        Picasso.get()
                .load(iconURL)
                .into(remoteViews_large, R.id.iv_icon, notificationID, builder.build())


        notf_show =1
        responseIsNotificatonShow(data["notif_id"]!!, data["p_id"]!!, data["cus_id"]!!)
    }


    private fun customNotification2(
            iconURL: String, title: String, shortDesc: String, longDesc: String?,
            feature: String?, notificationID: Int, data: MutableMap<String, String>
    ) {


        val content_intent = Intent(this, broadcastReciever::class.java)
        content_intent.putExtra("notif_id", notificationID)
        content_intent.putExtra("action_Type", data["contentClickType"])
        content_intent.putExtra("action", data["contentClickAction"]!!)
        content_intent.putExtra("putExtraName", data["contentPutExtraName"])
        content_intent.putExtra("putExtra_value", data["contentPutExtraValue"])
        val  pendingcontent_Intent = PendingIntent.getBroadcast(this, 0, content_intent, 0)




        val remoteViews = RemoteViews(packageName, R.layout.sample_notification2)
        val remoteViews_large = RemoteViews(packageName, R.layout.sample_notfification2_large)

        remoteViews.setTextViewText(R.id.tv_title, title)
        remoteViews_large.setTextViewText(R.id.tv_title, title)
        remoteViews.setTextViewText(R.id.tv_short_desc, shortDesc)
        remoteViews_large.setTextViewText(R.id.tv_short_desc, shortDesc)

        remoteViews.setViewVisibility(
                R.id.action1,
                if (data["action_Type1"] != null && data["action1"] != null && !data["action1"]!!.isEmpty() && data["action1"] != "null" && data["button_caption1"] != null && !data["button_caption1"]!!.isEmpty() && data["button_caption1"] != "null") View.VISIBLE else View.GONE
        )


        remoteViews.setViewVisibility(
                R.id.action2,
                if (data["action_Type2"] != null && data["action2"] != null && !data["action2"]!!.isEmpty() && data["action2"] != "null" && data["button_caption2"] != null && !data["button_caption2"]!!.isEmpty() && data["button_caption2"] != "null") View.VISIBLE else View.GONE
        )

        remoteViews.setViewVisibility(
                R.id.action3,
                if (data["action_Type3"] != null && data["action3"] != null && !data["action3"]!!.isEmpty() && data["action3"] != "null" && data["button_caption3"] != null && !data["button_caption3"]!!.isEmpty() && data["button_caption3"] != "null") View.VISIBLE else View.GONE
        )
        remoteViews_large.setViewVisibility(
                R.id.action1,
                if (data["action_Type1"] != null && data["action1"] != null && !data["action1"]!!.isEmpty() && data["action1"] != "null" && data["button_caption1"] != null && !data["button_caption1"]!!.isEmpty() && data["button_caption1"] != "null") View.VISIBLE else View.GONE
        )


        remoteViews_large.setViewVisibility(
                R.id.action2,
                if (data["action_Type2"] != null && data["action2"] != null && !data["action2"]!!.isEmpty() && data["action2"] != "null" && data["button_caption2"] != null && !data["button_caption2"]!!.isEmpty() && data["button_caption2"] != "null") View.VISIBLE else View.GONE
        )

        remoteViews_large.setViewVisibility(
                R.id.action3,
                if (data["action_Type3"] != null && data["action3"] != null && !data["action3"]!!.isEmpty() && data["action3"] != "null" && data["button_caption3"] != null && !data["button_caption3"]!!.isEmpty() && data["button_caption3"] != "null") View.VISIBLE else View.GONE
        )
       // remoteViews.setTextColor(R.id.action1,)

        if (data["action_Type1"] != null
                && data["action1"] != null && data["action_color1"] != null && !data["action1"]!!.isEmpty()
                && data["action1"]!="null" && data["button_caption1"] != null
                && !data["button_caption1"]!!.isEmpty() && data["button_caption1"]!="null"){
            remoteViews.setTextViewText(R.id.action1, data["button_caption1"])
            remoteViews_large.setTextViewText(R.id.action1, data["button_caption1"])

            remoteViews.setTextColor(R.id.action1, Color.parseColor(data["action_color1"]))
            remoteViews_large.setTextColor(R.id.action1, Color.parseColor(data["action_color1"]))

            val buttonIntent = Intent(this, broadcastReciever::class.java)
            buttonIntent.putExtra("notif_id", notificationID)
            buttonIntent.putExtra("action_Type", data["action_Type1"])
            buttonIntent.putExtra("action", data["action1"]!!)
            buttonIntent.putExtra("putExtraName", data["putExtra_Name1"])
            buttonIntent.putExtra("putExtra_value", data["putExtra_value1"])
            val  pendingAction1_Intent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0)
            remoteViews.setOnClickPendingIntent(R.id.action1, pendingAction1_Intent)
            remoteViews_large.setOnClickPendingIntent(R.id.action1, pendingAction1_Intent)


        }else   if (data["action_Type2"] != null
                && data["action2"] != null  && data["action_color2"] != null && !data["action2"]!!.isEmpty()
                && data["action2"]!="null" && data["button_caption2"] != null
                && !data["button_caption2"]!!.isEmpty() && data["button_caption2"]!="null"){
            remoteViews.setTextViewText(R.id.action2, data["button_caption2"])
            remoteViews_large.setTextViewText(R.id.action2, data["button_caption2"])
            remoteViews.setTextColor(R.id.action2, Color.parseColor(data["action_color2"]))
            remoteViews_large.setTextColor(R.id.action2, Color.parseColor(data["action_color2"]))


            val buttonIntent = Intent(this, broadcastReciever::class.java)
            buttonIntent.putExtra("notif_id", notificationID)
            buttonIntent.putExtra("action_Type", data["action_Type2"])
            buttonIntent.putExtra("action", data["action2"]!!)
            buttonIntent.putExtra("putExtraName", data["putExtra_Name2"])
            buttonIntent.putExtra("putExtra_value", data["putExtra_value2"])
            val  pendingAction2_Intent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0)
            remoteViews.setOnClickPendingIntent(R.id.action2, pendingAction2_Intent)
            remoteViews_large.setOnClickPendingIntent(R.id.action2, pendingAction2_Intent)




        }else  if (data["action_Type3"] != null
                && data["action3"] != null  && data["action_color3"] != null  && !data["action3"]!!.isEmpty()
                && data["action3"]!="null" && data["button_caption3"] != null
                && !data["button_caption3"]!!.isEmpty() && data["button_caption3"]!="null"){
            remoteViews.setTextViewText(R.id.action3, data["button_caption3"])
            remoteViews_large.setTextViewText(R.id.action3, data["button_caption3"])
            remoteViews.setTextColor(R.id.action3, Color.parseColor(data["action_color3"]))
            remoteViews_large.setTextColor(R.id.action3, Color.parseColor(data["action_color3"]))

            val buttonIntent = Intent(this, broadcastReciever::class.java)
            buttonIntent.putExtra("notif_id", notificationID)
            buttonIntent.putExtra("action_Type", data["action_Type3"])
            buttonIntent.putExtra("action", data["action3"]!!)
            buttonIntent.putExtra("putExtraName", data["putExtra_Name3"])
            buttonIntent.putExtra("putExtra_value", data["putExtra_value3"])
            val  pendingAction3_Intent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0)
            remoteViews.setOnClickPendingIntent(R.id.action3, pendingAction3_Intent)
            remoteViews_large.setOnClickPendingIntent(R.id.action3, pendingAction3_Intent)

        }



        val builder = NotificationCompat.Builder(this, title)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))

                .setSmallIcon(R.drawable.ic_ad_small)
              .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentIntent(pendingcontent_Intent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews_large)
                .setOngoing(true)
                .setShowWhen(true)
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    title,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            mNotificationManager.createNotificationChannel(channel)
        }

        mNotificationManager.notify(notificationID, builder.build())

        Picasso.get()
                .load(iconURL)
                .into(remoteViews_large, R.id.iv_icon, notificationID, builder.build())
        Picasso.get()
                .load(feature)
                .into(remoteViews_large, R.id.iv_feature, notificationID, builder.build())

        Picasso.get()
            .load(iconURL)
            .into(remoteViews, R.id.iv_icon, notificationID, builder.build())
        Picasso.get()
            .load(feature)
            .into(remoteViews, R.id.iv_feature, notificationID, builder.build())
        notf_show =1
        responseIsNotificatonShow(data["notif_id"]!!, data["p_id"]!!, data["cus_id"]!!)

    }
    private fun customNotification3(
            iconURL: String, title: String, shortDesc: String, longDesc: String?,
            feature: String?, notificationID: Int, data: MutableMap<String, String>
    ) {

        val content_intent = Intent(this, broadcastReciever::class.java)
        content_intent.putExtra("notif_id", notificationID)
        content_intent.putExtra("action_Type", data["contentClickType"])
        content_intent.putExtra("action", data["contentClickAction"]!!)
        content_intent.putExtra("putExtraName", data["contentPutExtraName"])
        content_intent.putExtra("putExtra_value", data["contentPutExtraValue"])
        val  pendingcontent_Intent = PendingIntent.getBroadcast(this, 0, content_intent, 0)


        val remoteViews = RemoteViews(packageName, R.layout.sample_notification3)
        val remoteViews_large = RemoteViews(packageName, R.layout.sample_notification3_large)

        remoteViews.setTextViewText(R.id.tv_title, title)
        remoteViews_large.setTextViewText(R.id.tv_title, title)
        remoteViews.setTextViewText(R.id.tv_short_desc, shortDesc)
        remoteViews_large.setTextViewText(R.id.tv_short_desc, shortDesc)
        remoteViews.setTextViewText(R.id.tv_long_desc, longDesc)
        remoteViews_large.setTextViewText(R.id.tv_long_desc, longDesc)
        remoteViews.setViewVisibility(
                R.id.tv_long_desc,
                if (longDesc != null && !longDesc.isEmpty()) View.VISIBLE else View.GONE
        )
        remoteViews_large.setViewVisibility(
                R.id.tv_long_desc,
                if (longDesc != null && !longDesc.isEmpty()) View.VISIBLE else View.GONE
        )

        val builder = NotificationCompat.Builder(this, title)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.drawable.ic_ad_small)
            .setContentIntent(pendingcontent_Intent)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews_large)
            .setOngoing(true)
            .setShowWhen(true)

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    title,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            mNotificationManager.createNotificationChannel(channel)
        }

        mNotificationManager.notify(notificationID, builder.build())

        Picasso.get()
            .load(iconURL)
            .into(remoteViews, R.id.iv_icon, notificationID, builder.build())
        Picasso.get()
            .load(iconURL)
            .into(remoteViews_large, R.id.iv_icon, notificationID, builder.build())
        Picasso.get()
            .load(feature)
            .into(remoteViews, R.id.iv_feature, notificationID, builder.build())
        Picasso.get()
            .load(feature)
            .into(remoteViews_large, R.id.iv_feature, notificationID, builder.build())

        notf_show =1
        responseIsNotificatonShow(data["notif_id"]!!, data["p_id"]!!, data["cus_id"]!!)
    }

    private fun customNotification4(
            iconURL: String, title: String, shortDesc: String, longDesc: String?,
            feature: String?,  notificationID: Int, data: MutableMap<String, String>
    ) {

        val content_intent = Intent(this, broadcastReciever::class.java)
        content_intent.putExtra("notif_id", notificationID)
        content_intent.putExtra("action_Type", data["contentClickType"])
        content_intent.putExtra("action", data["contentClickAction"]!!)
        content_intent.putExtra("putExtraName", data["contentPutExtraName"])
        content_intent.putExtra("putExtra_value", data["contentPutExtraValue"])
        val  pendingcontent_Intent = PendingIntent.getBroadcast(this, 0, content_intent, 0)


        val remoteViews = RemoteViews(packageName, R.layout.sample_notification4)
        val remoteViews_large = RemoteViews(packageName, R.layout.sample_notification4_large)

        remoteViews.setTextViewText(R.id.tv_title, title)
        remoteViews_large.setTextViewText(R.id.tv_title, title)
        remoteViews.setTextViewText(R.id.tv_short_desc, shortDesc)
        remoteViews_large.setTextViewText(R.id.tv_short_desc, shortDesc)
        remoteViews.setTextViewText(R.id.tv_long_desc, longDesc)
        remoteViews_large.setTextViewText(R.id.tv_long_desc, longDesc)
        remoteViews_large.setViewVisibility(
                R.id.tv_long_desc,
                View.GONE
        )
        remoteViews_large.setViewVisibility(
                R.id.tv_long_desc,
                if (longDesc != null && !longDesc.isEmpty()) View.VISIBLE else View.GONE
        )


        remoteViews.setViewVisibility(
                R.id.action1,
                if (data["action_Type1"] != null && data["action1"] != null
                        && !data["action1"]!!.isEmpty() && data["action1"] != "null"
                        && data["button_caption1"] != null && !data["button_caption1"]!!.isEmpty()
                        && data["button_caption1"] != "null") View.VISIBLE else View.GONE
        )
        remoteViews_large.setViewVisibility(
                R.id.action1,
                if (data["action_Type1"] != null && data["action1"] != null
                        && !data["action1"]!!.isEmpty() && data["action1"] != "null"
                        && data["button_caption1"] != null && !data["button_caption1"]!!.isEmpty()
                        && data["button_caption1"] != "null") View.VISIBLE else View.GONE
        )


        remoteViews.setViewVisibility(
                R.id.action2,
                if (data["action_Type2"] != null && data["action2"] != null
                        && !data["action2"]!!.isEmpty() && data["action2"] != "null"
                        && data["button_caption2"] != null && !data["button_caption2"]!!.isEmpty()
                        && data["button_caption2"] != "null") View.VISIBLE else View.GONE
        )
        remoteViews_large.setViewVisibility(
                R.id.action2,
                if (data["action_Type2"] != null && data["action2"] != null
                        && !data["action2"]!!.isEmpty() && data["action2"] != "null"
                        && data["button_caption2"] != null && !data["button_caption2"]!!.isEmpty()
                        && data["button_caption2"] != "null") View.VISIBLE else View.GONE
        )

        remoteViews.setViewVisibility(
                R.id.action3,
                if (data["action_Type3"] != null && data["action3"] != null
                        && !data["action3"]!!.isEmpty() && data["action3"] != "null"
                        && data["button_caption3"] != null && !data["button_caption3"]!!.isEmpty()
                        && data["button_caption3"] != "null") View.VISIBLE else View.GONE
        )
        remoteViews_large.setViewVisibility(
                R.id.action3,
                if (data["action_Type3"] != null && data["action3"] != null
                        && !data["action3"]!!.isEmpty() && data["action3"] != "null"
                        && data["button_caption3"] != null && !data["button_caption3"]!!.isEmpty()
                        && data["button_caption3"] != "null") View.VISIBLE else View.GONE
        )
        // remoteViews.setTextColor(R.id.action1,)

        if (data["action_Type1"] != null
                && data["action1"] != null && data["action_color1"] != null && !data["action1"]!!.isEmpty()
                && data["action1"]!="null" && data["button_caption1"] != null
                && !data["button_caption1"]!!.isEmpty() && data["button_caption1"]!="null"){
            remoteViews.setTextViewText(R.id.action1, data["button_caption1"])
            remoteViews_large.setTextViewText(R.id.action1, data["button_caption1"])

            remoteViews.setTextColor(R.id.action1, Color.parseColor(data["action_color1"]))
            remoteViews_large.setTextColor(R.id.action1, Color.parseColor(data["action_color1"]))

//            val buttonIntent = Intent(this, broadcastReciever::class.java)
//            buttonIntent.putExtra("notifid", notificationID)
//            val  pendingAction1_Intent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0)
//            remoteViews.setOnClickPendingIntent(R.id.action1, pendingAction1_Intent)
            val intentWithData = Intent(this, broadcastReciever::class.java)
            intentWithData.putExtra("notif_id", notificationID)
            intentWithData.putExtra("action_Type", data["action_Type1"])
            intentWithData.putExtra("action", data["action1"]!!)
            intentWithData.putExtra("putExtraName", data["putExtra_Name1"])
            intentWithData.putExtra("putExtra_value", data["putExtra_value1"])
            val pendingAction1_Intent = PendingIntent.getBroadcast(this, notificationID, intentWithData, 0)
            remoteViews.setOnClickPendingIntent(R.id.action1, pendingAction1_Intent)
            remoteViews_large.setOnClickPendingIntent(R.id.action1, pendingAction1_Intent)
        }else   if (data["action_Type2"] != null
                && data["action2"] != null  && data["action_color2"] != null && !data["action2"]!!.isEmpty()
                && data["action2"]!="null" && data["button_caption2"] != null
                && !data["button_caption2"]!!.isEmpty() && data["button_caption2"]!="null"){
            remoteViews.setTextViewText(R.id.action2, data["button_caption2"])
            remoteViews_large.setTextViewText(R.id.action2, data["button_caption2"])
            remoteViews.setTextColor(R.id.action2, Color.parseColor(data["action_color2"]))
            remoteViews_large.setTextColor(R.id.action2, Color.parseColor(data["action_color2"]))


            val buttonIntent = Intent(this, broadcastReciever::class.java)
            buttonIntent.putExtra("notif_id", notificationID)
            buttonIntent.putExtra("action_Type", data["action_Type2"])
            buttonIntent.putExtra("action", data["action2"]!!)
            buttonIntent.putExtra("putExtraName", data["putExtra_Name2"])
            buttonIntent.putExtra("putExtra_value", data["putExtra_value2"])
            val  pendingAction2_Intent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0)
            remoteViews.setOnClickPendingIntent(R.id.action2, pendingAction2_Intent)
            remoteViews_large.setOnClickPendingIntent(R.id.action2, pendingAction2_Intent)




        }else  if (data["action_Type3"] != null
                && data["action3"] != null  && data["action_color3"] != null  && !data["action3"]!!.isEmpty()
                && data["action3"]!="null" && data["button_caption3"] != null
                && !data["button_caption3"]!!.isEmpty() && data["button_caption3"]!="null"){
            remoteViews.setTextViewText(R.id.action3, data["button_caption3"])
            remoteViews_large.setTextViewText(R.id.action3, data["button_caption3"])
            remoteViews.setTextColor(R.id.action3, Color.parseColor(data["action_color3"]))
            remoteViews_large.setTextColor(R.id.action3, Color.parseColor(data["action_color3"]))

            val buttonIntent = Intent(this, broadcastReciever::class.java)
            buttonIntent.putExtra("notif_id", notificationID)
            buttonIntent.putExtra("action_Type", data["action_Type3"])
            buttonIntent.putExtra("action", data["action3"]!!)
            buttonIntent.putExtra("putExtraName", data["putExtra_Name3"])
            buttonIntent.putExtra("putExtra_value", data["putExtra_value3"])
            val  pendingAction3_Intent = PendingIntent.getBroadcast(this, 0, buttonIntent, 0)
            remoteViews.setOnClickPendingIntent(R.id.action3, pendingAction3_Intent)
            remoteViews_large.setOnClickPendingIntent(R.id.action3, pendingAction3_Intent)

        }






        val builder = NotificationCompat.Builder(this, title)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            .setSmallIcon(R.drawable.ic_ad_small)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentIntent(pendingcontent_Intent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews_large)
            .setOngoing(true)
            .setShowWhen(true)

        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    title,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            mNotificationManager.createNotificationChannel(channel)
        }

        mNotificationManager.notify(notificationID, builder.build())

        Picasso.get()
            .load(iconURL)
            .into(remoteViews, R.id.iv_icon, notificationID, builder.build())
        Picasso.get()
            .load(iconURL)
            .into(remoteViews_large, R.id.iv_icon, notificationID, builder.build())
        Picasso.get()
            .load(feature)
            .into(remoteViews, R.id.iv_feature, notificationID, builder.build())
        Picasso.get()
            .load(feature)
            .into(remoteViews_large, R.id.iv_feature, notificationID, builder.build())
        notf_show =1
        responseIsNotificatonShow(data["notif_id"]!!, data["p_id"]!!, data["cus_id"]!!)
    }



    fun responseIsNotificatonError(reason: String, notif_id: String, p_id: String, cus_id: String, isrecieve: Int, isShow: Int){

        val parameters = JSONObject()
        try {
            parameters.put("device_id", androidId)
            parameters.put("p_id", p_id)
            parameters.put("notif_id", notif_id)
            parameters.put("cus_id", cus_id)
            parameters.put("error", "1")
            parameters.put("recieve", "${isrecieve}")
            parameters.put("show", "${isShow}")
            parameters.put("reason", reason)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ApiRequest.Api_POST_Request(this, Constant.NOTIF_ERROR, parameters, object : Callback {
            override fun Responce(resp: String?) {
                Log.d("ApiRequestRespo", resp!!)
            }
        })
    }

    fun responseIsNotificatonRecieve(notif_id: String, p_id: String, cus_id: String){


        val parameters = JSONObject()
        try {
            parameters.put("device_id", androidId)
            parameters.put("notif_id", notif_id)
            parameters.put("cus_id", cus_id)
            parameters.put("p_id", p_id)
            parameters.put("recieve", "1")
            parameters.put("show", notf_show)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ApiRequest.Api_POST_Request(this, Constant.NOTIF_RECIEVE, parameters, object : Callback {
            override fun Responce(resp: String?) {
                Log.d("ApiRequestRespo", resp!!)
            }
        })
    }
    fun responseIsNotificatonShow(notif_id: String, p_id: String, cus_id: String){


        val parameters = JSONObject()
        try {
            parameters.put("device_id", androidId)
            parameters.put("notif_id", notif_id)
            parameters.put("cus_id", cus_id)
            parameters.put("p_id", p_id)
            parameters.put("show", notf_show)

        } catch (e: JSONException) {
            e.printStackTrace()
        }
        ApiRequest.Api_POST_Request(this, Constant.NOTIF_SHOW, parameters, object : Callback {
            override fun Responce(resp: String?) {
                Log.d("ApiRequestRespo", "Show" + resp!!)
            }
        })
    }
}