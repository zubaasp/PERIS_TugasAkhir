package com.example.peris.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.example.peris.R
import com.example.peris.RSA
import com.example.peris.databinding.ActivityResultBinding
import javax.xml.bind.DatatypeConverter

class ResultActivity : AppCompatActivity() {
    private lateinit var binding: ActivityResultBinding
    private var publicKey: String? = null
    private var digitalSignature: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)

        supportActionBar?.title = getString(R.string.bar_result)
        val rsaUtils = RSA()


        val result = intent.getStringExtra("result")
        binding.qrcodeContent.text = result

        binding.btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))

        }
        binding.PublicKey.setOnClickListener {
            openFileChooser(PICK_PUBLIC_KEY_FILE)
        }
        binding.DSA.setOnClickListener {
            openFileChooser(PICK_SIGNATURE_FILE)
        }

        binding.btnVerify.setOnClickListener {
            if (
                publicKey != null && digitalSignature != null
            ) {
                val publicKeyPair = rsaUtils.stringToBigIntPair(publicKey!!)
                val qrCodeData = result!!.toByteArray()
                val publicKey = rsaUtils.getPublicKeyFromPair(publicKeyPair)

                // Konversi string tanda tangan menjadi ByteArray
                val signatureBytes = DatatypeConverter.parseBase64Binary(digitalSignature)

                // Verifikasi tanda tangan
                val isValidSignature =
                    rsaUtils.verifySignature(qrCodeData, publicKey, signatureBytes)
                Log.d("kucing", qrCodeData.toString())
                Log.d("semut", publicKey.toString())
                Log.d("kuda", signatureBytes.toString())


                binding.txtStatus.text =
                    if (isValidSignature) getString(R.string.verified) else getString(R.string.notverified)
            }

        }
    }

    private fun openFileChooser(requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, requestCode)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            val uri = data.data ?: return
            val contentResolver = contentResolver
            val inputStream = contentResolver.openInputStream(uri)
            val fileContent = inputStream?.bufferedReader().use { it?.readText() }

            when (requestCode) {
                PICK_PUBLIC_KEY_FILE -> publicKey = fileContent
                PICK_SIGNATURE_FILE -> digitalSignature = fileContent
            }
        }
    }

    companion object {
        const val PICK_PUBLIC_KEY_FILE = 1
        const val PICK_SIGNATURE_FILE = 2
    }


}
