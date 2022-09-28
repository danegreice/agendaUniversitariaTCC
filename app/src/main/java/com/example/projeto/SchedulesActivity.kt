package com.example.projeto

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SchedulesActivity : AppCompatActivity() {

    private var schedulesActivityLayoutManager: RecyclerView.LayoutManager? = null
    private var schedulesActivityAdapter: RecyclerView.Adapter<SchedulesRecyclerView.ViewHolder>? = null
    lateinit var schedulesActivityRecyclerView: RecyclerView
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String
    lateinit var schedulesActivityButtonReturn: Button
    var subjectsList: ArrayList<Subject> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_schedules)
        supportActionBar?.hide();
        schedulesActivityButtonReturn = findViewById(R.id.schedulesActivityButtonReturn)

        schedulesActivityButtonReturn.setOnClickListener(object: View.OnClickListener{
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
            schedulesActivityRecyclerView = findViewById(R.id.schedulesActivityRecyclerView)
            schedulesActivityLayoutManager = LinearLayoutManager(this)
            schedulesActivityRecyclerView.layoutManager = schedulesActivityLayoutManager
            schedulesActivityAdapter = SchedulesRecyclerView(subjectsList)
            schedulesActivityRecyclerView.adapter = schedulesActivityAdapter
            subjectsList = ArrayList()
        }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "erro: $it", Toast.LENGTH_SHORT).show()
            }
    }
}