package com.example.matchmaker

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import org.intellij.lang.annotations.JdkConstants
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import com.example.matchmaker.R
import androidx.compose.runtime.*
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Icon
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.draw.scale
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun AdvtScreen(navController: NavHostController, viewModel: ImageViewModel = viewModel()) {
    val context = LocalContext.current
    var reveal by remember { mutableStateOf(false) }
    val isDarkTheme = isSystemInDarkTheme()


    // State for handling the animation trigger
    var animationTriggered by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (animationTriggered) 1.2f else 1f, // Scale when triggered
        animationSpec = tween(durationMillis = 500, easing = FastOutSlowInEasing)
    )
    val color by animateColorAsState(
        targetValue = if (animationTriggered) Color(0xFFFFC107) else Color(0xFF6200EE), // Color change for burst
        animationSpec = tween(durationMillis = 500)
    )

    // Load interstitial ad function
    val interstitialAd = remember { mutableStateOf<InterstitialAd?>(null) }

    // Function to load and show the interstitial ad
    fun loadInterstitialAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            "ca-app-pub-9204160176455905/2667087010", // Replace with your actual Ad Unit ID
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    // Save the loaded ad in state
                    interstitialAd.value = ad

                    Toast.makeText(context, "Ad Loaded", Toast.LENGTH_SHORT).show()
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Toast.makeText(context, "Failed to load ad: ${adError.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    // Show the interstitial ad if it's loaded
    fun showInterstitialAd() {
        interstitialAd.value?.let { ad ->
            ad.show(context as Activity)
            ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    interstitialAd.value = null // Nullify after ad is dismissed
                    loadInterstitialAd() // Reload the ad
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    // Handle failure to show ad
                    Toast.makeText(context, "Failed to show ad: ${adError.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    showInterstitialAd()

    // Load ad when the screen is first displayed and whenever it's resumed
    LaunchedEffect(Unit) {
        loadInterstitialAd()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "🎭 Who's behind this app?", fontSize = 18.sp, fontWeight = FontWeight.Bold)

        AnimatedVisibility(visible = reveal) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(
                        color = if (isDarkTheme) Color(0xFF303030) else Color(0xFFF0F0F0),
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🎭 It's a community effort!", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "🚀 The power of collaboration!",
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF7447FF)
                )
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "🛠️ Together, we bring ideas to life!", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(5.dp))
                Text(text = "📌 Kotlin | Compose | Collaboration", fontSize = 14.sp)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Button(
            onClick = {
                reveal = !reveal
                animationTriggered = true
                CoroutineScope(Dispatchers.Main).launch {
                    delay(600)
                    animationTriggered = false
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF90CAF9)
            ),
            modifier = Modifier.fillMaxWidth().scale(scale)
        ) {
            Text(
                text = if (reveal) "Hide Details 🕵️" else "Who’s Behind the Magic? 🎭",
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:")
                    putExtra(Intent.EXTRA_EMAIL, arrayOf("hitwickethqua@gmail.com"))
                    putExtra(Intent.EXTRA_SUBJECT, "Contribution/Idea for App")
                }
                context.startActivity(emailIntent)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3F51B5)),
            modifier = Modifier
                .width(250.dp)
                .height(48.dp),
        ) {
            Icon(imageVector = Icons.Default.Email, contentDescription = "Email Icon", tint = Color.White)
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "Contact Us 💡", color = Color.White)
        }

        Spacer(modifier = Modifier.height(20.dp))

        // AdMob Banner Ad
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            factory = { ctx ->
                AdView(ctx).apply {
                    setAdSize(AdSize.BANNER)
                    adUnitId = "ca-app-pub-9204160176455905/2667087010" // Replace with your AdMob Ad Unit ID
                    loadAd(AdRequest.Builder().build())
                }
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                showInterstitialAd()  // Show the ad when clicked
            },
            Modifier
                .width(150.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff907ad6))
        ) {
            Text(
                text = "Get More Info 🎉",
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(50.dp))

        Text(
            text = "💡 We welcome contributors! Join us and make it even better!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }

    Spacer(modifier = Modifier.height(30.dp))

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = "📢 This app is all yours! Made for our college community, where you can post anything without restrictions. Join us and make this platform thrive! 🚀",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
    }
}



