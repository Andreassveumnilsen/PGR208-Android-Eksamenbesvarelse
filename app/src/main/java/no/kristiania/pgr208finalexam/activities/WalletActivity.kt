package no.kristiania.pgr208finalexam.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.pgr208finalexam.R
import no.kristiania.pgr208finalexam.adapter.WalletContentAdapter
import no.kristiania.pgr208finalexam.domain.CryptoApplication
import no.kristiania.pgr208finalexam.domain.WalletContent
import no.kristiania.pgr208finalexam.viewmodel.WalletViewModel
import no.kristiania.pgr208finalexam.viewmodel.WalletViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.main_toolbar.*

class WalletActivity : AppCompatActivity() {

    private val walletViewModel: WalletViewModel by viewModels {
        WalletViewModelFactory((application as CryptoApplication).walletRepository)

    }

    private lateinit var freeCoin : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        var fromIntent : Double = intent.getDoubleExtra("coinValues", 10.0)


        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.wallet
        bottomNavigation.setOnNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.coins -> {

                    startActivity(Intent(this@WalletActivity, CoinStatsActivity::class.java))
                    overridePendingTransition(0,0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.transactions -> {
                    val intent = Intent(this@WalletActivity, TransactionActivity::class.java)
                    intent.putExtra("coinValues", fromIntent)
                    startActivity(intent)
                    overridePendingTransition(0,0)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }



        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_wallet)
        val adapter = WalletContentAdapter()
        adapter.setCoinValue(fromIntent)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)






        freeCoin = findViewById(R.id.free_coins)
        freeCoin.setText(String.format("%.2f",fromIntent))




        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        walletViewModel.walletContent.observe(this) { walletContent ->
            // Update the cached copy of the words in the adapter.
            walletContent.let { adapter.submitList(it) }
        }
    }



}