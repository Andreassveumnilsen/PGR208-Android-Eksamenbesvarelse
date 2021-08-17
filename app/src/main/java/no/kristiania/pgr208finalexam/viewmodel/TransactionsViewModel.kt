package no.kristiania.pgr208finalexam.viewmodel

import androidx.lifecycle.*

import no.kristiania.pgr208finalexam.repository.internal.TransactionRepository
import kotlinx.coroutines.launch
import no.kristiania.pgr208finalexam.domain.Transaction

class TransactionViewModel(private val repository: TransactionRepository) : ViewModel() {

    val transactions: LiveData<List<Transaction>> = repository.transactions.asLiveData()

    fun insert(transaction: Transaction) = viewModelScope.launch {
        repository.insert(transaction)
    }
}

class TransactionViewModelFactory(private val repository: TransactionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TransactionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TransactionViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}