package no.kristiania.pgr208finalexam.activities

import android.content.Intent
import android.os.Bundle

import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import no.kristiania.pgr208finalexam.fragments.dialogs.BuyDialog
import no.kristiania.pgr208finalexam.R
import no.kristiania.pgr208finalexam.domain.CryptoApplication
import no.kristiania.pgr208finalexam.fragments.dialogs.SellDialog
import no.kristiania.pgr208finalexam.model.CoinHistory
import no.kristiania.pgr208finalexam.model.DetailedCoinStat
import no.kristiania.pgr208finalexam.repository.external.RetrofitInstance
import no.kristiania.pgr208finalexam.repository.external.RetrofitService
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_coin_stat.*
import no.kristiania.pgr208finalexam.model.CoinStat
import no.kristiania.pgr208finalexam.service.QueueingService
import no.kristiania.pgr208finalexam.viewmodel.*
import no.kristiania.pgr208finalexam.viewmodel.WalletViewModel
import no.kristiania.pgr208finalexam.viewmodel.WalletViewModelFactory
import org.eazegraph.lib.charts.ValueLineChart
import org.eazegraph.lib.models.ValueLinePoint
import org.eazegraph.lib.models.ValueLineSeries
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import java.time.LocalDateTime
import java.time.ZoneOffset


class CoinStatActivity : AppCompatActivity() {

