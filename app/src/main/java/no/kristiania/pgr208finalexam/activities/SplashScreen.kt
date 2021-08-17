package no.kristiania.pgr208finalexam.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import no.kristiania.pgr208finalexam.R
import no.kristiania.pgr208finalexam.domain.CryptoApplication
import no.kristiania.pgr208finalexam.domain.WalletContent
import no.kristiania.pgr208finalexam.viewmodel.WalletViewModel
import no.kristiania.pgr208finalexam.viewmodel.WalletViewModelFactory

class SplashScreen : AppCompatActivity() {

    private val walletViewModel: WalletViewModel by viewModels {
        WalletViewModelFactory((application as CryptoApplication).walletRepository)

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)


        // Removing the top bar
        supportActionBar?.hide()

        //Intial investment
        walletViewModel.walletContent.observe(this) { walletContent ->
            if (walletContent.isEmpty()){
                walletViewModel.insert(
                    WalletContent(
                        "USDT",
                        "tether",
                        10000.0,
                        System.currentTimeMillis()
                    )
                )
            }
        }

        Handler().postDelayed({
            val intent = Intent(this@SplashScreen, CoinStatsActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}