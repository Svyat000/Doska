package com.sddrozdov.doska.act

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sddrozdov.doska.databinding.ActivityAuctionBidsBinding
import com.sddrozdov.doska.models.Bid
import com.sddrozdov.doska.recyclerViewAdapters.BidAdapter

class AuctionBidsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuctionBidsBinding
    private lateinit var adapter: BidAdapter
    private val bidsList = ArrayList<Bid>()

    private val userNamesMap = mutableMapOf<String, String>()
    private val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid
    private val currentUserEmail = FirebaseAuth.getInstance().currentUser?.email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuctionBidsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)

        val adKey = intent.getStringExtra("AD_KEY") ?: run {
            Toast.makeText(this, "Invalid ad reference", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setupRecyclerView()
        loadBids(adKey)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        adapter = BidAdapter(bidsList, userNamesMap, currentUserUid, currentUserEmail)
        binding.rvBids.adapter = adapter
        binding.rvBids.layoutManager = LinearLayoutManager(this)
    }

    private fun loadBids(adKey: String) {
        Firebase.database.getReference("main/$adKey/auctionHistory")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    bidsList.clear()
                    val userIdsToLoad = mutableSetOf<String>()

                    for (bidSnapshot in snapshot.children) {
                        val bid = bidSnapshot.getValue(Bid::class.java)
                        if (bid != null) {
                            bidsList.add(bid)
                            userIdsToLoad.add(bid.userId)
                        }
                    }

                    bidsList.sortByDescending { it.timestamp }

                    val iterator = userIdsToLoad.iterator()
                    fun loadNext() {
                        if (!iterator.hasNext()) {
                            adapter.notifyDataSetChanged()
                            return
                        }
                        val userId = iterator.next()
                        loadUserName(userId) {
                            loadNext()
                        }
                    }
                    loadNext()
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(
                        this@AuctionBidsActivity,
                        "Failed to load bids: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
    private fun loadUserName(userId: String, onLoaded: () -> Unit) {
        if (userNamesMap.containsKey(userId)) {
            onLoaded()
            return
        }

        Firebase.database.getReference("users/$userId/name")
            .get()
            .addOnSuccessListener { snapshot ->
                val name = snapshot.getValue(String::class.java) ?: userId
                userNamesMap[userId] = name
                onLoaded()
            }
            .addOnFailureListener {
                userNamesMap[userId] = userId
                onLoaded()
            }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}