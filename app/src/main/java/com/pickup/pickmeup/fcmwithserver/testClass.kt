package com.pickup.pickmeup.fcmwithserver

import android.content.Context
import android.widget.Toast

class testClass(private val context: Context) {
        init {
            Toast.makeText(context, "This class is running", Toast.LENGTH_SHORT).show()
        }
}