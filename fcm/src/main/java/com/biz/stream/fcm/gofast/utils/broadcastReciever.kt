package com.biz.stream.fcm.gofast.utils

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.biz.stream.fcm.gofast.services.MyFcmService
import org.json.JSONException
import org.json.JSONObject
import java.util.concurrent.Executors


class broadcastReciever : BroadcastReceiver() {
    override fun onReceive(context: Context, myintent: Intent) {
        var applicationContext=context.applicationContext;
        val notificationId = myintent.getIntExtra("notif_id", 0)
        val action_Type = myintent.getStringExtra("action_Type")
        val action = myintent.getStringExtra("action")
        val putExtraName = myintent.getStringExtra("putExtraName")
        val putExtra_value = myintent.getStringExtra("putExtra_value")
       val androidId =  Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
        )
        when (action_Type) {
            "StartActivity" -> {
                try {
                    responseIsNotificatonClicked(context, notificationId, androidId)
                    val manager =
                        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    manager.cancel(notificationId)
                    val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                    context.sendBroadcast(it)
                    val tinydb: TinyDB = TinyDB(context.applicationContext);
                  //  Toast.makeText(context, ""+tinydb.getString(action.toString()), Toast.LENGTH_SHORT).show()
                    val classname=tinydb.getString(action.toString());
                    if (classname!=null && classname!=""){
                        val activityClass = Class.forName(classname)
                        // Toast.makeText(context, "This is "+activityClass, Toast.LENGTH_SHORT).show()
                        val mintent = Intent(context, activityClass)
                        mintent.putExtra(putExtraName, putExtra_value)
                        mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(mintent)
                    }

                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }

            }
            "App" -> {
                responseIsNotificatonClicked(context,notificationId,androidId)
                val standard = "https://play.google.com/store/apps/details?id="
                val id = action!!.substring(standard.length)
                if (isAppInstalled(id, context) && !TinyDB.getInstance(context).getBoolean(
                                MyFcmService.IS_PREMIUM)) {
                    val mintent2 = Intent(Intent.ACTION_VIEW, Uri.parse(action))
                    context.startActivity(mintent2)
                }
                val manager1 = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager1.cancel(notificationId)
                val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                context.sendBroadcast(it)
            }
            "Link" -> {
                responseIsNotificatonClicked(context,notificationId,androidId)
                val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                context.sendBroadcast(it)
                val mintent3 = Intent(Intent.ACTION_VIEW, Uri.parse(action))
                mintent3.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(mintent3)
                val manager2 = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                manager2.cancel(notificationId)
            }
            else -> {
                val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager?
                notificationManager!!.cancel(notificationId)
                val it = Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)
                context.sendBroadcast(it)

            }
        }
        Log.d("enterInMethod", notificationId.toString())
    }

    private fun isAppInstalled(uri: String, context: Context): Boolean {
        val pm = context.packageManager
        return try {
            val applicationInfo = pm.getApplicationInfo(uri, 0)
            //            packageInfo
            applicationInfo.enabled
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }
    fun responseIsNotificatonClicked(context: Context,notif_id: Int,androidId: String){
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())
        executor.execute {
            val parameters = JSONObject()
            try {
                parameters.put("device_id",androidId)
                parameters.put("notif_id", notif_id)
                parameters.put("click", "1")

            } catch (e: JSONException) {
                e.printStackTrace()
            }
            ApiRequest.Api_POST_Request(
                context,
                Constant.NOTIF_CLICKED,
                parameters,
                object : Callback {
                    override fun Responce(resp: String?) {
                        Log.d("ApiRequestClikc", resp!!)
                    }
                })
            handler.post {

            }
        }


    }
}