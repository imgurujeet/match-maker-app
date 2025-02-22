package com.example.matchmaker


import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberImagePainter
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalConfiguration
import com.example.matchmaker.repository.ShortsViewModel
import com.example.matchmaker.repository.YouTubeVideo
import com.example.matchmaker.route.ShortsScreen


//api key AIzaSyCOMs1c23JsTDY6OEz2pNo7yIThrdpGLcA


import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShortsScreen(navController: NavHostController, viewModel: ShortsViewModel = viewModel()) {
    val shorts by viewModel.shortsList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.fetchRandomShorts("AIzaSyDnKRI4iV20fhGAJORty5_cvkfKysZGZwY")
    }

    val pagerState = rememberPagerState(
        initialPage = 0, // ✅ Fixed syntax
        pageCount = { shorts.size } // ✅ Pass page count inside a lambda
    )


    val coroutineScope = rememberCoroutineScope()

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize()
    ) { page ->
        ShortItem(video=shorts[page])
    }

}

@Composable
fun ShortItem(video: YouTubeVideo) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            FrameLayout(context).apply {
                val webView = WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()

                    loadUrl("https://www.youtube.com/embed/${video.id.videoId}?autoplay=1&playsinline=1&controls=0&rel=0&modestbranding=1")
                }

                addView(webView, FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ))
            }
        }
    )
}









