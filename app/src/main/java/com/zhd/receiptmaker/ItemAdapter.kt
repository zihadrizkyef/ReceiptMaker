package com.zhd.receiptmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.zhd.receiptmaker.databinding.ItemPembelianBinding
import java.text.DecimalFormat

class ItemAdapter(private val onDeleteClick: ((item: Item) -> Unit)?) : ListAdapter<Item, ItemAdapter.ViewHolder>(
    object: DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
) {

    var isShowDeleteButton = true
    private val numberFormat = DecimalFormat.getNumberInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPembelianBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = with(holder.binding) {
        val item = getItem(position)
        textNameQty.text = numberFormat.format(item.qty) + " x " + item.name
        textPrice.text = numberFormat.format(item.price)
        textTotal.text = numberFormat.format(item.price * item.qty)
        buttonDelete.isVisible = isShowDeleteButton
        buttonDelete.setOnClickListener {
            onDeleteClick?.invoke(item)
        }
    }

    class ViewHolder(val binding: ItemPembelianBinding) : RecyclerView.ViewHolder(binding.root)
}