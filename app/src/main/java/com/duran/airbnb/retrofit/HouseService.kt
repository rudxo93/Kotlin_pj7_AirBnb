package com.duran.airbnb.retrofit

import retrofit2.Call
import retrofit2.http.GET

interface HouseService {
    @GET("/v3/46d29e93-e91f-4ba0-adbe-72665f091cc1") // 생성해둔 mocky주소
    fun getHouseList(): Call<HouseDto>
}