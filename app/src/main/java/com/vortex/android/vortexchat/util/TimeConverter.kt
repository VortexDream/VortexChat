package com.vortex.android.vortexchat.util

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.text.format.DateFormat
import android.util.Log
import java.util.Locale

/*fun getTimeFromMilliseconds(millis: Long, context: Context): String {
    val timeFormatter = DateFormat.getTimeFormat(context)
    return timeFormatter.format(millis)
}*/

//Этот вариант работает с часовыми поясами
fun getTimeFromMilliseconds(millis: Long, context: Context): String {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = millis
    calendar.timeZone = TimeZone.getDefault()
    Log.d("TIME", TimeZone.getDefault().toString())

    // Формат для отображения времени в 12-часовом или 24-часовом формате в зависимости от локали
    val timeFormat = if (DateFormat.is24HourFormat(context)) "HH:mm" else "h:mm a"

    // Получение часов и минут в виде строки с учетом временной зоны
    val timeString = SimpleDateFormat(timeFormat, Locale.getDefault()).format(calendar.time)

    return timeString
}