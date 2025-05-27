package com.sddrozdov.doska.act

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sddrozdov.doska.R
import com.sddrozdov.doska.databinding.ActivityDialogsBinding
import com.sddrozdov.doska.models.Dialog
import com.sddrozdov.doska.recyclerViewAdapters.DialogAdapter

class DialogsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDialogsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: DialogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDialogsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        setupToolbar()
        setupRecyclerView()
        loadDialogs()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.dialogsToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.dialogsToolbar.findViewById<ImageButton>(R.id.backButton)
            .setOnClickListener { finish() }
    }

    private fun setupRecyclerView() {
        adapter = DialogAdapter { dialog ->
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("AD_ID", dialog.adId)
                putExtra("USER_ID", auth.currentUser?.uid)
                putExtra("OWNER_ID", getOtherParticipantId(dialog.participants))
                putExtra("CHAT_KEY", dialog.id)
            }
            startActivity(intent)
        }

        binding.dialogsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.dialogsRecyclerView.adapter = adapter
    }

    private fun getOtherParticipantId(participants: Map<String?, Boolean>): String {
        val currentUserId = auth.currentUser?.uid ?: return ""
        return participants.keys.firstOrNull { it != currentUserId } ?: ""
    }

    private fun loadDialogs() {
        val currentUserId = auth.currentUser?.uid ?: return
        val dialogsRef =
            Firebase.database.reference.child("users").child(currentUserId).child("dialogs")

        dialogsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dialogs = mutableListOf<Dialog>()
                for (dialogSnapshot in snapshot.children) {
                    val dialog = dialogSnapshot.getValue(Dialog::class.java)
                    dialog?.let { dialogs.add(it) }
                }
                adapter.submitList(dialogs)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DialogsActivity, "Ошибка загрузки диалогов", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}