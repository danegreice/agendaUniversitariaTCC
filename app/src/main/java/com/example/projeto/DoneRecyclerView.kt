package com.example.projeto

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DoneRecyclerView(
    private var data: ArrayList<Task>, private var homeActivityRecyclerViewDoing: RecyclerView
):
    RecyclerView.Adapter<DoneRecyclerView.ViewHolder>(){
    val TAG = "DoneRecyclerView"
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val doneRecyclerViewName: TextView
        val doneRecyclerViewDate: TextView
        val doneRecyclerViewSubject: TextView
        val doneRecyclerViewDone: ImageView
        val doneRecyclerViewBack: ImageView
        init{
            doneRecyclerViewName = view.findViewById(R.id.cardTasksNameKanban)
            doneRecyclerViewDate = view.findViewById(R.id.cardTasksDateKanban)
            doneRecyclerViewSubject = view.findViewById(R.id.cardTasksSubjectKanban)
            doneRecyclerViewDone = view.findViewById(R.id.cardTasksDoneKanban)
            doneRecyclerViewBack = view.findViewById(R.id.cardTasksBackKanban)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "onCreateViewHolder()")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_task_kanban, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder()")
        holder.doneRecyclerViewName.text = data[position].name
        holder.doneRecyclerViewDate.text = data[position].date
        holder.doneRecyclerViewSubject.text = data[position].subject

        holder.doneRecyclerViewDone.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                db = FirebaseFirestore.getInstance()
                auth = FirebaseAuth.getInstance()
                userID = auth.currentUser!!.uid

                val docRef = db.collection("Users").document(userID).collection("Tasks").document(data[holder.absoluteAdapterPosition].id as String)
                val taskUpdate = hashMapOf<String, Any>(
                    "status" to "3",
                )
                docRef.update(taskUpdate).addOnSuccessListener {
                    Log.i(TAG, "sucess")
                }.addOnFailureListener {
                    Log.i(TAG, "failure")
                }
                data.removeAt(holder.absoluteAdapterPosition)
                notifyDataSetChanged()
            }
        })

        holder.doneRecyclerViewBack.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                db = FirebaseFirestore.getInstance()
                auth = FirebaseAuth.getInstance()
                userID = auth.currentUser!!.uid

                val docRef = db.collection("Users").document(userID).collection("Tasks").document(data[holder.absoluteAdapterPosition].id as String)
                val taskUpdate = hashMapOf<String, Any>(
                    "status" to "1",
                )
                docRef.update(taskUpdate).addOnSuccessListener {
                    Log.i(TAG, "sucess")
                }.addOnFailureListener {
                    Log.i(TAG, "failure")
                }

                data.removeAt(holder.absoluteAdapterPosition)
                notifyDataSetChanged()
                val intent = Intent(holder.doneRecyclerViewName.context, HomeActivity::class.java)
                startActivity(holder.doneRecyclerViewName.context, intent, null)

            }
        })

    }

    override fun getItemCount(): Int {
        return data.size
    }
}