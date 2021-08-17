package no.kristiania.pgr208finalexam.viewmodel

import android.util.Log
import androidx.lifecycle.*
import no.kristiania.pgr208finalexam.domain.WalletContent
import no.kristiania.pgr208finalexam.repository.internal.WalletRepository
import kotlinx.coroutines.launch

class WalletViewModel(private val repository: WalletRepository) : ViewModel() {

    val walletContent: LiveData<List<WalletContent>> = repository.walletContent.asLiveData()

    fun insert(walletContent: WalletContent) = viewModelScope.launch {
        repository.insert(walletContent)
    }
    //not in use but left in for future development
    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }
    //not in use but left in for future development
    fun findById(id : String) = viewModelScope.launch {
        repository.getWalletItem(id)
    }

    fun updateCoin(volume: Double, symbol: String) = viewModelScope.launch {
        Log.d("WALLETVIEWMODEL", "TRYING TO UPDATE volume:  $volume symbol: $symbol")
        repository.updateCoinAfterPurchase(volume, symbol)
    }
    //not in use but left in for future development
    fun findBySymbol(symbol : String) : WalletContent?{
        var response : WalletContent? = null
        viewModelScope.launch {response = repository.findBySymbol(symbol)  }
        Log.d("WALLETVIEWMODEL", response?.symbol.toString())
        return response

    }
}

class WalletViewModelFactory(private val repository: WalletRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WalletViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WalletViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}