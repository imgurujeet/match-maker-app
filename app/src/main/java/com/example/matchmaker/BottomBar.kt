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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

@Composable
fun BottomNavigationBar(navController: NavController) {
    Card (
        modifier = Modifier
            .fillMaxWidth()
            .padding(0.dp)
            .height(100.dp)
            .padding(
                bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            ),
        elevation=CardDefaults.cardElevation(defaultElevation = 10.dp),


    ) {

        Row(
            modifier = Modifier.fillMaxSize(),
               // .windowInsetsPadding(WindowInsets.navigationBars),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,


        ){

            Column (horizontalAlignment = Alignment.CenterHorizontally) {

                IconButton(onClick = { navController.navigate("Home") {
                    popUpTo("Home") { inclusive = false }
                       // saveState = true // Saves the previous state

                    launchSingleTop = true
                    restoreState = true
                    }
                }
                )
                {

                    Image(
                        painter = painterResource(id=R.drawable.search),
                        contentDescription = "search",
                        modifier = Modifier.size(36.dp),
                        contentScale = ContentScale.Inside
                    )
//                    Icon(Icons.Filled.Face, contentDescription = "Home",
//
//                        modifier = Modifier.size(50.dp),
//                        tint = Color.Yellow
//                    )
                }
                Text(
                    text = "Match",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

            }

            Column (horizontalAlignment = Alignment.CenterHorizontally){
                IconButton(onClick = { navController.navigate("ConfessionScreen"){
                    launchSingleTop = true
                    restoreState = true
                }
                }) {
                    Image(
                        painter = painterResource(id=R.drawable.finger_crossed),
                        contentDescription = "ConfessionScreen",
                        modifier = Modifier.size(36.dp)
                    )
                  //  Text(text = "🤞", fontSize = 32.sp)
//                Icon(Icons.Filled.Favorite, contentDescription = "ConfessionScreen",
//                    modifier = Modifier.size(50.dp)
//                )
                }
                Text(
                    text = "Confess",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )

            }

        }

    }


}