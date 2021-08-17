package no.kristiania.pgr208finalexam.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
class Transaction (
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "txId") val txId: Int = 0,
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "symbol") val symbol: String,
    @ColumnInfo(name = "txType") val txType: String, // BOUGHT, SOLD, INSTALLATION REWARD
    @ColumnInfo(name = "volume") val volume: Double,
    @ColumnInfo(name = "price") val price: Double,
    @ColumnInfo(name = "timestamp") val timestamp: Long
)