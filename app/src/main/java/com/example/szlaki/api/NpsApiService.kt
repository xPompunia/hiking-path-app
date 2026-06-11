package com.example.szlaki.api

import com.example.szlaki.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NpsApiService {
    @GET("parks")
    suspend fun getParks(
        @Query("stateCode") stateCode: String = "WY,CA,UT,WA", // Yellowstone, Yosemite, Zion, Rainier itp.
        @Query("limit") limit: Int = 20,
        @Query("q") query: String = "hiking",
        @Query("api_key") apiKey: String = BuildConfig.NPS_API_KEY
    ): NpsResponse<ParkData>

    @GET("thingstodo")
    suspend fun getThingsToDo(
        @Query("parkCode") parkCode: String,
        @Query("q") query: String = "hiking",
        @Query("api_key") apiKey: String = BuildConfig.NPS_API_KEY
    ): NpsResponse<ThingToDoItem>

    companion object {
        private const val BASE_URL = "https://developer.nps.gov/api/v1/"

        fun create(): NpsApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NpsApiService::class.java)
            }
    }
}
