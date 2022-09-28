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

class GradesActivity : AppCompatActivity() {

    private var gradesActivityLayoutManager: RecyclerView.LayoutManager? = null
    private var gradesActivityAdapter: RecyclerView.Adapter<GradesRecyclerView.ViewHolder>? = null
    lateinit var gradesActivityRecyclerView: RecyclerView
    lateinit var gradesActivityButtonReturn: Button
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String
    var subjectsList: ArrayList<Subject> = ArrayList()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_grades)
        supportActionBar?.hide();

        gradesActivityButtonReturn = findViewById(R.id.gradesActivityReturn)
        gradesActivityButtonReturn.setOnClickListener(object: View.OnClickListener{
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

        subjects.get().addOnSuccessListener {documents ->
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
            gradesActivityButtonReturn = findViewById(R.id.gradesActivityReturn)
            gradesActivityRecyclerView = findViewById(R.id.gradesActivityTitleRecyclerView)
            gradesActivityLayoutManager = LinearLayoutManager(this)
            gradesActivityRecyclerView.layoutManager = gradesActivityLayoutManager
            gradesActivityAdapter = GradesRecyclerView(subjectsList)
            gradesActivityRecyclerView.adapter = gradesActivityAdapter
            subjectsList = ArrayList()
        }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "erro: $it", Toast.LENGTH_SHORT).show()
            }
    }
}