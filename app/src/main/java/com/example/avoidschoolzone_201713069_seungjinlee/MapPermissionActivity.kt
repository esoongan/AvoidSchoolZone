package com.example.avoidschoolzone_201713069_seungjinlee

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.android.synthetic.main.activity_map_permission.*

class MapPermissionActivity : AppCompatActivity() {
    // FusedLocationProviderClient 클래스 타입의 객체 생성하고 null값으로 초기화
    var fusedLocationClient: FusedLocationProviderClient? = null

    // LocationCallback 클래스 타입의 객체 생성하고 null값으로 초기화
    var locationCallback: LocationCallback? = null

    // LocationRequest 클래스 타입의 객체 생성하고 null값으로 초기화
    var locationRequest: LocationRequest? = null


    // 아직 초기화 하지않은 구글맵타입의 구글맵 객체를 멤버로 선언함
    // 맵객체는 초기화가 완료되어야 객체가 사용가능한데 초기화는 프래그먼트로 진행함.
    lateinit var googleMap: GoogleMap
    // latlng으로 객체 만들고 그 객체로 위도경도 정보 저장하고있는 변수하나 생성..
    lateinit var myloc:LatLng
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_permission)
        initLocation()



    }

    fun intenttoIntro(){
        val intenttoIntro = Intent(this, IntroActivity::class.java) //단어장으로 넘어가야되니까 인텐트 생성 this여기서 mainact의 클래스로 이동
        Toast.makeText(this, "위치정보를 허용하여 앱이 시작되었습니다.", Toast.LENGTH_SHORT).show()
        startActivity(intenttoIntro)
    }


    // 권한정보 체크하는 부분.
    fun initLocation() {
        // 권한정보 두개 체크하면댐,
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            getuserloaction()
            startLocationUpdates()
            initmap()
        }
        // 승인이 안됫을때 처음 실행했을ㄸ처럼 ㅇㅇ
        else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100
            )
        }
    }

    fun getuserloaction() {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient?.lastLocation?.addOnSuccessListener {
            // 멤버로 있던 loc을 현재위치로 바꿔줌
            myloc = LatLng(it.latitude, it.longitude)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                getuserloaction()
                startLocationUpdates()
                initmap()

            } else{
                Toast.makeText(this, "위치정보제공의 동의가 필요합니다.", Toast.LENGTH_SHORT).show()
                initmap()
            }
        }
    }

    fun startLocationUpdates() {
        // 1. 클래스 멤버러 위에서 선언했떤 locationRequest에 정보넣어줌. 업데이트간격 10초 가장빠른 업데이트 간격 5초로 설정
        // 엄청 빠른거임
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        //2. 로케이션 콜백 객체 만들고 함수 오버라이딩 해줌. 업데이트가 등록이 되면 조건을 만족할땜다ㅏ 위치정보를 가져옴
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    myloc = LatLng(location.latitude, location.longitude)
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myloc, 16.0f))
                }
            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper())
    }

    fun stopLocationUpdates(){
        fusedLocationClient?.removeLocationUpdates(
            locationCallback
        )
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    fun initMarkers(){
        val options = MarkerOptions()
        //loc가 가지고있는는 랫랭 객체의 정보에 해당하는 위치에 마커를 위치시킴
        options.position(myloc)
        // 마커아이콘은 디포릍 아이콘인데 색은 레드로 할고얌
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
//        options.title("역")
//        options.snippet("서울역")
        // 마커를 추가한 객체를 생성하고. 윈도우에 보이게함!
        val mk1 = googleMap.addMarker(options)
        mk1.showInfoWindow()
    }
    // 지도가 준비되었으면 지도를 보여주는부분.

    fun initmap(){
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        // getMapAsync() 인자로 준비가되었다는걸 받으면 호출되 함수
        // 따로 OnMapReadyCallBack 상속받아서 메소드 오버라이딩 하지 않아도
        // 샘변환에 의해서? 바로 이렇게 그냥 괄호 열어주면댐
        mapFragment.getMapAsync{
            //구글맵정보 초기화 ,, 맵이 준비가 되었을때 구글맵정보가 들어오니까 그정보로 초기화시켜줌
            googleMap = it
            // 구글맵의 특정위치로 이동하게함
           // googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 8.0f))
            // 사용자가 확대할수 있는 줌 정도를 10 에서 18로 설정하겠다.
           // googleMap.setMinZoomPreference(10.0f)
            //googleMap.setMaxZoomPreference(18.0f)
            //initMarkers()

        }
        intenttoIntro()
    }
    // 사용자가 지도를 클릭했을때 발생하는 이벤트 처리


}