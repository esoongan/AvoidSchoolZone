package com.example.avoidschoolzone_201713069_seungjinlee

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myfirebase_201713069_seungjinlee.DetailAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.row_detail.view.*

// 파이어베이스와 연결해서 정보 가져오는 액티비티.
class DetailActivity : AppCompatActivity() {


    lateinit var layoutManager: LinearLayoutManager
    lateinit var adapter: DetailAdapter
    lateinit var rdb: DatabaseReference
    var findQuery = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        init()
        initbtn()
    }


//    override fun onConfigurationChanged(newConfig: Configuration) {
//        super.onConfigurationChanged(newConfig)
//        if(newConfig.hardKeyboardHidden == Configuration.HARDKEYBOARDHIDDEN_NO){
//            tabs.visibility = View.GONE
//        }
//        else if(newConfig.hardKeyboardHidden==Configuration.HARDKEYBOARDHIDDEN_YES){
//            tabs.visibility = View.VISIBLE
//        }
//    }

    // 검색버튼 눌렀을때 수행할부분~!~!~!
    private fun initbtn() {
        findBtn.setOnClickListener {
            if(findQuery) {
                findQueryAdapter()
                nameInput.setText("")
            }
            else{
                findQuery = true
                findQueryAdapter()
                nameInput.setText("")
            }

            adapter.itemClickListener = object : DetailAdapter.OnItemClickListener {
                override fun OnItemClick(view: View, position: Int) {
                    // 오류났떤것 -> context정보에서 this로 하면 안먹고 View.context로 했더니 됨
                    val i = Intent(view.context, CommunityActivity::class.java) //단어장으로 넘어가야되니까 인텐트 생성 this여기서 mainact의 클래스로 이동
                 // 인텐트에 클릭한 리사이클러뷰의 이름에 해당하는거담아서 보냄
                    // 구글에있던 막 겟엑스트라. 이런거 아니고 리사이클러뷰에서는 adapter.getItem(position)하면댐 두번째인자 잘 기억..!
                    i.putExtra("title", adapter.getItem(position).대상시설명)
                    i.putExtra("latitude", adapter.getItem(position).위도)
                    i.putExtra("longitude", adapter.getItem(position).경도)
//                    i.putExtra("latitude",adapter.getItem(position).CCTV설치대수)
//                    i.putExtra("longtitude",adapter.getItem(position).경도)

                    // nameInput.setText(adapter.getItem(position).관할경찰서명) - 잘되는거 확인
                    startActivity(i)// intent를 받아서 액티비티 시작

                }
            }
        }
    }

 // 검색버튼을 눌렀을때 쿼리가 바뀌는 어댑터.
    fun findQueryAdapter() {
     if (adapter != null)
         adapter.startListening()
     //사용자가 입력한 스쿨존이에 해당하는 거랑 일치하는 데이터 가져오는 쿼리작성.
     val query = rdb.orderByChild("대상시설명").equalTo(nameInput.text.toString())
     // 옵션객체 만들어서 질의문 수행.
     // 옵션객체란 자동으로 디비에서 질의문에 맞는 데이터들을 가져옴.
     val option = FirebaseRecyclerOptions.Builder<Detail>()
         .setQuery(query, Detail::class.java)
         .build()
     adapter = DetailAdapter(option)
     recyclerView_D.adapter = adapter
//     if(adapter == null) Toast.makeText(this,"검색결과가 없습니다.", Toast.LENGTH_SHORT).show()
     adapter.startListening()

 }



    fun init(){
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView_D.layoutManager = layoutManager
        //rdb = FirebaseDatabase.getInstance().getReference("seungjin")
        // 파이어데이스로부터 records에 해당하는 레퍼런스 객체를 생성.
        rdb = FirebaseDatabase.getInstance().getReference("records")

        val query = FirebaseDatabase.getInstance().
        reference.child("records").limitToFirst(150)
        val option = FirebaseRecyclerOptions.Builder<Detail>()
            // query를 실행한다음에 detail에다가 담아서 옴
            .setQuery(query, Detail::class.java)
            .build()

        // 위에서 수행한 옵션으로 어댑터 만들어줌.
        adapter = DetailAdapter(option)

        // 리사이클러뷰의 row을 선택했을때 해당 값들을 위에다가 보여줌.
        adapter.itemClickListener = object : DetailAdapter.OnItemClickListener {
            override fun OnItemClick(view: View, position: Int) {
                // 오류났떤것 -> context정보에서 this로 하면 안먹고 View.context로 했더니 됨
                val i = Intent(view.context, CommunityActivity::class.java) //단어장으로 넘어가야되니까 인텐트 생성 this여기서 mainact의 클래스로 이동
                i.putExtra("title",adapter.getItem(position).대상시설명)
                i.putExtra("latitude", adapter.getItem(position).위도)
                i.putExtra("longitude", adapter.getItem(position).경도)
                // nameInput.setText(adapter.getItem(position).관할경찰서명)
               // Log.i("test",recyclerView_D.get(position).name.toString() )
                startActivity(i)// intent를 받아서 액티비티 시작


            }
        }
        recyclerView_D.adapter = adapter // 어댑터달아줌
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