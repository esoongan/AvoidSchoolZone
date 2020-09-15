package com.example.avoidschoolzone_201713069_seungjinlee

// 데이터베이스에다가 삽입/ 받고 하는 단위가 이거가 되는데. 데이터베이스에다가 이걸 넘기면 json형태로 필드랑 이름이랑 쌍으로 저장하게됨
// 파이어베이스가 실제 스쿨존 정보 받고주는 단위 클래스
data class Detail(var 관할경찰서명:String, var 대상시설명 :String, var 소재지도로명주소 :String, var CCTV설치대수 : String,var 위도 : String,var 경도 : String ){
    // 디폴트 생성자가 꼭 필요함
    constructor():this("noinfo","noinfo","noinfo","noinfo","noinfo","noinfo")

}