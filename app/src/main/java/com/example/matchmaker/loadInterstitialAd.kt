package com.example.matchmaker

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback


var interstitialAd: InterstitialAd? = null



fun loadInterstitialAd(context: Context) {
    val adRequest = AdRequest.Builder().build()
    InterstitialAd.load(
        context,
        "ca-app-pub-9204160176455905/2667087010", // Replace with your ad unit ID
        adRequest,
        object : InterstitialAdLoadCallback() {
            override fun onAdLoaded(ad: InterstitialAd) {
                interstitialAd = ad // Store the loaded ad
                Log.d("AdMob", "Interstitial ad loaded")
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                Log.d("AdMob", "Failed to load interstitial ad: ${loadAdError.message}")
            }
        })
}
