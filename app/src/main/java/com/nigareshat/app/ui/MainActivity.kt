package com.nigareshat.app.ui

import CustomerDetailsModel
import NigareshatModel
import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.nigareshat.app.databinding.ActivityMainBinding
import com.nigareshat.app.databinding.DialogFormBinding
import com.nigarishat.adapter.TableAdapter
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class MainActivity : AppCompatActivity(), TableAdapter.ItemClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: TableAdapter
    private lateinit var shippingAmount: String
    private lateinit var customerDetails: CustomerDetailsModel
    private lateinit var bookEntryList: ArrayList<NigareshatModel>
    private var pdfUri: Uri? = null // URI for the generated PDF

    private var isPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!isPermissionGranted) {
            requestPermissions()
        }

        setupRecyclerView()
        populateCustomerDetails()
        setupPrintButton()
    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                intent.data = Uri.parse("package:${applicationContext.packageName}")
                startActivityForResult(intent, REQUEST_CODE_MANAGE_STORAGE)
            } else {
                isPermissionGranted = true
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_CODE_WRITE_EXTERNAL_STORAGE
                )
            } else {
                isPermissionGranted = true
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_WRITE_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    isPermissionGranted = true
                } else {
                    isPermissionGranted = false
                    Toast.makeText(
                        this,
                        "Storage permission denied! The app may not function properly.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_MANAGE_STORAGE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    isPermissionGranted = true
                    Toast.makeText(
                        this,
                        "Manage External Storage permission granted!",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    isPermissionGranted = false
                    Toast.makeText(
                        this,
                        "Manage External Storage permission denied! The app may not function properly.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupRecyclerView() {
        // Retrieve the list of books from the intent
        bookEntryList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.getParcelableArrayListExtra("bel", NigareshatModel::class.java)!!
        } else {
            intent?.getParcelableArrayListExtra("bel")!!
        }
        adapter = TableAdapter(this, bookEntryList, this)
        binding.rvTodayOrder.adapter = adapter
    }

    private fun populateCustomerDetails() {
        // Retrieve and display customer details from the intent
        intent?.let {
            customerDetails = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                intent.getParcelableExtra("cd", CustomerDetailsModel::class.java)!!
            } else {
                intent.getParcelableExtra("cd")!!
            }
            binding.tvCustomerName.text = customerDetails.customerName
            binding.shipAmountValue.text = customerDetails.shipAmount
            binding.shipViaValue.text = customerDetails.shipVia
            binding.shipDateValue.text = customerDetails.shipDate

            shippingAmount = customerDetails.shipAmount
            binding.tvShippingChargeValue.text = shippingAmount
            finalAmount()
        }
    }

    private fun setupPrintButton() {
        // Set up print button functionality
        binding.download.setOnClickListener {
            if (isPermissionGranted) {
                pdfUri = generatePdf() // Generate the PDF and get the URI
                if (pdfUri != null) {
                    Toast.makeText(this, "PDF upload successfully in history.", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Error generating PDF.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(
                    this,
                    "Storage permission is required to save the PDF.",
                    Toast.LENGTH_SHORT
                ).show()
                requestPermissions()
            }
        }

        // Set up share button functionality
        binding.share.setOnClickListener {
            sharePDFFile()
        }
    }

    private fun generatePdf(): Uri? {
        // Generate a PDF from the contents of the ScrollView
        val scrollView = binding.scrollView
        val totalHeight = scrollView.getChildAt(0).height
        val totalWidth = scrollView.width

        // Create a bitmap from the ScrollView
        val bitmap = Bitmap.createBitmap(totalWidth, totalHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        scrollView.draw(canvas)

        // Create a PDF document
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(totalWidth, totalHeight, 1).create()
        val page: PdfDocument.Page = pdfDocument.startPage(pageInfo)
        val pdfCanvas = page.canvas
        pdfCanvas.drawBitmap(bitmap, 0f, 0f, null)
        pdfDocument.finishPage(page)

        val customerName = customerDetails.customerName.replace(" ", "_") // Replace spaces in name
        val fileName = "Nigareshat$customerName.pdf"

        var pdfUri: Uri? = null

        // Create a directory within the app's package for storing PDFs
        val pdfDir = File(getExternalFilesDir(null), "MaktabeFarooqPDFs")
        if (!pdfDir.exists()) {
            pdfDir.mkdirs() // Create directory if it doesn't exist
        }

        val file = File(pdfDir, fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(file))

            // Use FileProvider to generate a content URI
            pdfUri = FileProvider.getUriForFile(
                this,
                "${applicationContext.packageName}.provider",  // Use your app's package name
                file
            )
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(this, "Error saving PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }

        pdfDocument.close()
        return pdfUri // Return the URI of the generated PDF
    }


    private fun sharePDFFile() {
        // Check if pdfUri is null, if so, generate the PDF first
        if (pdfUri == null) {
            if (isPermissionGranted) {
                pdfUri = generatePdf() // Generate the PDF and get the URI
                if (pdfUri == null) {
                    Toast.makeText(this, "Error generating PDF.", Toast.LENGTH_SHORT).show()
                    return
                }
            } else {
                Toast.makeText(
                    this,
                    "Storage permission is required to save the PDF.",
                    Toast.LENGTH_SHORT
                ).show()
                requestPermissions()
                return
            }
        }

        // Proceed to share the PDF now that pdfUri is guaranteed to be non-null
        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, pdfUri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        // Start the intent to share the PDF
        try {
            startActivity(Intent.createChooser(shareIntent, "Share PDF via"))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(this, "No application found to share PDF.", Toast.LENGTH_SHORT).show()
        }
    }


    // Other existing methods...

    override fun onItemClick(position: Int) {
        val shippingInfo = adapter.shippingInfoList[position]
        val builder = MaterialAlertDialogBuilder(this)
        val dialogBinding = DialogFormBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)

        with(dialogBinding) {
            // Set initial values from the shipping info
            bookName.setText(shippingInfo.bookName)
            val quantityPosition =
                (quantitySpinner.adapter as ArrayAdapter<String>).getPosition(shippingInfo.quantity)
            quantitySpinner.setSelection(quantityPosition)
            price.setText(shippingInfo.price)
            discount.setText(shippingInfo.discount)
            amount.setText(shippingInfo.amount.toString())

            updateAmount(dialogBinding)

            val textWatcher = object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    updateAmount(dialogBinding)
                }

                override fun afterTextChanged(s: Editable?) {}
            }

            // Apply the TextWatcher to price and discount fields
            price.addTextChangedListener(textWatcher)
            discount.addTextChangedListener(textWatcher)

            // Update amount when quantity changes
            quantitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    updateAmount(dialogBinding)
                }

                override fun onNothingSelected(parent: AdapterView<*>) {}
            }
        }

        val dialog = builder.create()
        dialog.show()

        dialogBinding.updateBtn.setOnClickListener {
            shippingInfo.bookName = dialogBinding.bookName.text.toString().trim()
            shippingInfo.quantity = dialogBinding.quantitySpinner.selectedItem.toString()
            shippingInfo.price = dialogBinding.price.text.toString().trim()
            shippingInfo.discount = dialogBinding.discount.text.toString()
            shippingInfo.amount = dialogBinding.amount.text.toString().toDouble().toInt()

            finalAmount()  // Update total amount after item update
            adapter.notifyItemChanged(position)
            dialog.dismiss()
        }
    }

    // Function to dynamically calculate the amount
    private fun updateAmount(dialogBinding: DialogFormBinding) {
        val quantity = dialogBinding.quantitySpinner.selectedItem.toString().toDoubleOrNull() ?: 1.0
        val price = dialogBinding.price.text.toString().toDoubleOrNull() ?: 0.0
        val discount = dialogBinding.discount.text.toString().toDoubleOrNull() ?: 0.0
        // Calculate the amount based on quantity * price - discount
        val disValue = (price * (discount / 100))
        val calculatedAmount = quantity * (price - disValue)
        // Set the amount dynamically in the TextView
        dialogBinding.amount.text = calculatedAmount.toString()
    }

    private fun finalAmount() {
        val sumOfPrice = adapter.calculateTotalAmount()
        binding.tvTotalAmountValue.text = sumOfPrice.toString()
        val totalAmount = sumOfPrice + shippingAmount.toDouble()
        binding.tvTotalValue.text = totalAmount.toString()
    }

    companion object {
        private const val REQUEST_CODE_MANAGE_STORAGE = 1
        private const val REQUEST_CODE_WRITE_EXTERNAL_STORAGE = 2
    }
}
