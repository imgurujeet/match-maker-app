package com.example.matchmaker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
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



class ConfessionViewModel : ViewModel() {
    private val _confessions = MutableLiveData<List<Confession>>(emptyList())
    val confessions: LiveData<List<Confession>> = _confessions

    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadData()  // Load data initially
    }

    // Simulate data fetching
    fun loadData() {
        _isLoading.value = true
        val db = FirebaseFirestore.getInstance()
        db.collection("confessions")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { result ->
                val confessionList = mutableListOf<Confession>()
                for (document in result) {
                    val confession = document.toObject(Confession::class.java)
                    confessionList.add(confession)
                }
                _confessions.value = confessionList
                _isLoading.value = false
            }
            .addOnFailureListener {
                _isLoading.value = false
            }
    }

    // Optional: Method for loading more data, if required
//    fun loadMoreData() {
//        if (_isLoading.value == true) return  // Prevent simultaneous loading
//        _isLoading.value = true
//        // Simulate load more data
//        viewModelScope.launch {
//            delay(1500)
//            // Simulate adding more confessions
//            val moreConfessions = _confessions.value.orEmpty() + listOf(
//                Confession("12345678901236", "This is a confession 3")
//            )
//            _confessions.value = moreConfessions
//            _isLoading.value = false
//        }
//    }
}

data class Confession(
    val enrollmentNumber: String = "",
    val text: String = "",
    @get:PropertyName("timestamp") @set:PropertyName("timestamp")
    var timestamp: Timestamp? = null  // Use Firestore Timestamp type
)



@Composable
fun ConfessionScreenMessage(viewModel: ConfessionViewModel) {
    val context = LocalContext.current

    // Observing LiveData using observeAsState
//    val confessions by viewModel.confessions.observeAsState(emptyList())
//    val isLoading by viewModel.isLoading.observeAsState(false)

    var enrollmentNumber by remember { mutableStateOf("") }
    var confessionText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Share Your Confession 💌",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Enrollment number input
        OutlinedTextField(
            value = enrollmentNumber,
            onValueChange = { enrollmentNumber = it },
            label = { Text("Who’s the lucky one you want to confess to?") },
            //keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Confession text input
        OutlinedTextField(
            value = confessionText,
            onValueChange = { confessionText = it },
            label = { Text("Write Your Confession...") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            shape = RoundedCornerShape(20.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Post confession button
        Button(
            onClick = {
                if (enrollmentNumber.length < 4) {
                    Toast.makeText(context, "Please enter a valid Name or Enrollment number", Toast.LENGTH_SHORT).show()
                } else if (confessionText.isEmpty()) {
                    Toast.makeText(context, "Confession cannot be empty!", Toast.LENGTH_SHORT).show()
                } else {
                    val confession = Confession(enrollmentNumber, confessionText, Timestamp.now())
                    // Post to Firestore (or other storage)
                    FirebaseFirestore.getInstance().collection("confessions").add(confession)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Confession posted successfully!", Toast.LENGTH_SHORT).show()
                            viewModel.loadData()  // Load data after posting
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to post confession!", Toast.LENGTH_SHORT).show()
                        }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xff907ad6), contentColor = Color(0xFFFFFFFF)),
            modifier = Modifier.fillMaxWidth()

        ) {
            Text(text = "Post Confession ✨")

        }

        Spacer(modifier = Modifier.height(10.dp))


        ConfessionMessageScreen(viewModel)

    }

}




@Composable
fun ConfessionMessageScreen(viewModel: ConfessionViewModel){
    // Observing LiveData using observeAsState
    val confessions by viewModel.confessions.observeAsState(emptyList())
    val isLoading by viewModel.isLoading.observeAsState(false)

    Column(
        modifier = Modifier
            .fillMaxSize(),
           // .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Text(
            text = "Confession Board \uD83D\uDCDC",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 10.dp,bottom = 10.dp)
        )

        // LazyColumn to display confessions
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Display each confession
            items(confessions) { confession ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Confessed to: ${confession.enrollmentNumber}",)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text= "Confession: ${confession.text}",fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        // Display timestamp
                        val formattedTime = confession.timestamp?.toDate()?.let { date ->
                            SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(date)
                        } ?: "Unknown time"

                        Text(
                            text = "Confessed on: $formattedTime",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Light
                        )


                    }
                }
            }

            // Show loading indicator while fetching data
            item {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.fillMaxWidth().padding(16.dp))
                }
            }

            // Fetch more data when scrolling to bottom
//            item {
//                LaunchedEffect(Unit) {
//                    if (!isLoading) {
//                        viewModel.loadMoreData()
//                    }
//                }
//            }
        }

    }


}