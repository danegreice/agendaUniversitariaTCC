package com.example.projeto

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DoingRecyclerView(
    private var data: ArrayList<Task>,
    homeActivityRecyclerViewToDo: RecyclerView,
    homeActivityRecyclerViewDone: RecyclerView
):
    RecyclerView.Adapter<DoingRecyclerView.ViewHolder>(){
    val TAG = "DoingRecyclerView"
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val doingRecyclerViewName: TextView
        val doingRecyclerViewDate: TextView
        val doingRecyclerViewSubject: TextView
        val doingRecyclerViewDone: ImageView
        val doingRecyclerViewBack: ImageView
        init{
            doingRecyclerViewName = view.findViewById(R.id.cardTasksNameKanban)
            doingRecyclerViewDate = view.findViewById(R.id.cardTasksDateKanban)
            doingRecyclerViewSubject = view.findViewById(R.id.cardTasksSubjectKanban)
            doingRecyclerViewDone = view.findViewById(R.id.cardTasksDoneKanban)
            doingRecyclerViewBack = view.findViewById(R.id.cardTasksBackKanban)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "onCreateViewHolder()")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_task_kanban, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder()")
        holder.doingRecyclerViewName.text = data[position].name
        holder.doingRecyclerViewDate.text = data[position].date
        holder.doingRecyclerViewSubject.text = data[position].subject

        holder.doingRecyclerViewDone.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                db = FirebaseFirestore.getInstance()
                auth = FirebaseAuth.getInstance()
                userID = auth.currentUser!!.uid

                val docRef = db.collection("Users").document(userID).collection("Tasks").document(data[holder.absoluteAdapterPosition].id as String)
                val taskUpdate = hashMapOf<String, Any>(
                    "status" to "2",
                )
                docRef.update(taskUpdate).addOnSuccessListener {
                    Toast.makeText(holder.doingRecyclerViewName.context, "success", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(holder.doingRecyclerViewName.context, "failure: $it", Toast.LENGTH_SHORT).show()
                }

                data.removeAt(holder.absoluteAdapterPosition)
                notifyDataSetChanged()
                val intent = Intent(holder.doingRecyclerViewName.context, HomeActivity::class.java)
                ContextCompat.startActivity(holder.doingRecyclerViewName.context, intent, null)

            }
        })

        holder.doingRecyclerViewBack.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                db = FirebaseFirestore.getInstance()
                auth = FirebaseAuth.getInstance()
                userID = auth.currentUser!!.uid

                val docRef = db.collection("Users").document(userID).collection("Tasks").document(data[holder.absoluteAdapterPosition].id as String)
                val taskUpdate = hashMapOf<String, Any>(
                    "status" to "0",
                )
                docRef.update(taskUpdate).addOnSuccessListener {
                    Toast.makeText(holder.doingRecyclerViewName.context, "success", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(holder.doingRecyclerViewName.context, "failure: $it", Toast.LENGTH_SHORT).show()
                }

                data.removeAt(holder.absoluteAdapterPosition)
                notifyDataSetChanged()
                val intent = Intent(holder.doingRecyclerViewName.context, HomeActivity::class.java)
                ContextCompat.startActivity(holder.doingRecyclerViewName.context, intent, null)
            }
        })

    }

    override fun getItemCount(): Int {
        return data.size
    }
}