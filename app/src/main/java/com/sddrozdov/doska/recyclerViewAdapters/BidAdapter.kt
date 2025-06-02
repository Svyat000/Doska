package com.sddrozdov.doska.recyclerViewAdapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sddrozdov.doska.R
import com.sddrozdov.doska.models.Bid
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BidAdapter(
    private val bids: List<Bid>,
    private val userNamesMap: Map<String, String>,
    private val currentUserUid: String?,
    private val currentUserEmail: String?
) : RecyclerView.Adapter<BidAdapter.BidViewHolder>() {

    inner class BidViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAmount: TextView = itemView.findViewById(R.id.tvAmount)
        val tvUser: TextView = itemView.findViewById(R.id.tvUser)
        val tvTime: TextView = itemView.findViewById(R.id.tvTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BidViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_bid, parent, false)
        return BidViewHolder(view)
    }

    override fun onBindViewHolder(holder: BidViewHolder, position: Int) {
        val bid = bids[position]
        try {
            holder.tvAmount.text = "Ставка: ${bid.amount}"

            val userName = if (bid.userId == currentUserUid) {
                currentUserEmail ?: "Вы"
            } else {
                userNamesMap[bid.userId] ?: getShortUserId(bid.userId)
            }
            holder.tvUser.text = "Участник: $userName"

            holder.tvTime.text = "Время: ${formatTime(bid.timestamp)}"
        } catch (e: Exception) {
            Log.e("BidAdapter", "Error binding bid", e)
            holder.tvAmount.text = "Ошибка отображения ставки"
            holder.tvUser.text = ""
            holder.tvTime.text = ""
        }
    }

    private fun getShortUserId(userId: String): String {
        return if (userId.length > 8) userId.take(8) + "..." else userId
    }

    private fun formatTime(timestamp: Long): String {
        return try {
            SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
        } catch (e: Exception) {
            "неизвестно"
        }
    }

    override fun getItemCount() = bids.size
}