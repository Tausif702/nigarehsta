package com.nigareshat.app.ui.socialMedia.ui

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import coil3.load
import coil3.request.crossfade
import coil3.request.placeholder
import com.nigareshat.app.R
import com.nigareshat.app.data.model.MyDownloadRequest
import com.nigareshat.app.data.viewModel.DownloadViewModel
import com.nigareshat.app.databinding.ActivityDownloadVideoBinding
import com.nigareshat.app.utils.isInternetAvailable
import com.nigareshat.app.utils.requestDownloadWithPermission
import com.nigareshat.app.utils.showToast
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DownloadVideoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDownloadVideoBinding
    private val viewModel: DownloadViewModel by viewModels()


    companion object {
        private const val STORAGE_PERMISSION_CODE = 1000
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDownloadVideoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        setupListeners()
        observeViewModel()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == STORAGE_PERMISSION_CODE &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "Permission granted 🎉", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "No permission, no downloads 😢", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupListeners() {
        binding.myimage.load("https://shorturl.at/SWOQU") {
            placeholder(R.drawable.download)
            crossfade(true)
            crossfade(1000)
        }

        binding.downloadButton.setOnClickListener {
            val url = binding.etLink.text.toString().trim()
            when {
                !isInternetAvailable() -> showToast("No internet connection")
                url.isEmpty() -> showToast("Please paste your URL here")
                else -> viewModel.download(MyDownloadRequest(url = url))
            }
            binding.etLink.setText("")
        }
    }

    private fun observeViewModel() {
        viewModel.downloadLiveData.observe(this) {
            binding.progressBar.isVisible = false
            it.videoUrl?.let { url ->
                requestDownloadWithPermission(url, STORAGE_PERMISSION_CODE)
            }
        }

        viewModel.progress.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.error.observe(this) { errorMessage ->
            binding.progressBar.isVisible = false
            showToast(errorMessage)
        }
    }
}
