package com.example.matchmaker

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import coil.compose.rememberAsyncImagePainter
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavHostController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import androidx.compose.ui.res.painterResource
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import android.app.Activity
import kotlinx.coroutines.delay


@Composable
fun ImageBox(navController: NavHostController, viewModel: ImageViewModel = ImageViewModel()) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val enrollmentNumber by viewModel.enrollmentNumber.collectAsState()
    val imageURL by viewModel.imageURL.collectAsState()
    val randomImageURL by viewModel.randomImageURL.collectAsState()
    val matchedEnrollment by viewModel.matchedEnrollment.collectAsState()
    val isMatchedEnrollmentVisible by viewModel.isMatchedEnrollmentVisible.collectAsState()
    val isSearchCompleted by viewModel.isSearchCompleted.collectAsState()
    var isButtonVisible by remember { mutableStateOf(false) }
    var showAd by remember { mutableStateOf(false) }
    var interstitialAd: InterstitialAd? by remember { mutableStateOf(null) }
    var showEnrollmentNumber by remember { mutableStateOf(false) }  // Track if enrollment number is revealed
    var isRevealClicked by remember { mutableStateOf(false) }


    LaunchedEffect(true) {
        viewModel.loadInterstitialAd(context)
    }

    val isDarkTheme = isSystemInDarkTheme()

    // Set background color dynamically
    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color.White


    LaunchedEffect(enrollmentNumber,isSearchCompleted) {
        if (isSearchCompleted) {
            // Reset state when the search is completed or a new search is triggered
            showEnrollmentNumber = false
            isRevealClicked = false
        }
    }



    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(8.dp),  // Add padding for responsiveness
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Image Box for user avatar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.8f)
            ) {
                Image(
                    painter = when {
                        imageURL.isNotEmpty() && imageURL.startsWith("http") -> {
                            rememberAsyncImagePainter(imageURL)  // Load from the internet
                        }
                        imageURL.startsWith("android.resource://${context.packageName}") -> {
                            val resourceId = imageURL.substringAfterLast("/")
                                .toIntOrNull() ?: R.drawable.default_avatar
                            painterResource(id = resourceId) // Load from drawable
                        }
                        else -> painterResource(id = R.drawable.default_avatar) // Default if no image found
                    },
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Heart Animation Icon
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedHeartIcon()
            }

            // Image Box for random image
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.8f)
            ) {
                Image(
                    painter = when {
                        randomImageURL.isNotEmpty() && randomImageURL.startsWith("http") -> {
                            rememberAsyncImagePainter(randomImageURL)  // Load random image from internet
                        }
                        randomImageURL.startsWith("android.resource://${context.packageName}") -> {
                            val resourceId = randomImageURL.substringAfterLast("/")
                                .toIntOrNull() ?: R.drawable.default_avatar
                            painterResource(id = resourceId) // Load random image from drawable
                        }
                        else -> painterResource(id = R.drawable.default_avatar) // Default if no image found
                    },
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Enrollment Number input
            OutlinedTextField(
                value = enrollmentNumber,
                onValueChange = { viewModel.updateEnrollmentNumber(it) },
                label = { Text("Enter Enrollment Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth(0.8f)  // Responsive width
                    .padding(vertical = 8.dp),
                shape = RoundedCornerShape(20.dp),
                singleLine = true
            )


            Button(
                onClick = {
                    showEnrollmentNumber = false
                    isRevealClicked = false
                    viewModel.resetMatchedEnrollmentVisibility() // Reset the matched enrollment visibility


                    if (enrollmentNumber.isEmpty()) {
                        Toast.makeText(context, "Please enter your enrollment number", Toast.LENGTH_SHORT).show()
                    } else if (enrollmentNumber.length != 14) {
                        Toast.makeText(context, "Please enter a valid 14-digit enrollment number", Toast.LENGTH_SHORT).show()
                    } else {

                        viewModel.findMatch(context, enrollmentNumber)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xff907ad6), contentColor = Color.White),
                modifier = Modifier.fillMaxWidth(0.8f)  // Responsive width
            ) {
                Text(text = "Find My Match")
            }

            // Reveal Enrollment Number Button (for now shows ad placeholder)
            Text(
                text = "Tap to Reveal Matched Enrollment",
                color = if (isSearchCompleted) Color(0xFF4DB6AC) else Color.Gray,
                modifier = Modifier
                    .clickable() {
                        if (!showEnrollmentNumber) {
                            // If not shown, reveal the matched enrollment
                            showEnrollmentNumber = true
                            isRevealClicked = true
                            viewModel.toggleMatchedEnrollmentVisibility() // Toggle the enrollment visibility
                            showAd = true // Show ad

                        } else {
                            // If already shown, hide the matched enrollment number
                            showEnrollmentNumber = false
                            viewModel.toggleMatchedEnrollmentVisibility() // Hide the enrollment visibility
                        }

                    }
                    .alpha(if (isSearchCompleted) 1f else 0.5f) // Fade when disabled
            )

            if (showAd) {
                viewModel.showInterstitialAd(context)
                Toast.makeText(context, "Ad is showing", Toast.LENGTH_SHORT).show()
                LaunchedEffect(Unit) {
                    delay(1000)  // Adjust this delay as needed
                    showAd = false // Reset the ad state after showing the ad
                }

                LaunchedEffect(true) {
                    viewModel.loadInterstitialAd(context)
                }
            }


            // Show Matched Enrollment if revealed
            if (showEnrollmentNumber && isMatchedEnrollmentVisible && matchedEnrollment != null) {
                Text(text = "$matchedEnrollment")
            }
        }
    }
}



class ImageViewModel : ViewModel() {
    private val _enrollmentNumber = MutableStateFlow("")
    val enrollmentNumber: StateFlow<String> = _enrollmentNumber

    private val _imageURL = MutableStateFlow("")
    val imageURL: StateFlow<String> = _imageURL

    private val _randomImageURL = MutableStateFlow("")
    val randomImageURL: StateFlow<String> = _randomImageURL

    // New state for storing matched enrollment number
    private val _matchedEnrollment = MutableStateFlow<String?>(null)
    val matchedEnrollment: StateFlow<String?> = _matchedEnrollment

    // New state for toggling the reveal of matched enrollment
    private val _isMatchedEnrollmentVisible = MutableStateFlow(false)
    val isMatchedEnrollmentVisible: StateFlow<Boolean> = _isMatchedEnrollmentVisible

    //is search completed
    private val _isSearchCompleted = MutableStateFlow(false)
    val isSearchCompleted: StateFlow<Boolean> = _isSearchCompleted

    fun updateEnrollmentNumber(newNumber: String) {
        _enrollmentNumber.value = newNumber
    }

    fun resetMatchedEnrollmentVisibility() {
        _isMatchedEnrollmentVisible.value = false // Hide the matched enrollment number
        _matchedEnrollment.value = null // Clear the matched enrollment value
    }

    fun toggleMatchedEnrollmentVisibility() {
        _isMatchedEnrollmentVisible.value = !_isMatchedEnrollmentVisible.value
    }

    fun findMatch(context: Context, enrollmentNumber: String) {
        viewModelScope.launch {
            try {
                // Load enrollment numbers from assets
                val femaleEnrollments = context.assets.open("enrollment_numbers.txt").bufferedReader().use { it.readLines() }
                val maleEnrollments = context.assets.open("enrollment_numbers_m.txt").bufferedReader().use { it.readLines() }

                // Define specific mappings
                val fixedMatches = mapOf(
                    "12023052019015" to "12023052019065",
                    "12023052019065" to "12023052019015",
                    "12023052016051" to "12023052018054",
                    "12023052002132" to "12023052018054",
                    "12023052018054" to "12023052002132",
                    "12023052002083" to "12023052017046",
                    "12023052004009" to "12023052019065",
                    "12023052017046" to "12023052002083"
                )

                val drawableMappings = mapOf(
                    "12023052018054" to R.drawable.manish,
                    "12023052016051" to R.drawable.nourin_image,
                    "12023052019065" to R.drawable.gurujeet_img,
                    "12023052002144" to R.drawable.utkarsh,
                    "12023052002132" to R.drawable.sania,
                    "12023052020078" to R.drawable.harsh_gaurav
                )

                var matchedEnrollment: String? = null

                if (fixedMatches.containsKey(enrollmentNumber)) {
                    matchedEnrollment = fixedMatches[enrollmentNumber]
                } else if (femaleEnrollments.contains(enrollmentNumber)) {
                    matchedEnrollment = maleEnrollments.randomOrNull()
                } else if (maleEnrollments.contains(enrollmentNumber)) {
                    matchedEnrollment = femaleEnrollments.randomOrNull()
                } else {
                    matchedEnrollment = femaleEnrollments.randomOrNull()
                }

                if (matchedEnrollment != null) {
                    // Set matched enrollment number to be revealed
                    _matchedEnrollment.value = matchedEnrollment

                    // Check if the searched enrollment number exists in drawable mappings
                    if (drawableMappings.containsKey(enrollmentNumber)) {
                        val drawableRes = drawableMappings[enrollmentNumber]!!
                        _imageURL.value = "android.resource://${context.packageName}/$drawableRes"
                    } else {
                        _imageURL.value = "https://www.iemcrp.com/iemEn/photo/104/$enrollmentNumber.jpg"
                    }

                    // Check if the matched enrollment number exists in drawable mappings
                    if (drawableMappings.containsKey(matchedEnrollment)) {
                        val drawableRes = drawableMappings[matchedEnrollment]!!
                        _randomImageURL.value = "android.resource://${context.packageName}/$drawableRes"
                    } else {
                        _randomImageURL.value = "https://www.iemcrp.com/iemEn/photo/104/$matchedEnrollment.jpg"
                    }
                    _isSearchCompleted.value = true
                } else {
                    Toast.makeText(context, "Enrollment number not found!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error reading enrollment files", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private val _interstitialAd = MutableStateFlow<InterstitialAd?>(null)
    val interstitialAd: StateFlow<InterstitialAd?> = _interstitialAd

    fun loadInterstitialAd(context: Context) {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            context,
            "ca-app-pub-9204160176455905/2667087010",  // Replace with your Ad Unit ID
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    _interstitialAd.value = ad
                    Log.d("AdMob", "Interstitial Ad loaded")
                }

                override fun onAdFailedToLoad(adError: LoadAdError) {
                    Log.d("AdMob", "Failed to load interstitial ad: ${adError.message}")
                    _interstitialAd.value = null
                }
            }
        )
    }

    fun showInterstitialAd(context: Context) {
        interstitialAd.value?.show(context as Activity) ?: Log.d("AdMob", "Ad not loaded yet")
    }



}



