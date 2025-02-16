package com.example.matchmaker

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
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


@Composable
fun ImageBox(navController: NavHostController,viewModel: ImageViewModel = ImageViewModel()) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val enrollmentNumber by viewModel.enrollmentNumber.collectAsState()
    val imageURL by viewModel.imageURL.collectAsState()
    val randomImageURL by viewModel.randomImageURL.collectAsState()

    //    var enrollmentNumber by rememberSaveable { mutableStateOf("") }
//    var imageURL by rememberSaveable { mutableStateOf(R.drawable.default_female_avatar.toString()) }
//    var randomImageURL by rememberSaveable { mutableStateOf(R.drawable.default_male_avatar.toString()) }

    val isDarkTheme = isSystemInDarkTheme()

    // Set background color dynamically
    val backgroundColor = if (isDarkTheme) Color.DarkGray else Color.White

    MaterialTheme(
        colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
    ){
        Column(
            modifier = Modifier.fillMaxSize()
                .background(backgroundColor),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally

        ){

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1.5f)
                    ) {
                        Image(
                            painter = when {
                                imageURL.isNotEmpty() && imageURL.startsWith("http") -> {
                                    rememberAsyncImagePainter(imageURL)  // Load from the internet
                                }
                                imageURL.startsWith("android.resource://${context.packageName}") -> {
                                    val resourceId = imageURL.substringAfterLast("/")
                                        .toIntOrNull() ?: R.drawable.default_avatar
                                    painterResource(id = resourceId) // Load searched image from drawable
                                }
                                else -> {
                                    painterResource(id = R.drawable.default_avatar) // Default if nothing found
                                }
                            },
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize()
                            // contentScale = ContentScale.Crop
                        )
                    }

            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                AnimatedHeartIcon()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1.5f)
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
                        else -> {
                            painterResource(id = R.drawable.default_avatar) // Default if nothing found
                        }
                    },
                    contentDescription = "",
                    modifier = Modifier.fillMaxSize()
                    // contentScale = ContentScale.Crop
                )
            }


            OutlinedTextField(
                value = enrollmentNumber,
                onValueChange = { viewModel.updateEnrollmentNumber(it) },
                label = { Text("Enter Enrollment Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(0.8f),
                shape = RoundedCornerShape(20.dp),
                singleLine = true
            )

            Button(onClick = {
                if (enrollmentNumber.isEmpty()) {
                    Toast.makeText(context, "Please enter your enrollment number", Toast.LENGTH_SHORT).show()
                } else if (enrollmentNumber.length != 14) {
                    Toast.makeText(context, "Please enter a valid 14-digit enrollment number", Toast.LENGTH_SHORT).show()
                } else {
                    viewModel.findMatch(context, enrollmentNumber)
                }

            }) {
                Text(text = "Find My Match")
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

    fun updateEnrollmentNumber(newNumber: String) {
        _enrollmentNumber.value = newNumber
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
                    "12023052016051" to "12023052018054",
                    "12023052002132" to "12023052018054",
                    "12023052002083" to "12023052017046",
                    "12023052004009" to "12023052019065"
                )

                val drawableMappings = mapOf(
                    "12023052018054" to R.drawable.manish,
                    "12023052016051" to R.drawable.nourin_image,
                    "12023052019065" to R.drawable.gurujeet_image,
                    "12023052002144" to R.drawable.utkarsh,
                    "12023052002132" to R.drawable.sania
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
                } else {
                    Toast.makeText(context, "Enrollment number not found!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error reading enrollment files", Toast.LENGTH_SHORT).show()
            }
        }
    }

}

