package no.kristiania.pgr208finalexam.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import no.kristiania.pgr208finalexam.R
import no.kristiania.pgr208finalexam.adapter.TransactionAdapter
import no.kristiania.pgr208finalexam.domain.CryptoApplication
import no.kristiania.pgr208finalexam.viewmodel.TransactionViewModel
import no.kristiania.pgr208finalexam.viewmodel.TransactionViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView
import no.kristiania.pgr208finalexam.viewmodel.WalletViewModel
import no.kristiania.pgr208finalexam.viewmodel.WalletViewModelFactory

class TransactionActivity : AppCompatActivity() {

    private val transactionViewModel: TransactionViewModel by viewModels {
        TransactionViewModelFactory((application as CryptoApplication).transactionRepository)
    }

    private lateinit var freeCoin : TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction)

        var fromIntent : Double = intent.getDoubleExtra("coinValues", 10.0)

        freeCoin = findViewById(R.id.free_coins)
        freeCoin.setText(String.format("%.2f",fromIntent))


        val bottomNavigation: BottomNavigationView = findViewById(R.id.bottom_navigation)
        bottomNavigation.selectedItemId = R.id.transactions
        bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.wallet -> {
                    val intent = Intent(this@TransactionActivity, WalletActivity::class.java)
                    intent.putExtra("coinValues", fromIntent)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.coins -> {
                    startActivity(Intent(this@TransactionActivity, CoinStatsActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> return@setOnNavigationItemSelectedListener false
            }
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerview_transactions)
        val adapter = TransactionAdapter()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        transactionViewModel.transactions.observe(this) { transactions ->
            transactions.let { adapter.submitList(it) }
        }
    }
}