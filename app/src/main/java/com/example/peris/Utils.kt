package com.example.peris

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun getFormattedDate(timeInMillis: Long): String {
    val dateFormat = SimpleDateFormat("dd-MM-yyyy HHmmss", Locale.getDefault())
    val date = Date(timeInMillis)
    return dateFormat.format(date)
}