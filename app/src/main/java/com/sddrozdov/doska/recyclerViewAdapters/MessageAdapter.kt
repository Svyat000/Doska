package com.sddrozdov.doska.recyclerViewAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.sddrozdov.doska.R
import com.sddrozdov.doska.models.Message

class MessageAdapter : ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_MESSAGE_SENT = 1
        private const val VIEW_TYPE_MESSAGE_RECEIVED = 2
    }

    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderId == FirebaseAuth.getInstance().currentUser?.uid) {
            VIEW_TYPE_MESSAGE_SENT
        } else {
            VIEW_TYPE_MESSAGE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_MESSAGE_SENT -> SentMessageHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_left, parent, false)
            )

            else -> ReceivedMessageHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_message_right, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is SentMessageHolder -> holder.bind(getItem(position))
            is ReceivedMessageHolder -> holder.bind(getItem(position))
        }
    }

    inner class SentMessageHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageText: TextView = view.findViewById(R.id.userMessage)

        fun bind(message: Message) {
            messageText.text = message.text
        }
    }

    inner class ReceivedMessageHolder(view: View) : RecyclerView.ViewHolder(view) {
        private val messageText: TextView = view.findViewById(R.id.userMessage)
        private val userName: TextView = view.findViewById(R.id.userName)

        fun bind(message: Message) {
            messageText.text = message.text
            userName.text = message.senderName
        }
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.timestamp == newItem.timestamp
    }

    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.text == newItem.text &&
                oldItem.senderId == newItem.senderId &&
                oldItem.senderName == newItem.senderName
    }
}