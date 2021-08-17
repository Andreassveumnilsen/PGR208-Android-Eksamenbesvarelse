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
import no.kristiania.pgr208finalexam.R
import no.kristiania.pgr208finalexam.domain.Transaction
import no.kristiania.pgr208finalexam.viewmodel.TransactionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import no.kristiania.pgr208finalexam.domain.WalletContent
import no.kristiania.pgr208finalexam.model.CoinStat
import no.kristiania.pgr208finalexam.viewmodel.WalletViewModel
import kotlin.coroutines.CoroutineContext

class BuyDialog(private val transactionViewModel: TransactionViewModel, private val walletViewModel: WalletViewModel,private val coin: CoinStat) : DialogFragment(), CoroutineScope {



    private var job: Job = Job()

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.buy_dialog_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buyAmount : EditText = view.findViewById(R.id.buy_usd_amount)
        val buyCoinAmount : EditText = view.findViewById(R.id.buy_coin_amount)

        buyAmount.addTextChangedListener{
            val value : Double = buyAmount.text.toString().toDouble()/coin.priceUsd.toDouble()
            val string : String = String.format("%.8f", value)  //the 8th decimal equals one satoshi
            buyCoinAmount.setText(string)
        }


        val buyBtn: Button = view.findViewById(R.id.dialog_buy_button)
        buyBtn.setOnClickListener {
            launch {
                Log.d("BUYTDIALOG", "Pressed Button trying to buy ${buyCoinAmount.text}")
                createTransaction(buyAmount.text.toString().toDouble(), buyCoinAmount.text.toString().toDouble())
                dismiss()
            }
        }
    }

    private fun createTransaction(dollarAmount: Double, volume: Double) {
        val list : List<WalletContent>? = walletViewModel.walletContent.value
        Log.d("COINSTATACTIVITY", "Trying to enter loop, list got ${list?.size} items")
        if(list != null){
            breakit@   for (l in list){

                //first we need the USDT coin at hand
                if (l.symbol == "USDT"){
                    //Then we need to check if we have enough USDT
                    if (l.volume >= dollarAmount){
                        //if you are trying to  buy usdt, fail
                        if(coin.symbol.equals("USDT", ignoreCase = true)){
                            return
                            break@breakit
                        }
                        //if you are buying a coin you have from before, update instead

                        for(l in list){
                            if (l.symbol.equals(coin.symbol, ignoreCase = true)){
                                walletViewModel.updateCoin(volume, l.symbol)
                                Log.d("BUYTDIALOG", "inside")

                                transactionViewModel.insert(
                                    Transaction(
                                        id = coin.id,
                                        symbol = coin.symbol,
                                        txType = "BUY",
                                        volume = volume,
                                        price = dollarAmount,
                                        timestamp = System.currentTimeMillis()))
                                walletViewModel.updateCoin(-dollarAmount, "USDT")
                                return
                                break@breakit
                            }
                        }

                        transactionViewModel.insert(
                            Transaction(
                                id = coin.id,
                                symbol = coin.symbol,
                                txType = "BUY",
                                volume = volume,
                                price = dollarAmount,
                                timestamp = System.currentTimeMillis()))

                        walletViewModel.insert(WalletContent(
                            coin.symbol,
                            coin.name,
                            volume,
                            System.currentTimeMillis()
                        ))

                        walletViewModel.updateCoin(-dollarAmount, "USDT")
                        break@breakit

                    }else{
                        Toast.makeText(activity, "eee", Toast.LENGTH_LONG).show()
                        break@breakit

                    }
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.40) .toInt()
        dialog!!.window?.setLayout(width, height)
    }

}