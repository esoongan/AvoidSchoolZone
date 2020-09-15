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
import kotlinx.android.synthetic.main.activity_all_community.*
import kotlinx.android.synthetic.main.activity_detail.*

// 수정할것 - 전부다 보여주는게 아니고 사용자가 클릭한 스쿨존의 리뷰만 보여주게바꾸기..

class AllCommunityActivity : AppCompatActivity() {


    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: CommunityAdapter
    lateinit var rdb: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_community)
        init()
        initBtn()
    }

    private fun initBtn() {

        // 저장 버튼을 눌렀을떄 넘어온 인텐트에 담긴 이름, 위도, 경도 정보 내부 데이터베이스에 저장!!!!!!!!!
        btn_findid.setOnClickListener {
            if (adapter != null)
                adapter.startListening()
            //사용자가 입력한 아이디에 해당하는 거랑 일치하는 데이터 가져오는 쿼리작성.
            if (edit_id.text.toString() == "") {
                val query = rdb.orderByChild("사용자아이디")
                doquery(query)
            } else {
                val query = rdb.orderByChild("사용자아이디").equalTo(edit_id.text.toString())
                doquery(query)

            }
        }
    }

    fun doquery(query:Query){
        val option = FirebaseRecyclerOptions.Builder<Community>()
            .setQuery(query, Community::class.java)
            .build()
        adapter = CommunityAdapter(option)
        recyclerView_all.adapter = adapter
        adapter.startListening()
        edit_id.setText("")
    }


    fun init(){
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView_all.layoutManager = layoutManager
        //rdb = FirebaseDatabase.getInstance().getReference("seungjin")
        rdb = FirebaseDatabase.getInstance().getReference("Review")

        //전체리뷰말고 해당구역 리뷰만 볼 수 있음.
        val query = FirebaseDatabase.getInstance().
        reference.child("Review")
        val option = FirebaseRecyclerOptions.Builder<Community>()
            // query를 실행한다음에 커뮤니티에다가 담아서 옴
            .setQuery(query, Community::class.java)
            .build()

        // 위에서 수행한 옵션으로 어댑터 만들어줌.
        adapter = CommunityAdapter(option)

        // 리사이클러뷰의 row을 선택했을때 해당 값들을 위에다가 보여줌.
        adapter.itemClickListener = object : CommunityAdapter.OnItemClickListener {
            override fun OnItemClick(view: View, position: Int) {
                // 오류났떤것 -> context정보에서 this로 하면 안먹고 View.context로 했더니 됨
                edit_id.setText(adapter.getItem(position).사용자아이디)
            }
        }

        recyclerView_all.adapter = adapter // 어댑터달아줌
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