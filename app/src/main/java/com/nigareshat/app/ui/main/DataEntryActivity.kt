package com.nigareshat.app.ui.main

import CustomerDetailsModel
import NigareshatModel
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.nigareshat.app.databinding.ActivityDataEntryBinding
import com.nigareshat.app.ui.socialMedia.ui.DownloadVideoActivity
import com.nigareshat.book.store.adapter.BookListAdapter
import com.nigareshat.book.store.model.BookItemModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DataEntryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataEntryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // testing


        binding.history.setOnClickListener {
            startActivity(Intent(this, PdfListActivity::class.java))
        }

        binding.logo.setOnClickListener {
            val intent = Intent(this, DownloadVideoActivity::class.java)
            startActivity(intent)
        }


        val bookListAdapter = BookListAdapter()
        binding.rvBookEntry.adapter = bookListAdapter

        bookListAdapter.submitList(mutableListOf(BookItemModel(1)))

        // Handle adding more books
        binding.addMoreBtn.setOnClickListener {
            val newList = bookListAdapter.currentList.toMutableList()
            newList.add(BookItemModel(newList.last().id + 1))
            bookListAdapter.submitList(newList)
        }

        // Handle form submission
        binding.BtnSubmit.setOnClickListener {
            if (!isBookDataValid(bookListAdapter)) {
                Toast.makeText(this, "Please fill all the fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bookEntryList =
                ArrayList(bookListAdapter.currentList.map { it.toNigareshatModel() })
            val customerData = getCustomerData()

            if (customerData == null) {
                Toast.makeText(this, "Please Fill Customer Data", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Start MainActivity with the collected data
            val intent = Intent(this, MainActivity::class.java).apply {
                putParcelableArrayListExtra("bel", bookEntryList)
                putExtra("cd", customerData)
            }
            startActivity(intent)
        }
    }

    private fun isBookDataValid(adapter: BookListAdapter): Boolean {
        return adapter.currentList.all {
            it.bookName.isNotEmpty() && it.quantity.isNotEmpty() && it.price.isNotEmpty() && it.discount.isNotEmpty() && it.amount.isNotEmpty()
        }
    }

    private fun getCustomerData(): CustomerDetailsModel? {
        val customerName = binding.tvCustomerName.text.toString()
        val shippingMethod = binding.ShippingViaSpinner.selectedItem.toString()
        val shipAmount = binding.shipAmountValue.text.toString()

        return if (customerName.isNotBlank() && shipAmount.isNotBlank()) {
            val formattedDate = SimpleDateFormat(
                "dd/MM/yyyy",
                Locale.getDefault()
            ).format(Calendar.getInstance().time)
            CustomerDetailsModel(customerName, shippingMethod, shipAmount, formattedDate)
        } else {
            null
        }
    }

    private fun BookItemModel.toNigareshatModel(): NigareshatModel {
        return NigareshatModel(
            bookName = bookName,
            quantity = quantity,
            price = price,
            discount = discount,
            amount = amount.toInt()
        )
    }


}
