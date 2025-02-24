package com.example.matchmaker

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Environment
import android.util.Log
import androidx.core.content.FileProvider
import org.json.JSONObject
import java.io.File
import java.net.URL
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

object UpdateManager {

    private const val UPDATE_URL = "https://github.com/imgurujeet/match-maker-updates/raw/main/update.json"
    private const val PREFS_NAME = "MatchMakerPrefs"
    private const val LAST_UPDATE_CHECK = "lastUpdateCheck"
    private const val CURRENT_VERSION = "2.5" // Change this with your app's actual version
    private const val APK_NAME = "MatchMaker.apk"

    fun startAutoUpdateCheck(context: Context) {
        thread {
            while (true) {
                checkForUpdate(context)
                Thread.sleep(10_000) // Check every 10 seconds
            }
        }
    }

    private fun checkForUpdate(context: Context) {
        if (!shouldCheckForUpdate(context)) {
            return // Skip update check if it's too soon
        }

        thread {
            try {
                val response = URL(UPDATE_URL).readText()
                val jsonObject = JSONObject(response)
                val latestVersion = jsonObject.getString("latestVersion")
                val apkUrl = jsonObject.getString("apkUrl")

                if (CURRENT_VERSION < latestVersion) {
                    // Start downloading and installing the APK
                    downloadAndInstallApk(context, apkUrl)
                }

                saveUpdateCheckTime(context) // Save last check time
            } catch (e: Exception) {
                Log.e("UpdateCheck", "Failed to fetch update", e)
            }
        }
    }

    private fun shouldCheckForUpdate(context: Context): Boolean {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val lastCheck = sharedPref.getLong(LAST_UPDATE_CHECK, 0)
        val currentTime = System.currentTimeMillis()

        return (currentTime - lastCheck) > TimeUnit.SECONDS.toMillis(10000) // Check every 10 seconds
    }

    private fun saveUpdateCheckTime(context: Context) {
        val sharedPref: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPref.edit().putLong(LAST_UPDATE_CHECK, System.currentTimeMillis()).apply()
    }

    private fun downloadAndInstallApk(context: Context, apkUrl: String) {
        val destination = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), APK_NAME)

        val request = DownloadManager.Request(Uri.parse(apkUrl))
            .setTitle("Downloading Update")
            .setDescription("Downloading the latest version of MatchMaker.")
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, APK_NAME)
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)

        // Monitor download completion and install
        val query = DownloadManager.Query().setFilterById(downloadId)

        thread {
            var downloading = true
            while (downloading) {
                Thread.sleep(2000) // Sleep to reduce CPU usage
                val cursor = manager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS))
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        downloading = false
                        installApk(context, destination)
                    }
                }
                cursor.close()
            }
        }
    }

    private fun installApk(context: Context, apkFile: File) {
        if (!apkFile.exists()) {
            Log.e("UpdateManager", "APK file not found!")
            return
        }

        val apkUri = FileProvider.getUriForFile(context, "${context.packageName}.provider", apkFile)

        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            Log.e("UpdateManager", "Error installing APK", e)
        }
    }

}
