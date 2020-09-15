package com.example.avoidschoolzone_201713069_seungjinlee


import android.app.TabActivity
import android.content.Intent
import android.os.Bundle
import android.widget.TabHost

@Suppress("DEPRECATION")
class IntroActivity : TabActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        init()
    }
    private fun init() {

        val tab_host = tabHost

        val intent1 = Intent(this, MapSelectActivity::class.java)
        val spec1: TabHost.TabSpec
        spec1 = tab_host.newTabSpec("Map")
            .setIndicator("", resources.getDrawable(R.drawable.tabicon_1))
            .setContent(intent1)

        // TabSpec 객체에 FrameLayout 이 출력할 페이지를 설정한다.
        //intent1.putExtra("저장위치","33333")
        // 탭호스트에 해당 정보를 가진 탭을 추가한다.
        tab_host.addTab(spec1)

        val intent2 = Intent(this, DetailActivity::class.java)
        val spec2: TabHost.TabSpec
        // "book" 이라는 태그 값을 가진 TabSpec 객체를 생성한다.
        spec2 = tab_host.newTabSpec("book")
            .setIndicator("", resources.getDrawable(R.drawable.tabicon_2))
            .setContent(intent2)
        tab_host.addTab(spec2)

        val intent3 = Intent(this, AllCommunityActivity::class.java)
        val spec3: TabHost.TabSpec
        // "book" 이라는 태그 값을 가진 TabSpec 객체를 생성한다.
        spec3 = tab_host.newTabSpec("book")
            .setIndicator("",resources.getDrawable(R.drawable.tabicon_3))
            .setContent(intent3)
        // 탭호스트에 해당 정보를 가진 탭을 추가한다.
        tab_host.addTab(spec3)

        val intent4 = Intent(this, DBActivity::class.java)
        val spec4: TabHost.TabSpec
        // "book" 이라는 태그 값을 가진 TabSpec 객체를 생성한다.
        spec4 = tab_host.newTabSpec("book")
            .setIndicator("",resources.getDrawable(R.drawable.tabicon_4))
            .setContent(intent4)
        // 탭호스트에 해당 정보를 가진 탭을 추가한다.
        tab_host.addTab(spec4)


    }
}


