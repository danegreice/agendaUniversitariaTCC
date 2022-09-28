package com.example.projeto


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CalendarView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.shrikanthravi.collapsiblecalendarview.data.Day
import com.shrikanthravi.collapsiblecalendarview.widget.CollapsibleCalendar
import kotlin.collections.ArrayList

class CalendarActivity : AppCompatActivity() {
    lateinit var calendarActivityDialog: AlertDialog
    lateinit var calendarActivityDialogBuilder: AlertDialog.Builder
    lateinit var calendarActivityRecyclerView: RecyclerView
    private var calendarActivityLayoutManager: RecyclerView.LayoutManager? = null
    private var calendarActivityAdapter: RecyclerView.Adapter<CalendarRecyclerView.ViewHolder>? = null
    lateinit var calendarActivityReturn: Button
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String
    var tasksList: ArrayList<Task> = ArrayList()
    var tasksListToday: ArrayList<Task> = ArrayList()
    lateinit var calendar: CollapsibleCalendar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)

        calendar = findViewById(R.id.calendarView)

        calendarActivityReturn = findViewById(R.id.calendarActivityReturn)
        calendarActivityReturn.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                onBackPressed()
            }
        })

    }


    override fun onStart() {
        super.onStart()

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid

        val tasksEvent = db.collection("Users").document(userID).collection("Tasks")

        tasksEvent.get().addOnSuccessListener { documents ->
            for (document in documents) {
                val getId = document.get("id").toString()
                val getName = document.get("name").toString()
                val getStatus = document.get("status").toString()
                val getDate = document.get("date").toString()
                val getHour = document.get("hour").toString()
                val getSubject = document.get("subject").toString()
                val getDescription = document.get("description").toString()
                val task =
                    Task(getId, getName, getStatus, getDescription, getDate, getHour, getSubject)
                tasksList.add(task)
            }

            for (task in tasksList) {

                val taskDay = task.date!!.substring(0, 2)
                val taskMonth = task.date.substring(3, 5)
                val taskYear = task.date.substring(6, 10)

                calendar.addEventTag(taskYear.toInt(), taskMonth.toInt() - 1, taskDay.toInt())
            }

            tasksList = ArrayList()
        }

        calendar.setCalendarListener(object: CollapsibleCalendar.CalendarListener{
            override fun onClickListener() {
            }

            override fun onDataUpdate() {
            }

            override fun onDayChanged() {
            }

            override fun onDaySelect() {
                val day: Day = calendar.selectedDay!!

                db = FirebaseFirestore.getInstance()
                auth = FirebaseAuth.getInstance()
                userID = auth.currentUser!!.uid

                val tasks = db.collection("Users").document(userID).collection("Tasks")

                tasks.get().addOnSuccessListener {documents ->
                    for (document in documents){
                        val getId = document.get("id").toString()
                        val getName = document.get("name").toString()
                        val getStatus = document.get("status").toString()
                        val getDate = document.get("date").toString()
                        val getHour = document.get("hour").toString()
                        val getSubject = document.get("subject").toString()
                        val getDescription = document.get("description").toString()
                        val task = Task(getId, getName, getStatus, getDescription, getDate, getHour, getSubject)
                        tasksList.add(task)
                    }

                    for (task in tasksList){
                        val getDay = task.date!!.substring(0,2)
                        val getMonth = task.date.substring(3,5)
                        val getYear = task.date.substring(6,10)
                        if ((day.day == getDay.toInt()) && ((day.month +1) == getMonth.toInt()) && (day.year == getYear.toInt())){
                            tasksListToday.add(task)
                        }
                    }

                    if (tasksListToday.size > 0){
                        calendarActivityDialogBuilder = AlertDialog.Builder(this@CalendarActivity)
                        val view = LayoutInflater.from(this@CalendarActivity).inflate(R.layout.view_tasks, null)
                        calendarActivityRecyclerView = view.findViewById(R.id.calendarActivityRecyclerView)

                        calendarActivityRecyclerView.setHasFixedSize(true)
                        calendarActivityLayoutManager = LinearLayoutManager(this@CalendarActivity)
                        calendarActivityRecyclerView.layoutManager = calendarActivityLayoutManager
                        calendarActivityAdapter = CalendarRecyclerView(tasksListToday)
                        calendarActivityRecyclerView.adapter = calendarActivityAdapter

                        calendarActivityDialogBuilder.setView(view)
                        calendarActivityDialog = calendarActivityDialogBuilder.create()
                        calendarActivityDialog.show()
                    }

                    tasksList = ArrayList()
                    tasksListToday = ArrayList()
                }
            }

            override fun onItemClick(v: View) {
            }

            override fun onMonthChange() {
            }

            override fun onWeekChange(position: Int) {
            }

        })

    }
}