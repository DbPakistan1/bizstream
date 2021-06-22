package com.biz.stream.fcm.gofast.utils

import android.content.Context
import android.util.Log
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.error.AuthFailureError
import com.android.volley.request.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import java.util.*

object ApiRequest {
    fun Api_POST_Request(
        context: Context?, url: String, jsonObject: JSONObject?,
        callback: Callback?
    ) {

        val urlsplit = url.split("/".toRegex()).toTypedArray()
        Log.d("ApiRequest", url)
        if (jsonObject != null)
            Log.d("ApiRequestRespoStart", jsonObject.toString())
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(
            Method.POST,
            url, jsonObject,
            Response.Listener { response ->
                val urlsplit = url.split("/".toRegex()).toTypedArray()
                Log.d("ApiRequestRespoRes" + urlsplit[urlsplit.size - 1], response.toString())
                callback?.Responce(response.toString())

            }, Response.ErrorListener { error ->
                val urlsplit = url.split("/".toRegex()).toTypedArray()
                Log.d("ApiRequest" + urlsplit[urlsplit.size - 1], error.toString())
                callback?.Responce(error.toString())
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                return HashMap()
            }
        }
        try {
            if (context != null) {
                val requestQueue = Volley.newRequestQueue(context)
                jsonObjReq.retryPolicy = DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
                requestQueue.cache.clear()
                requestQueue.add(jsonObjReq)
            }
        } catch (e: Exception) {
            Log.d("ApiRequestError", e.toString())
        }
    }

    fun Api_GetRequest(
        context: Context?, url: String,
        callback: Callback?
    ) {
        val urlsplit = url.split("/".toRegex()).toTypedArray()
        Log.d("ApiRequest", url)
        val jsonObjReq: JsonObjectRequest = object : JsonObjectRequest(
            Method.GET,
            url, null,
            Response.Listener { response ->
                val urlsplit = url.split("/".toRegex()).toTypedArray()
                Log.d("ApiRequest" + urlsplit[urlsplit.size - 1], response.toString())
                callback?.Responce(response.toString())
                if (response.optString("code", "").equals("501", ignoreCase = true)) {
                }
            }, Response.ErrorListener { error ->
                val urlsplit = url.split("/".toRegex()).toTypedArray()
                Log.d("ApiRequest" + urlsplit[urlsplit.size - 1], error.toString())
                callback?.Responce(error.toString())
            }) {
            override fun getBodyContentType(): String {
                return "application/json; charset=utf-8"
            }

            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                Log.d("Failure", headers.toString())
                return headers
            }
        }
        try {
            if (context != null) {
                val requestQueue = Volley.newRequestQueue(context)
                jsonObjReq.retryPolicy = DefaultRetryPolicy(
                    60000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
                )
                requestQueue.cache.clear()
                requestQueue.add(jsonObjReq)
            }
        } catch (e: Exception) {
            Log.d("ApiRequestError", e.toString())
        }
    }
}