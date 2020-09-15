package com.example.avoidschoolzone_201713069_seungjinlee

// 데이터베이스에다가 삽입/ 받고 하는 단위가 이거가 되는데. 데이터베이스에다가 이걸 넘기면 json형태로 필드랑 이름이랑 쌍으로 저장하게됨
// 커뮤니티 탭이 파이어베이스와 주고받는 데이터 타입
data class Community(var 스쿨존이름: String, var 사용자아이디:String, var 날짜:String, var 코멘트 :String) {
    // 디폴트 생성자가 꼭 필요함
    constructor() : this("noinfo", "noinfo","noinfo","noinfo")
}
