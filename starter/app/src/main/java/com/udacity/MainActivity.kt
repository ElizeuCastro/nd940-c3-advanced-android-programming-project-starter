package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var currentURL: String = ""
    private var currentFileName: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            download()
        }

        createChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name)
        )
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            custom_button.onCompletedEvent()
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            context?.notificationManager()?.sendNotification(
                getDownloadStatus(id),
                currentFileName,
                applicationContext.getString(
                    R.string.notification_description,
                    currentFileName
                ),
                context
            )
        }

        private fun getDownloadStatus(downloadId: Long?): Int {
            var status: Int = DownloadManager.STATUS_FAILED
            downloadId?.let {
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val cursor =
                    downloadManager.query(DownloadManager.Query().setFilterById(it))
                if (cursor.moveToFirst()) {
                    status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                }
                cursor.close()
            }
            return status
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            val checked = view.isChecked
            when (view.getId()) {
                R.id.radio_glide ->
                    if (checked) {
                        currentURL = LOAD_AP_URL
                        currentFileName = getString(R.string.button_load_app)
                    }
                R.id.radio_load_app ->
                    if (checked) {
                        currentURL = GLIDE_URL
                        currentFileName = getString(R.string.button_glide)
                    }
                R.id.radio_retrofit ->
                    if (checked) {
                        currentURL = RETROFIT_URL
                        currentFileName = getString(R.string.button_retrofit)
                    }
            }
        }
    }

    private fun download() {
        if (currentURL.isNotEmpty()) {
            custom_button.onDownloadingEvent()
            val request =
                DownloadManager.Request(Uri.parse(currentURL))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadManager.enqueue(request)
        } else {
            Toast.makeText(
                this,
                getString(R.string.no_url_selected),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                setShowBadge(false)
            }
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.app_name)

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val LOAD_AP_URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GLIDE_URL = "https://github.com/bumptech/glide"
        private const val RETROFIT_URL = "https://github.com/square/retrofit"
    }

}
