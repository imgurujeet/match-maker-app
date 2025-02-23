package com.example.matchmaker.route

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import com.example.matchmaker.R

interface Destinations {
    val route: String
    @Composable
    fun icon(): Painter
    val title: String
}

object Home : Destinations {
    override val route = "Home"
    @Composable
    override fun icon(): Painter = painterResource(id = R.drawable.search)
    override val title: String= "Match"
}

object ConfessionScreen : Destinations {
    override val route = "ConfessionScreen"
    @Composable
    override fun icon(): Painter = painterResource(id = R.drawable.finger_crossed)
    override val title: String= "SayIt"
}

object ShortsScreen : Destinations {
    override val route = "ShortsScreen"
    @Composable
    override fun icon(): Painter = painterResource(id=R.drawable.ytshort)
    override val title: String= "CircleReels"
}

object AdvtScreen : Destinations {
    override val route = "AdvtScreen"
    @Composable
    override fun icon(): Painter = painterResource(id=R.drawable.appdev)
    override val title: String= "Join"
}