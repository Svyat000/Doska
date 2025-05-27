package com.sddrozdov.doska.act

import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sddrozdov.doska.R
import com.sddrozdov.doska.accountHelper.AccountHelperGoogleSignIn
import com.sddrozdov.doska.accountHelper.Listener
import com.sddrozdov.doska.databinding.ActivityMainBinding
import com.sddrozdov.doska.dialogHelper.DialogConstants
import com.sddrozdov.doska.dialogHelper.DialogHelper
import com.sddrozdov.doska.models.Ads
import com.sddrozdov.doska.models.Dialog
import com.sddrozdov.doska.recyclerViewAdapters.AdsAdapter
import com.sddrozdov.doska.recyclerViewAdapters.DialogAdapter
import com.sddrozdov.doska.utilites.FilterManager
import com.sddrozdov.doska.viewModel.FirebaseViewModel
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener,
    AdsAdapter.ItemListener {

    private lateinit var accountTextView: TextView
    private lateinit var imageViewAccount: ImageView
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")
    private val dialogHelper = DialogHelper(this)
    private var accountHelperGoogle: AccountHelperGoogleSignIn? = null
    val mAuth = Firebase.auth
    val adsAdapter = AdsAdapter(this)
    private var cleadUpdate: Boolean = true
    private var currentCategory: String? = null
    private var filter: String = SearchActivity.EMPTY
    private var filterDB: String = ""
    private lateinit var filterLauncher: ActivityResultLauncher<Intent>
    private val firebaseViewModel: FirebaseViewModel by viewModels()
    private lateinit var dialogAdapter: DialogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        WindowInsetUtil.applyWindowInsets(binding.root)
        setupActionBarToggle()
        initRecyclerView()
        accountHelperGoogle = AccountHelperGoogleSignIn(this)
        initViewModel()
        //firebaseViewModel.loadAllAds("0")

        bottomMenuOnClick()
        scrollListener()
        onActivityResultFilter()
    }

    override fun onResume() {
        super.onResume()
        binding.mainContent.bottomNavigationView.selectedItemId = R.id.id_home
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_search) {
            val intent = Intent(this@MainActivity, SearchActivity::class.java).apply {
                putExtra(SearchActivity.FILTER_KEY, filter)
            }
            filterLauncher.launch(intent)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    private fun initRecyclerView() {
        binding.apply {
            mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter = adsAdapter
        }
    }

    private fun setupRecyclerView() {
        dialogAdapter = DialogAdapter { dialog ->
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("AD_ID", dialog.adId)
                putExtra("USER_ID", mAuth.currentUser?.uid)
                putExtra("OWNER_ID", getOtherParticipantId(dialog.participants))
                putExtra("CHAT_KEY", dialog.id)
            }
            startActivity(intent)
        }
        binding.apply {
            mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter = dialogAdapter
        }
    }

    private fun getOtherParticipantId(participants: Map<String?, Boolean>): String {
        val currentUserId = mAuth.currentUser?.uid ?: return ""
        return participants.keys.firstOrNull { it != currentUserId } ?: ""
    }

    private fun loadDialogs() {
        val currentUserId = mAuth.currentUser?.uid ?: return
        val dialogsRef =
            Firebase.database.reference.child("users").child(currentUserId).child("dialogs")

        dialogsRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val dialogs = mutableListOf<Dialog>()
                for (dialogSnapshot in snapshot.children) {
                    val dialog = dialogSnapshot.getValue(Dialog::class.java)
                    dialog?.let { dialogs.add(it) }
                }
                dialogAdapter.submitList(dialogs)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Ошибка загрузки диалогов", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }

    private fun bottomMenuOnClick() = with(binding) {
        mainContent.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            cleadUpdate = true
            when (item.itemId) {
                R.id.id_new_ads -> {
                    val intent = Intent(this@MainActivity, EditAdsActivity::class.java)
                    startActivity(intent)
                }

                R.id.id_my_ads -> {
                    mainContent.rcView.adapter = adsAdapter
                    firebaseViewModel.loadMyAds()
                    mainContent.toolbar.title = getString(R.string.menu_ads_my_items)
                }

                R.id.id_chat -> {
                    setupRecyclerView()
                    loadDialogs()
                    mainContent.toolbar.title = getString(R.string.Dialogs)
//                    val intent = Intent(this@MainActivity, DialogsActivity::class.java)
//                    startActivity(intent)
                }


                R.id.id_home -> {
                    mainContent.rcView.adapter = adsAdapter
                    currentCategory = getString(R.string.menu_ads_main_ads)
                    firebaseViewModel.loadAllAdsFirstPage(filterDB)
                    mainContent.toolbar.title = getString(R.string.menu_ads_main_ads)
                }
            }
            true
        }
    }

    private fun initViewModel() {
        firebaseViewModel.liveAdsData.observe(this) {
            val list = getAdsByCategory(it)
            if (!cleadUpdate) {
                adsAdapter.updateAdapter(list)
            } else {
                adsAdapter.updateAdapterWithClear(list)
            }
            binding.mainContent.EmptyFavAds.visibility =
                if (adsAdapter.itemCount == 0) View.VISIBLE else View.GONE
        }
    }

    private fun getAdsByCategory(list: ArrayList<Ads>): ArrayList<Ads> {
        val tempList = ArrayList<Ads>()
        tempList.addAll(list)
        if (currentCategory != getString(R.string.menu_ads_main_ads)) {
            tempList.clear()
            list.forEach {
                if (currentCategory == it.category) {
                    tempList.add(it)
                }
            }
        }
        tempList.reverse()
        return tempList
    }

    private fun setupActionBarToggle() {
        currentCategory = getString(R.string.menu_ads_main_ads)
        setSupportActionBar(binding.mainContent.toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.mainDrawerLayout,
            binding.mainContent.toolbar,
            R.string.open,
            R.string.close
        )
        binding.mainDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.mainNavigationView.setNavigationItemSelectedListener(this)

        accountTextView =
            binding.mainNavigationView.getHeaderView(0).findViewById(R.id.account_email)
        imageViewAccount =
            binding.mainNavigationView.getHeaderView(0).findViewById(R.id.account_image)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        cleadUpdate = true
        when (item.itemId) {
            R.id.menu_ads_favorite -> {
                firebaseViewModel.loadMyFavoriteAds()
                binding.mainContent.toolbar.title = getString(R.string.menu_ads_favorite)
            }

            R.id.menu_ads_my_items -> {
                firebaseViewModel.loadMyAds()
                binding.mainContent.toolbar.title = getString(R.string.menu_ads_my_items)
            }

            R.id.menu_ads_cars -> {
                getAdsFromCategory(getString(R.string.menu_ads_cars))
            }

            R.id.menu_ads_computers -> {
                getAdsFromCategory(getString(R.string.menu_ads_computers))
            }

            R.id.menu_ads_phones -> {
                getAdsFromCategory(getString(R.string.menu_ads_phones))
            }

            R.id.menu_ads_appliances -> {
                getAdsFromCategory(getString(R.string.menu_ads_appliances))
            }

            R.id.menu_account_register -> {
                dialogHelper.createSignDialog(DialogConstants.SIGN_UP_STATE)

            }

            R.id.menu_account_login -> {
                dialogHelper.createSignDialog(DialogConstants.SIGN_IN_STATE)
            }

            R.id.menu_account_logout -> {
                if (mAuth.currentUser?.isAnonymous == true) {
                    binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
                    return true
                }
                //uiUpdate(null)
                accountHelperGoogle?.signOut()

            }

            R.id.menu_help -> {
                sendSupportEmail()
            }

        }
        binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun sendSupportEmail() {
        val emailIntent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("DoskaSupport@gmail.com"))
        }
        try {
            startActivity(Intent.createChooser(emailIntent, getString(R.string.OPEN_WITH)))
        } catch (exception: ActivityNotFoundException) {
            Toast.makeText(this, "Нет приложения для отправки почты", Toast.LENGTH_SHORT).show()
        }

    }

    fun uiUpdate(user: FirebaseUser?) {
        //accountTextView.text = resources.getString(R.string.not_reg)
        if (user == null) {
            dialogHelper.accountHelperAnonymously.signInAnonymously(object : Listener {
                override fun onComplete() {
                    accountTextView.setText(R.string.Guest)
                    imageViewAccount.setImageResource(R.drawable.ic_profile_placeholder)
                }
            })
        } else if (user.isAnonymous) {
            accountTextView.setText(R.string.Guest)
            imageViewAccount.setImageResource(R.drawable.ic_profile_placeholder)
        } else if (!user.isAnonymous) {
            accountTextView.text = user.email
            Picasso.get().load(user.photoUrl).into(imageViewAccount)
        }
    }

    object WindowInsetUtil {
        fun applyWindowInsets(view: View) {
            ViewCompat.setOnApplyWindowInsetsListener(view) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }
    }

    override fun onDeleteItem(ads: Ads) {
        firebaseViewModel.deleteItem(ads)
    }

    override fun onAdViewed(ads: Ads) {
        firebaseViewModel.adViewed(ads)
        val intent = Intent(this, DescAdsActivity::class.java)
        intent.putExtra("AD", ads)
        startActivity(intent)
    }

    override fun onFavoriteCLicked(ads: Ads) {
        firebaseViewModel.onFavoriteCLick(ads)
    }

    private fun scrollListener() = with(binding.mainContent) {
        rcView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(SCROLL_DOWN) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    cleadUpdate = false
                    val adsList = firebaseViewModel.liveAdsData.value
                    if (adsList != null) {
                        if (adsList.isNotEmpty()) {
                            getAdsFromCategory(adsList)
                        }
                    }
                }
            }
        })
    }

    private fun getAdsFromCategory(category: String) {
        currentCategory = category
        firebaseViewModel.loadAllAdsFromCategory(category, filterDB)
    }

    private fun getAdsFromCategory(adsList: ArrayList<Ads>) {
        adsList[0].let {
            if (currentCategory == getString(R.string.menu_ads_main_ads)) {
                firebaseViewModel.loadAllAdsNextPage(it.time, filterDB)
            } else {
                firebaseViewModel.loadAllAdsFromCategoryNextPage(it.category!!, it.time, filterDB)
            }
        }
    }

    private fun onActivityResultFilter() {
        filterLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    filter = it.data?.getStringExtra(SearchActivity.FILTER_KEY)!!
                    Log.d("MAIN", "filter $filter")
                    Log.d("MAIN", "filter ${FilterManager.getFilter(filter)}")
                    filterDB = FilterManager.getFilter(filter)
                } else if (it.resultCode == RESULT_CANCELED) {
                    filterDB = ""
                    filter = SearchActivity.EMPTY
                }
            }
    }

    companion object {
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
        const val SCROLL_DOWN = 1
    }

}
