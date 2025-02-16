package com.example.matchmaker

import android.content.Context
import androidx.compose.runtime.Composable
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.random.Random


fun getRandomEnrollmentNumber(context: Context): String{
    val enrollments = mutableListOf<String>()

    try {
        val inputStream = context.assets.open("enrollment_numbers.txt") // File in `assets`
        val reader = BufferedReader(InputStreamReader(inputStream))

        var line: String?
        while (reader.readLine().also { line = it } != null) {
            line?.let { enrollments.add(it) }
        }

        reader.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return if (enrollments.isNotEmpty()) enrollments[Random.nextInt(enrollments.size)] else "000000" // Default if empty

}