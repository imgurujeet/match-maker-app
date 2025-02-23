package com.example.matchmaker

import android.R
import android.R.attr.height
import android.content.Context
import android.net.Uri
import androidx.compose.ui.viewinterop.AndroidView
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.KeyboardDoubleArrowDown
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Card
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.view.size
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import com.google.android.gms.ads.MobileAds
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import coil.compose.rememberAsyncImagePainter
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ServerTimestamp
import com.google.type.Date
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import com.google.firebase.firestore.PropertyName
import com.google.firebase.Timestamp
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID



/*
"cloud_name", "dpk3xem7s",
  "api_key", "542354964431797",
  "api_secret", "KvS1XXN-ERHoHm1FXm0Lq48dhgM"
   */


class ConfessionViewModel : ViewModel( ) {
    private val _confessions = MutableLiveData<List<Confession>>(emptyList())
    val confessions: LiveData<List<Confession>> = _confessions

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private var lastConfessionTimestamp: Timestamp? = null // Track the latest confession
    private var notificationHelper: NotificationHelper? = null // Notification manager

    fun initNotificationHelper(context: Context) {
        notificationHelper = NotificationHelper(context.applicationContext)
    }

    init {
        loadData()  // Load data initially
        listenForNewConfessions()  // Start real-time listener
    }

    private fun listenForNewConfessions() {
        val db = FirebaseFirestore.getInstance()

        db.collection("confessions")
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Log.e("Firestore", "Error listening for confessions", e)
                    return@addSnapshotListener
                }

                if (snapshots == null || snapshots.isEmpty) {
                    Log.d("Firestore", "No confessions found")
                    return@addSnapshotListener
                }

                val confessionList = snapshots.documents.mapNotNull { document ->
                    document.toObject(Confession::class.java)?.copy(documentId = document.id)
                }

                _confessions.postValue(confessionList)

                val latestConfession = confessionList.firstOrNull()
                if (latestConfession != null && latestConfession.timestamp != null) {
                    val newTimestamp = latestConfession.timestamp


                    if (lastConfessionTimestamp == null || newTimestamp.toDate().after(lastConfessionTimestamp!!.toDate())) {
                        lastConfessionTimestamp = newTimestamp

                        Log.d("ConfessionViewModel", "🔥 New confession detected!")

                        if (notificationHelper == null) {
                            Log.e("ConfessionViewModel", "❌ NotificationHelper is NOT initialized!")
                        } else {
                            Log.d("ConfessionViewModel", "✅ NotificationHelper is initialized. Sending notification...")
                            notificationHelper?.showNotification(
                                "Confession booth is LIVE! 🎤",
                                "Who’s spilling the tea today?"
                            )
                        }



                    }
                }
            }
    }




    fun uploadImageToCloudinary(
        imageUri: Uri,
        context: Context,
        onSuccess: (String) -> Unit,
        onFailure: (Exception?) -> Unit
    ) {
        val cloudinary = Cloudinary(
            ObjectUtils.asMap(
                "cloud_name", "dpk3xem7s",
                "api_key", "542354964431797",
                "api_secret", "KvS1XXN-ERHoHm1FXm0Lq48dhgM"
            )
        )

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes != null) {
                    val options = HashMap<String, Any>().apply {
                        put("resource_type", "image")
                    }

                    val result = cloudinary.uploader().upload(bytes, options)
                    val imageUrl = result["secure_url"].toString()

                    withContext(Dispatchers.Main) {
                        onSuccess(imageUrl)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        onFailure(null)
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onFailure(e)
                }
            }
        }
    }

    fun postConfession(enrollmentNumber: String, text: String, imageUrl: String?, onComplete: () -> Unit) {
        val confession = Confession(enrollmentNumber, text, imageUrl, Timestamp.now(), emptyList())
        FirebaseFirestore.getInstance().collection("confessions").add(confession)
            .addOnSuccessListener { documentReference ->
                loadData()
                onComplete()
            }
            .addOnFailureListener {
                onComplete()
            }
    }

    fun addComment(confession: Confession, comment: String) {
        val updatedComments = confession.comments + comment
        val updatedConfession = confession.copy(comments = updatedComments)

        FirebaseFirestore.getInstance().collection("confessions")
            .document(confession.documentId)
            .update("comments", updatedComments)
            .addOnSuccessListener {
                // Update the local state immediately
                _confessions.value = _confessions.value?.map {
                    if (it.documentId == confession.documentId) updatedConfession else it
                }
            }
            .addOnFailureListener {
                // Handle failure (e.g., show a toast or log the error)
            }
    }


    fun loadData() {
        _isLoading.value = true
        FirebaseFirestore.getInstance().collection("confessions")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val confessionList = mutableListOf<Confession>()
                for (document in result) {
                    val confession = document.toObject(Confession::class.java)
                    val confessionWithId = confession.copy(documentId = document.id) // Adding documentId correctly
                    confessionList.add(confessionWithId)
                }
                _confessions.value = confessionList
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }



}

