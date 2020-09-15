package com.example.avoidschoolzone_201713069_seungjinlee

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity

class LoadingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        SystemClock.sleep(1000)
        val intenttoPermission = Intent(this, MapPermissionActivity::class.java)
        startActivity(intenttoPermission)
        finish()
    }


}