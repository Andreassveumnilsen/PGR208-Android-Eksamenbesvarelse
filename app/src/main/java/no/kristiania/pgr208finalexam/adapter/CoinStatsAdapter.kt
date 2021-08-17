package no.kristiania.pgr208finalexam.adapter

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import no.kristiania.pgr208finalexam.activities.CoinStatActivity
import no.kristiania.pgr208finalexam.R
import no.kristiania.pgr208finalexam.model.CoinStat
import kotlinx.android.synthetic.main.recyclerview_row.view.*
import java.util.*
import kotlin.collections.ArrayList

class CoinStatsAdapter : RecyclerView.Adapter<CoinStatsAdapter.CoinStatViewHolder>() {

    var items = ArrayList<CoinStat>()
    var coinvalue : Double = 0.0

    fun setListData(data: ArrayList<CoinStat>) {
        this.items = data
    }

    fun setCoinValue(value : Double){
        this.coinvalue = value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoinStatViewHolder {
        val inflater = LayoutInflater.from(parent.context).inflate(R.layout.recyclerview_row, parent, false)

        return CoinStatViewHolder(inflater)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: CoinStatViewHolder, position: Int) {
        holder.bind(items[position])
        with(holder.itemView) {
            setOnClickListener {
                val intent = Intent(context, CoinStatActivity::class.java)
                intent.putExtra("id", items[position].id)
                intent.putExtra("symbol", items[position].symbol)
                intent.putExtra("coinValues", coinvalue)

                context.startActivity(intent)
            }
        }
    }

    class CoinStatViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtViewName = view.tvName
        val txtViewChangePercent24Hr = view.tvChangePercent24Hr
        val txtViewSymbol = view.tvSymbol
        val txtViewPriceUsd = view.tvPriceUsd
        val imgViewIcon = view.ivIcon
        val context = view.context

        fun bind(data: CoinStat) {
            txtViewName.text = data.name
            bindChangePercent24Hr(data)
            txtViewSymbol.text = data.symbol
            bindPriceUsd(data)
            bindIcon(data, context)
        }

        fun bindChangePercent24Hr(data: CoinStat) {
            if (data.changePercent24Hr.toDouble() > 0.00) {
                txtViewChangePercent24Hr.setTextColor(Color.parseColor("#59c758"))
            } else {
                txtViewChangePercent24Hr.setTextColor(Color.parseColor("#d15949"))
            }
            val percentChangeTxt = data.changePercent24Hr + "%"
            txtViewChangePercent24Hr.text = percentChangeTxt
        }

        fun bindPriceUsd(data: CoinStat) {
            val priceUsdTxt = "$" +  data.priceUsd
            txtViewPriceUsd.text = priceUsdTxt
        }

        private fun bindIcon(data: CoinStat, parent: Context) {
            val iconUrl = "https://static.coincap.io/assets/icons/${data.symbol.toLowerCase(Locale.getDefault())}@2x.png"
            Glide.with(parent)
                .load(iconUrl)
                .into(imgViewIcon)
        }
    }

}