package dev.lukeponga.pricesnap

import android.app.Application
import androidx.room.Room
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory
import com.google.firebase.appcheck.recaptcha.RecaptchaAppCheckProviderFactory
import com.google.firebase.auth.FirebaseAuth
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

/**
 * Main Application class for PriceSnap.
 * Handles initialization of dependencies, Firebase, Room database, and external SDKs.
 */
class PriceSnapApp : Application() {
    companion object {
        const val RECAPTCHA_SITE_KEY = "6LfKJsUtAAAAADXSiynxypRKf5DhLS1BiIMOFOcA"
    }

    lateinit var repository: HistoryRepository
    lateinit var authManager: FirebaseAuthenticationManager
    lateinit var scanPreferenceManager: ScanPreferenceManager
    var firestore: FirebaseFirestore? = null

    private var recaptchaClient: RecaptchaClient? = null
    private val recaptchaScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        initializeLocalData()
        initializeFirebase()
        initializeRepositories()
        initializeExternalSdks()
    }

    private fun initializeLocalData() {
        scanPreferenceManager = ScanPreferenceManager(this)
    }

    private fun initializeFirebase() {
        // Initialize Firebase with fallback to programmatic options
        val firebaseApp = try {
            FirebaseApp.initializeApp(this)
        } catch (e: Exception) {
            null
        } ?: try {
            val options = FirebaseOptions.Builder()
                .setApplicationId("1:805381652466:android:305c2b849f6c6c60fa756b")
                .setApiKey("AIzaSyDOf97HZbpeynO4NAATr4QuDj_b6izMQrg")
                .setProjectId("snapvalue-4607a")
                .setStorageBucket("snapvalue-4607a.firebasestorage.app")
                .setGcmSenderId("805381652466")
                .build()
            FirebaseApp.initializeApp(this, options)
        } catch (e: Exception) {
            android.util.Log.e("PriceSnapApp", "Failed to initialize Firebase with options", e)
            null
        }

        if (firebaseApp != null) {
            setupAppCheck(firebaseApp)
            setupFirebaseServices()
        } else {
            android.util.Log.w("PriceSnapApp", "FirebaseApp could not be initialized")
            firestore = null
            authManager = FirebaseAuthenticationManager(null)
        }
    }

    private fun setupAppCheck(firebaseApp: FirebaseApp) {
        try {
            val appCheck = FirebaseAppCheck.getInstance(firebaseApp)
            val appCheckFactory = if (BuildConfig.DEBUG) {
                DebugAppCheckProviderFactory.getInstance()
            } else {
                RecaptchaAppCheckProviderFactory.getInstance(RECAPTCHA_SITE_KEY)
            }
            appCheck.installAppCheckProviderFactory(appCheckFactory)
            appCheck.setTokenAutoRefreshEnabled(true)
            android.util.Log.i("PriceSnapApp", "Firebase App Check successfully initialized")
        } catch (e: Exception) {
            android.util.Log.w("PriceSnapApp", "Firebase App Check initialization skipped/failed: ${e.message}")
        }
    }

    private fun setupFirebaseServices() {
        try {
            firestore = FirebaseFirestore.getInstance()
            authManager = FirebaseAuthenticationManager(FirebaseAuth.getInstance())
            android.util.Log.i("PriceSnapApp", "Firebase successfully initialized")
        } catch (e: Exception) {
            android.util.Log.e("PriceSnapApp", "Failed to get Firestore/FirebaseAuth", e)
            firestore = null
            authManager = FirebaseAuthenticationManager(null)
        }
    }

    private fun initializeRepositories() {
        val database = Room.databaseBuilder(
            this,
            AppDatabase::class.java,
            "pricesnap_history_db"
        ).build()
        repository = HistoryRepository(database.historyDao(), NetworkClient.apiService, firestore, authManager, this)
    }

    private fun initializeExternalSdks() {
        initializeRecaptchaClient()
        initializeGoogleMobileAds()
    }

    private fun initializeGoogleMobileAds() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                com.google.android.gms.ads.MobileAds.initialize(this@PriceSnapApp) { status ->
                    android.util.Log.i("PriceSnapApp", "Google Mobile Ads initialized: $status")
                }
            } catch (e: Exception) {
                android.util.Log.w("PriceSnapApp", "MobileAds initialization skipped: ${e.message}")
            }
        }
    }

    private fun initializeRecaptchaClient() {
        recaptchaScope.launch {
            try {
                recaptchaClient = Recaptcha.fetchClient(this@PriceSnapApp, RECAPTCHA_SITE_KEY)
            } catch(e: RecaptchaException) {
                android.util.Log.e("PriceSnapApp", "Failed to fetch Recaptcha client", e)
            } catch(e: Exception) {
                android.util.Log.w("PriceSnapApp", "Recaptcha initialization skipped: ${e.message}")
            }
        }
    }
}
