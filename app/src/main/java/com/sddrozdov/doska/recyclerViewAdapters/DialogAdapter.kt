package com.sddrozdov.doska.recyclerViewAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.sddrozdov.doska.R
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

        private val user = FirebaseAuth.getInstance().currentUser
        val photoUri = user?.photoUrl

        fun bind(dialog: Dialog) {
            with(binding) {

                lastMessage.text = dialog.lastMessage
                time.text = SimpleDateFormat("HH:mm", Locale.getDefault())
                    .format(Date(dialog.timestamp))

                Glide.with(root.context)
                    .load(photoUri)
                    .placeholder(R.drawable.ic_account_circle)
                    .circleCrop()
                    .into(userAvatar)

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