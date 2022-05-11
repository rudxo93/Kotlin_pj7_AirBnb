# 에어비앤비 앱 프로젝트 클론 코딩
## _사용 라이브러리 및 skill_

### _build.gradle_
- Naver Map API
- Retrofit
- Glide

### _App_
 - Behavior
 - layout include
 - CoordinatorLayout
 - OnMapReadyCallback 
 - 레이아웃 include

## _Skill Study_

### _Naver Map API_

https://navermaps.github.io/android-map-sdk/guide-ko/1.html
 -> 지도객체(MapView) / 카메라 이동(CameraUpdate) / 위치(FusedLocationSource) / 마커
 
 * 의존성을 추가하는데 이때 `build.gradle`이 아니라 `setting.gradle`에 의존성을 추가한다.
 * ` maven{ url "https://naver.jfrog.io/artifactory/maven/" } `
 
### _Glide_
 이미지 처리 라이브러리
 dependency 
 ```sh
 implementation 'com.github.bumptech.glide:glide:4.12.0'
 ```
의존성을 추가해준다.
 ```sh
val imgView: ImageView = findViewById(R.id.img1)
Glide.with(this).load("외부 url 링크").into(imgView)
 ```
외부에 존재하는 사진 링크를 가지고 이를 불러올것이다.
load안은 내가 선택한 사진의 링크를 넣어준다.
실행하게되면 img1이라는 id를 가진 이미지뷰가 내가 선택한 사진으로 바뀐다.
 ```sh
<uses-permission android:name="android.permission.INTERNET" />
 ```
외부 url링크가 실행하기 전 인터넷 권한을 주어야한다.

### _CoordinatorLayout과 Behavior_

https://06block26.tistory.com/25
-> 개인 블로그에 개념 정리

### _OnMapReadyCallback_

지도 객체를 얻으려면 `OnMapReadyCallback` 인터페이스를 구현한 클래스를 `getMapAsync ( )` 함수를 이용하여 등록한다.
이렇게 해놓으면 지도 객체를 사용할 수 있을 때 `onMapReady( )` 함수가 자동으로 호출되면서 매개변수로 NaverMap 객체가
전달된다.

```sh
MainActivity.kt
...
class MainActivity : AppCompatActivity(),OnMapReadyCallback {
    ...
    naverMapView.getMapAsync(this)
}

override fun onMapReady(map: NaverMap) {
    naverMap = map
    ...
}
```

### 레이아웃 include
include는 한 번 작성한 레이아웃의 일부를 여기저기에서 가져다 쓸 수 있도록 해준다.(layout 재사용)
```sg
<include layout="@layout/bottom_sheet"/>
```
