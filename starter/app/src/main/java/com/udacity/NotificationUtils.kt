package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

private const val NOTIFICATION_ID = 0

fun Context.notificationManager(): NotificationManager {
    return ContextCompat.getSystemService(
        this,
        NotificationManager::class.java
    ) as NotificationManager
}

fun NotificationManager.sendNotification(
    downloadStatus: Int,
    fileName: String,
    messageBody: String,
    applicationContext: Context
) {

    val contentIntent = Intent(applicationContext, DetailActivity::class.java)
    contentIntent.putExtra(DetailActivity.DOWNLOAD_STATUS, downloadStatus)
    contentIntent.putExtra(DetailActivity.FILE_NAME, fileName)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.notification_channel_id)
    )

    builder.setSmallIcon(R.drawable.dowload)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .addAction(
            R.drawable.dowload,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent
        ).priority = NotificationCompat.PRIORITY_HIGH

    notify(NOTIFICATION_ID, builder.build())
}