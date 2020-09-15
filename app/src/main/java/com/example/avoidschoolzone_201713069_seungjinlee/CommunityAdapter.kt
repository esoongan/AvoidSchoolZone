package com.example.avoidschoolzone_201713069_seungjinlee

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.avoidschoolzone_201713069_seungjinlee.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


// <product 프로덕트 타입으로 데이터들을 가지고오고 넣고 함, 즉 주고받고 하는 데이터의 타입!!!!!
//option - 질의에 따른 데이터의 어댑터?..

class CommunityAdapter(options: FirebaseRecyclerOptions<Community>)
    : FirebaseRecyclerAdapter<Community, CommunityAdapter.ViewHolder>(options) {

    // 인터페이스 멤버로 하나 선언해놓고, 뭔가 이벤트처리가 됬을때 해당 객체를 통해서 OnItemClick 이용
    var itemClickListener : OnItemClickListener?=null

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val UZone : TextView
        var UId : TextView
        var UDate:TextView
        var UReview : TextView
        init{
            UZone = itemView.findViewById(R.id.user_zone)
            UId = itemView.findViewById(R.id.user_id)
            UDate = itemView.findViewById(R.id.user_date)
            UReview = itemView.findViewById(R.id.user_review)
            // 리사이클러뷰에서 해당아이템뷰를 선택했을때 이벤트 처리
            itemView.setOnClickListener{
                itemClickListener?.OnItemClick(it, adapterPosition)
            }

        }
    }
    // 클릭했을때 이벤트처리해주려고 인터페이스 하나 정의하고,
    interface OnItemClickListener{
        fun OnItemClick(view:View, position:Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_community, parent, false)
        return ViewHolder(v)
    }

    // 뷰홀더가 가지고있는 텍스트에 데이터를 매핑
    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Community) {
        holder.UZone.text = model.스쿨존이름
        holder.UId.text = model.사용자아이디
        holder.UDate.text = model.날짜
        holder.UReview.text = model.코멘트
    }

}


