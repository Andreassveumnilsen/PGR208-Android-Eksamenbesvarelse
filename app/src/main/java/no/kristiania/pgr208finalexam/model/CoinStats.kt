package no.kristiania.pgr208finalexam.model

data class CoinStats(val data: ArrayList<CoinStat>)
data class CoinStat(val rank: Long, val id: String, val name: String, var changePercent24Hr: String, val symbol: String, var priceUsd: String)
data class DetailedCoinStat(val data: CoinStat, val timestamp: Long)
data class CoinHistory(val data: ArrayList<CoinHistoryItem>)
data class CoinHistoryItem(val priceUsd: String, val time: Long, val date: String)