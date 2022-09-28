package com.example.projeto

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CalendarRecyclerView (private var data: ArrayList<Task>):
    RecyclerView.Adapter<CalendarRecyclerView.ViewHolder>() {
    val TAG = "CalendarRecyclerView"
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val calendarRecyclerViewName: TextView
        val calendarRecyclerViewHour: TextView
        val calendarRecyclerViewSubject: TextView
        val calendarRecyclerViewDescription: TextView

        init {
            calendarRecyclerViewName = view.findViewById(R.id.cardTasksCalendarName)
            calendarRecyclerViewHour = view.findViewById(R.id.cardTasksCalendarHour)
            calendarRecyclerViewSubject = view.findViewById(R.id.cardTasksCalendarSubject)
            calendarRecyclerViewDescription = view.findViewById(R.id.cardTasksCalendarDescription)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "onCreateViewHolder()")
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.card_task_calendar, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder()")
        holder.calendarRecyclerViewName.text = data[position].name
        holder.calendarRecyclerViewHour.text = data[position].hour
        holder.calendarRecyclerViewSubject.text = data[position].subject
        holder.calendarRecyclerViewDescription.text = data[position].description

    }

    override fun getItemCount(): Int {
        return data.size
    }
}