package com.nigarishat.adapter

import NigareshatModel
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.nigareshat.app.databinding.TableSingleItemBinding

class TableAdapter(
    context: Context,
    val shippingInfoList: List<NigareshatModel>,
    private val itemClickListner: ItemClickListener
) :
    RecyclerView.Adapter<TableAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: TableSingleItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            TableSingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = shippingInfoList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val shippingInfo = shippingInfoList[position]
        holder.binding.bookName.text = shippingInfo.bookName
        holder.binding.quntity.text = shippingInfo.quantity
        holder.binding.price.text = shippingInfo.price
        holder.binding.amount.text = shippingInfo.amount.toString()
        holder.binding.discount.text = shippingInfo.discount
        holder.binding.Sn.text = (position + 1).toString()

        holder.binding.root.setOnClickListener {
            itemClickListner.onItemClick(position)
        }
    }

    fun calculateTotalAmount(): Int {
        var totalAmount = 0
        for (item in shippingInfoList) {
            totalAmount += item.amount
        }
        return totalAmount
    }

    interface ItemClickListener {
        fun onItemClick(position: Int)
    }
}