// Update data class

data class Confession(
    val enrollmentNumber: String = "",
    val text: String = "",
    val imageUrl: String? = null,
    val timestamp: Timestamp? = null,
    val comments: List<String> = emptyList(), // List of comments for each confession
    val documentId: String = "" // Added documentId to store Firestore document ID
)






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfessionScreenMessage(viewModel: ConfessionViewModel) {
    val context = LocalContext.current
    var enrollmentNumber by remember { mutableStateOf("") }
    var confessionText by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showConfessionSheet by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        selectedImageUri = uri
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ConfessionMessageScreen(viewModel)

        FloatingActionButton(
            onClick = { showConfessionSheet = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp),
            containerColor = Color(0xFF00E676)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Confession", tint = Color.White)
        }
    }

    if (showConfessionSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = { showConfessionSheet = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Your Space, Your Words 📝",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = enrollmentNumber,
                    onValueChange = { enrollmentNumber = it },
                    label = { Text("Drop a Name? (If You Dare) 😏") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )
                Spacer(modifier = Modifier.height(5.dp))

                OutlinedTextField(
                    value = confessionText,
                    onValueChange = { confessionText = it },
                    label = { Text("Write Freely, No Rules! ✍️") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(20.dp)
                )
                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = { launcher.launch("image/*") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface, // Adapts to light/dark mode
                        contentColor = MaterialTheme.colorScheme.onSurface // Adapts text/icon color to theme
                    ),
                    shape = RoundedCornerShape(10.dp), // Subtle rounded corners
                    modifier = Modifier
                        .wrapContentWidth() // Adjusts width to content
                        .height(40.dp) // Compact height
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp)) // Subtle border
                ) {
                    Icon(
                        imageVector = Icons.Default.Upload, // Upload icon
                        contentDescription = "Upload",
                        modifier = Modifier.size(18.dp) // Compact icon size
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Upload Image ", // Simple text
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))


                selectedImageUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = "Selected Image",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RoundedCornerShape(10.dp))
                    )
                }

                var isPosting by remember { mutableStateOf(false) }

                Button(
                    onClick = {
                        if (enrollmentNumber.length < 3) {
                            Toast.makeText(context, "Please enter a valid Name or Enrollment number", Toast.LENGTH_SHORT).show()
                        } else if (confessionText.isEmpty()) {
                            Toast.makeText(context, "Post cannot be empty!", Toast.LENGTH_SHORT).show()
                        } else {
                            isPosting = true  // Disable the button after click

                            selectedImageUri?.let { uri ->
                                viewModel.uploadImageToCloudinary(uri, context,
                                    onSuccess = { imageUrl ->
                                        viewModel.postConfession(enrollmentNumber, confessionText, imageUrl) {
                                            Toast.makeText(context, "Posted successfully!", Toast.LENGTH_SHORT).show()
                                            showConfessionSheet = false
                                            enrollmentNumber = ""
                                            confessionText = ""
                                            selectedImageUri = null
                                            isPosting = false  // Re-enable button after success
                                        }
                                    },
                                    onFailure = {
                                        Toast.makeText(context, "Image upload failed!", Toast.LENGTH_SHORT).show()
                                        isPosting = false  // Re-enable button on failure
                                    }
                                )
                            } ?: run {
                                viewModel.postConfession(enrollmentNumber, confessionText, null) {
                                    Toast.makeText(context, "Posted successfully!", Toast.LENGTH_SHORT).show()
                                    showConfessionSheet = false
                                    enrollmentNumber = ""
                                    confessionText = ""
                                    isPosting = false  // Re-enable button after success
                                }
                            }
                        }
                    },
                    enabled = !isPosting, // Disable button while posting
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                    modifier = Modifier.fillMaxWidth()
                        .navigationBarsPadding()
                ) {
                    Text(text = if (isPosting) "Dropping... ⏳" else "Drop It 🚀")
                }
            }
        }
    }
}




