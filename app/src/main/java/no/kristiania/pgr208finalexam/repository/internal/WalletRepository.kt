package no.kristiania.pgr208finalexam.repository.internal

import android.util.Log
import androidx.annotation.WorkerThread
import no.kristiania.pgr208finalexam.domain.WalletContent
import kotlinx.coroutines.flow.Flow

class WalletRepository(private val walletDao: WalletDao) {

    val walletContent: Flow<List<WalletContent>> = walletDao.getWalletContentByVolumeDesc()

    @WorkerThread
    fun getWalletItem(id: String): Flow<WalletContent> {
        return walletDao.getWalletItem(id)
    }

    @WorkerThread
    suspend fun insert(walletContent: WalletContent) {
        walletDao.insert(walletContent)
    }

    @WorkerThread
    suspend fun deleteAll() {
        walletDao.deleteAll()
    }

    @WorkerThread
    suspend fun updateCoinAfterPurchase(volume : Double, symbol : String){
        walletDao.updateCoinVolumeAfterPurchase(volume, symbol)
    }

    @WorkerThread
    suspend fun findBySymbol(symbol: String) : WalletContent {
        val returnvalue : WalletContent = walletDao.findBySymbol(symbol)
        Log.d("WALLETREPOSITORY", returnvalue.volume.toString())

        return returnvalue
    }
}