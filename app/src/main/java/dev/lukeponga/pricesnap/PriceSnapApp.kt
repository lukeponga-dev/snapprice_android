package dev.lukeponga.pricesnap

import android.app.Application
import androidx.room.Room
import dev.lukeponga.pricesnap.history.AppDatabase
import dev.lukeponga.pricesnap.history.HistoryRepository
import dev.lukeponga.pricesnap.network.NetworkClient

class PriceSnapApp : Application() {
    lateinit var repository: HistoryRepository

    override fun onCreate() {
        super.onCreate()

        val database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "pricesnap_history_db"
        ).build()

        repository = HistoryRepository(database.historyDao(), NetworkClient.apiService)
    }
}
