package com.pickup.pickmeup.fcmwithserver

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.biz.stream.fcm.gofast.utils.fcmCustoms


class MainActivity : AppCompatActivity() {
    var tv_title:TextView?=null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_title=findViewById(R.id.tv_title)
      //  FirebaseApp.initializeApp(this)
            val subscribes= fcmCustoms(this)
        subscribes.SubscribeSpecficTopic(this, "weather");
        subscribes.intentKeyword(this, MainActivity::class.java.name, "weather");
        Toast.makeText(this, "Nawa aya hin sohriyan", Toast.LENGTH_SHORT).show()
        tv_title!!.setOnClickListener {
            Toast.makeText(this, "ata", Toast.LENGTH_SHORT).show()

        }


    }

}