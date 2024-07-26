package com.example.peris.activity

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.Manifest
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.peris.R
import com.example.peris.RSA
import com.example.peris.getFormattedDate
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.zxing.BarcodeFormat
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.QRCodeWriter
import org.bouncycastle.jce.provider.BouncyCastleProvider
import java.security.Security
import javax.xml.bind.DatatypeConverter

private const val REQUEST_CODE_READ_STORAGE = 101
private const val REQUEST_CODE_WRITE_STORAGE = 102

class CreateQrCode : AppCompatActivity() {

    lateinit var btn_saveQr: Button
    lateinit var et_plain_text: EditText
    lateinit var et1_plain_text: EditText
    lateinit var et2_plain_text: EditText
    lateinit var img_qrcode: ImageView
    var rsaUtils: RSA = RSA()
    val keyPair = rsaUtils.generateRSAKeyPair()

    override fun onCreate(savedInstanceState: Bundle?) {

        Security.addProvider(BouncyCastleProvider())

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_qr_code)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)


        supportActionBar?.title = getString(R.string.bar_createqr)
        initializeViews()

        btn_saveQr.setOnClickListener {
            val textSatu = et_plain_text.text.toString()
            val textDua = et1_plain_text.text.toString()
            val textTiga = et2_plain_text.text.toString()
            val textEntered = "$textSatu.$textDua.$textTiga"
            val data = textEntered.toByteArray()
            if (TextUtils.isEmpty(textEntered)) {
                Toast.makeText(this, "Enter text to encrypt", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            } else {
                val publicKey = rsaUtils.getPublicKeyDetails(keyPair.public)
                val privateKey = rsaUtils.getPrivateKeyDetails(keyPair.private)
                val signature = rsaUtils.signData(data, keyPair.private)
                val qrCodeData = DatatypeConverter.printBase64Binary(signature)

                val time = getFormattedDate(System.currentTimeMillis())
                rsaUtils.saveKeyToFile("publicKey_$time.txt", publicKey.toString())
                rsaUtils.saveKeyToFile("privateKey_$time.txt", privateKey.toString())
                rsaUtils.saveKeyToFile("signature_$time.txt", qrCodeData)
                rsaUtils.saveKeyToFile("result_$time.txt", textEntered)

                val qrCodeImage = generateQRCode(textEntered)
                img_qrcode.setImageBitmap(qrCodeImage)
                rsaUtils.saveQRCodeImage(qrCodeImage, "qrcode_$time")

                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.Informasi))
                    .setMessage(resources.getString(R.string.infoAttention))
                    .setNeutralButton(resources.getString(R.string.Close)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
            setupPermissions()
    }
    fun generateQRCode(data: String): Bitmap {
        val qrCodeWriter = QRCodeWriter()
        val bitMatrix: BitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, 200, 200)

        val width = bitMatrix.width
        val height = bitMatrix.height
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)

        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }

        return bitmap
    }

    private fun setupPermissions() {
        val permissionRead = ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
        val permissionWrite = ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionRead != PackageManager.PERMISSION_GRANTED || permissionWrite != PackageManager.PERMISSION_GRANTED) {
            checkAndRequestPermissions()
        }
    }


    private fun checkAndRequestPermissions() {
        val permissionsNeeded = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        if (permissionsNeeded.isNotEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsNeeded.toTypedArray(), REQUEST_CODE_READ_STORAGE)
        }
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_CODE_READ_STORAGE, REQUEST_CODE_WRITE_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                    // Izin diberikan, lanjutkan dengan tindakan yang membutuhkan izin
                    Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
                } else {
                    // Izin tidak diberikan, beri tahu pengguna
                    Toast.makeText(this, "Permissions are required to proceed", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }


    private fun initializeViews() {
        btn_saveQr = findViewById(R.id.btn_saveQr)
        et_plain_text = findViewById(R.id.et_plain_text)
        et1_plain_text= findViewById(R.id.et1_plain_text)
        et2_plain_text= findViewById(R.id.et2_plain_text)
        img_qrcode= findViewById(R.id.image_qr)
    }
}






