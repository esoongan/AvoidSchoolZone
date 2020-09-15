package com.example.avoidschoolzone_201713069_seungjinlee

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_community.*

// 수정할것 - 전부다 보여주는게 아니고 사용자가 클릭한 스쿨존의 리뷰만 보여주게바꾸기..

class CommunityActivity : AppCompatActivity() {


    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: CommunityAdapter
    lateinit var rdb: DatabaseReference
    lateinit var myDBHelper:DBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_community)
        init()
        intent()
        initBtn()
    }

    // Info에서 클릭한 리사이클러로부터 인텐트로 데이터 받아와서
    // 해당 스쿨존에대한 리뷰 작성할수 있도록 함
    fun intent(){
        val intentFromINFO = intent
        val text = intentFromINFO.getStringExtra("title")
        nameIntent.setText(text)
    }
    private fun initBtn() {
        myDBHelper = DBHelper(this)

        //추버튼 눌렀을때 해당스쿨존 이름에 해당하는거 받아옴.
        insertBtn.setOnClickListener {
            val item = Community(
                nameIntent.text.toString(),
                idEdit.text.toString(),
                dateEdit.text.toString(),
                reviewEdit.text.toString()
            )
            rdb.child(idEdit.text.toString()).setValue(item)  //유저아이디를 스쿨존이름으로 데이터 정렬
            idEdit.setText("")
            dateEdit.setText("")
            reviewEdit.setText("")
            Toast.makeText(this, "코멘트가 작성되었습니다.", Toast.LENGTH_SHORT).show()
        }

        // 저장 버튼을 눌렀을떄 넘어온 인텐트에 담긴 이름, 위도, 경도 정보 내부 데이터베이스에 저장!!!!!!!!!
        btn_save.setOnClickListener {

            val intentFromINFO = intent
            val title_intent = intentFromINFO.getStringExtra("title")
            val lat_intent = intentFromINFO.getStringExtra("latitude")
            //Log.i("testest", lat_intent!!)
            val lot_intent = intentFromINFO.getStringExtra("longitude")
            // 데이터베이스에 에 인텐트로 받은 정보 넘겨줌 (DBData객체로 넘겨줌)
            val dbData = DBData(0, title_intent!!, lat_intent!!, lot_intent!!)
            val result = myDBHelper.insertProduct(dbData)
            if(result){
                //"DB INSERT SUCCESS"
                Toast.makeText(this, "MYZONE에 추가되었습니다.", Toast.LENGTH_SHORT).show()
              //  maps2Fragment.marker()
            }
            else {
                Toast.makeText(this, "DB INSERT FAILED", Toast.LENGTH_SHORT).show()
            }



        }
    }


    fun init(){
        val intentFromINFO = intent
        // 사용자가 클릭한 해당 스쿨존 이름이 담겨있음
        val text = intentFromINFO.getStringExtra("title")

        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerview_C.layoutManager = layoutManager
        //rdb = FirebaseDatabase.getInstance().getReference("seungjin")
        rdb = FirebaseDatabase.getInstance().getReference("Review")

        //전체리뷰말고 해당구역 리뷰만 볼 수 있음.
        val query = rdb.orderByChild("스쿨존이름").equalTo(text.toString())
        val option = FirebaseRecyclerOptions.Builder<Community>()
            // query를 실행한다음에 커뮤니티에다가 담아서 옴
            .setQuery(query, Community::class.java)
            .build()

        // 위에서 수행한 옵션으로 어댑터 만들어줌.
        adapter = CommunityAdapter(option)

//        // 리사이클러뷰의 row을 선택했을때 해당 값들을 위에다가 보여줌.
//        adapter.itemClickListener = object : DetailAdapter.OnItemClickListener {
//            override fun OnItemClick(view: View, position: Int) {
//                // 오류났떤것 -> context정보에서 this로 하면 안먹고 View.context로 했더니 됨
//                val i = Intent(view.context, CommunityActivity::class.java) //단어장으로 넘어가야되니까 인텐트 생성 this여기서 mainact의 클래스로 이동
//                startActivity(i)// intent를 받아서 액티비티 시작
//
//
//            }
//        }
        recyclerview_C.adapter = adapter // 어댑터달아줌
        // 만약 데베가 바뀌면 노티파이 할필요없고 자동으로 변경됨
    }
    // 질의에 대한 변화를 노티파이 해주지 않아도 자동으로 모니터링함
    // 원하는 질의가 변경되엇을때 자동으로 적요됨.
    override fun onStart(){
        super.onStart()
        adapter.startListening()
    }

    override fun onStop(){
        super.onStop()
        adapter.startListening()
    }


}