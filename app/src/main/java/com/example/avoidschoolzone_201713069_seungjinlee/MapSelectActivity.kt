package com.example.avoidschoolzone_201713069_seungjinlee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_map_select.*

class MapSelectActivity : AppCompatActivity() {
    val maps1Fragment = Maps1Fragment()
    val maps2Fragment = Maps2Fragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_select)
        init()
    }
    private fun init() {

        val fragment = supportFragmentManager.beginTransaction()
//        fragment.addToBackStack(null)
        fragment.replace(R.id.frameLayout, maps1Fragment)
        fragment.commit()
        special.setOnClickListener {
            if (!maps1Fragment.isVisible) {//이미지 프래그먼트가 보여지고있지 않을때,
                val fragment = supportFragmentManager.beginTransaction()
                fragment.addToBackStack(null)
                fragment.replace(R.id.frameLayout, maps1Fragment)
                fragment.commit()
            }
        }
        general.setOnClickListener {
            if (!maps2Fragment.isVisible) {
                val fragment = supportFragmentManager.beginTransaction()
                fragment.addToBackStack(null)
                fragment.replace(R.id.frameLayout, maps2Fragment)
                fragment.commit()
            }
        }

    }

}
