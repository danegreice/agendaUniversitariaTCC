package com.example.projeto

import android.content.Context
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class GradesRecyclerView(private val data: ArrayList<Subject>): RecyclerView.Adapter<GradesRecyclerView.ViewHolder>() {
    private val TAG = "GradesRecyclerView"
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String

    class ViewHolder(view: View):  RecyclerView.ViewHolder(view){
        val gradesRecyclerViewCode: TextView
        val gradesRecyclerViewAv1: TextInputEditText
        val gradesRecyclerViewP1: TextInputEditText
        val gradesRecyclerViewAv2: TextInputEditText
        val gradesRecyclerViewP2: TextInputEditText
        val gradesRecyclerViewAv3: TextInputEditText
        val gradesRecyclerViewP3: TextInputEditText
        val gradesRecyclerViewFinalGrade: TextView
        val gradesRecyclerViewSituation: TextView
        val gradesRecyclerViewRegister: ImageButton
        val gradesRecyclerViewDelete: ImageButton
        init {
            gradesRecyclerViewCode = view.findViewById(R.id.cardGradesCode)
            gradesRecyclerViewAv1= view.findViewById(R.id.cardGradesAv1)
            gradesRecyclerViewP1= view.findViewById(R.id.cardGradesP1)
            gradesRecyclerViewAv2= view.findViewById(R.id.cardGradesAv2)
            gradesRecyclerViewP2= view.findViewById(R.id.cardGradesP2)
            gradesRecyclerViewAv3= view.findViewById(R.id.cardGradesAv3)
            gradesRecyclerViewP3= view.findViewById(R.id.cardGradesP3)
            gradesRecyclerViewFinalGrade= view.findViewById(R.id.cardGradesFinalGrade)
            gradesRecyclerViewSituation= view.findViewById(R.id.cardGradesSituation)
            gradesRecyclerViewRegister = view.findViewById(R.id.cardGradesRegister)
            gradesRecyclerViewDelete = view.findViewById(R.id.cardGradesDelete)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "onCreateViewHolder()")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_grades, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder()")
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid

        val grade = db.collection("Users").document(userID).collection("Subjects").document(data[holder.absoluteAdapterPosition].id as String)
        grade.get().addOnSuccessListener{document ->
            val getId = document.get("id").toString()
            val getName = document.get("name").toString()
            val getCode = document.get("code").toString()
            val getDaysOfWeek = document.get("daysOfWeek")
            val getTeacher = document.get("teacher").toString()
            val getGrade = document.get("grade")
            val getStartTime = document.get("startTime").toString()
            val getEndTime = document.get("endTime").toString()
            val subject = Subject(getId, getCode, getName, getTeacher, getGrade as ArrayList<String>, getDaysOfWeek as ArrayList<Boolean>,getStartTime, getEndTime)

            holder.gradesRecyclerViewCode.text = " ${data[holder.absoluteAdapterPosition].code} - ${data[holder.absoluteAdapterPosition].name}"
            holder.gradesRecyclerViewAv1.setText(getGrade[0])
            holder.gradesRecyclerViewP1.setText(getGrade[1])
            holder.gradesRecyclerViewAv2.setText(getGrade[2])
            holder.gradesRecyclerViewP2.setText(getGrade[3])
            holder.gradesRecyclerViewAv3.setText(getGrade[4])
            holder.gradesRecyclerViewP3.setText(getGrade[5])

            val f: Float = (((getGrade[0].toFloat()*(getGrade[1].toInt())) + (getGrade[2].toFloat()*(getGrade[3].toInt())) + (getGrade[4].toFloat()*(getGrade[5].toInt()))) / (getGrade[1].toInt()+getGrade[3].toInt()+getGrade[5].toInt()))
            holder.gradesRecyclerViewFinalGrade.text = f.toString()
            if (f in 6.0..10.0){
                holder.gradesRecyclerViewSituation.text = "AP"
            } else if(f >= 0 && f < 6){
                holder.gradesRecyclerViewSituation.text = "RP"
            } else{
                holder.gradesRecyclerViewSituation.text = "ER"
            }

        }.addOnFailureListener {
            Toast.makeText(holder.gradesRecyclerViewCode.context, "erro: $it", Toast.LENGTH_SHORT).show()
        }


        holder.gradesRecyclerViewRegister.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View) {
                val gradeAv1 = holder.gradesRecyclerViewAv1.text.toString()
                val gradeP1 = holder.gradesRecyclerViewP1.text.toString()
                val gradeAv2 = holder.gradesRecyclerViewAv2.text.toString()
                val gradeP2 = holder.gradesRecyclerViewP2.text.toString()
                val gradeAv3 = holder.gradesRecyclerViewAv3.text.toString()
                val gradeP3 = holder.gradesRecyclerViewP3.text.toString()

                if ((gradeAv1.toFloat() in 0.0..10.0)  && (gradeP1.toInt() in 1..9) && (gradeAv2.toFloat() in 0.0..10.0) && (gradeP2.toInt() in 1..9) && (gradeAv3.toFloat() in 0.0..10.0) && (gradeP3.toInt() in 1..9)){
                    db = FirebaseFirestore.getInstance()
                    auth = FirebaseAuth.getInstance()
                    userID = auth.currentUser!!.uid

                    val gradeUpdate: ArrayList<String> = ArrayList()
                    gradeUpdate.add(0, gradeAv1)
                    gradeUpdate.add(1, gradeP1)
                    gradeUpdate.add(2, gradeAv2)
                    gradeUpdate.add(3, gradeP2)
                    gradeUpdate.add(4, gradeAv3)
                    gradeUpdate.add(5, gradeP3)

                    val grades = hashMapOf<String, Any>(
                        "grade" to gradeUpdate,
                    )

                    val documentReference = db.collection("Users").document(userID).collection("Subjects").document(data[holder.absoluteAdapterPosition].id as String)

                    documentReference.update(grades).addOnSuccessListener {
                        Toast.makeText(holder.gradesRecyclerViewCode.context, "success", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(holder.gradesRecyclerViewCode.context, "failure: $it", Toast.LENGTH_SHORT).show()
                    }

                    documentReference.get().addOnSuccessListener {document ->
                        val getId = document.get("id").toString()
                        val getName = document.get("name").toString()
                        val getCode = document.get("code").toString()
                        val getDaysOfWeek = document.get("daysOfWeek")
                        val getTeacher = document.get("teacher").toString()
                        val getGrade = document.get("grade")
                        val getStartTime = document.get("startTime").toString()
                        val getEndTime = document.get("endTime").toString()
                        val subject = Subject(getId, getCode, getName, getTeacher, getGrade as ArrayList<String>, getDaysOfWeek as ArrayList<Boolean>,getStartTime, getEndTime)
                        data[holder.absoluteAdapterPosition] = subject
                        notifyDataSetChanged()

                    }.addOnFailureListener {
                        Toast.makeText(holder.gradesRecyclerViewCode.context, "erro: $it", Toast.LENGTH_SHORT).show()
                    }
                } else{
                    Toast.makeText(holder.gradesRecyclerViewCode.context, "preencha corretamente", Toast.LENGTH_SHORT).show()
                    }
                }
            })

        holder.gradesRecyclerViewDelete.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                db = FirebaseFirestore.getInstance()
                auth = FirebaseAuth.getInstance()
                userID = auth.currentUser!!.uid

                val gradeUpdate: ArrayList<String> = ArrayList()
                gradeUpdate.add(0, "0")
                gradeUpdate.add(1, "1")
                gradeUpdate.add(2, "0")
                gradeUpdate.add(3, "1")
                gradeUpdate.add(4, "0")
                gradeUpdate.add(5, "1")

                val grades = hashMapOf<String, Any>(
                    "grade" to gradeUpdate,
                )

                val documentReference = db.collection("Users").document(userID).collection("Subjects").document(data[holder.absoluteAdapterPosition].id as String)

                documentReference.update(grades).addOnSuccessListener {
                    Toast.makeText(holder.gradesRecyclerViewCode.context, "success", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(holder.gradesRecyclerViewCode.context, "failure: $it", Toast.LENGTH_SHORT).show()
                }

                documentReference.get().addOnSuccessListener {document ->
                    val getId = document.get("id").toString()
                    val getName = document.get("name").toString()
                    val getCode = document.get("code").toString()
                    val getDaysOfWeek = document.get("daysOfWeek")
                    val getTeacher = document.get("teacher").toString()
                    val getGrade = document.get("grade")
                    val getStartTime = document.get("startTime").toString()
                    val getEndTime = document.get("endTime").toString()
                    val subject = Subject(getId, getCode, getName, getTeacher, getGrade as ArrayList<String>, getDaysOfWeek as ArrayList<Boolean>,getStartTime, getEndTime)
                    data[holder.absoluteAdapterPosition] = subject
                    notifyDataSetChanged()

                }.addOnFailureListener {
                    Toast.makeText(holder.gradesRecyclerViewCode.context, "erro: $it", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return data.size
    }



}