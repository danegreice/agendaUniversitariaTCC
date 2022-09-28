package com.example.projeto

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SchedulesRecyclerView(private val data: ArrayList<Subject>): RecyclerView.Adapter<SchedulesRecyclerView.ViewHolder>(){
    private var TAG= "ListActivitySchedules"

    class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val schedulesRecyclerViewCode: TextView
        val schedulesRecyclerViewMonday: TextView
        val schedulesRecyclerViewTuesday: TextView
        val schedulesRecyclerViewWednesday: TextView
        val schedulesRecyclerViewThursday: TextView
        val schedulesRecyclerViewFriday: TextView
        init {
            schedulesRecyclerViewCode = view.findViewById(R.id.cardSchedulesCode)
            schedulesRecyclerViewMonday = view.findViewById(R.id.cardSchedulesMonday)
            schedulesRecyclerViewTuesday = view.findViewById(R.id.cardSchedulesTuesday)
            schedulesRecyclerViewWednesday = view.findViewById(R.id.cardSchedulesWednesday)
            schedulesRecyclerViewThursday = view.findViewById(R.id.cardSchedulesThursday)
            schedulesRecyclerViewFriday = view.findViewById(R.id.cardSchedulesFriday)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        Log.i(TAG, "onCreateViewHolder()")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_schedules, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: SchedulesRecyclerView.ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder()")
        holder.schedulesRecyclerViewCode.text = data[position].code
        if (data[position].daysOfWeek!![0]){
            holder.schedulesRecyclerViewMonday.text = "${data[position].startTime} ${data[position].endTime}"
        }
        if (data[position].daysOfWeek!![1]){
            holder.schedulesRecyclerViewTuesday.text = "${data[position].startTime} ${data[position].endTime}"
        }
        if (data[position].daysOfWeek!![2]){
            holder.schedulesRecyclerViewWednesday.text = "${data[position].startTime} ${data[position].endTime}"
        }
        if (data[position].daysOfWeek!![3]){
            holder.schedulesRecyclerViewThursday.text = "${data[position].startTime} ${data[position].endTime}"
        }
        if (data[position].daysOfWeek!![4]){
            holder.schedulesRecyclerViewFriday.text = "${data[position].startTime} ${data[position].endTime}"
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }
}