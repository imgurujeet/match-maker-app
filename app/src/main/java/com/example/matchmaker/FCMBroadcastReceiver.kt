package com.example.matchmaker


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMBroadcastReceiver : BroadcastReceiver() {

    companion object {
        private const val TAG = "FCMBroadcastReceiver"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            Log.d(TAG, "Device Booted: Re-registering FCM token")

            // Re-subscribe to FCM topics if needed
            FirebaseMessaging.getInstance().subscribeToTopic("general")
                .addOnCompleteListener { Log.d(TAG, "Subscribed to FCM Topic: general") }
        }
    }
}





//class MyFirebaseMessagingService : FirebaseMessagingService() {
//
//    override fun onMessageReceived(remoteMessage: RemoteMessage) {
//        Log.d("FCM", "Message Received: ${remoteMessage.notification?.body}")
//
//        remoteMessage.notification?.let {
//            sendNotification(it.title, it.body)
//        }
//    }
//
//    private fun sendNotification(title: String?, message: String?) {
//        val channelId = "matchmaker_channel"
//        val notificationId = System.currentTimeMillis().toInt()
//
//        // Create intent to open the app when clicked
//        val intent = Intent(this, MainActivity::class.java)
//        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//        val pendingIntent = PendingIntent.getActivity(
//            this, 0, intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        // Create Notification Channel (For Android 8.0+)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            val channel = NotificationChannel(
//                channelId, "Matchmaker Notifications",
//                NotificationManager.IMPORTANCE_HIGH
//            ).apply { description = "New confession alerts" }
//
//            val manager = getSystemService(NotificationManager::class.java)
//            manager.createNotificationChannel(channel)
//        }
//
//        // Build and Show Notification
//        val notification = NotificationCompat.Builder(this, channelId)
//            .setSmallIcon(android.R.drawable.ic_dialog_info)
//            .setContentTitle(title)
//            .setContentText(message)
//            .setAutoCancel(true)
//            .setContentIntent(pendingIntent)
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .build()
//
//        NotificationManagerCompat.from(this).notify(notificationId, notification)
//    }
//}

