package no.kristiania.pgr208finalexam.repository.internal

import androidx.annotation.WorkerThread
import no.kristiania.pgr208finalexam.domain.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(private val transactionDao: TransactionDao) {

    val transactions: Flow<List<Transaction>> = transactionDao.getTransactions()

    @WorkerThread
    suspend fun insert(transaction: Transaction) {
        transactionDao.insert(transaction)
    }
    //not in use but left in for future development
    @WorkerThread
    fun getAllTransactions() {
        transactionDao.getTransactions()
    }
}