package com.example.avoidschoolzone_201713069_seungjinlee

import android.Manifest
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


class Maps2Fragment : Fragment(),OnMapReadyCallback {
    lateinit var rootView: View
    lateinit var mapView: MapView
    lateinit var googleMapout: GoogleMap
    lateinit var myDBHelper:DBHelper


    val options = MarkerOptions()
    val myoptions = MarkerOptions()


    var name = ArrayList<String>()
    var caslt_cnt = ArrayList<String>()
    var occrrnc_cnt = ArrayList<String>()
    val latitude = arrayListOf<Double>()
    val longitude = arrayListOf<Double>()

    lateinit var loc: LatLng
    lateinit var myloc : LatLng

    var fusedLocationClient: FusedLocationProviderClient? = null

    // LocationCallback 클래스 타입의 객체 생성하고 null값으로 초기화
    var locationCallback: LocationCallback? = null

    // LocationRequest 클래스 타입의 객체 생성하고 null값으로 초기화
    var locationRequest: LocationRequest? = null



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setHasOptionsMenu(true)
        super.onCreate(savedInstanceState)
    }

    // 프래그먼트 내에서는 맵뷰로 지도를 실행한다.
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.fragment_maps2, container, false)
        mapView = rootView.findViewById(R.id.mapView)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this)
        return rootView

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        //Fragment에서의 OnCreateView를 마치고, 액티비티에서 onCreate()가
        //호출되고나서 호출되는 메소드.
        //액티비티와 프래그먼트의 뷰가 모두 생성된 상태로 뷰를 변경하는 작업이 가능하다.
        super.onActivityCreated(savedInstanceState)
        //액티비티가 처음 생성될때 실행되는 함수
        MapsInitializer.initialize(this.activity)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        myDBHelper = DBHelper(requireContext())

        googleMapout = googleMap

        initLocation()
        marker()

    }

    // 디비로부터 데이터 읽어오기
    // 커서란 선택된 행의 집합 객체임!!!
    // 각 열에 해당하는 데이터를 획득하려면 커서 객체로 행을 선택하고
    // 선택된 행의 열 데이터를 획득하는 구조임
    // moveTO~함수로 행을 먼저 선택한후 열데이터를 가져올수 있음.


    fun marker(){
        // 디비헬퍼클래스에서 위치좌표를 가지는 커서 반환하는 getlocation함수 호출.
        // 반환값 변수에 저장.
        val cursor = myDBHelper.getlocation()
        while (cursor.moveToNext()) {
            val lat = cursor.getDouble(0)
            val lot = cursor.getDouble(1)
            val name = cursor.getString(2)
            Log.i("test", lat.toString())
            loc = LatLng(lat, lot)
            options.position(loc)
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
            options.title("내가 찜한 곳!")
            options.snippet(name)
            val marker = googleMapout.addMarker(options)
            marker.showInfoWindow()
        }
    }


    fun initLocation() {
        // 권한정보 두개 체크하면댐,
        if (ActivityCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            getuserloaction()
            startLocationUpdates()
            //initmap()
        }
        // 승인이 안됫을때 처음 실행했을ㄸ처럼 ㅇㅇ
        else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), 100
            )
        }
    }

    fun getuserloaction() {
        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
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
                //initmap()
            } else{
                Toast.makeText(requireContext(), "위치정보제공의 동의가 필요합니다.", Toast.LENGTH_SHORT).show()
                // initmap()
            }
        }
    }

    fun startLocationUpdates() {
        // 1. 클래스 멤버러 위에서 선언했떤 locationRequest에 정보넣어줌. 업데이트간격 10초 가장빠른 업데이트 간격 5초로 설정
        // 엄청 빠른거임
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 50000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        //2. 로케이션 콜백 객체 만들고 함수 오버라이딩 해줌. 업데이트가 등록이 되면 조건을 만족할땜다ㅏ 위치정보를 가져옴
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    myloc = LatLng(location.latitude, location.longitude)
                    googleMapout.moveCamera(CameraUpdateFactory.newLatLngZoom(myloc, 8.0f))
                    myoptions.position(myloc)
                    myoptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    myoptions.title("현재위치")
                    val marker = googleMapout.addMarker(myoptions)
                    marker.showInfoWindow()
                }
            }
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
    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }
    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }


}

