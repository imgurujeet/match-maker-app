# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# Google Play Services Ads
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.firebase.** { *; }
-dontwarn com.google.android.gms.**
-keep class com.example.matchmaker.** { *; }
-keep class com.google.gson.** { *; }
-keep class com.firebase.** { *; }
-keep class androidx.lifecycle.** { *; }
-keep class android.webkit.** { *; }
# ------------------------------
-keep class com.google.android.gms.** { *; }
-keep class com.google.firebase.messaging.** { *; }
-keep class com.google.firebase.analytics.** { *; }
-keep class com.google.firebase.firestore.** { *; }
-keep class com.google.firebase.database.** { *; }
-keep class com.google.firebase.storage.** { *; }

# ------------------------------
# 🚀 Keep YouTube API
# ------------------------------
-keep class com.google.api.services.youtube.** { *; }
-keep class com.google.api.client.** { *; }
-keep class com.google.api.client.http.** { *; }
-keep class com.google.api.client.googleapis.** { *; }
-keep class com.google.api.client.json.** { *; }

# ------------------------------
# 🚀 Keep Accompanist Pager
# ------------------------------
-keep class com.google.accompanist.pager.** { *; }
-keep class com.google.accompanist.pager.indicators.** { *; }

# 🚀 Keep Play Services (Google Ads & Auth)
# ------------------------------
-keep class com.google.android.gms.ads.** { *; }
-keep class com.google.android.gms.auth.** { *; }
-keep class com.google.android.gms.base.** { *; }


# ------------------------------
# 🚀 Logging (Remove for Release Builds)
# ------------------------------
-dontwarn okhttp3.**
-dontwarn retrofit2.**
-dontwarn com.google.firebase.**
-dontwarn com.squareup.okhttp3.**
-dontwarn com.bumptech.glide.**
-dontwarn com.google.android.exoplayer2.**
-dontwarn com.google.api.services.youtube.**

# Allow code shrinking but keep important logs
-assumenosideeffects class android.util.Log { *; }


# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.sun.net.httpserver.Headers
-dontwarn com.sun.net.httpserver.HttpContext
-dontwarn com.sun.net.httpserver.HttpExchange
-dontwarn com.sun.net.httpserver.HttpHandler
-dontwarn com.sun.net.httpserver.HttpServer
-dontwarn java.awt.Desktop$Action
-dontwarn java.awt.Desktop
-dontwarn org.ietf.jgss.GSSContext
-dontwarn org.ietf.jgss.GSSCredential
-dontwarn org.ietf.jgss.GSSException
-dontwarn org.ietf.jgss.GSSManager
-dontwarn org.ietf.jgss.GSSName
-dontwarn org.ietf.jgss.Oid


# Keep Retrofit API interfaces
-keep interface com.example.matchmaker.repository.YouTubeApiService { *; }

# Keep Retrofit response models
-keepclassmembers class com.example.matchmaker.repository.YouTubeResponse { *; }
-keepclassmembers class com.example.matchmaker.repository.YouTubeVideo { *; }
-keepclassmembers class com.example.matchmaker.repository.VideoId { *; }
-keepclassmembers class com.example.matchmaker.repository.Snippet { *; }
-keepclassmembers class com.example.matchmaker.repository.Thumbnails { *; }
-keepclassmembers class com.example.matchmaker.repository.Thumbnail { *; }

# Keep Retrofit and Gson classes
-keep class retrofit2.** { *; }
-keep class com.google.gson.** { *; }
-keep class kotlinx.coroutines.** { *; }

# Keep Retrofit methods and prevent obfuscation
-keepclassmembers,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Keep YouTube API related classes
-keep class com.google.api.services.youtube.** { *; }
-keep class com.google.android.youtube.** { *; }
-dontwarn com.google.api.services.youtube.**
-dontwarn com.google.android.youtube.**


-keep class androidx.lifecycle.ViewModel { *; }

