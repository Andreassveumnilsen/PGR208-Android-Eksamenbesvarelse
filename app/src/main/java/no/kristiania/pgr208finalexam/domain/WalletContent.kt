package no.kristiania.pgr208finalexam.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "wallet")
class WalletContent(
    @PrimaryKey @ColumnInfo(name = "symbol") val symbol: String,
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "volume") val volume: Double,
    @ColumnInfo(name = "lastPurchaseTimestamp") val lastPurchaseTimestamp: Long
)