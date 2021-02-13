package com.udacity

import android.app.DownloadManager
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        file_name_text.text = intent.getStringExtra(FILE_NAME).orEmpty()

        handleStatusInfo(intent.getIntExtra(DOWNLOAD_STATUS, -1))

        button_ok.setOnClickListener {
            finish()
        }

        this.notificationManager().cancelAll()
    }

    private fun handleStatusInfo(status: Int) {
        when (status) {
            DownloadManager.STATUS_SUCCESSFUL -> {
                status_text.text = getString(R.string.status_success)
                status_text.setTextColor(Color.GREEN)
            }
            DownloadManager.STATUS_FAILED -> {
                status_text.text = getString(R.string.status_failed)
                status_text.setTextColor(Color.RED)
            }
        }
    }

    companion object {
        const val FILE_NAME = "FILE_NAME"
        const val DOWNLOAD_STATUS = "DOWNLOAD_ID"
    }

}
