package com.sddrozdov.doska

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sddrozdov.doska.accountHelper.AccountHelperGoogleSignIn
import com.sddrozdov.doska.act.EditAdsActivity
import com.sddrozdov.doska.databinding.ActivityMainBinding
import com.sddrozdov.doska.dialogHelper.DialogConstants
import com.sddrozdov.doska.dialogHelper.DialogHelper
import com.sddrozdov.doska.recyclerViewAdapters.AdsAdapter
import com.sddrozdov.doska.viewModel.FirebaseViewModel

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {

    private lateinit var accountTextView: TextView

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    private val dialogHelper = DialogHelper(this)

    private var accountHelperGoogle: AccountHelperGoogleSignIn? = null

    val mAuth = Firebase.auth

    val adsAdapter = AdsAdapter(mAuth)

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

                R.id.id_my_ads -> Toast.makeText(
                    this@MainActivity,
                    "TEST MY ADS",
                    Toast.LENGTH_LONG
                ).show()

                R.id.id_favorites -> Toast.makeText(
                    this@MainActivity,
                    "TEST MY FAVORITE",
                    Toast.LENGTH_LONG
                ).show()

                R.id.id_home -> Toast.makeText(this@MainActivity, "TEST HO HOME", Toast.LENGTH_LONG)
                    .show()
            }
            true
        }
    }

    private fun initViewModel() {
        firebaseViewModel.liveAdsData.observe(this, {
            adsAdapter.updateAdapter(it)
        })
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
                uiUpdate(null)
                accountHelperGoogle?.signOut()
            }
        }
        binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    fun uiUpdate(user: FirebaseUser?) {
        accountTextView.text = if (user == null) {
            resources.getString(R.string.not_reg)
        } else {
            user.email
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
}