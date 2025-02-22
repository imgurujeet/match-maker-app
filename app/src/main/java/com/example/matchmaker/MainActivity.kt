package com.example.matchmaker

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.matchmaker.ui.theme.MatchMakerTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import android.content.Context
import android.os.Build
import java.util.UUID
import android.provider.Settings
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.example.matchmaker.UpdateManager
import com.example.matchmaker.UpdateManager.startAutoUpdateCheck
import com.example.matchmaker.repository.ShortsViewModel
import com.google.firebase.messaging.FirebaseMessaging
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.Manifest
import com.example.matchmaker.route.AdvtScreen


class MainActivity : ComponentActivity() {
    private val confessionViewModel: ConfessionViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        confessionViewModel.initNotificationHelper(this)



        val backgroundScope = CoroutineScope(Dispatchers.IO)
        backgroundScope.launch {
            // Initialize the Google Mobile Ads SDK on a background thread.
            MobileAds.initialize(this@MainActivity) {}
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
        }


        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
        FirebaseMessaging.getInstance().subscribeToTopic("general")
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("FCM_Token", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            Log.d("FCM_Token", "Your token: $token")
        }

        startAutoUpdateCheck(this)
        setContent {
            MatchMakerTheme {
                BottomBar()
            }
        }
    }
}


@Composable
fun BottomBar(){
    val navController = rememberNavController()
    Scaffold(
        bottomBar = { BottomNavigationBar(navController) }
    ) { innerPadding ->
        NavHost(navController, startDestination = "Home", Modifier.padding(innerPadding)) {
            composable("Home") {
                val viewModel: ImageViewModel = viewModel()
                ImageBox(navController,viewModel)
            }
            composable("ConfessionScreen") {
                val viewModel: ConfessionViewModel = viewModel()
                ConfessionScreen(navController,viewModel)
            }
            composable("ShortsScreen") {
                val viewModel: ShortsViewModel = viewModel()
                //val apiKey = "AIzaSyCOMs1c23JsTDY6OEz2pNo7yIThrdpGLcA" // Replace with your actual API key
                ShortsScreen(navController,viewModel)
            }
            composable("AdvtScreen") {
                val viewModel: ImageViewModel = viewModel()
                AdvtScreen(navController,viewModel)
            }
        }
    }
}


// In your Activity/Fragment:
@Composable
fun MyApp() {
    val navController = rememberNavController()
    NavHost(navController, startDestination = "confession") {
        composable("confession") {
            ConfessionScreen(navController)
        }
    }
}

// Updated Composable with proper ViewModel initialization





