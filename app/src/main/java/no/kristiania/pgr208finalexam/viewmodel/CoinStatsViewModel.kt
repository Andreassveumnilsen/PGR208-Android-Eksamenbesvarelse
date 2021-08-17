package no.kristiania.pgr208finalexam.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import no.kristiania.pgr208finalexam.model.CoinStats
import no.kristiania.pgr208finalexam.repository.external.RetrofitInstance
import no.kristiania.pgr208finalexam.repository.external.RetrofitService
import retrofit2.Call
import retrofit2.Response

class CoinStatsViewModel : ViewModel() {

    var coinStatsData: MutableLiveData<CoinStats> = MutableLiveData()

    fun getRecyclerListDataObserver(): MutableLiveData<CoinStats> {
        return coinStatsData
    }

    fun makeApiCall() {
        val retrofitInstance = RetrofitInstance.getRetrofitInstance().create(RetrofitService::class.java)
        val call = retrofitInstance.getDataFromAPI()
        call.enqueue(object : retrofit2.Callback<CoinStats> {
            override fun onResponse(call: Call<CoinStats>, response: Response<CoinStats>) {
                if (response.isSuccessful) {
                    for (i in response.body()?.data!!) {
                        i.changePercent24Hr = trimDigitWithXDecimalPlaces(4, i.changePercent24Hr)
                        i.priceUsd = trimDigitWithXDecimalPlaces(2, i.priceUsd)
                    }
                    coinStatsData.postValue(response.body())
                } else {
                    coinStatsData.postValue(null)
                }
            }

            override fun onFailure(call: Call<CoinStats>, t: Throwable) {
                coinStatsData.postValue(null)
            }
        })
    }

    private fun trimDigitWithXDecimalPlaces(decimalPlaces: Int, input: String): String {
        return input.substringBefore('.') + "." + input.substringAfter(
            '.'
        ).substring(0, decimalPlaces)
    }
}