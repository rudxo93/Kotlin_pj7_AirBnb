package com.duran.airbnb

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.util.FusedLocationSource

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private val mapView: MapView by lazy {
        findViewById<MapView>(R.id.mapView)
    }

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // onCreate 연결
        mapView.onCreate(savedInstanceState)

        // 지도 객체를 얻어서 등록
        // mapView의 getMapAsync() 메서드로 OnMapReadyCallback을 등록하면 비동기로 naverMap 객체를 얻어올 수 있다.
        // naverMap객체가 준비되면 onMapReady 콜백 메서드가 호출된다.
        mapView.getMapAsync(this)
    }

    // 지도 객체를 사용할 수 있을 때 해당 함수 자동으로 호출
    override fun onMapReady(map: NaverMap) {
        naverMap = map

        naverMap.maxZoom = 18.0 // 지도의 최대 줌 레벨
        naverMap.minZoom = 10.0 // 지도의 최소 줌 레벨

        // 카메라 이동
        // CameraUpdate = 카메라를 이동할 위치, 방법 등을 정의하는 클래스
        // scrollTo() : 카메라의 대상 지점을 지정한 좌표로 삼는다.
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.5666102, 126.9783881))
        naverMap.moveCamera(cameraUpdate) // 호출해서 카메라는 움직인다.

        // 현위치 기능
        val uiSetting = naverMap.uiSettings
        uiSetting.isLocationButtonEnabled = true // 현위치 버튼 컨트롤 사용 활성화

        // 생성자에 액티비티 객체를 전달하고 권한 요청 코드를 지정한다.
        // FusedLocationSource를 생성하고 NaverMap에 지정
        locationSource =
            FusedLocationSource(this@MainActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource
    }

    // onRequestPermissionResult()의 결과를 FusedLocationSource의 onRequestPermissionsResult()에 전달
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE)
            return

        if (locationSource.onRequestPermissionsResult(requestCode,permissions,grantResults)){
            if(!locationSource.isActivated){ // 권한 거부됨
                naverMap.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }
    
}