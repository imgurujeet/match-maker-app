package com.example.matchmaker

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.navigation.NavHostController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.collectAsState
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

import android.util.Log
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener



@Composable
fun ConfessionScreen(navController: NavHostController, viewModel: ConfessionViewModel = ConfessionViewModel()) {
    Column {
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("messages")

        myRef.setValue("Hello, Firebase!")

        TopBar()
        ConfessionScreenMessage(viewModel = ConfessionViewModel())
    }
}


//@Composable
//fun ConfessionScreenmessage(viewModel: ConfessionViewModel) {
//    var confessionText by remember { mutableStateOf("") }
//    var enrollmentNumber by remember { mutableStateOf("") }
//    val context = LocalContext.current
//
//    // Observe the live data for confessions
//    val confessions by viewModel.confessions.observeAsState(emptyList())
//
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp),
//        horizontalAlignment = Alignment.CenterHorizontally
//    ) {
//        Text(
//            text = "Share Your Confession 💌",
//            fontSize = 22.sp,
//            fontWeight = FontWeight.Bold,
//            textAlign = TextAlign.Center
//        )
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        // Enrollment Number Input
//        OutlinedTextField(
//            value = enrollmentNumber,
//            onValueChange = { enrollmentNumber = it },
//            label = { Text("Enter your friend's or love's enrollment number") },
//            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
//            singleLine = true,
//            modifier = Modifier.fillMaxWidth()
//        )
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        // Confession Input
//        OutlinedTextField(
//            value = confessionText,
//            onValueChange = { confessionText = it },
//            label = { Text("Write Your Confession...") },
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(120.dp)
//        )
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        // Post Button
//        Button(
//            onClick = {
//                if (enrollmentNumber.length < 12) {
//                    Toast.makeText(context, "Please enter a valid 12-digit enrollment number", Toast.LENGTH_SHORT).show()
//                } else if (confessionText.isEmpty()) {
//                    Toast.makeText(context, "Confession cannot be empty!", Toast.LENGTH_SHORT).show()
//                } else {
//                   // viewModel.postConfession(enrollmentNumber, confessionText)
//                    confessionText = "" // Clear text after posting
//                    Toast.makeText(context, "Confession posted successfully!", Toast.LENGTH_SHORT).show()
//                }
//            },
//            modifier = Modifier.fillMaxWidth()
//        ) {
//            Text(text = "Post Confession ✨")
//        }
//
//        Spacer(modifier = Modifier.height(20.dp))
//
//        // Fetch & Display Confessions
//        Text(
//            text = "Your Confessions 📜",
//            fontSize = 18.sp,
//            fontWeight = FontWeight.Bold,
//            modifier = Modifier.padding(top = 20.dp)
//        )
//
//        // Display the confessions in a LazyColumn
//        LazyColumn {
//            items(confessions) { confession ->
//                Card(
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(8.dp),
//                    elevation = CardDefaults.cardElevation(4.dp)
//                ) {
//                    Text(
//                        text = confession.text, // Display confession text
//                        modifier = Modifier.padding(12.dp),
//                        fontSize = 16.sp
//                    )
//                }
//            }
//        }
//    }
//}













