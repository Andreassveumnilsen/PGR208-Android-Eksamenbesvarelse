package no.kristiania.pgr208finalexam.domain

import android.app.Application
import no.kristiania.pgr208finalexam.repository.internal.TransactionRepository
import no.kristiania.pgr208finalexam.repository.internal.WalletRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class CryptoApplication : Application() {
    private val applicationScope = CoroutineScope(SupervisorJob())

    private val database by lazy { CryptoDatabase.getDatabase(this, applicationScope) }
    val walletRepository by lazy { WalletRepository(database.walletDao()) }
    val transactionRepository by lazy { TransactionRepository(database.transactionDao()) }
}