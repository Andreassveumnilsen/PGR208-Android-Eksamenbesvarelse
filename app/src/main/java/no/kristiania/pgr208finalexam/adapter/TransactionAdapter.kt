package no.kristiania.pgr208finalexam.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import no.kristiania.pgr208finalexam.R
import no.kristiania.pgr208finalexam.domain.Transaction
import java.util.*

class TransactionAdapter : ListAdapter<Transaction, TransactionAdapter.TransactionViewHolder>(TRANSACTION_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current)
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txSymbolIcon: ImageView = itemView.findViewById(R.id.tx_symbol_icon)
        private val txSymbol: TextView = itemView.findViewById(R.id.tx_symbol)
        private val txVolume: TextView = itemView.findViewById(R.id.tx_volume)
        private val txType: TextView = itemView.findViewById(R.id.tx_type)
        val context = itemView.context

        fun bind(current: Transaction) {
            val iconUrl = "https://static.coincap.io/assets/icons/${current.symbol.toLowerCase(
                Locale.getDefault())}@2x.png"
            Glide.with(context)
                .load(iconUrl)
                .into(txSymbolIcon)
            txSymbol.text = current.symbol
            txVolume.text = current.volume.toString()
            txType.text = current.txType.toString()
        }

        companion object {
            fun create(parent: ViewGroup) : TransactionViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_transaction_item, parent, false)
                return TransactionViewHolder(view)
            }
        }
    }

    companion object {
        private val TRANSACTION_COMPARATOR = object : DiffUtil.ItemCallback<Transaction>() {
            override fun areItemsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem === newItem
            }

            override fun areContentsTheSame(oldItem: Transaction, newItem: Transaction): Boolean {
                return oldItem.txId == newItem.txId
            }
        }
    }

}