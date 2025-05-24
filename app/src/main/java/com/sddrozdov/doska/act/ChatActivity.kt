package com.sddrozdov.doska.act

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sddrozdov.doska.R
import com.sddrozdov.doska.databinding.ActivityChatBinding
import com.sddrozdov.doska.models.Dialog
import com.sddrozdov.doska.models.Message
import com.sddrozdov.doska.recyclerViewAdapters.MessageAdapter
import com.squareup.picasso.Picasso

class ChatActivity : AppCompatActivity() {
    private var _binding: ActivityChatBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    private lateinit var auth: FirebaseAuth
    private lateinit var adapter: MessageAdapter

    private var chatKey: String? = null
    private var adId: String? = null
    private var currentUserId: String? = null
    private var ownerId: String? = null
    private var isChatFound = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        MainActivity.WindowInsetUtil.applyWindowInsets(binding.root)


        auth = Firebase.auth
        adId = intent.getStringExtra("AD_ID")
        currentUserId = intent.getStringExtra("USER_ID")
        ownerId = intent.getStringExtra("OWNER_ID")
        if (adId == null || currentUserId == null || ownerId == null) {
            Toast.makeText(this, "Ошибка: недостаточно данных", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setSupportActionBar(binding.chatToolbar)
        toolbarInit()
        init()
        setupSendButton()
        checkExistingChats()

    }

    private fun toolbarInit(){
        setSupportActionBar(binding.chatToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.chatToolbar.findViewById<ImageButton>(R.id.backButton).setOnClickListener {
            finish()
        }
        val userPhotoView = binding.chatToolbar.findViewById<ImageView>(R.id.userPhoto)
        val toolbarTitle = binding.chatToolbar.findViewById<TextView>(R.id.toolbarTitle)

        toolbarTitle.text = auth.currentUser?.displayName ?: "User"

        val photoUrl = auth.currentUser?.photoUrl
        if (photoUrl != null) {
            Picasso.get().load(photoUrl).into(userPhotoView)
        } else {
            userPhotoView.setImageResource(R.drawable.ic_account_circle)
        }
    }

    private fun init() = with(binding) {
        adapter = MessageAdapter()
        recyclerView.layoutManager = LinearLayoutManager(this@ChatActivity)
        recyclerView.adapter = adapter
    }

    private fun checkExistingChats() {
        val possibleKeys = listOf(
            "${currentUserId}_$ownerId",
            "${ownerId}_$currentUserId"
        )

        possibleKeys.forEach { key ->
            Firebase.database.getReference("main")
                .child(adId!!)
                .child("CHATS")
                .child(key)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (isChatFound) return
                        if (snapshot.exists()) {
                            isChatFound = true
                            chatKey = key
                            listenForMessages(key)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ChatError", "Ошибка проверки чата: ${error.message}")
                    }
                })
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupSendButton() {
        binding.sendMessage.setOnClickListener {
            val messageText = binding.editTextText.text.toString()
            if (messageText.isEmpty()) return@setOnClickListener

            if (chatKey == null) {
                Toast.makeText(this, "Создаем новый чат...", Toast.LENGTH_SHORT).show()
                createChat(messageText)
            } else {
                sendMessage(messageText)
            }
            binding.editTextText.text.clear()
        }
    }


    @SuppressLint("RestrictedApi")
    private fun createChat(messageText: String) {
        val currentUser = auth.currentUser ?: return

        val sortedIds = listOf(currentUserId, ownerId).sortedBy { it }
        chatKey = "${sortedIds[0]}_${sortedIds[1]}"

        val message = hashMapOf(
            "text" to messageText,
            "sender" to currentUser.uid,
            "senderName" to currentUser.displayName
        )

        val chatRef = Firebase.database.getReference("main")
            .child(adId!!)
            .child("CHATS")
            .child(chatKey!!)

        val updates = hashMapOf<String, Any>(
            "participants/${currentUser.uid}" to true,
            "participants/$ownerId" to true,
            "MESSAGES/${chatRef.push().key}" to message
        )

        chatRef.updateChildren(updates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val dialog = Dialog(
                        id = chatKey!!,
                        adId = adId!!,
                        participants = mapOf(
                            currentUser.uid to true,
                            ownerId to true
                        ),
                        lastMessage = messageText,
                        timestamp = System.currentTimeMillis()
                    )
                    val currentUserRef =
                        Firebase.database.reference  // Ссылки для обновления диалогов обоих пользователей
                            .child("users")
                            .child(currentUser.uid)
                            .child("dialogs")
                            .child(chatKey!!)

                    val ownerUserRef = Firebase.database.reference
                        .child("users")
                        .child(ownerId!!)
                        .child("dialogs")
                        .child(chatKey!!)

                    // Пакетное обновление
                    val combinedUpdates = hashMapOf<String, Any>(
                        currentUserRef.path.toString() to dialog,
                        ownerUserRef.path.toString() to dialog
                    )

                    Firebase.database.reference.updateChildren(combinedUpdates)
                        .addOnSuccessListener {
                            listenForMessages(chatKey!!)
                        }
                        .addOnFailureListener { e ->
                            Log.e("ChatError", "Ошибка сохранения диалогов: ${e.message}")
                        }

                } else {
                    Log.e("ChatError", "Ошибка создания чата: ${task.exception?.message}")
                }
            }
    }

    private fun listenForMessages(chatKey: String) {
        val query = Firebase.database.getReference("main").child(adId!!).child("CHATS").child(chatKey)
                .child("MESSAGES")

                query.addChildEventListener(object : ChildEventListener {
                        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedMessage = snapshot.getValue(Message::class.java)
                updatedMessage?.let {
                    val newList = adapter.currentList.toMutableList()
                    val index = newList.indexOfFirst { msg -> msg.timestamp == it.timestamp }
                    if (index != -1) {
                        newList[index] = it
                        adapter.submitList(newList)
                    }
                }
            }
            override fun onChildRemoved(snapshot: DataSnapshot) {
//                val removedMessage = snapshot.getValue(Message::class.java)
//                removedMessage?.let {
//                    val newList = adapter.currentList.toMutableList().apply { remove(it) }
//                    adapter.submitList(newList)
//                }
            }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatError", "Ошибка загрузки сообщений: ${error.message}")
            }

            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                if (message != null) {
                    adapter.submitList(ArrayList(adapter.currentList + message))// Обновление списка сообщений
                    message.text?.let { updateLastMessage(it) }// Обновление последнего сообщение в диалогах
                }
            }

            private fun updateLastMessage(messageText: String) {
                val updateData = hashMapOf<String, Any>(
                    "lastMessage" to messageText,
                    "timestamp" to ServerValue.TIMESTAMP
                )

                Firebase.database.reference.child("users")
                    .child(currentUserId!!)
                    .child("dialogs")
                    .child(chatKey)
                    .updateChildren(updateData)

                Firebase.database.reference.child("users")
                    .child(ownerId!!)
                    .child("dialogs")
                    .child(chatKey)
                    .updateChildren(updateData)
            }
        })
    }

    private fun sendMessage(messageText: String) {
        val currentUser = auth.currentUser ?: return

        chatKey?.let { key ->
            val messagesRef = Firebase.database.getReference("main")
                .child(adId!!)
                .child("CHATS")
                .child(key)
                .child("MESSAGES")

            Firebase.database.getReference("main")// Проверка участие в чате
                .child(adId!!)
                .child("CHATS")
                .child(key)
                .child("participants")
                .child(currentUser.uid)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            val message = hashMapOf(
                                "text" to messageText,
                                "sender" to currentUser.uid,
                                "senderName" to currentUser.displayName
                            )
                            messagesRef.push().setValue(message)
                        } else {
                            Toast.makeText(
                                this@ChatActivity,
                                "Вы не участник",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("ChatError", "Проверка участников не пройдена: ${error.message}")
                    }
                })
        }
    }
}