package no.kristiania.pgr208finalexam.repository.internal

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import no.kristiania.pgr208finalexam.domain.WalletContent
import kotlinx.coroutines.flow.Flow

@Dao
interface WalletDao {

    @Query("SELECT * FROM wallet ORDER BY volume DESC")
    fun getWalletContentByVolumeDesc(): Flow<List<WalletContent>>

    @Query("SELECT * FROM wallet where id = :id")
    fun getWalletItem(id: String): Flow<WalletContent>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(walletContent: WalletContent)

    @Query("DELETE FROM wallet")
    suspend fun deleteAll()

    @Query("UPDATE wallet SET volume = ((SELECT volume FROM wallet WHERE symbol = :symbol) + :purchasedVolume) WHERE symbol = :symbol")
    suspend fun updateCoinVolumeAfterPurchase(purchasedVolume: Double, symbol: String)

    @Query("SELECT * FROM wallet where symbol = :symbol")
    suspend fun findBySymbol(symbol: String) : WalletContent

}