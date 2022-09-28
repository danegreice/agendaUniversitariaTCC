package com.example.projeto

import android.app.TimePickerDialog
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SubjectsActivity : AppCompatActivity() {
    private val TAG = "SubjectsActivity"
    lateinit var subjectsActivityButtonAdd: Button
    lateinit var subjectsActivityDialog: AlertDialog
    lateinit var subjectsActivityDialogBuilder: AlertDialog.Builder
    lateinit var subjectsActivityCheckBoxMonday: CheckBox
    lateinit var subjectsActivityCheckBoxTuesday: CheckBox
    lateinit var subjectsActivityCheckBoxWednesday: CheckBox
    lateinit var subjectsActivityCheckBoxThursday: CheckBox
    lateinit var subjectsActivityCheckBoxFriday: CheckBox
    lateinit var subjectsActivityStartTime: TextInputEditText
    lateinit var subjectsActivityEndTime: TextInputEditText
    lateinit var subjectsActivityCode: TextInputEditText
    lateinit var subjectsActivityName: TextInputEditText
    lateinit var subjectsActivityTeacher: TextInputEditText
    lateinit var subjetsActivityButtonReturn: Button
    lateinit var subjectsActivityButtonRegister: Button
    lateinit var subjectsActivityButtonCancel: Button
    lateinit var subjectsActivityRecyclerView: RecyclerView
    private var subjectsActivityLayoutManager: RecyclerView.LayoutManager? = null
    private var subjectsActivityAdapter: RecyclerView.Adapter<SubjectsRecyclerView.ViewHolder>? = null
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String
    val calendar = Calendar.getInstance()
    var subjectsList: ArrayList<Subject> = ArrayList()
    var id: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subjects)

        subjectsActivityButtonAdd = findViewById(R.id.subjectsActivityButtonAdd)
        subjetsActivityButtonReturn = findViewById(R.id.subjectsActivityReturn)


        subjectsActivityButtonAdd.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                openDialog()
            }
        })

        subjetsActivityButtonReturn.setOnClickListener(object: View.OnClickListener{
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

        val subjects = db.collection("Users").document(userID).collection("Subjects")

        subjects.orderBy("order").get().addOnSuccessListener {documents ->
            for (document in documents){
                val getId = document.get("id").toString()
                val getName = document.get("name").toString()
                val getCode = document.get("code").toString()
                val getDaysOfWeek = document.get("daysOfWeek")
                val getTeacher = document.get("teacher").toString()
                val getGrade = document.get("grade")
                val getStartTime = document.get("startTime").toString()
                val getEndTime = document.get("endTime").toString()
                val subject = Subject(getId, getCode, getName, getTeacher, getGrade as ArrayList<String>, getDaysOfWeek as ArrayList<Boolean>,getStartTime, getEndTime)
                subjectsList.add(subject)
            }
            subjectsActivityRecyclerView = findViewById(R.id.subjectsActivityRecyclerView)
            subjectsActivityLayoutManager = LinearLayoutManager(this)
            subjectsActivityRecyclerView.layoutManager = subjectsActivityLayoutManager
            subjectsActivityAdapter = SubjectsRecyclerView(subjectsList)
            subjectsActivityRecyclerView.adapter = subjectsActivityAdapter
            subjectsList = ArrayList()
        }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "erro: $it", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openDialog(){
        subjectsActivityDialogBuilder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.register_subjects_layout, null)
        subjectsActivityCheckBoxMonday = view.findViewById(R.id.registerSubjectsCheckBoxMonday)
        subjectsActivityCheckBoxTuesday = view.findViewById(R.id.registerSubjectsCheckBoxTuesday)
        subjectsActivityCheckBoxWednesday = view.findViewById(R.id.registerSubjectsCheckBoxWednesday)
        subjectsActivityCheckBoxThursday = view.findViewById(R.id.registerSubjectsCheckBoxThursday)
        subjectsActivityCheckBoxFriday = view.findViewById(R.id.registerSubjectsCheckBoxFriday)
        subjectsActivityStartTime= view.findViewById(R.id.registerSubjectsEditTextStartTime)
        subjectsActivityEndTime= view.findViewById(R.id.registerSubjectsEditTextEndTime)
        subjectsActivityCode= view.findViewById(R.id.registerSubjectsEditTextCode)
        subjectsActivityName= view.findViewById(R.id.registerSubjectsEditTextName)
        subjectsActivityTeacher= view.findViewById(R.id.registerSubjectsEditTextTeacher)
        subjectsActivityButtonRegister= view.findViewById(R.id.registerSubjectsButtonRegister)
        subjectsActivityButtonCancel= view.findViewById(R.id.registerSubjectsButtonCancel)


        val timerSetListStart = object: TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker, hourOfDay: Int, minute: Int) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val stf = SimpleDateFormat("HH:mm")
                subjectsActivityStartTime.setText(stf.format(calendar.time))

            }
        }

        subjectsActivityStartTime.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                TimePickerDialog(this@SubjectsActivity, timerSetListStart, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(
                    Calendar.MINUTE), true).show()
            }
        })

        val timerSetListEnd = object: TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker, hourOfDay: Int, minute: Int) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val stf = SimpleDateFormat("HH:mm")
                subjectsActivityEndTime.setText(stf.format(calendar.time))

            }
        }

        subjectsActivityEndTime.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                TimePickerDialog(this@SubjectsActivity, timerSetListEnd, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(
                    Calendar.MINUTE), true).show()
            }
        })

        subjectsActivityButtonRegister.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View) {
                val checkBoxMonday = subjectsActivityCheckBoxMonday.isChecked
                val checkBoxTuesday = subjectsActivityCheckBoxTuesday.isChecked
                val checkBoxWednesday = subjectsActivityCheckBoxWednesday.isChecked
                val checkBoxThursday = subjectsActivityCheckBoxThursday.isChecked
                val checkBoxFriday = subjectsActivityCheckBoxFriday.isChecked
                val startTime = subjectsActivityStartTime.text.toString()
                val endTime = subjectsActivityEndTime.text.toString()
                val code = subjectsActivityCode.text.toString()
                val name = subjectsActivityName.text.toString()
                val teacher = subjectsActivityTeacher.text.toString()

                val startHour = startTime.substring(0,2).toInt()
                val startMinute = startTime.substring(3,5).toInt()

                val endHour = endTime.substring(0,2).toInt()
                val endMinute = endTime.substring(3,5).toInt()

                if(name.isEmpty() || code.isEmpty() || teacher.isEmpty() || endTime.isEmpty() || startTime.isEmpty() || (!checkBoxMonday && !checkBoxTuesday && !checkBoxWednesday && !checkBoxThursday && !checkBoxFriday)){
                    val snackbar: Snackbar = Snackbar.make(p0, "Preencha todos os dados", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()
                } else if((endHour == startHour) && (startMinute >= endMinute) || (startHour > endHour)){
                    val snackbar: Snackbar = Snackbar.make(p0, "Preencha corretamente", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()
                }else{
                    id += 1
                    saveSubject(name, code, teacher, endTime, startTime, checkBoxFriday, checkBoxMonday, checkBoxWednesday, checkBoxTuesday, checkBoxThursday, id)
                }

            }
        })

        subjectsActivityButtonCancel.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                subjectsActivityDialog.dismiss()
            }
        })

        subjectsActivityDialogBuilder.setView(view)
        subjectsActivityDialog = subjectsActivityDialogBuilder.create()
        subjectsActivityDialog.show()

    }

    private fun saveSubject(
        name: String,
        code: String,
        teacher: String,
        endTime: String,
        startTime: String,
        checkBoxFriday: Boolean,
        checkBoxMonday: Boolean,
        checkBoxWednesday: Boolean,
        checkBoxTuesday: Boolean,
        checkBoxThursday: Boolean,
        id: Int,
    ) {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid

        val daysOfWeek: ArrayList<Boolean> = ArrayList()
        daysOfWeek.add(0, checkBoxMonday)
        daysOfWeek.add(1, checkBoxTuesday)
        daysOfWeek.add(2, checkBoxWednesday)
        daysOfWeek.add(3, checkBoxThursday)
        daysOfWeek.add(4, checkBoxFriday)

        val grade: ArrayList<String> = ArrayList()
        grade.add(0, "0")
        grade.add(1, "1")
        grade.add(2, "0")
        grade.add(3, "1")
        grade.add(4, "0")
        grade.add(5, "1")

        val documentReference = db.collection("Users").document(userID).collection("Subjects").document()

        val subject = hashMapOf(
            "id" to documentReference.id,
            "order" to id,
            "name" to name,
            "code" to code,
            "teacher" to teacher,
            "grade" to grade,
            "startTime" to startTime,
            "endTime" to endTime,
            "daysOfWeek" to daysOfWeek,
        )

        documentReference.set(subject).addOnSuccessListener(OnSuccessListener {
            Log.d(TAG, "Success by saving the data")
            subjectsActivityDialog.dismiss()
            val subjectsList0: ArrayList<Subject> = ArrayList()
            val subjects = db.collection("Users").document(userID).collection("Subjects")

            subjects.orderBy("order").get().addOnSuccessListener {documents ->
                for (document in documents){
                    val getId = document.get("id").toString()
                    val getName = document.get("name").toString()
                    val getCode = document.get("code").toString()
                    val getDaysOfWeek = document.get("daysOfWeek")
                    val getTeacher = document.get("teacher").toString()
                    val getGrade = document.get("grade")
                    val getStartTime = document.get("startTime").toString()
                    val getEndTime = document.get("endTime").toString()
                    val getSubject = Subject(getId, getCode, getName, getTeacher,getGrade as ArrayList<String> , getDaysOfWeek as ArrayList<Boolean>,getStartTime, getEndTime)
                    subjectsList0.add(getSubject)
                }
                subjectsActivityRecyclerView = findViewById(R.id.subjectsActivityRecyclerView)
                subjectsActivityLayoutManager = LinearLayoutManager(this)
                subjectsActivityRecyclerView.layoutManager = subjectsActivityLayoutManager
                subjectsActivityAdapter = SubjectsRecyclerView(subjectsList0)
                subjectsActivityRecyclerView.adapter = subjectsActivityAdapter

            }
                .addOnFailureListener {
                    Toast.makeText(applicationContext, "erro: $it", Toast.LENGTH_SHORT).show()
                }


        }).addOnFailureListener(OnFailureListener {
            Log.d(TAG, "Failure by saving the data ${it.toString()}")
        })
    }
}