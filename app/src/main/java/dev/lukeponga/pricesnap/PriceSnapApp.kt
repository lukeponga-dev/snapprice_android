package dev.lukeponga.pricesnap

import android.app.Application
import androidx.room.Room
import com.google.firebase.firestore.FirebaseFirestore
import dev.lukeponga.pricesnap.auth.FirebaseAuthenticationManager
import dev.lukeponga.pricesnap.data.ScanPreferenceManager
import dev.lukeponga.pricesnap.history.AppDatabase
import dev.lukeponga.pricesnap.history.HistoryRepository
import dev.lukeponga.pricesnap.network.NetworkClient
import com.google.android.recaptcha.Recaptcha
import com.google.android.recaptcha.RecaptchaClient
import com.google.android.recaptcha.RecaptchaException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class PriceSnapApp : Application() {
    lateinit var repository: HistoryRepository
    lateinit var authManager: FirebaseAuthenticationManager
    lateinit var scanPreferenceManager: ScanPreferenceManager
    var firestore: FirebaseFirestore? = null
    
    private lateinit var recaptchaClient: RecaptchaClient
    private val recaptchaScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        scanPreferenceManager = ScanPreferenceManager(this)

        val database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "pricesnap_history_db"
        ).build()

        // Initialize Firebase with fallback to programmatic options
        val firebaseApp = try {
            com.google.firebase.FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            null
        } ?: try {
            val options = com.google.firebase.FirebaseOptions.Builder()
                .setApplicationId("1:805381652466:android:305c2b849f6c6c60fa756b")
                .setApiKey("AIzaSyDOf97HZbpeynO4NAATr4QuDj_b6izMQrg")
                .setProjectId("snapvalue-4607a")
                .setStorageBucket("snapvalue-4607a.firebasestorage.app")
                .setGcmSenderId("805381652466")
                .build()
            com.google.firebase.FirebaseApp.initializeApp(this, options)
        } catch (e: Exception) {
            android.util.Log.e("PriceSnapApp", "Failed to initialize Firebase with options", e)
            null
        }

        if (firebaseApp != null) {
            try {
                firestore = FirebaseFirestore.getInstance()
                authManager = FirebaseAuthenticationManager(com.google.firebase.auth.FirebaseAuth.getInstance())
                android.util.Log.i("PriceSnapApp", "Firebase successfully initialized")
            } catch (e: Exception) {
                android.util.Log.e("PriceSnapApp", "Failed to get Firestore/FirebaseAuth", e)
                firestore = null
                authManager = FirebaseAuthenticationManager(null)
            }
        } else {
            android.util.Log.w("PriceSnapApp", "FirebaseApp could not be initialized")
            firestore = null
            authManager = FirebaseAuthenticationManager(null)
        }
        
        repository = HistoryRepository(database.historyDao(), NetworkClient.apiService, firestore, authManager)
        
        initializeRecaptchaClient()
    }

    private fun initializeRecaptchaClient() {
        recaptchaScope.launch {
            try {
                recaptchaClient = Recaptcha.fetchClient(this@PriceSnapApp, "6LfKJsUtAAAAADXSiynxypRKf5DhLS1BiIMOFOcA")
            } catch(e: RecaptchaException) {
                // Handle errors ...
            }
        }
    }
}
