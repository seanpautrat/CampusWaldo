package com.example.campuswaldo.data.remote


import com.example.campuswaldo.data.model.*
import retrofit2.http.*


interface WaldoApiService {

    @GET("/waldo/today")
    suspend fun getTodayWaldo(): WaldoTodayResponse

    @GET("/waldo/code")
    suspend fun getSecretCode(): SecretCodeResponse

    @POST("/waldo/found/")
    suspend fun redeemWaldo(
        @Body request: RedeemRequest
    ): RedeemApiResponse

    @GET("/leaderboard")
    suspend fun getLeaderboard(): LeaderboardResponse
}