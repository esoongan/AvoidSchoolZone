package com.example.avoidschoolzone_201713069_seungjinlee

import android.Manifest
import android.content.pm.PackageManager
import android.os.AsyncTask
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.app.ActivityCompat
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


class Maps1Fragment : Fragment(),OnMapReadyCallback {
    lateinit var rootView: View
    lateinit var mapView: MapView
    lateinit var googleMapout: GoogleMap
    val options = MarkerOptions()
    val myoptions = MarkerOptions()
    var name = ArrayList<String>()
    var latitude = ArrayList<String>()
    var longitude = ArrayList<String>()
    var caslt_cnt = ArrayList<String>()
    var occrrnc_cnt = ArrayList<String>()
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
        rootView = inflater.inflate(R.layout.fragment_maps1, container, false)
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
//        val locationRequest = LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)//정확도최우선
//            .setInterval(5000)//위치가 업데이트 되는 주기
//            .setFastestInterval(50000)// 위취획득후 업데이트 되는 주기
//
//        val builder:LocationSettingsRequest.Builder = LocationSettingsRequest.Builder()
//        builder.addLocationRequest(locationRequest)
//
//        //FusedLocationProviderClient 객체생성
//        val mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireContext())

    }






    override fun onMapReady(googleMap: GoogleMap) {

        googleMapout = googleMap

        initLocation()
        jsonParsing()
        getBundle()
    }

    fun getBundle(){
        val bundle = arguments?.getBundle("bundle")
        val mylat = bundle?.getDouble("mylat")
        Log.i("testest", mylat.toString())
    }

    fun jsonParsing() {

        lateinit var temp: String

        class MyAsyncTask() : AsyncTask<Unit, Unit, String>() {
            override fun doInBackground(vararg params: Unit?): String {
                val stream =
                    URL("http://apis.data.go.kr/B552061/schoolzoneChild/getRestSchoolzoneChild?serviceKey=12mOkgcAgeadHdzeSRUGgKUvI27BrpwUiYv%2B7HH4DRlTCP5ZswzgQmu7Hn63cwQJnQVWvDeHVbaUarR0eY1rYw%3D%3D&searchYearCd=2018&siDo=&guGun=&type=json&numOfRows=100&pageNo=1&").openStream()
                val read = BufferedReader(InputStreamReader(stream, "UTF-8"))
                temp = read.readLine()
                // 이거는 되 는ㄷ ㅔ ㅠㅠㅠ  Log.i("ddddd", temp)
                return temp
            }

            //위에서 수행한 결과를 String값으로 받아옴 (result)
            override fun onPostExecute(result: String) {
                super.onPostExecute(result)
                Log.i("ddddd", result)
                //result에 저장된 텍스트형식의 json파일있음.
                val jsonObject = JSONObject(result)
                val itemArray = jsonObject.getJSONObject("items")
                val array = itemArray.getJSONArray("item")
                for (i in 0 until array.length()) {
                    name.add(array.getJSONObject(i).getString("spot_nm"))  // 구역이름.
                    latitude.add(array.getJSONObject(i).getString("la_crd"))//위도.
                    longitude.add(array.getJSONObject(i).getString("lo_crd"))//경도.
                    caslt_cnt.add(array.getJSONObject(i).getString("caslt_cnt"))//사상자수.
                    occrrnc_cnt.add(array.getJSONObject(i).getString("occrrnc_cnt"))//발생건수.

                }
                for (i in 0 until latitude.size) {
                    loc = LatLng(latitude[i].toDouble(), longitude[i].toDouble())
                    options.position(loc)
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                    options.title(" 사고다발 어린이보호구역")
                    options.snippet(name[i])
                    val marker = googleMapout.addMarker(options)
                    marker.showInfoWindow()

                }

            }
        }
        MyAsyncTask().execute()
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
            Log.i("맵프래그먼 액티비티에서 현재 내위치위도", myloc.latitude.toString())

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
            interval = 1000
            fastestInterval = 50000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
        //2. 로케이션 콜백 객체 만들고 함수 오버라이딩 해줌. 업데이트가 등록이 되면 조건을 만족할땜다ㅏ 위치정보를 가져옴
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult ?: return
                for (location in locationResult.locations) {
                    myloc = LatLng(location.latitude, location.longitude)
                    googleMapout.moveCamera(CameraUpdateFactory.newLatLngZoom(myloc, 10.0f))
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

