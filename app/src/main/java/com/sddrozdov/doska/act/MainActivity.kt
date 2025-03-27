package com.sddrozdov.doska.act

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
import com.google.firebase.ktx.Firebase
import com.sddrozdov.doska.R
import com.sddrozdov.doska.accountHelper.AccountHelperGoogleSignIn
import com.sddrozdov.doska.accountHelper.Listener
import com.sddrozdov.doska.databinding.ActivityMainBinding
import com.sddrozdov.doska.dialogHelper.DialogConstants
import com.sddrozdov.doska.dialogHelper.DialogHelper
import com.sddrozdov.doska.models.Ads
import com.sddrozdov.doska.recyclerViewAdapters.AdsAdapter
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

    private val firebaseViewModel: FirebaseViewModel by viewModels()

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
        firebaseViewModel.loadAllAds()
        bottomMenuOnClick()
        scrollListener()
    }

    override fun onResume() {
        super.onResume()
        binding.mainContent.bottomNavigationView.selectedItemId = R.id.id_home
    }


//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        return super.onOptionsItemSelected(item) TODO()
//    } метод когда пользователь выбирает элемент из меню

//    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
//        menuInflater.inflate(R.menu.main_menu, menu)
//        return super.onCreateOptionsMenu(menu) TODO()
//    } метод для надувания меню из xml файла menu

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

    private fun bottomMenuOnClick() = with(binding) {
        mainContent.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.id_new_ads -> {
                    val intent = Intent(this@MainActivity, EditAdsActivity::class.java)
                    startActivity(intent)
                }

                R.id.id_my_ads -> {
                    firebaseViewModel.loadMyAds()
                    mainContent.toolbar.title = getString(R.string.menu_ads_my_items)
                }

                R.id.id_favorites -> firebaseViewModel.loadMyFavoriteAds()

                R.id.id_home -> {
                    firebaseViewModel.loadAllAds()
                    mainContent.toolbar.title = getString(R.string.menu_ads_main_ads)
                }
            }
            true
        }
    }

    private fun initViewModel() {
        firebaseViewModel.liveAdsData.observe(this) {
            adsAdapter.updateAdapter(it)
            binding.mainContent.EmptyFavAds.visibility =
                if (it.isEmpty()) View.VISIBLE else View.GONE
        }
    }


    private fun setupActionBarToggle() {
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

        when (item.itemId) {
            R.id.menu_ads_my_items -> {
                Toast.makeText(this, "Нажали на мои объявления", Toast.LENGTH_LONG).show()
            }

            R.id.menu_ads_cars -> {
                Toast.makeText(this, "Нажали на объявления по машинам", Toast.LENGTH_LONG).show()
            }

            R.id.menu_ads_computers -> {
                Toast.makeText(this, "Нажали на объявления по компьютерам", Toast.LENGTH_LONG)
                    .show()
            }

            R.id.menu_ads_phones -> {
                Toast.makeText(this, "Нажали на объявления по телефонам", Toast.LENGTH_LONG).show()
            }

            R.id.menu_ads_appliances -> {
                Toast.makeText(this, "Нажали на объявления по бытовой технике", Toast.LENGTH_LONG)
                    .show()
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
        }
        binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
        return true
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

    companion object {
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
        const val SCROLL_DOWN = 1
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

                }
            }
        })
    }
}
