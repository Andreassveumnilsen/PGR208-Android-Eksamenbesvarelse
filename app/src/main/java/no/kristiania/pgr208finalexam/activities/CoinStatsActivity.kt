package no.kristiania.pgr208finalexam.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager.VERTICAL
import no.kristiania.pgr208finalexam.R
import no.kristiania.pgr208finalexam.adapter.CoinStatsAdapter
import no.kristiania.pgr208finalexam.model.CoinStatSorting
import no.kristiania.pgr208finalexam.model.CoinStatSorting.*
import no.kristiania.pgr208finalexam.service.QueueingService
import no.kristiania.pgr208finalexam.viewmodel.CoinStatsViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_main.*
import no.kristiania.pgr208finalexam.domain.CryptoApplication
import no.kristiania.pgr208finalexam.viewmodel.WalletViewModel
import no.kristiania.pgr208finalexam.viewmodel.WalletViewModelFactory
import java.util.*
import kotlin.properties.Delegates

class CoinStatsActivity : AppCompatActivity() {

    var coinValues by Delegates.notNull<Double>()

    private val walletViewModel: WalletViewModel by viewModels {
        WalletViewModelFactory((application as CryptoApplication).walletRepository)
    }

    private lateinit var freeCoin : TextView
    private lateinit var coinStatsAdapter: CoinStatsAdapter
    private val queuingService: QueueingService = QueueingService()

    companion object {
        var CURRENT_SORTING_METHOD = RANK
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        toolbar.inflateMenu(R.menu.menu)

        freeCoin = findViewById(R.id.free_coins)
        walletViewModel.walletContent.observe(this){ list->
            for(l in list){
                if(l.symbol == "USDT"){
                    freeCoin.text = l.volume.toString()
                }
            }
        }

        CURRENT_SORTING_METHOD = RANK
        initRecyclerView()

        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.coins
        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.wallet -> {
                    val intent = Intent(this@CoinStatsActivity, WalletActivity::class.java)
                    intent.putExtra("coinValues", coinValues)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.transactions -> {
                    val intent = Intent(this@CoinStatsActivity, TransactionActivity::class.java)
                    intent.putExtra("coinValues", coinValues)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }

        queuingService.startQueue { fetchData() }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        menu.findItem(R.id.rank).isChecked = true
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.rank -> sortData(RANK)
            R.id.priceDesc -> sortData(PRICE_DESC)
            R.id.priceAsc -> sortData(PRICE_ASC)
            R.id.percentChangeDesc -> sortData(PERCENT_CHANGE_DESC)
            R.id.percentChangeAsc -> sortData(PERCENT_CHANGE_ASC)
        }
        item.isChecked = true
        return true
    }

    private fun initRecyclerView() {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@CoinStatsActivity)
            coinStatsAdapter = CoinStatsAdapter()
            adapter = coinStatsAdapter

            val decoration = DividerItemDecoration(applicationContext, VERTICAL)
            addItemDecoration(decoration)
        }
    }

    private fun fetchData() {
        val viewModel = ViewModelProvider(this).get(CoinStatsViewModel::class.java)
        viewModel.getRecyclerListDataObserver().observe(this, {
            if (it != null) {
                coinStatsAdapter.setListData(it.data)
                coinStatsAdapter.notifyDataSetChanged()


                val list = it.data
                val listWallet = walletViewModel.walletContent.value
                Log.d("Fetch", "Entered fetchdata ${it.data.size} and ${listWallet?.size}")
                var totalValue : Double= 0.0
                for (l in list){
                    if (listWallet != null){
                        for (w in listWallet){
                            if (l.symbol.equals(w.symbol, ignoreCase = true)){
                                totalValue += l.priceUsd.toDouble()*w.volume
                            }
                        }
                    }
                }
                Log.d("Fetch", "exited fetchdata $totalValue")
                coinValues = totalValue
                freeCoin.setText(String.format("%.2f", totalValue))
                coinStatsAdapter.setCoinValue(coinValues)
            } else {
                Toast.makeText(this@CoinStatsActivity, "Unable to reach API..", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.makeApiCall()
    }

    private fun sortData(coinStatSorting: CoinStatSorting) {
        CURRENT_SORTING_METHOD = coinStatSorting
        val viewModel = ViewModelProvider(this).get(CoinStatsViewModel::class.java)
        viewModel.getRecyclerListDataObserver().observe(this, {
            if (it != null) {
                when (coinStatSorting) {
                    RANK -> it.data.sortBy { coinStat -> coinStat.rank }
                    PRICE_DESC -> it.data.sortByDescending { coinStat -> coinStat.priceUsd.toDouble() }
                    PRICE_ASC -> it.data.sortBy { coinStat -> coinStat.priceUsd.toDouble() }
                    PERCENT_CHANGE_DESC -> it.data.sortByDescending { coinStat -> coinStat.changePercent24Hr.toDouble() }
                    PERCENT_CHANGE_ASC -> it.data.sortBy { coinStat -> coinStat.changePercent24Hr.toDouble() }
                }
                coinStatsAdapter.notifyDataSetChanged()
            }
        })
    }

    // Overridden to restart the dataFetchingService
    override fun onResume() {
        queuingService.startQueue { fetchData() }
        sortData(CURRENT_SORTING_METHOD)
        super.onResume()
    }

    // Overridden to restart the dataFetchingService
    override fun onRestart() {
        queuingService.startQueue { fetchData() }
        sortData(CURRENT_SORTING_METHOD)
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