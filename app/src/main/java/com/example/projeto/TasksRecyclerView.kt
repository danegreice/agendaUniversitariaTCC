package com.example.projeto

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class TasksRecyclerView(private var data: ArrayList<Task>):
    RecyclerView.Adapter<TasksRecyclerView.ViewHolder>() {
    private var TAG = "TaskRecyclerView"
    lateinit var tasksRecyclerViewDialogBuilder: AlertDialog.Builder
    lateinit var tasksRecyclerViewDialog: AlertDialog
    lateinit var tasksRecyclerViewName: TextView
    lateinit var tasksRecyclerViewDate: TextView
    lateinit var tasksRecyclerViewHour: TextView
    lateinit var tasksRecyclerViewSubject: TextView
    lateinit var tasksRecyclerViewDescription: TextView
    lateinit var tasksRecyclerViewButtonSave: Button
    lateinit var tasksRecyclerViewButtonCancel: Button
    val calendar = Calendar.getInstance()
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val cardTaskIcon: ImageView
        val cardTaskName: TextView
        val cardTaskDate: TextView
        val cardTaskDelete: ImageView
        val cardTaskEdit: ImageView

        init{
            cardTaskIcon = view.findViewById(R.id.cardTasksIcon)
            cardTaskName = view.findViewById(R.id.cardTasksName)
            cardTaskDate = view.findViewById(R.id.cardTasksDate)
            cardTaskDelete = view.findViewById(R.id.cardTasksDelete)
            cardTaskEdit = view.findViewById(R.id.cardTasksEdit)

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "onCreateViewHolder()")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_tasks, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder()")

        holder.cardTaskName.text = data[position].name
        holder.cardTaskDate.text = data[position].date

        holder.cardTaskDelete.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                tasksRecyclerViewDialogBuilder = AlertDialog.Builder(holder.cardTaskName.context)
                tasksRecyclerViewDialogBuilder.setTitle(holder.cardTaskName.context.getString(R.string.confirmation)).setMessage(holder.cardTaskName.context.getString(R.string.isSure)).setPositiveButton(holder.cardTaskName.context.getText(R.string.buttonDelete), object: DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        db = FirebaseFirestore.getInstance()
                        auth = FirebaseAuth.getInstance()
                        userID = auth.currentUser!!.uid

                        db.collection("Users").document(userID).collection("Tasks").document(data[holder.absoluteAdapterPosition].id as String).delete().addOnSuccessListener { Toast.makeText(holder.cardTaskName.context, "sucess", Toast.LENGTH_SHORT).show() }
                            .addOnFailureListener { e -> Toast.makeText(holder.cardTaskName.context, "falhou: $e", Toast.LENGTH_SHORT).show()}

                        data.removeAt(holder.absoluteAdapterPosition)

                        notifyDataSetChanged()

                    }
                }).setNegativeButton(holder.cardTaskName.context.getText(R.string.stringRegisterActivityButtonCancel), null).create().show()

            }
        })

        holder.cardTaskEdit.setOnClickListener {
            openDialog(data[holder.absoluteAdapterPosition],
                holder.cardTaskName.context,
                holder.absoluteAdapterPosition
            )
        }

    }


    private fun openDialog(task: Task, context: Context, position: Int) {
        tasksRecyclerViewDialogBuilder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.register_tasks_layout, null)
        tasksRecyclerViewName = view.findViewById(R.id.registerTasksEditTextName)
        tasksRecyclerViewDate = view.findViewById(R.id.registerTasksEditTextDate)
        tasksRecyclerViewHour = view.findViewById(R.id.registerTasksEditTextHour)
        tasksRecyclerViewSubject = view.findViewById(R.id.registerTasksEditTextSubject)
        tasksRecyclerViewDescription = view.findViewById(R.id.registerTasksEditTextDescription)
        tasksRecyclerViewButtonSave = view.findViewById(R.id.registerTasksButtonRegister)
        tasksRecyclerViewButtonCancel = view.findViewById(R.id.registerTasksButtonCancel)

        tasksRecyclerViewName.text = task.name
        tasksRecyclerViewDate.text = task.date
        tasksRecyclerViewHour.text = task.hour
        tasksRecyclerViewSubject.text = task.subject
        tasksRecyclerViewDescription.text = task.description


        tasksRecyclerViewDialogBuilder.setView(view)
        tasksRecyclerViewDialog = tasksRecyclerViewDialogBuilder.create()
        tasksRecyclerViewDialog.show()


        val dateSetList = object : DatePickerDialog.OnDateSetListener{
            override fun onDateSet(view: DatePicker, year: Int, monthOfYear: Int, dayOfMonth: Int) {
                calendar.set(Calendar.YEAR, year)
                calendar.set(Calendar.MONTH, monthOfYear)
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                view.updateDate(year, monthOfYear, dayOfMonth)

                val sdf = SimpleDateFormat("dd/MM/yyyy")
                tasksRecyclerViewDate.text = sdf.format(calendar.time)
            }
        }

        tasksRecyclerViewDate.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                val dialog = DatePickerDialog(context, dateSetList, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))
                dialog.datePicker.minDate = calendar.timeInMillis
                dialog.show()
            }
        })

        val timerSetList = object: TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker, hourOfDay: Int, minute: Int) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val stf = SimpleDateFormat("HH:mm")
                tasksRecyclerViewHour.text = stf.format(calendar.time)

            }
        }

        tasksRecyclerViewHour.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                TimePickerDialog(context, timerSetList, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
            }
        })

        tasksRecyclerViewButtonSave.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {

                if ((tasksRecyclerViewName.text.toString().isEmpty() || tasksRecyclerViewDate.text.toString().isEmpty() || tasksRecyclerViewHour.text.toString().isEmpty() || tasksRecyclerViewSubject.text.toString().isEmpty() || tasksRecyclerViewDescription.text.toString().isEmpty()) || (tasksRecyclerViewDate.text.toString().substring(6,10).toInt() < 2022) ) {
                    Toast.makeText(tasksRecyclerViewName.context, tasksRecyclerViewName.context.getString(R.string.fields), Toast.LENGTH_SHORT).show()
                } else{

                    db = FirebaseFirestore.getInstance()
                    auth = FirebaseAuth.getInstance()
                    userID = auth.currentUser!!.uid

                    val name = tasksRecyclerViewName.text.toString()
                    val date = tasksRecyclerViewDate.text.toString()
                    val hour = tasksRecyclerViewHour.text.toString()
                    val subject = tasksRecyclerViewSubject.text.toString()
                    val description = tasksRecyclerViewDescription.text.toString()


                    val taskUpdate = hashMapOf<String, Any>(
                        "name" to name,
                        "date" to date,
                        "hour" to hour,
                        "subject" to subject,
                        "description" to description,
                    )

                    val sub = db.collection("Users").document(userID).collection("Tasks").document(task.id as String)

                    sub.update(taskUpdate).addOnSuccessListener {
                        Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(context, "failure: $it", Toast.LENGTH_SHORT).show()
                    }

                    sub.get().addOnSuccessListener {document ->
                        val getId = document.get("id").toString()
                        val getName = document.get("name").toString()
                        val getStatus = document.get("status").toString()
                        val getDate = document.get("date").toString()
                        val getHour = document.get("hour").toString()
                        val getSubject = document.get("subject").toString()
                        val getDescription = document.get("description").toString()
                        val task = Task(getId, getName, getStatus, getDescription, getDate, getHour, getSubject)
                        data[position] = task
                        notifyDataSetChanged()

                    }.addOnFailureListener {
                        Toast.makeText(context, "erro: $it", Toast.LENGTH_SHORT).show()
                    }

                    tasksRecyclerViewDialog.dismiss()

                }
            }
        })

        tasksRecyclerViewButtonCancel.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                tasksRecyclerViewDialog.dismiss()
            }
        })
    }


    override fun getItemCount(): Int {
        return data.size
    }

}