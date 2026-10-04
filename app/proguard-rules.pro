# Add project specific ProGuard rules here.

# Preserve line numbers and source file for Play Console deobfuscation stack traces
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Preserve generic signatures and annotations for Retrofit and Gson reflection
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes InnerClasses,EnclosingMethod

# Gson reflection support: preserve fields with @SerializedName
-keepclassmembers class * {
    @com.google.gson.annotations.SerializedName <fields>;
}

# Keep network & API data models serialized by Gson
-keep class dev.lukeponga.pricesnap.model.** { *; }
-keep class dev.lukeponga.pricesnap.network.PingResponse { *; }
-keep class dev.lukeponga.pricesnap.network.ConnectionResponse { *; }
-keep class dev.lukeponga.pricesnap.network.EngineStatus { *; }

# Keep Retrofit service interface methods
-keep interface dev.lukeponga.pricesnap.network.PriceSnapApiService { *; }
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Room Database & DAOs
-keep class * extends androidx.room.RoomDatabase
-keep @androidx.room.Entity class * { *; }
-keep @androidx.room.Dao interface * { *; }
-dontwarn androidx.room.paging.**

# Firebase App Check
-keep class com.google.firebase.appcheck.** { *; }
-dontwarn com.google.firebase.appcheck.**

