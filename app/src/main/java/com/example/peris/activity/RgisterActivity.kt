package com.example.peris.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.peris.R
import com.example.peris.RegisterRequest
import com.example.peris.RegisterViewModel
import com.example.peris.Repository
import com.example.peris.ViewModelFactory
import com.example.peris.databinding.ActivityRegisterBinding
import com.example.peris.network.ApiService

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var registerViewModel : RegisterViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)


        supportActionBar?.title = getString(R.string.bar_register)
        val apiService = ApiService.getInstance()
        registerViewModel = ViewModelProvider(
            this, ViewModelFactory(
                Repository(apiService)
            )
        )[RegisterViewModel::class.java]

        binding.btnRegister.setOnClickListener{
            val name = binding.edtName.text.toString()
            val email = binding.edtEmail.text.toString()
            val password = binding.edtPassword.text.toString()
            registerViewModel.register(RegisterRequest(name,email, password))
        }
        registerViewModel.errorMessage.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }
        registerViewModel.register.observe(this){
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }


}