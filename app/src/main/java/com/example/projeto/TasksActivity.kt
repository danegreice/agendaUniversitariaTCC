package com.example.projeto

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.DatePicker
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TasksActivity : AppCompatActivity() {
    val TAG = "TaskActivity"
    lateinit var tasksActivityButtonAdd: Button
    lateinit var tasksActivityDialog: AlertDialog
    lateinit var tasksActivityDialogBuilder: AlertDialog.Builder
    lateinit var tasksActivityName: TextInputEditText
    lateinit var tasksActivityDate: TextInputEditText
    lateinit var tasksActivityHour: TextInputEditText
    lateinit var tasksActivitySubject: TextInputEditText
    lateinit var tasksActivityDescription: TextInputEditText
    lateinit var tasksActivityButtonReturn: Button
    lateinit var tasksActivityButtonRegister: Button
    lateinit var tasksActivityButtonCancel: Button
    lateinit var tasksActivityRecyclerView: RecyclerView
    private var tasksActivityLayoutManager: RecyclerView.LayoutManager? = null
    private var tasksActivityAdapter: RecyclerView.Adapter<TasksRecyclerView.ViewHolder>? = null
    private var calendar = Calendar.getInstance()
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String
    var tasksList: ArrayList<Task> = ArrayList()
    var id: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tasks)
        tasksActivityButtonAdd = findViewById(R.id.tasksActivityButtonAdd)

        tasksActivityButtonReturn = findViewById(R.id.tasksActivityReturn)
        tasksActivityButtonReturn.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                onBackPressed()
            }
        })

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid

        val tasks = db.collection("Users").document(userID).collection("Tasks")

        tasks.orderBy("date").get().addOnSuccessListener {documents ->
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
            tasksActivityRecyclerView = findViewById(R.id.tasksActivityRecyclerView)
            tasksActivityLayoutManager = LinearLayoutManager(this)
            tasksActivityRecyclerView.layoutManager = tasksActivityLayoutManager
            tasksActivityAdapter = TasksRecyclerView(tasksList)
            tasksActivityRecyclerView.adapter = tasksActivityAdapter
            tasksList = ArrayList()
        }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "erro: $it", Toast.LENGTH_SHORT).show()
            }

        tasksActivityButtonAdd.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                openDialog()
            }
        })
    }

    private fun openDialog() {
        tasksActivityDialogBuilder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.register_tasks_layout, null)
        tasksActivityName = view.findViewById(R.id.registerTasksEditTextName)
        tasksActivityDate = view.findViewById(R.id.registerTasksEditTextDate)
        tasksActivityHour = view.findViewById(R.id.registerTasksEditTextHour)
        tasksActivitySubject = view.findViewById(R.id.registerTasksEditTextSubject)
        tasksActivityDescription = view.findViewById(R.id.registerTasksEditTextDescription)
        tasksActivityButtonRegister = view.findViewById(R.id.registerTasksButtonRegister)
        tasksActivityButtonCancel = view.findViewById(R.id.registerTasksButtonCancel)


        tasksActivityDialogBuilder.setView(view)
        tasksActivityDialog = tasksActivityDialogBuilder.create()
        tasksActivityDialog.show()

        val dateSetList = object : DatePickerDialog.OnDateSetListener{
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                calendar.set(year, monthOfYear, dayOfMonth)
                view.updateDate(year, monthOfYear, dayOfMonth)

                val sdf = SimpleDateFormat("dd/MM/yyyy")
                tasksActivityDate.setText(sdf.format(calendar.time))
            }
        }

        tasksActivityDate.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View) {
                val dialog = DatePickerDialog(this@TasksActivity, dateSetList, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                dialog.datePicker.minDate = calendar.timeInMillis
                dialog.show()

            }
        })

        val timerSetList = object: TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker, hourOfDay: Int, minute: Int) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val stf = SimpleDateFormat("HH:mm")
                tasksActivityHour.setText(stf.format(calendar.time))

            }
        }

        tasksActivityHour.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                TimePickerDialog(this@TasksActivity, timerSetList, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
            }
        })



        tasksActivityButtonRegister.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View) {
                val name = tasksActivityName.text.toString()
                val date = tasksActivityDate.text.toString()
                val hour = tasksActivityHour.text.toString()
                val subject = tasksActivitySubject.text.toString()
                val description = tasksActivityDescription.text.toString()

                val getYear = date.substring(6,10)
                Toast.makeText(applicationContext, getYear, Toast.LENGTH_SHORT).show()

                if((name.isEmpty() || date.isEmpty() || hour.isEmpty() || subject.isEmpty() || description.isEmpty()) || (getYear.toInt() < 2022)) {
                    val snackbar: Snackbar = Snackbar.make(p0, "Preencha corretamente", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()
                } else{
                    id += 1
                    saveTask(name, date, hour, subject, description, id)
                }

            }
        })

        tasksActivityButtonCancel.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                tasksActivityDialog.dismiss()
            }
        })
    }

    private fun saveTask(name: String, date: String, hour: String, subject: String, description: String, id: Int) {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid

        val documentReference = db.collection("Users").document(userID).collection("Tasks").document()

        val task = hashMapOf(
            "id" to documentReference.id,
            "order" to id,
            "name" to name,
            "status" to "0",
            "date" to date,
            "hour" to hour,
            "subject" to subject,
            "description" to description,
        )

        documentReference.set(task).addOnSuccessListener(OnSuccessListener {
            Log.d(TAG, "Success by saving the data")
            tasksActivityDialog.dismiss()
            val tasksList0: ArrayList<Task> = ArrayList()
            val tasks = db.collection("Users").document(userID).collection("Tasks")

            tasks.orderBy("date").get().addOnSuccessListener {documents ->
                for (document in documents){
                    val getId = document.get("id").toString()
                    val getName = document.get("name").toString()
                    val getStatus = document.get("status").toString()
                    val getDate = document.get("date").toString()
                    val getHour = document.get("hour").toString()
                    val getSubject = document.get("subject").toString()
                    val getDescription = document.get("description").toString()
                    val task = Task(getId, getName,getStatus, getDescription, getDate, getHour, getSubject)
                    tasksList0.add(task)
                }
                tasksActivityRecyclerView = findViewById(R.id.tasksActivityRecyclerView)
                tasksActivityLayoutManager = LinearLayoutManager(this)
                tasksActivityRecyclerView.layoutManager = tasksActivityLayoutManager
                tasksActivityAdapter = TasksRecyclerView(tasksList0)
                tasksActivityRecyclerView.adapter = tasksActivityAdapter

            }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "erro: $it", Toast.LENGTH_SHORT).show()
                }


        }).addOnFailureListener(OnFailureListener {
            Log.d(TAG, "Failure by saving the data ${it.toString()}")
        })
    }
}