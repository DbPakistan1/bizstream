package com.biz.stream.fcm.gofast

import android.content.Context
import android.net.ConnectivityManager
import android.provider.Settings
import android.util.Log
import com.google.firebase.FirebaseApp
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.biz.stream.fcm.gofast.utils.ApiRequest
import com.biz.stream.fcm.gofast.utils.Callback
import com.biz.stream.fcm.gofast.utils.Constant
import org.json.JSONException
import org.json.JSONObject


class XamiFcm(private val context: Context) {
    private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null

    init {
        FirebaseApp.initializeApp(context)
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
        mFirebaseRemoteConfig!!.setDefaultsAsync(R.xml.remote_config_defaults)
        FirebaseMessaging.getInstance().subscribeToTopic("fcm_test")
        FirebaseMessaging.getInstance().subscribeToTopic(context.packageName)
        mFirebaseRemoteConfig!!.fetchAndActivate()

        if(isInternetConnected()){
            Get_Public_IP()
        }

        }


    protected fun Get_Public_IP() {

     // ApiRequest.Api_GetRequest(context, "http://192.168.10.9:8000/api/findlocale") { resp ->
        ApiRequest.Api_GetRequest(context, "https://geolocation-db.com/json/") { resp ->
            try {
                val responce = JSONObject(resp)
                val ip = responce.optString("IPv4")
                var countryname = responce.optString("country_name")
                val country_code = responce.optString("country_code")
                val city = responce.optString("city")
                val postal = responce.optString("postal")
                val state = responce.optString("state")
                val latitude = responce.optString("latitude")
                val longitude = responce.optString("longitude")
                if(countryname!=null){
                    FirebaseMessaging.getInstance().subscribeToTopic(countryname)
                    mFirebaseRemoteConfig!!.fetchAndActivate()
                }else{
                    countryname=""
                }
                sendResponseUserActive(countryname)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
    }

    protected fun sendResponseUserActive(country: String) {
        val androidId =  Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID
        )
        //   val date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val parameters = JSONObject()
        try {
            parameters.put("country", country)
            parameters.put("device_id", androidId)
            parameters.put("package_name", context.packageName)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        ApiRequest.Api_POST_Request(context, Constant.USER_ACTIVE, parameters, object : Callback {
            override fun Responce(resp: String?) {
                Log.d("ApiRequestRespo", resp!!)
            }
        })
    }



    private fun isInternetConnected(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val nwInfo = connectivityManager.activeNetworkInfo ?: return false
        return nwInfo.isConnected

    }
//    fun isInternetConnected(): Boolean {
//        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
//        val activeNetwork = cm.isActiveNetworkMetered()
//        return activeNetwork != null &&
//                activeNetwork.isConnectedOrConnecting
//    }

}