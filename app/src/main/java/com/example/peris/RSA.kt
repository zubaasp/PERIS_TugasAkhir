package com.example.peris

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Environment
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PrivateKey
import java.security.PublicKey
import java.security.Signature
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec
import java.util.Base64


class RSA {
    private val ALGORITHM = "RSA"

    fun generateRSAKeyPair(): KeyPair {
        val keyGen = KeyPairGenerator.getInstance(ALGORITHM)
        keyGen.initialize(2048)
        return keyGen.genKeyPair()
    }

    fun getPublicKeyDetails(publicKey: PublicKey): Pair<BigInteger, BigInteger> {
        val keyFactory = KeyFactory.getInstance(ALGORITHM)
        val keySpec = keyFactory.getKeySpec(publicKey, RSAPublicKeySpec::class.java)
        return Pair(keySpec.modulus, keySpec.publicExponent)
    }

    fun getPrivateKeyDetails(privateKey: PrivateKey): Pair<BigInteger, BigInteger> {
        val keyFactory = KeyFactory.getInstance(ALGORITHM)
        val keySpec = keyFactory.getKeySpec(privateKey, RSAPrivateKeySpec::class.java)
        return Pair(keySpec.modulus, keySpec.privateExponent)
    }

    fun stringToBigIntPair(input: String): Pair<BigInteger, BigInteger> {
        // Hapus tanda kurung dan spasi
        val cleanedInput = input.removeSurrounding("(", ")").replace(" ", "")
        // Pisahkan string menjadi dua bagian berdasarkan koma
        val parts = cleanedInput.split(",")
        if (parts.size != 2) {
            throw IllegalArgumentException("Input string does not contain exactly two parts separated by a comma")
        }
        // Konversi bagian-bagian tersebut menjadi BigInteger
        val modulus = BigInteger(parts[0].trim())
        val exponent = BigInteger(parts[1].trim())
        return Pair(modulus, exponent)
    }
    fun getPublicKeyFromPair(pair: Pair<BigInteger, BigInteger>): PublicKey {
        val (modulus, exponent) = pair
        val keySpec = RSAPublicKeySpec(modulus, exponent)
        val keyFactory = KeyFactory.getInstance("RSA")
        return keyFactory.generatePublic(keySpec)
    }


    fun saveQRCodeImage(bitmap: Bitmap, fileName: String) {
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(directory, "$fileName.png")

        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
        }
    }

    fun saveKeyToFile(fileName: String, key: String) {
        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloadsDir, fileName)
        file.writeText(key)
    }

    fun signData(data: ByteArray, privateKey: PrivateKey): ByteArray {
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initSign(privateKey)
        signature.update(data)
        return signature.sign()
    }

    fun verifySignature(data: ByteArray, publicKey: PublicKey, signatureBytes: ByteArray): Boolean {
        val signature = Signature.getInstance("SHA256withRSA")
        signature.initVerify(publicKey)
        signature.update(data)
        return signature.verify(signatureBytes)
    }

}