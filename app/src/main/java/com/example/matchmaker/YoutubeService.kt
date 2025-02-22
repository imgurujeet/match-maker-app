package com.example.matchmaker.repository

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.awaitResponse
import retrofit2.http.GET
import retrofit2.http.Query
import kotlin.random.Random

// ============================ API SERVICE ============================
interface YouTubeApiService {
    @GET("search")
    fun getShorts(
        @Query("part") part: String = "snippet",
        @Query("channelId") channelId: String,
        @Query("maxResults") maxResults: Int = 10,
        @Query("type") type: String = "video",
        @Query("order") order: String = "date",
        @Query("key") apiKey: String
    ): retrofit2.Call<YouTubeResponse>
}

// ============================ DATA MODELS ============================
data class YouTubeResponse(val items: List<YouTubeVideo>)

data class YouTubeVideo(
    val id: VideoId,
    val snippet: Snippet
)

data class VideoId(val videoId: String)

data class Snippet(
    val title: String,
    val thumbnails: Thumbnails
)

data class Thumbnails(
    val high: Thumbnail
)

data class Thumbnail(val url: String)

// ============================ RETROFIT INSTANCE ============================
object RetrofitInstance {
    val api: YouTubeApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://www.googleapis.com/youtube/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(YouTubeApiService::class.java)
    }
}

// ============================ VIEWMODEL ============================
class ShortsViewModel : ViewModel() {
    private val _shortsList = MutableStateFlow<List<YouTubeVideo>>(emptyList())
    val shortsList: StateFlow<List<YouTubeVideo>> = _shortsList

    private val channelIds = listOf(
        "UCRvJ_D38sTRPMgRY8knjH9g", // GurujeetKr
        "UCo1RTfDzI4y0F-vb0otT-vg", // UnsungCoders
        "UChsL4brFRi1nPq-6CQz6jyg", //Inspirex
        "UCo86K3bhYny0KNKGLoRIy8A", //GDG IEM
        "UC0lnxziPvKqiwNnlVrnbLrA",//Piyush MINI VLOG
        "UCBIquLdf_Rsh8u0avMGnsFw" // Harshgaurv
    )

    fun fetchRandomShorts(apiKey: String) {
        viewModelScope.launch {
            val allVideos = mutableListOf<YouTubeVideo>()

            channelIds.forEach { channelId ->
                try {
                    val response = RetrofitInstance.api.getShorts(channelId = channelId, apiKey = apiKey).awaitResponse()
                    if (response.isSuccessful) {
                        response.body()?.items?.let { allVideos.addAll(it) }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            // Shuffle the videos to get random shorts from different channels
            _shortsList.value = allVideos.shuffled()
        }
    }
}

