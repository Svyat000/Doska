package com.sddrozdov.doska.recyclerViewAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sddrozdov.doska.databinding.DialogItemBinding
import com.sddrozdov.doska.models.Dialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DialogAdapter(
    private val onDialogClick: (Dialog) -> Unit
) : ListAdapter<Dialog, DialogAdapter.DialogViewHolder>(DiffCallback()) {

    class DialogViewHolder(
        private val binding: DialogItemBinding,
        private val onDialogClick: (Dialog) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind( dialog: Dialog) {
            with(binding) {
                lastMessage.text = dialog.lastMessage
                time.text = SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(Date(dialog.timestamp))

                root.setOnClickListener { onDialogClick(dialog) }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Dialog>() {
        override fun areItemsTheSame(oldItem: Dialog, newItem: Dialog) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Dialog, newItem: Dialog) = oldItem == newItem
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DialogViewHolder {
        val binding = DialogItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DialogViewHolder(binding, onDialogClick)
    }

    override fun onBindViewHolder(holder: DialogViewHolder, position: Int) {
            holder.bind(getItem(position))
    }
}