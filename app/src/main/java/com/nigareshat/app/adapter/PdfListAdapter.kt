package com.nigareshat.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nigareshat.app.databinding.ListPdfSingleItemBinding
import java.io.File

class PdfListAdapter(
    private var pdfFiles: List<File>,
    private val onPdfClick: (File) -> Unit,
    private val onDeleteClick: (File) -> Unit
) : RecyclerView.Adapter<PdfListAdapter.PdfViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PdfViewHolder {
        val binding =
            ListPdfSingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PdfViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PdfViewHolder, position: Int) {
        val pdfFile = pdfFiles[position]
        holder.bind(pdfFile, onPdfClick, onDeleteClick)
    }

    override fun getItemCount(): Int = pdfFiles.size

    class PdfViewHolder(private val binding: ListPdfSingleItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pdfFile: File, onPdfClick: (File) -> Unit, onDeleteClick: (File) -> Unit) {
            binding.pdfName.text = pdfFile.name
            binding.size.text = "${pdfFile.length() / 1000} kb"
            // Handle PDF item click
            itemView.setOnClickListener { onPdfClick(pdfFile) }

            // Handle delete icon click
            binding.deleteIcon.setOnClickListener {
                onDeleteClick(pdfFile)
            }
        }
    }
}
