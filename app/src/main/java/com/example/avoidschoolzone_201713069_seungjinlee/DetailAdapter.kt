package com.example.myfirebase_201713069_seungjinlee

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.avoidschoolzone_201713069_seungjinlee.Detail
import com.example.avoidschoolzone_201713069_seungjinlee.R
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions


// <product 프로덕트 타입으로 데이터들을 가지고오고 넣고 함, 즉 주고받고 하는 데이터의 타입!!!!!
//option - 질의에 따른 데이터의 어댑터?..

class DetailAdapter(options: FirebaseRecyclerOptions<Detail>)
    : FirebaseRecyclerAdapter<Detail, DetailAdapter.ViewHolder>(options) {

    // 인터페이스 멤버로 하나 선언해놓고, 뭔가 이벤트처리가 됬을때 해당 객체를 통해서 OnItemClick 이용
    var itemClickListener : OnItemClickListener?=null

    inner class ViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        var Dname : TextView
        var Daddress:TextView
        var Dpolice : TextView
        var Dcctvnum : TextView
        init{
            Dname = itemView.findViewById(R.id.name)
            Daddress = itemView.findViewById(R.id.address)
            Dpolice = itemView.findViewById(R.id.police)
            Dcctvnum = itemView.findViewById(R.id.cctvnum)
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
            .inflate(R.layout.row_detail, parent, false)
        return ViewHolder(v)
    }

    // 뷰홀더가 가지고있는 텍스트에 데이터를 매핑
    override fun onBindViewHolder(holder: ViewHolder, position: Int, model: Detail) {
        holder.Dname.text = model.대상시설명
        holder.Daddress.text = model.소재지도로명주소
        holder.Dpolice.text = model.관할경찰서명
        holder.Dcctvnum.text = model.CCTV설치대수

    }

}


