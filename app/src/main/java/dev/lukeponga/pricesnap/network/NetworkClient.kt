package dev.lukeponga.pricesnap.network

import com.google.android.gms.tasks.Tasks
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.auth.FirebaseAuth
import dev.lukeponga.pricesnap.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object NetworkClient {

    private const val BASE_URL = BuildConfig.PRICESNAP_BASE_URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BASIC
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val appCheckInterceptor = Interceptor { chain ->
        val originalRequest = chain.request()
        val requestBuilder = originalRequest.newBuilder()

        try {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                val idToken = Tasks.await(user.getIdToken(false), 5, TimeUnit.SECONDS).token
                if (!idToken.isNullOrBlank()) requestBuilder.header("Authorization", "Bearer $idToken")
            }

            val appCheck = FirebaseAppCheck.getInstance()
            val tokenTask = appCheck.getAppCheckToken(false)
            val tokenResult = Tasks.await(tokenTask, 5, TimeUnit.SECONDS)
            val token = tokenResult?.token
            if (!token.isNullOrBlank()) {
                requestBuilder.header("X-Firebase-AppCheck", token)
            }
        } catch (e: Throwable) {
            android.util.Log.w("NetworkClient", "Security token acquisition failed; backend will reject the request", e)
        }

        chain.proceed(requestBuilder.build())
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .callTimeout(150, TimeUnit.SECONDS)
        .retryOnConnectionFailure(false)
        .writeTimeout(60, TimeUnit.SECONDS)
        .addInterceptor(appCheckInterceptor)
        .addInterceptor(loggingInterceptor)
        .build()

    val apiService: PriceSnapApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PriceSnapApiService::class.java)
    }
}
