package com.example.avoidschoolzone_201713069_seungjinlee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_community.*
import kotlinx.android.synthetic.main.activity_d_b.*

class DBActivity : AppCompatActivity() {
    lateinit var myDBHelper:DBHelper // MyDBHelper클래스의 객체 생성. 계속해서 이용할거임

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_d_b)
        getAllRecord()
        delete()
    }

    fun getAllRecord(){
        myDBHelper = DBHelper(this)
        myDBHelper.getAllRecord()
    }

    fun delete(){
        btn_delete.setOnClickListener {
            val result = myDBHelper.deleteProduct(text_select.text.toString())
            if(result){
                Toast.makeText(this, "MYZONE에서 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                getAllRecord()
            }
            else {
                Toast.makeText(this, "삭제에 실패하였습니다. 다시시도해주세요.", Toast.LENGTH_SHORT).show()
            }
        }

    }
}