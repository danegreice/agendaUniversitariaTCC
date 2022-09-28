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

class ToDoRecyclerView(
    private var data: ArrayList<Task>,
    homeActivityRecyclerViewDoing: RecyclerView
):
    RecyclerView.Adapter<ToDoRecyclerView.ViewHolder>(){
    val TAG = "ToDoRecyclerView"
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val toDoRecyclerViewName: TextView
        val toDoRecyclerViewDate: TextView
        val toDoRecyclerViewSubject: TextView
        val toDoRecyclerViewDone: ImageView
        val toDoRecyclerViewBack: ImageView
        init{
            toDoRecyclerViewName = view.findViewById(R.id.cardTasksNameKanban)
            toDoRecyclerViewDate = view.findViewById(R.id.cardTasksDateKanban)
            toDoRecyclerViewSubject = view.findViewById(R.id.cardTasksSubjectKanban)
            toDoRecyclerViewDone = view.findViewById(R.id.cardTasksDoneKanban)
            toDoRecyclerViewBack = view.findViewById(R.id.cardTasksBackKanban)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "onCreateViewHolder()")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_task_kanban, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder()")
        holder.toDoRecyclerViewName.text = data[position].name
        holder.toDoRecyclerViewDate.text = data[position].date
        holder.toDoRecyclerViewSubject.text = data[position].subject

        holder.toDoRecyclerViewDone.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                db = FirebaseFirestore.getInstance()
                auth = FirebaseAuth.getInstance()
                userID = auth.currentUser!!.uid

                val docRef = db.collection("Users").document(userID).collection("Tasks").document(data[holder.absoluteAdapterPosition].id as String)
                val taskUpdate = hashMapOf<String, Any>(
                    "status" to "1",
                )
                docRef.update(taskUpdate).addOnSuccessListener {
                    Toast.makeText(holder.toDoRecyclerViewName.context, "success", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(holder.toDoRecyclerViewName.context, "failure: $it", Toast.LENGTH_SHORT).show()
                }

                data.removeAt(holder.absoluteAdapterPosition)
                notifyDataSetChanged()
                val intent = Intent(holder.toDoRecyclerViewName.context, HomeActivity::class.java)
                ContextCompat.startActivity(holder.toDoRecyclerViewName.context, intent, null)

            }
        })

        holder.toDoRecyclerViewBack.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                Toast.makeText(holder.toDoRecyclerViewName.context, "Não pode voltar", Toast.LENGTH_SHORT).show()
            }
        })

    }

    override fun getItemCount(): Int {
        return data.size
    }
}