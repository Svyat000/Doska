package com.sddrozdov.doska

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sddrozdov.doska.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding ?: throw IllegalStateException("Binding must not be null")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        init()

        ViewCompat.setOnApplyWindowInsetsListener(binding.mainDrawerLayout) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun init() {
        val toggle = ActionBarDrawerToggle(
            this,
            binding.mainDrawerLayout,
            findViewById(R.id.toolbar),
            R.string.open,
            R.string.close
        )
        binding.mainDrawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null

    }

}