    private val walletViewModel: WalletViewModel by viewModels {
        WalletViewModelFactory((application as CryptoApplication).walletRepository)
    }
    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as CryptoApplication).transactionRepository)
    }


    private lateinit var freeCoin:TextView

    private lateinit var name: TextView
    private lateinit var symbol: TextView
    private lateinit var price: TextView
    private lateinit var lastUpdate: TextView

    private lateinit var coinHowManyYouHave : TextView
    private lateinit var coinHowMuchYourCoinsAreWorthTotal : TextView
    private lateinit var coinPricePrUnit : TextView


    companion object {
        var CURRENT_COIN_ID = ""
        var CURRENT_COIN_SYMBOL = ""
    }
    private lateinit var coin : CoinStat

    private val queuingService: QueueingService = QueueingService()



    override fun onCreate(savedInstanceState: Bundle?) {
        overridePendingTransition(0,0)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coin_stat)


        val bundle: Bundle? = intent.extras
        CURRENT_COIN_ID = bundle!!.get("id") as String
        CURRENT_COIN_SYMBOL = bundle.get("symbol") as String

        var fromIntent : Double = intent.getDoubleExtra("coinValues", 10.0)


        val buyButton: Button = findViewById(R.id.buy_coin)
        buyButton.setOnClickListener { BuyDialog(transactionViewModel,walletViewModel, coin).show(supportFragmentManager, "BuyDialog") }

        val sellButton: Button = findViewById(R.id.sell_coin)
        sellButton.setOnClickListener { SellDialog(transactionViewModel, walletViewModel, coin).show(supportFragmentManager, "SellDialog") }

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.coins
        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.wallet -> {
                    val intent = Intent(this@CoinStatActivity, WalletActivity::class.java)
                    intent.putExtra("coinValues", fromIntent)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.coins -> {
                    val intent = Intent(this@CoinStatActivity, CoinStatsActivity::class.java)
                    intent.putExtra("coinValues", fromIntent)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.transactions -> {
                    val intent = Intent(this@CoinStatActivity, TransactionActivity::class.java)
                    intent.putExtra("coinValues", fromIntent)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }
        freeCoin = findViewById(R.id.free_coins)
        freeCoin.setText(String.format("%.2f",fromIntent))

        name = findViewById(R.id.coinName)
        symbol = findViewById(R.id.coinSymbol)
        price = findViewById(R.id.coinPriceUsd)
        lastUpdate = findViewById(R.id.singelCoinLastUpdate)

        coinHowManyYouHave = findViewById(R.id.singleCoinYouHave)
        coinPricePrUnit = findViewById(R.id.singelCoinYourValue)
        coinHowMuchYourCoinsAreWorthTotal = findViewById(R.id.singelCoinCalculation)



        walletViewModel.walletContent.observe(this){ walletList ->
            var howManyCoins: Double = 0.0
            for (l in walletList){
                if (l.symbol == CURRENT_COIN_SYMBOL){
                    howManyCoins = l.volume
                }
            }
            coinHowManyYouHave.text = howManyCoins.toString()
        }

        queuingService.startQueue { fetchData(CURRENT_COIN_ID) }

        fetchHistoricData(CURRENT_COIN_ID)




        val iconUrl = "https://static.coincap.io/assets/icons/${
            CURRENT_COIN_SYMBOL.toLowerCase(Locale.getDefault())}@2x.png"
        Glide.with(this@CoinStatActivity)
            .load(iconUrl)
            .into(coinIcon)



    }




    private fun fetchHistoricData(coinName: String) {
        val retrofitInstance =
            RetrofitInstance.getRetrofitInstance().create(RetrofitService::class.java)
        val start = LocalDateTime.now().minusDays(7).toEpochSecond(ZoneOffset.UTC) * 1000
        val end = System.currentTimeMillis()
        Log.d("CHART_REQ", "start: $start, end: $end")
        val call =
            retrofitInstance.getCoinPriceHistory(
                coinName.toLowerCase(Locale.getDefault()),
                "h12",
                LocalDateTime.now().minusDays(7).toEpochSecond(ZoneOffset.UTC) * 1000,
                System.currentTimeMillis()
            )
        call.enqueue(object : retrofit2.Callback<CoinHistory> {
            override fun onResponse(
                call: Call<CoinHistory>,
                response: Response<CoinHistory>
            ) {
                if (response.isSuccessful) {
                    val mCubicValueLineChart = findViewById<View>(R.id.lineChartView) as ValueLineChart

                    val series = ValueLineSeries()
                    series.color = -0xa9480f

                    response.body()?.data?.forEach {
                        series.addPoint(
                            ValueLinePoint(
                                "${it.date.substring(5,7)}/${it.date.substring(8,10)}", // henter ut de 2 sifferene for mnd og dag.
                                it.priceUsd.toFloat())
                        )
                    }

                    mCubicValueLineChart.addSeries(series)
                    mCubicValueLineChart.startAnimation()
                }
            }

            override fun onFailure(call: Call<CoinHistory>, t: Throwable) {
                return
            }
        })
    }

    private fun fetchData(coinName: String) {
        val retrofitInstance =
            RetrofitInstance.getRetrofitInstance().create(RetrofitService::class.java)
        val call =
            retrofitInstance.getSingleCoinDataFromAPI(coinName.toLowerCase(Locale.getDefault()))
        call.enqueue(object : retrofit2.Callback<DetailedCoinStat> {
            override fun onResponse(
                call: Call<DetailedCoinStat>,
                response: Response<DetailedCoinStat>
            ) {
                if (response.isSuccessful) {
                    name.text = response.body()?.data?.name
                    symbol.text = response.body()?.data?.symbol
                    price.text = trimDigitWithXDecimalPlaces(2, response.body()?.data?.priceUsd.toString())
                    lastUpdate.text = convertTimestampToDate(response.body()?.timestamp!!)
                    coinPricePrUnit.text = price.text.toString()
                    coin = response.body()?.data!! //this is for buy and selling coin.
                    val value : Double = coinHowManyYouHave.text.toString().toDouble()*coinPricePrUnit.text.toString().toDouble()
                    coinHowMuchYourCoinsAreWorthTotal.text = value.toString()

                }
            }
            override fun onFailure(call: Call<DetailedCoinStat>, t: Throwable) {
                return
            }
        })
    }



    private fun trimDigitWithXDecimalPlaces(decimalPlaces: Int, input: String): String {
        return input.substringBefore('.') + "." + input.substringAfter(
            '.'
        ).substring(0, decimalPlaces)
    }

    private fun convertTimestampToDate(input: Long): String {
        val sdf = SimpleDateFormat("hh:mm:ss")
        val netDate = Date(input)
        return "Last updated: ${sdf.format(netDate)}"
    }


    // Overridden to restart the dataFetchingService
    override fun onResume() {
        queuingService.startQueue { fetchData(CURRENT_COIN_ID) }
        super.onResume()
    }

    // Overridden to restart the dataFetchingService
    override fun onRestart() {
        queuingService.startQueue { fetchData(CURRENT_COIN_ID) }
        super.onRestart()
    }

    // Overridden to stop the dataFetchingService, and dump the queue
    override fun onPause() {
        queuingService.stopQueue()
        super.onPause()
    }

    // Overridden to stop the dataFetchingService, and dump the queue
    override fun onDestroy() {
        queuingService.stopQueue()
        super.onDestroy()
    }

    // Overridden to stop the dataFetchingService, and dump the queue
    override fun onBackPressed() {
        queuingService.stopQueue()
        super.onBackPressed()
    }
}


