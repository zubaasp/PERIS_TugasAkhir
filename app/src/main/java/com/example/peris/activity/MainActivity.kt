package com.example.peris.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.peris.R
import com.example.peris.databinding.ActivityMainBinding
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer


private const val CAMERA_REQUEST_CODE = 101
private const val GALLERY_REQUEST_CODE = 102


class MainActivity : AppCompatActivity() {

    private lateinit var codeScanner: CodeScanner
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)


        supportActionBar?.title = getString(R.string.bar_scan)

        setupPermissions()
        codeScanner()

        binding.btnHome.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        binding.gallery.setOnClickListener {
            openFileChooser(GALLERY_REQUEST_CODE)
        }
    }
    private fun openFileChooser(requestCode: Int) {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        startActivityForResult(intent, requestCode)
    }


    private fun codeScanner() {
        codeScanner = CodeScanner(this, binding.codeScannerView)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = CodeScanner.ALL_FORMATS

            scanMode = ScanMode.CONTINUOUS
            isAutoFocusEnabled = true
            isFlashEnabled = false
            decodeCallback = DecodeCallback {
                runOnUiThread {
                    val intent = Intent(this@MainActivity, ResultActivity::class.java)
                    intent.putExtra("result", it.text)
                    startActivity(intent)
                }
            }

            errorCallback = ErrorCallback {
                runOnUiThread {
                    Log.e("Main", "Camera error: ${it.message}")
                }
            }
        }

        binding.codeScannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }


    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            val selectedImage = data.data
            val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
            val decodedText = decodeQRCode(bitmap)
            val intent = Intent(this@MainActivity, ResultActivity::class.java)
            intent.putExtra("result", decodedText)
            startActivity(intent)
        }
    }

    fun decodeQRCode(bitmap: Bitmap): String? {
        val intArray = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(intArray, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        val source = RGBLuminanceSource(bitmap.width, bitmap.height, intArray)
        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

        return try {
            val result = MultiFormatReader().decode(binaryBitmap)
            result.text
        } catch (e: Exception) {
            null
        }
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)

        if (permission != PackageManager.PERMISSION_GRANTED) { //ngecek kamera diizinakn atau belum
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this, arrayOf(android.Manifest.permission.CAMERA), //munculin pop up request kamera
            CAMERA_REQUEST_CODE
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int, //klo kodenya 101 di izinkan
        permissions: Array<out String>, //tipe perizinannya camera
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) { //perizinann pas mau jalanin kamera, granted itu perizinan
                    //ini ketika user nolak izin kamera, grandresult itu bentuk array jadi 0
                    Toast.makeText(
                        this,
                        "You need the camera permission to be able to use this app",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                }
            }
        }
    }
}