package com.example.samplephoneapp.utils

import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.samplephoneapp.MainActivity
import com.example.samplephoneapp.R

private val NOTIFICATION_ID = 0
private val REQUEST_CODE = 0
private val FLAGS = 0

/**
 * Builds and delivers a notification.
 *
 * @param messageBody, notification text.
 * @param context, activity context.
 */
fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context) {
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val notificationIcon = BitmapFactory.decodeResource(applicationContext.resources, R.drawable.notification_custom_image)

    val pictureStyle = NotificationCompat.BigPictureStyle()
        .bigPicture(notificationIcon)
        .bigLargeIcon(null)


    val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
        NotificationCompat.Builder(
            applicationContext,
            applicationContext.getString(R.string.sample_notification_channel_id)
        ).setSmallIcon(R.drawable.notification_custom_image)
            .setContentTitle(applicationContext.getString(R.string.notification_title))
            .setContentText(messageBody)
            .setContentIntent(contentPendingIntent)
            .setAutoCancel(true)
            .setStyle(pictureStyle)
            .setLargeIcon(notificationIcon)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .extend(NotificationCompat.WearableExtender()
                .setContentIntentAvailableOffline(true))
    } else {
        TODO("VERSION.SDK_INT < KITKAT_WATCH")
    }
    notify(NOTIFICATION_ID, builder.build())

}


fun NotificationManager.cancelNotifications() {
    cancelAll()
}