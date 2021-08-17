package no.kristiania.pgr208finalexam.fragments.dialogs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.recyclerview_transaction_item.*
import no.kristiania.pgr208finalexam.R
import no.kristiania.pgr208finalexam.domain.Transaction
import no.kristiania.pgr208finalexam.domain.WalletContent
import no.kristiania.pgr208finalexam.model.CoinStat
import no.kristiania.pgr208finalexam.viewmodel.TransactionViewModel
import no.kristiania.pgr208finalexam.viewmodel.WalletViewModel


class SellDialog(private val transactionViewModel: TransactionViewModel, private val walletViewModel: WalletViewModel, private val coin: CoinStat) : DialogFragment() {




    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.sell_dialog_fragment, container, false)

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sellCoinAmount : EditText = view.findViewById(R.id.sell_coin_amount)
        val sellUsdAmount : EditText = view.findViewById(R.id.sell_usd_amount)
        val btn : Button = view.findViewById(R.id.dialog_sell_btn)



        sellCoinAmount.addTextChangedListener{
            val value : Double = sellCoinAmount.text.toString().toDouble()*coin.priceUsd.toDouble()
            val theText = String.format("%.2f", value)
            sellUsdAmount.setText(theText)

        }
        btn.setOnClickListener {
            createSellOrder(sellCoinAmount.text.toString().toDouble())
            dismiss()
        }


    }

    private fun createSellOrder(volume: Double) {
        Log.d("SELLDIALOG", "Trying to sell $volume ${coin.symbol} for ${coin.priceUsd.toDouble()*volume} dollars")
        val list : List<WalletContent>? = walletViewModel.walletContent.value
        if (list != null) {
     breaker@ for(item in list){
                if (item.symbol==coin.symbol){
                    Log.d("SELLDIALOG", "I'm INSIDE")
                    if(item.volume >= volume){
                        Log.d("SELLDIALOG", "YOU HAVE ENOUGH TO SELL THIS")
                        transactionViewModel.insert(Transaction(
                            id = coin.id,
                            symbol = coin.symbol,
                            txType = "SELL",
                            volume = volume,
                            price = coin.priceUsd.toDouble(),
                            timestamp = System.currentTimeMillis()))

                            walletViewModel.updateCoin(coin.priceUsd.toDouble()*volume, "USDT")
                            walletViewModel.updateCoin(-volume, coin.symbol)
                    }else{
                        Toast.makeText(context, "YOU ARE BROKE!", Toast.LENGTH_LONG).show()
                        Log.d("SELLDIALOG", "YOU ARE BROKE")
                        break@breaker
                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40).toInt()
        dialog!!.window?.setLayout(width, height)
    }

}