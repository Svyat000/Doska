package com.sddrozdov.doska.act

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
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
        retrieveIntentData()
        init()
        setupToolbar()
        setupSendButton()
        checkExistingChats()
    }

    private fun retrieveIntentData() {
        adId = intent.getStringExtra("AD_ID")
        currentUserId = intent.getStringExtra("USER_ID")
        ownerId = intent.getStringExtra("OWNER_ID")
        if (adId == null || currentUserId == null || ownerId == null) {
            Toast.makeText(this, "Ошибка: недостаточно данных", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun init() = with(binding) {
        adapter = MessageAdapter()
        chatRecyclerView.layoutManager = LinearLayoutManager(this@ChatActivity).apply {
            reverseLayout = false
            stackFromEnd = true
        }
        chatRecyclerView.itemAnimator = null
        chatRecyclerView.addItemDecoration(createItemDecoration())
        chatRecyclerView.adapter = adapter
    }

    private fun createItemDecoration() = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.bottom = 32.dpToPx(this@ChatActivity)
            }
        }
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.chatToolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.chatToolbar.findViewById<ImageButton>(R.id.backButton).setOnClickListener { finish() }
        val userPhotoView = binding.chatToolbar.findViewById<ImageView>(R.id.userPhoto)
        val toolbarTitle = binding.chatToolbar.findViewById<TextView>(R.id.toolbarTitle)

        toolbarTitle.text = auth.currentUser ?.displayName ?: "User "
        loadUserPhoto(userPhotoView)
    }

    private fun loadUserPhoto(imageView: ImageView) {
        auth.currentUser ?.photoUrl?.let { photoUrl ->
            Picasso.get().load(photoUrl).into(imageView)
        } ?: run {
            imageView.setImageResource(R.drawable.ic_account_circle)
        }
    }

    private fun setupSendButton() {
        binding.sendMessage.setOnClickListener {
            val messageText = binding.editTextText.text.toString().trim()
            if (messageText.isNotEmpty()) {
                if (chatKey == null) {
                    Toast.makeText(this, "Создаем новый чат...", Toast.LENGTH_SHORT).show()
                    createChat(messageText)
                } else {
                    sendMessage(messageText)
                }
                binding.editTextText.text.clear()
            }
        }
    }

    private fun checkExistingChats() {
        val possibleKeys = listOf("${currentUserId}_$ownerId", "${ownerId}_$currentUserId")
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

    @SuppressLint("RestrictedApi")
    private fun createChat(messageText: String) {
        val currentUser  = auth.currentUser  ?: return

        val sortedIds = listOf(currentUserId, ownerId).sortedBy { it }
        chatKey = "${sortedIds[0]}_${sortedIds[1]}"

        val message = mapOf(
            "text" to messageText,
            "senderId" to currentUser.uid,
            "senderName" to currentUser.displayName
        )

        val chatRef = Firebase.database.getReference("main").child(adId!!).child("CHATS").child(chatKey!!)
        val updates = mapOf(
            "participants/${currentUser.uid}" to true,
            "participants/$ownerId" to true,
            "MESSAGES/${chatRef.push().key}" to message
        )

        chatRef.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                createDialog(messageText, currentUser )
            } else {
                Log.e("ChatError", "Ошибка создания чата: ${task.exception?.message}")
            }
        }
    }

    @SuppressLint("RestrictedApi")
    private fun createDialog(messageText: String, currentUser: FirebaseUser) {
        val dialog = Dialog(
            id = chatKey!!,
            adId = adId!!,
            participants = mapOf(currentUser .uid to true, ownerId to true),
            lastMessage = messageText,
            timestamp = System.currentTimeMillis()
        )

        val currentUserRef = Firebase.database.reference.child("users").child(currentUser .uid).child("dialogs").child(chatKey!!)
        val ownerUserRef = Firebase.database.reference.child("users").child(ownerId!!).child("dialogs").child(chatKey!!)

        val combinedUpdates = mapOf(
            currentUserRef.path.toString() to dialog,
            ownerUserRef.path.toString() to dialog
        )

        Firebase.database.reference.updateChildren(combinedUpdates).addOnSuccessListener {
            listenForMessages(chatKey!!)
        }.addOnFailureListener { e ->
            Log.e("ChatError", "Ошибка сохранения диалогов: ${e.message}")
        }
    }

    private fun listenForMessages(chatKey: String) {
        val query = Firebase.database.getReference("main").child(adId!!).child("CHATS").child(chatKey).child("MESSAGES")

        query.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val message = snapshot.getValue(Message::class.java)
                message?.let {
                    updateMessageList(it)
                    updateLastMessage(it.text)
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val updatedMessage = snapshot.getValue(Message::class.java)
                updatedMessage?.let { updateMessageList(it) }
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {}
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
            override fun onCancelled(error: DatabaseError) {
                Log.e("ChatError", "Ошибка загрузки сообщений: ${error.message}")
            }
        })
    }

    private fun updateMessageList(message: Message) {
        val newList = ArrayList(adapter.currentList)
        newList.add(message)
        adapter.submitList(newList)
    }

    private fun updateLastMessage(messageText: String) {
        val updateData = mapOf(
            "lastMessage" to messageText,
            "timestamp" to ServerValue.TIMESTAMP
        )

        chatKey?.let {
            Firebase.database.reference.child("users").child(currentUserId!!).child("dialogs").child(
                it
            ).updateChildren(updateData)
        }
        chatKey?.let {
            Firebase.database.reference.child("users").child(ownerId!!).child("dialogs").child(
                it
            ).updateChildren(updateData)
        }
    }

    private fun sendMessage(messageText: String) {
        val currentUser  = auth.currentUser  ?: return

        chatKey?.let { key ->
            val messagesRef = Firebase.database.getReference("main").child(adId!!).child("CHATS").child(key).child("MESSAGES")
            checkParticipant(key, currentUser .uid) { isParticipant ->
                if (isParticipant) {
                    val message = mapOf(
                        "text" to messageText,
                        "senderId" to currentUser .uid,
                        "senderName" to currentUser .displayName
                    )
                    messagesRef.push().setValue(message)
                } else {
                    Toast.makeText(this@ChatActivity, "Вы не участник", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkParticipant(chatKey: String, userId: String, callback: (Boolean) -> Unit) {
        Firebase.database.getReference("main").child(adId!!).child("CHATS").child(chatKey).child("participants").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.exists())
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("ChatError", "Проверка участников не пройдена: ${error.message}")
                    callback(false)
                }
            })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    fun Int.dpToPx(context: Context): Int = (this * context.resources.displayMetrics.density).toInt()
}
