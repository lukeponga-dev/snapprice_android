package dev.lukeponga.pricesnap

import android.app.Application
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import dev.lukeponga.pricesnap.auth.FirebaseAuthenticationManager
import dev.lukeponga.pricesnap.data.ScanPreferenceManager
import dev.lukeponga.pricesnap.history.AppDatabase
import dev.lukeponga.pricesnap.history.HistoryRepository
import dev.lukeponga.pricesnap.network.NetworkClient

class PriceSnapApp : Application() {
    lateinit var repository: HistoryRepository
    lateinit var authManager: FirebaseAuthenticationManager
    lateinit var scanPreferenceManager: ScanPreferenceManager
    var firestore: FirebaseFirestore? = null

    override fun onCreate() {
        super.onCreate()

        scanPreferenceManager = ScanPreferenceManager(this)

        val database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "pricesnap_history_db"
        ).build()

        // Check if Firebase is initialized (i.e. google-services.json is present)
        val firebaseApp = try {
            com.google.firebase.FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            null
        }

        if (firebaseApp != null) {
            try {
                val fs = FirebaseFirestore.getInstance()
                firestore = fs
                authManager = FirebaseAuthenticationManager(com.google.firebase.auth.FirebaseAuth.getInstance())
            } catch (e: Exception) {
                firestore = null
                authManager = FirebaseAuthenticationManager(null)
            }
        } else {
            firestore = null
            authManager = FirebaseAuthenticationManager(null)
        }
        
        repository = HistoryRepository(database.historyDao(), NetworkClient.apiService, firestore)
    }
}
