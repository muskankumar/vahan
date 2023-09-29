package com.example.vahan

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UniversityAdapter(private val universities: List<UniversityListItem>,private  val context: Context) :
    RecyclerView.Adapter<UniversityAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val countryTextView: TextView = itemView.findViewById(R.id.countryTextView)
        val websiteTextView: TextView = itemView.findViewById(R.id.websiteTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.university_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val university = universities[position]
        holder.nameTextView.text = university.country
        holder.countryTextView.text = university.name
        holder.websiteTextView.text = university.web_page
        holder.websiteTextView.setOnClickListener {
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("webUrl", university.web_page)
            intent.putExtra("name", university.country)
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return universities.size
    }
}
