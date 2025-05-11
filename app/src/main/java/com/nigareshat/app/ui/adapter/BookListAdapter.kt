package com.nigareshat.book.store.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.nigareshat.app.databinding.BookListSingleItemBinding
import com.nigareshat.book.store.model.BookItemModel

class BookListAdapter : ListAdapter<BookItemModel, BookListAdapter.ViewHolder>(diffUtil) {
    class ViewHolder(val binding: BookListSingleItemBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            BookListSingleItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = getItem(holder.adapterPosition)

        with(holder.binding) {
            remove.setOnClickListener {
                val list = currentList.toMutableList()
                list.removeAt(holder.adapterPosition)
                submitList(list)
                Log.d("TAG", "deleted position : " + holder.adapterPosition.toString())
            }
            remove.isVisible = holder.adapterPosition != 0



            bookName.addTextChangedListener {
                item.bookName = it.toString()
            }

            price.addTextChangedListener {
                item.price = it.toString()
                amount.calculateTotalAmount(item)
            }

            discount.addTextChangedListener {
                item.discount = it.toString()
                amount.calculateTotalAmount(item)
            }

            quantitySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    item.quantity = parent?.selectedItem.toString()
                    amount.calculateTotalAmount(item)

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }


        }


    }

    private fun TextView.calculateTotalAmount(item: BookItemModel) {
        // Get the values entered by the user
        val quantityStr = item.quantity
        val priceStr = item.price
        val discountStr = item.discount

        if (quantityStr.isNotEmpty() && priceStr.isNotEmpty() && discountStr.isNotEmpty()) {
            val quantity = quantityStr.toInt()
            val price = priceStr.toDouble()
            val discount = discountStr.toDouble()


            val per = (price * discount) / 100
            val total = quantity * (price - per)

            this.text = total.toInt().toString()
            item.amount = this.text.toString()
        }

    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<BookItemModel>() {
            override fun areItemsTheSame(oldItem: BookItemModel, newItem: BookItemModel): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: BookItemModel,
                newItem: BookItemModel
            ): Boolean {
                return oldItem == newItem
            }

        }
    }

}