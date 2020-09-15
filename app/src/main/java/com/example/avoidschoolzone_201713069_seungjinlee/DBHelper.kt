package com.example.avoidschoolzone_201713069_seungjinlee

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_d_b.*

class DBHelper(val context : Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    val row = arrayOf("no.","어린이보호구역")


    val data = arrayListOf<String>()
    companion object{
        val DB_VERSION = 1
        val DB_NAME = "mydb.db"
        val TABLE_NAME = "myData"   // 테이블이름
        val ID = "id"
        val NAME = "name"
        val LAT = "latitude"
        val LOT = "longitude"

    }

    // 테이블 정보 생성
    override fun onCreate(db: SQLiteDatabase?) {
        val create_table = "create table if not exists " + TABLE_NAME +"("+
                ID +" integer primary key autoincrement," +
                NAME +" text,"+
                LAT +" text,"+
                LOT +" text)"
        // 객체로 테이블을 실제로 만드는 구문
        db?.execSQL(create_table)
    }

    // 버전정보가 바뀌었을때
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        val drop_table = "drob table if exists " + TABLE_NAME
        db?.execSQL(drop_table)
        onCreate(db)
    }

    // 입력할 객체를 인자로 받는 데이터를 테이블에 넣어줌.
    // 불린값으로 데이터가 잘 들어갔는지 반환.
    fun insertProduct(db_data: DBData):Boolean {
        val values = ContentValues().apply {
            put(NAME, db_data.대상시설명) // 사용자가 텍스트뷰에 입력한 이름값을 테이블에 넣어줌
            put(LAT, db_data.위도)
            put(LOT, db_data.경도)
        }
        // 디비 객체 획득 (읽고쓰기)
        val db = this.writableDatabase
        // 획득한 디비한테 테이블 삽입해주면 됨, -1이 아니면 잘 전달되었다는 뜻,
        if (db.insert(TABLE_NAME, null, values ) > 0) {
            db.close()
            return true // 반환값 (불린값) 으로 참이 출력되었다 -> 즉 디비에 테이블 정보가 제대로 전달됨.
        } else {
            db.close()
            return false
        }
    }

    fun deleteProduct(name:String):Boolean{
        // 테이블로부터 모든걸 가지고오는데 어디서? 아이디값이 pid인것을 가지고와라
        val strsql = "select * from "+TABLE_NAME + " where "+
                NAME +"= \'"+name+"\'"
        val db = this.writableDatabase
        // rawQuery로 질의문 실행해서 결과값 커서에 저장
        val cursor = db.rawQuery(strsql,null)
        // 커서값이 있다면 첫번째로 돌아가서 삭제 수행함
        if(cursor.moveToFirst()){
            db.delete(TABLE_NAME, NAME+"=?", arrayOf(name)) // 두번째 파라미터에 물음표가 들어갈경우 세번째 파라미터에 물음표에 해당하는값을 넣어줄수 있다.
            val activity = context as DBActivity
            activity.text_select.setText("")  // 삭제한 후에 입력칸 비워주는 구문
            cursor.close()
            db.close()
            return true
        }
        cursor.close()
        db.close()
        return false
    }



    // 디비로부터 위도경정보 읽어서 변수에 저장하고 이거를 인텐트로 map2프래그먼트로 넘겨줄것임.
    fun getlocation():Cursor{
        // 데이터베이스에 질의문 날리는 구문
        val strsql = "select latitude,longitude,name from "+TABLE_NAME  // 테이블로부터 모든걸 가지고 와 라는 질의문 생성
        // 데이터베이스에다가 이 질의문을 실행해 라는 명령
        val db = this.readableDatabase // 읽기전용의 데베로 데베 오픈
        // 커서에 저장되있는것 - 위도, 경도 한행.
        val cursor = db.rawQuery(strsql, null)  // 커서에 값을 넘겨준다.

        return cursor
    }

    fun getAllRecord(){
        // 데이터베이스에 질의문 날리는 구문
        val strsql = "select id, name from "+TABLE_NAME  // 테이블로부터 모든걸 가지고 와 라는 질의문 생성
        // 데이터베이스에다가 이 질의문을 실행해 라는 명령
        val db = this.readableDatabase // 읽기전용의 데베로 데베 오픈
        val cursor = db.rawQuery(strsql, null)  // 커서에 값을 넘겨준다.
        //커서가 가지고이쓴 값에 따라서
        if(cursor.count !=0) {
            showRecord(cursor) //커서정보 넘겨서 함수 실행
        }
        cursor.close()
        db.close()
    }

    fun showRecord(cursor: Cursor){ // 이 함수에서 커서정보를 가지고 출력해주는 기능
        cursor.moveToFirst() // dataset의 처음위치로 이동해서 처음데이터부터 읽어들임
        val count = cursor.columnCount // column을 몇개 가지고있는지 알수 있음
        val recordcount = cursor.count // 전체 몇개의 데이터를 가지고 있는지
        val activity = context as DBActivity
        // 컨텍스트를 메인액티비로 했으니까 이걸통해서 메인액티비티에 접근가능
        activity.tableLayout.removeAllViewsInLayout()// 테이블 레이아웃에있는 모든 뷰들을 지우겠다.
        // 아래는 컬럼 타이틀 만들기
        val tablerow = TableRow(activity)
        val rowParam = TableRow.LayoutParams(
            TableRow.LayoutParams.MATCH_PARENT,
            TableRow.LayoutParams.WRAP_CONTENT,
            count.toFloat()) // 카운트가 가지고있는 정보를 실수로 바꿔서 --> 카운트개수만큼 가중치값을 부여해서 넣겟다. 즉 행의 폭정보는 가중치로 결정ㅎ겠다.
        tablerow.layoutParams = rowParam
        val viewParm = TableRow.LayoutParams(0,100, 1f)
        for(i in 0 until count){
            val textView  = TextView(activity)
            textView.layoutParams = viewParm
            // 커서가 가지고있는 열 이름을 텍스트에 배치.
            textView.text = row[i]
            textView.setBackgroundColor(Color.LTGRAY)
            textView.textSize = 15.0f
            textView.gravity = Gravity.CENTER
            tablerow.addView(textView)
        } // 여기까지 구문은 데이터를 읽은거 아니고 데이터의 컬럼정보만 읽은상태임 레이블만 만든상태 ..
        activity.tableLayout.addView(tablerow)
         //실제 레코드 읽어오기 두와일문으로
        do{
            val row = TableRow(activity)
            row.layoutParams = rowParam

            row.setOnClickListener{
                for(i in 0 until count){
                    // 한행에 해당하는 row의 차일드뷰를 가지고올수있음
                    val txtView = row.getChildAt(i) as TextView
                    when(txtView.tag){
                        1 -> activity.text_select.setText(txtView.text)
//                        1 -> activity.pNameEdit.setText(txtView.text)
//                        2 -> activity.pQuantityEdit.setText(txtView.text)
                    }
                }
            }

            // 한 행에 추가가 된 상태
            for(i in 0 until count){
                val textView  = TextView(activity)
                textView.layoutParams = viewParm
                textView.text = cursor.getString(i)
                textView.textSize = 13.0f
                textView.setTag(i)  // 태그값으로 텍스트뷰를 식별함 .
                row.addView(textView)
            }
            activity.tableLayout.addView(row)
        }while(cursor.moveToNext())
    }
}