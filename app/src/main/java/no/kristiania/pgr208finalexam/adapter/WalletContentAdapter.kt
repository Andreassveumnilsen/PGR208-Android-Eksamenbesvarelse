package no.kristiania.pgr208finalexam.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import no.kristiania.pgr208finalexam.activities.CoinStatActivity
import no.kristiania.pgr208finalexam.R
import no.kristiania.pgr208finalexam.domain.WalletContent
import java.util.*

class WalletContentAdapter : ListAdapter<WalletContent, WalletContentAdapter.WalletContentViewHolder>(WALLET_CONTENT_COMPARATOR) {

    var coinvalue : Double = 0.0

    fun setCoinValue(value : Double){
        this.coinvalue = value
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletContentViewHolder {
        return WalletContentViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: WalletContentViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
        with(holder.itemView) {
            setOnClickListener {
                val intent = Intent(context, CoinStatActivity::class.java)
                intent.putExtra("id", current.id)
                intent.putExtra("symbol", current.symbol)
                intent.putExtra("coinValues", coinvalue)
                context.startActivity(intent)
            }
        }
    }

    class WalletContentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val walletContentSymbolIcon: ImageView = itemView.findViewById(R.id.wallet_symbol_icon)
        private val walletContentSymbol: TextView = itemView.findViewById(R.id.wallet_symbol)
        private val walletContentVolume: TextView = itemView.findViewById(R.id.wallet_volume)
        val context = itemView.context

        fun bind(current: WalletContent) {
            val iconUrl = "https://static.coincap.io/assets/icons/${current.symbol.toLowerCase(Locale.getDefault())}@2x.png"
            Glide.with(context)
                .load(iconUrl)
                .into(walletContentSymbolIcon)
            walletContentSymbol.text = current.symbol
            walletContentVolume.text = current.volume.toString()
        }

        companion object {
            fun create(parent: ViewGroup) : WalletContentViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_wallet_item, parent, false)
                return WalletContentViewHolder(view)
            }
        }
    }

    companion object {
        private val WALLET_CONTENT_COMPARATOR = object : DiffUtil.ItemCallback<WalletContent>() {
            override fun areItemsTheSame(oldItem: WalletContent, newItem: WalletContent): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: WalletContent, newItem: WalletContent): Boolean {
                return oldItem.symbol == newItem.symbol && oldItem.volume == newItem.volume && oldItem.lastPurchaseTimestamp == newItem.lastPurchaseTimestamp
            }
        }
    }

}