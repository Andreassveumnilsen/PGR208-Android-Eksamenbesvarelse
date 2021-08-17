package no.kristiania.pgr208finalexam.repository.external

import no.kristiania.pgr208finalexam.model.CoinHistory
import no.kristiania.pgr208finalexam.model.CoinStats
import no.kristiania.pgr208finalexam.model.DetailedCoinStat
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface RetrofitService {

    @GET("v2/assets")
    fun getDataFromAPI(): Call<CoinStats>

    @GET("v2/assets/{coinName}")
    fun getSingleCoinDataFromAPI(@Path("coinName") coinName: String): Call<DetailedCoinStat>

    @GET("v2/assets/{coinName}/history")
    fun getCoinPriceHistory(
        @Path("coinName") coinName: String,
        @Query("interval") interval: String,
        @Query("start") start: Long,
        @Query("end") end: Long
    ) : Call<CoinHistory>
}