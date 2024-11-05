package com.nigareshat.app.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.nigareshat.app.adapter.PdfListAdapter
import com.nigareshat.app.databinding.ActivityPdfListBinding
import java.io.File

class PdfListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPdfListBinding
    private lateinit var pdfAdapter: PdfListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPdfListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up RecyclerView
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.back.setOnClickListener {
            finish()
        }
        // Load PDF files from the app's private directory
        loadPdfFilesInLocal()
    }

    private fun loadPdfFilesInLocal() {
        // Accessing the app's private directory
        val pdfDirectory = File(getExternalFilesDir(null), "MaktabeFarooqPDFs")
        val pdfFiles =
            pdfDirectory.listFiles { file -> file.extension.equals("pdf", true) }?.toList()
                ?: emptyList()

        // Set up RecyclerView
        pdfAdapter = PdfListAdapter(
            pdfFiles,
            { pdfFile -> openPdf(pdfFile) }) { pdfFile -> deletePdf(pdfFile) }
        binding.recyclerView.adapter = pdfAdapter
    }


    private fun openPdf(pdfFile: File) {
        // Get the Uri for the PDF file using FileProvider
        val pdfUri: Uri = FileProvider.getUriForFile(this, "${packageName}.provider", pdfFile)
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(pdfUri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No application found to open PDF.", Toast.LENGTH_SHORT).show()
        }

    }

    private fun deletePdf(pdfFile: File) {
        if (pdfFile.delete()) {
            Toast.makeText(this, "PDF deleted successfully.", Toast.LENGTH_SHORT).show()
            loadPdfFilesInLocal() // Reload the list after deletion
        } else {
            Toast.makeText(this, "Error deleting PDF.", Toast.LENGTH_SHORT).show()
        }
    }
}
