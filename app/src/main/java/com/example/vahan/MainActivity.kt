package com.example.vahan

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: UniversityAdapter
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.recylerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = UniversityAdapter(emptyList(), applicationContext)
        recyclerView.adapter = adapter
        progressBar = findViewById(R.id.progressBar)
        fetchData()
        val serviceIntent = Intent(this, RefreshService::class.java)
        startService(serviceIntent)
    }

    private fun fetchData() {
        progressBar.visibility = View.VISIBLE
        GlobalScope.launch {
            val universities = getUniversities()
            withContext(Dispatchers.Main) {
                progressBar.visibility = View.GONE
                adapter = UniversityAdapter(universities, applicationContext)
                recyclerView.adapter = adapter
            }
        }
    }

    private fun getUniversities(): List<UniversityListItem> {
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

        return universities
    }
}