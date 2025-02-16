package com.example.matchmaker

import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedHeartIcon() {
    val infiniteTransition = rememberInfiniteTransition()

    // Animate alpha (opacity) for fade in & out effect
    val alpha = infiniteTransition.animateFloat(
        initialValue = 0.2f, // Start faded out
        targetValue = 1f,   // Fully visible
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing), // Smooth transition
            repeatMode = RepeatMode.Reverse // Reverses the animation (fade in → fade out)
        )
    )

    // Animate color change between Red and Pink
    val color = infiniteTransition.animateColor(
        initialValue = Color.Magenta,
        targetValue = Color.Red,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Heart Icon with animation
    Icon(
        imageVector = Icons.Default.Favorite,
        contentDescription = "Heart",
        tint = color.value, // Changing color
        modifier = Modifier
            .size(50.dp)
            .graphicsLayer(alpha = alpha.value) // Fading effect
    )
}
