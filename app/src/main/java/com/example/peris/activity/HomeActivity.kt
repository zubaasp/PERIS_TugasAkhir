package com.example.peris.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import com.example.peris.DataStoreKeys
import com.example.peris.R
import com.example.peris.databinding.ActivityHomeBinding
import com.example.peris.user
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)


        supportActionBar?.title = getString(R.string.bar_home)

        binding.btnCreateQr.setOnClickListener{
            startActivity(Intent(this, CreateQrCode::class.java))
        }
        binding.btnScanQr.setOnClickListener{
            startActivity(Intent(this, MainActivity::class.java))

        }
        binding.btnLogout.setOnClickListener{
            logout()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }
    fun logout() {
        CoroutineScope(Dispatchers.IO).launch {
            user.edit { data ->
                data.clear()
            }
        }
    }
}