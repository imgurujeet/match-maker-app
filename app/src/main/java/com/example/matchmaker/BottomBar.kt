package com.example.matchmaker

import android.R.attr.clipToPadding
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchmaker.route.Home
import com.example.matchmaker.route.ConfessionScreen
import com.example.matchmaker.route.ShortsScreen
import com.example.matchmaker.route.AdvtScreen

@Composable
fun BottomNavigationBar(navController: NavController) {


    val bottomDestinations= listOf(
        Home,
        ConfessionScreen,
        ShortsScreen,
        AdvtScreen

    )

    val selectedIndex = rememberSaveable { mutableStateOf(0) }

    NavigationBar {
        bottomDestinations.forEachIndexed { index, destination ->
            NavigationBarItem(
                label = ({ Text(text = destination.title) }),
                icon = {
                    Icon(
                        painter = destination.icon(),
                        contentDescription = destination.title,
                        modifier = Modifier.size(24.dp),
                        tint = Color.Unspecified
                    )
                },
                selected = index == selectedIndex.value,
                onClick = {
                    selectedIndex.value = index
                    navController.navigate(bottomDestinations[index].route) {
                        popUpTo(Home.route)
                        launchSingleTop = true
                    }

                }

            )
        }
    }

}