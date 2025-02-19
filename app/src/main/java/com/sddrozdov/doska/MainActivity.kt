package com.sddrozdov.doska

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.navigation.NavigationView.OnNavigationItemSelectedListener
import com.sddrozdov.doska.databinding.ActivityMainBinding
import com.sddrozdov.doska.dialogHelper.DialogHelper

class MainActivity : AppCompatActivity(), OnNavigationItemSelectedListener {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    private val dialogHelper = DialogHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupActionBarToggle()

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainDrawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupActionBarToggle() {
        val toggle = ActionBarDrawerToggle(
            this,
            binding.mainDrawerLayout,
            binding.mainContentToolbar.toolbar,
            R.string.open,
            R.string.close
        )
        binding.mainDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.mainNavigationView.setNavigationItemSelectedListener(this)
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
                dialogHelper.createSignDialog(0)
            }

            R.id.menu_account_login -> {
                dialogHelper.createSignDialog(1)
            }

            R.id.menu_account_logout -> {
                Toast.makeText(this, "Нажали на выход", Toast.LENGTH_LONG).show()
            }
        }
        binding.mainDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

}