package com.example.matchmaker

import android.os.Bundle
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

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this)
        }
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



