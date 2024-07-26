package com.example.peris.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModelProvider
import com.example.peris.DataStoreKeys
import com.example.peris.LoginRequest
import com.example.peris.LoginViewModel
import com.example.peris.R
import com.example.peris.Repository
import com.example.peris.ViewModelFactory
import com.example.peris.databinding.ActivityLoginBinding
import com.example.peris.network.ApiService
import com.example.peris.user
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginViewModel: LoginViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        CoroutineScope(Dispatchers.IO).launch {
            user.data.collect { data ->
                val token = data[DataStoreKeys.TOKEN] ?: "none"
                runOnUiThread {
                    if (token != "none") {
                        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
                        finish()
                    }
                }
            }
        }
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        supportActionBar?.title = getString(R.string.bar_login)
        val apiService = ApiService.getInstance()
        loginViewModel = ViewModelProvider(
            this, ViewModelFactory(
                Repository(apiService)
            )
        )[LoginViewModel::class.java]

        binding.btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            loginViewModel.login(LoginRequest(email, password))
        }
        loginViewModel.errorMessage.observe(this) {
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        loginViewModel.login.observe(this) {
            saveUserData(it.token)
            startActivity(Intent(this, HomeActivity::class.java))
        }

    }

    fun saveUserData(token: String) {
        CoroutineScope(Dispatchers.IO).launch {
            user.edit { data ->
                data[DataStoreKeys.TOKEN] = token
            }
        }
    }
}