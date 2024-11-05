package com.nigareshat.app.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.nigareshat.app.R


class SplashScreenActivity : AppCompatActivity() {
    private lateinit var progressBar: ProgressBar
    private lateinit var percentageTextView: TextView
    private val totalProgressTime = 3000
    private val progressUpdateInterval = 30

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        progressBar = findViewById(R.id.progressBar)
        percentageTextView = findViewById(R.id.percentageTextView)

        loadProgress()
    }

    private fun loadProgress() {
        val handler = Handler(Looper.getMainLooper())
        var progressStatus = 0

        Thread {
            while (progressStatus < 100) {
                progressStatus++
                Thread.sleep((totalProgressTime / 100).toLong()) // Adjust the sleep time for smooth loading
                handler.post {
                    progressBar.progress = progressStatus
                    //percentageTextView.text = "$progressStatus%"
                }
            }
            startActivity(Intent(this@SplashScreenActivity, DataEntryActivity::class.java))
            finish()
        }.start()
    }
}
