package com.example.vahan

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class RefreshService : Service() {

    private var isServiceRunning = false

    override fun onCreate() {
        super.onCreate()
        isServiceRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        createNotificationChannel()

        val notification = buildForegroundNotification()
        startForeground(FOREGROUND_SERVICE_ID, notification)
        refreshDataPeriodically()
        return START_STICKY
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "refresh_service_channel"
            val channelName = "Refresh Service"
            val channel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager =
                getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildForegroundNotification(): Notification {
        val channelId = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            "refresh_service_channel"
        } else {
            ""
        }

        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Data Refresh Service")
            .setContentText("Refreshing data every 10 seconds")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .build()
    }

    private fun refreshDataPeriodically() {
        Thread {
            while (isServiceRunning) {
                fetchDataFromAPI()
                SystemClock.sleep(10000)
            }
        }.start()
    }

    private fun fetchDataFromAPI() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://universities.hipolabs.com/search")
            .build()

        val response = client.newCall(request).execute()
        val responseBody: String = response.body?.string().toString()

        val universities = mutableListOf<UniversityListItem>()

        if (!responseBody.isNullOrEmpty()) {
            val jsonArray = JSONArray(responseBody)
            for (i in 0 until jsonArray.length()) {
                val jsonObject: JSONObject = jsonArray.getJSONObject(i)
                val name = jsonObject.getString("name")
                val country = jsonObject.getString("country")
                val websiteLink = jsonObject.getJSONArray("web_pages").getString(0)
                universities.add(UniversityListItem(name, country, websiteLink))
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        isServiceRunning = false
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    companion object {
        private const val FOREGROUND_SERVICE_ID = 101
    }
}