@Composable
fun ConfessionMessageScreen(viewModel: ConfessionViewModel) {
    val confessions by viewModel.confessions.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)
    var newCommentText by remember { mutableStateOf("") }

    // State for showing full image dialog
    var selectedImageUrl by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "AnonWall \uD83D\uDD75\uFE0F",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            itemsIndexed(confessions) { index ,confession ->
                // Generate a random aesthetic background color
                val isDarkMode = isSystemInDarkTheme()
                val backgroundColor = remember {
                    if (isDarkMode) {
                        listOf(
                            Color(0xFF1B1B2F), // Deep Midnight Blue
                            Color(0xFF2C2C54), // Dark Indigo
                            Color(0xFF3E1F47), // Plum Noir
                            Color(0xFF2A2D43), // Steel Blue Gray
                            Color(0xFF40394A), // Muted Burgundy
                            Color(0xFF1F2A38), // Urban Navy
                            Color(0xFF2D3E50), // Shadow Teal
                            Color(0xFF222831), // Dark Cyan Black
                            Color(0xFF2B2E4A), // Grape Charcoal
                            Color(0xFF3B4252), // Nordic Night
                            Color(0xFF4A4E69), // Smoky Purple
                            Color(0xFF31363F), // Graphite Gray
                            Color(0xFF252A34), // Blackened Blue
                            Color(0xFF12151C), // Obsidian Black
                            Color(0xFF373A40), // Shadow Fog
                            Color(0xFF2E3440), // Nordic Twilight
                            Color(0xFF4B5267), // Ash Navy
                            Color(0xFF212936)  // Deep Ocean
                        ).random()
                    } else {
                        listOf(
                            Color(0xFFFCE4EC), // Light Pink
                            Color(0xFFE3F2FD), // Light Blue
                            Color(0xFFE8F5E9), // Light Green
                            Color(0xFFFFF9C4), // Light Yellow
                            Color(0xFFFFE0B2), // Light Orange
                            Color(0xFFD1C4E9), // Light Purple
                            Color(0xFFFFF3E0), // Light Peach
                            Color(0xFFEDE7F6), // Lavender
                            Color(0xFFF8BBD0), // Soft Rose
                            Color(0xFFC8E6C9), // Mint Green
                            Color(0xFFDCEDC8), // Pale Lime
                            Color(0xFFBBDEFB), // Baby Blue
                            Color(0xFFFFCDD2), // Blush Pink
                            Color(0xFFF0F4C3), // Soft Lemon
                            Color(0xFFFBE9E7), // Salmon Cream
                            Color(0xFFD7CCC8), // Light Cocoa
                            Color(0xFFF9EBEA), // Rosy Cloud
                            Color(0xFFE0F2F1)  // Aqua Mist
                        ).random()
                    }
                }


                //val textColor =  Color.Black

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = backgroundColor)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 🔹 "To: Enrollment Number"
                        Text(
                            text = "To: ${confession.enrollmentNumber}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            //color = textColor,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 🔸 Confession Post (Message)
                        Text(
                            text = confession.text,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Normal,
                            //color = textColor,
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 🔵 Image (Square & Clickable)
                        confession.imageUrl?.let { imageUrl ->
                            Image(
                                painter = rememberAsyncImagePainter(model = imageUrl),
                                contentDescription = "Confession Image",
                                contentScale = ContentScale.Crop, // Keeps it square
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f) // Makes it a perfect square
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { selectedImageUrl = imageUrl } // Open full image on click
                            )
                        }


                        Spacer(modifier = Modifier.height(8.dp))

                        // Display Timestamp
                        val formattedTime = confession.timestamp?.toDate()?.let { date ->
                            SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(date)
                        } ?: "Unknown time"

                        Text(
                            text = "Posted On: $formattedTime",
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Light,
                            modifier = Modifier.align(Alignment.Start)
                            //color = textColor.copy(alpha = 0.7f)
                        )


                        // Display Comment Count



                        val isDarkTheme = isSystemInDarkTheme()
                        // Comment Section with Icon (Instagram-like)
                        var showComments by remember { mutableStateOf(false) }


                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween, // This will space the items between
                            verticalAlignment = Alignment.CenterVertically // This ensures vertical alignment
                        ){
                            Text(
                                text = "Comments: ${confession.comments.size}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                               // modifier = Modifier.align(Alignment.Start)
                            )
                            // Comment Icon (Instagram-like)
                            IconButton(
                                onClick = { showComments = !showComments },
                                modifier = Modifier
                                    .padding(8.dp)

                            ) {

                                // Change color based on theme (Black for light, White for dark)
                                Icon(
                                    imageVector = Icons.Default.Comment,
                                    contentDescription = "Comments",
                                    tint = if (isDarkTheme) Color.White else Color.Black
                                )
                            }
                        }


                        // If showComments is true, display the comments section
                        if (showComments) {


                            // Display all previous comments
                            Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                                confession.comments.forEach { comment ->
                                    // Display each comment inside a styled box
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .background(if (isDarkTheme) Color.Gray else Color(0x80FFFFFF), // Adjust background color based on theme
                                                shape = RoundedCornerShape(8.dp))
                                    ) {
                                        Text(
                                            text = "$comment",
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Normal,
                                            modifier = Modifier.padding(8.dp),
                                            color = if (isDarkTheme) Color.White else Color.Black
                                        )
                                    }
                                }

                                // Spacer or Separator
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxSize()
                                        .padding(horizontal = 1.dp),
                                    //horizontalArrangement = Arrangement.SpaceBetween, // This will space the items between
                                    verticalAlignment = Alignment.CenterVertically
                                ){

                                    // TextField for Adding a Comment
                                    var commentText by remember { mutableStateOf("") }
                                    TextField(
                                        value = commentText,
                                        onValueChange = { commentText = it },
                                        placeholder = { Text("Add a comment...") },
                                        modifier = Modifier
                                            .weight(1f) // Allows the text field to take available space
                                            .padding(end = 8.dp), // Space between text field and icon,
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions.Default.copy(
                                            imeAction = ImeAction.Done
                                        ),
                                        keyboardActions = KeyboardActions(onDone = {
                                            if (commentText.isNotBlank()) {
                                                // Add the comment using ViewModel
                                                viewModel.addComment(confession, commentText)
                                                commentText = "" // Clear the text field
                                            }
                                        })
                                    )

                                    // Send Comment Icon
                                    IconButton(
                                        onClick = {
                                            if (commentText.isNotBlank()) {
                                                viewModel.addComment(confession, commentText)
                                                commentText = "" // Clear text field after submitting
                                            }
                                        },
                                        //modifier = Modifier.padding(8.dp)
                                    ) {
                                        // Send icon will also change color based on the theme
                                        Icon(
                                            imageVector = Icons.Default.Send,
                                            contentDescription = "Send Comment",
                                            tint = if (isDarkTheme) Color.White else Color.Black
                                        )
                                    }

                                }

                            }
                        }




                    }

                }

                if ((index + 1) % 4 == 0) {
                    Log.d("AdDebug", "Ad is being shown after item $index")
                    key(index) {
                        AdBanner()
                    }
                }



            }



            // Show loading indicator
            item {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.fillMaxWidth().padding(10.dp))
                }
            }
        }
    }

    // 🔥 Full-Size Image Dialog (Popup)
    selectedImageUrl?.let { imageUrl ->
        Dialog(onDismissRequest = { selectedImageUrl = null }) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f)), // Dark background like Instagram
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = imageUrl),
                    contentDescription = "Full-Size Image",
                    contentScale = ContentScale.Fit, // Shows full image without cropping
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { selectedImageUrl = null } // Close on click
                )
            }
        }
    }
}



@Composable
fun AdBanner() {
    val context = LocalContext.current
    val adSize = AdSize.BANNER

    val adUnitId="ca-app-pub-9204160176455905/5090108363"
    // Load the ad
    val adRequest = AdRequest.Builder().build()
    // Display the AdView
    AndroidView(
       factory = {context ->
           AdView(context).apply {
               setAdSize(adSize)
               setAdUnitId(adUnitId)
               loadAd(adRequest)
           }
       },
        modifier = Modifier.fillMaxWidth()
            .height(adSize.getHeightInPixels(context).dp)
    )
}







@Preview
@Composable
fun ConfessionScreenMessagePreview() {
    ConfessionScreenMessage(viewModel = ConfessionViewModel())

}