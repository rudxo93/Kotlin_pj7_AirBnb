package com.duran.airbnb

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.duran.airbnb.adapter.HouseListAdapter
import com.duran.airbnb.adapter.HouseViewPagerAdapter
import com.duran.airbnb.retrofit.HouseDto
import com.duran.airbnb.retrofit.HouseModel
import com.duran.airbnb.retrofit.HouseService
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.Overlay
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.util.MarkerIcons
import com.naver.maps.map.widget.LocationButtonView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class MainActivity : AppCompatActivity(), OnMapReadyCallback, Overlay.OnClickListener {

    private val mapView: MapView by lazy {
        findViewById<MapView>(R.id.mapView)
    }

    private lateinit var naverMap: NaverMap
    private lateinit var locationSource: FusedLocationSource

    private val viewPager: ViewPager2 by lazy {
        findViewById(R.id.houseViewPager)
    }

    private val viewPagerAdapter = HouseViewPagerAdapter(itemClicked = {
        onHouseModelClicked(houseModel = it)
    })
    private val recyclerViewAdapter = HouseListAdapter()

    private val recyclerView: RecyclerView by lazy {
        findViewById(R.id.recyclerView)
    }

    private val currentLocationButton : LocationButtonView by lazy {
        findViewById(R.id.currentLocationButton)
    }

    private val bottomSheetTitleTextView : TextView by lazy {
        findViewById(R.id.bottomSheetTitleTextView)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // onCreate 연결
        mapView.onCreate(savedInstanceState)

        // 지도 객체를 얻어서 등록
        // mapView의 getMapAsync() 메서드로 OnMapReadyCallback을 등록하면 비동기로 naverMap 객체를 얻어올 수 있다.
        // naverMap객체가 준비되면 onMapReady 콜백 메서드가 호출된다.
        mapView.getMapAsync(this)

        viewPager.adapter = viewPagerAdapter
        recyclerView.adapter = recyclerViewAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // page 변경시 처리
        viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val selectedHouseModel = viewPagerAdapter.currentList[position]
                val cameraUpdate = CameraUpdate.scrollTo(LatLng(selectedHouseModel.lat,selectedHouseModel.lng))
                    .animate(CameraAnimation.Easing)

                naverMap.moveCamera(cameraUpdate)
            }
        })
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
        uiSetting.isLocationButtonEnabled = false

        currentLocationButton.map = naverMap

        // 생성자에 액티비티 객체를 전달하고 권한 요청 코드를 지정한다.
        // FusedLocationSource를 생성하고 NaverMap에 지정
        locationSource =
            FusedLocationSource(this@MainActivity, LOCATION_PERMISSION_REQUEST_CODE)
        naverMap.locationSource = locationSource

        /*
        // 마커 기능 - 지도상의 한 지점을 나타낸다.
        val marker = Marker()
        marker.position = LatLng(37.5670135, 126.9783740) // 좌표
        marker.map = naverMap // null을 지정하면 지도에서 마커가 사라진다.
        marker.icon = MarkerIcons.BLACK // 검은색 아이콘 -> 덧입히기 적합한 이미지인 MarkerIcons.BLACK을 빌트인으로 제공
        marker.iconTintColor = Color.RED // 덧입힐 색상
        */
        // 지도 전부 로드 이후에 가져오기
        getHouseListFromAPI()
    }

    private fun getHouseListFromAPI() {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://run.mocky.io")
            .addConverterFactory(retrofit2.converter.gson.GsonConverterFactory.create())
            .build()

        retrofit.create(HouseService::class.java).also {
            it.getHouseList()
                .enqueue(object : Callback<HouseDto> {
                    override fun onResponse(call: Call<HouseDto>, response: Response<HouseDto>) {
                        if(response.isSuccessful.not()) {
                            // fail
                            Log.d("Retrofit", "실패1")
                            return
                        }
                        response.body()?.let { dto ->
                            updateMarker(dto.items)
                            viewPagerAdapter.submitList(dto.items)
                            recyclerViewAdapter.submitList(dto.items)
                            bottomSheetTitleTextView.text = "${dto.items.size}개의 숙소"
                        }
                    }
                    override fun onFailure(call: Call<HouseDto>, t: Throwable) {
                        // 실패 처리 구현;
                        Log.d("Retrofit", "실패2")
                        Log.d("Retrofit", t.stackTraceToString())
                    }
                })
        }
    }

    private fun updateMarker(houses: List<HouseModel>){
        houses.forEach { house ->

            val marker = Marker()
            marker.position = LatLng(house.lat, house.lng)
            marker.onClickListener = this // 마커 클릭 시 viewPager연동
            marker.map = naverMap
            marker.tag = house.id
            marker.icon = MarkerIcons.BLACK
            marker.iconTintColor = Color.RED

        }
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

    override fun onClick(overlay: Overlay): Boolean {
        // overlay : 마커

        val selectedModel = viewPagerAdapter.currentList.firstOrNull{
            it.id == overlay.tag
        }
        selectedModel?.let{
            val position = viewPagerAdapter.currentList.indexOf(it)
            viewPager.currentItem = position
        }
        return true
    }

    private fun onHouseModelClicked(houseModel: HouseModel){
        // 공유 기능; 인텐트에있는 츄져사용할것임
        val intent = Intent()
            .apply {
                action = Intent.ACTION_SEND
                putExtra(
                    Intent.EXTRA_TEXT,
                    "[지금 이 가격에 예약하세요!!] ${houseModel.title} ${houseModel.price} 사진 보기(${houseModel.imgUrl}",
                )
                type = "text/plain"
            }
        startActivity(Intent.createChooser(intent, null))
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1000
    }

}