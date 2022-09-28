package com.example.projeto

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
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class SubjectsRecyclerView(private var data: ArrayList<Subject>):
    RecyclerView.Adapter<SubjectsRecyclerView.ViewHolder>() {
    private var TAG = "SubjectsRecyclerView"
    lateinit var subjectsRecyclerViewDialogBuilder: AlertDialog.Builder
    lateinit var subjectsRecyclerViewDialog: AlertDialog
    lateinit var subjectsRecyclerViewMonday: CheckBox
    lateinit var subjectsRecyclerViewTuesday: CheckBox
    lateinit var subjectsRecyclerViewWednesday: CheckBox
    lateinit var subjectsRecyclerViewThursday: CheckBox
    lateinit var subjectsRecyclerViewFriday: CheckBox
    lateinit var subjectsRecyclerViewSubjectCode: TextView
    lateinit var subjectsRecyclerViewSubjectName: TextView
    lateinit var subjectsRecyclerViewSubjectTeacher: TextView
    lateinit var subjectsRecyclerViewSubjectStartTime: TextView
    lateinit var subjectsRecyclerViewSubjectEndTime: TextView
    lateinit var subjectsRecyclerViewSubjectButtonSave: Button
    lateinit var subjectsRecyclerViewSubjectButtonCancel: Button
    var calendar = Calendar.getInstance()
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val cardSubjectIcon: ImageView
        val cardSubjectCode: TextView
        val cardSubjectName: TextView
        val cardSubjectDelete: ImageView
        val cardSubjectEdit: ImageView

        init{
            cardSubjectIcon = view.findViewById(R.id.cardSubjectsIcon)
            cardSubjectCode = view.findViewById(R.id.cardSubjectsCode)
            cardSubjectName = view.findViewById(R.id.cardSubjectsName)
            cardSubjectDelete = view.findViewById(R.id.cardSubjectsDelete)
            cardSubjectEdit = view.findViewById(R.id.cardSubjectsEdit)

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "onCreateViewHolder()")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_subjects, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder()")

        holder.cardSubjectCode.text = data[position].code
        holder.cardSubjectName.text = data[position].name

        holder.cardSubjectDelete.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                subjectsRecyclerViewDialogBuilder = AlertDialog.Builder(holder.cardSubjectName.context)
                subjectsRecyclerViewDialogBuilder.setTitle(holder.cardSubjectName.context.getString(R.string.confirmation)).setMessage(holder.cardSubjectName.context.getString(R.string.isSure)).setPositiveButton(holder.cardSubjectName.context.getText(R.string.buttonDelete), object: DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        db = FirebaseFirestore.getInstance()
                        auth = FirebaseAuth.getInstance()
                        userID = auth.currentUser!!.uid

                        db.collection("Users").document(userID).collection("Subjects").document(data[holder.absoluteAdapterPosition].id as String).delete().addOnSuccessListener { Toast.makeText(holder.cardSubjectName.context, "sucess", Toast.LENGTH_SHORT).show() }
                            .addOnFailureListener { e -> Toast.makeText(holder.cardSubjectName.context, "falhou: $e", Toast.LENGTH_SHORT).show()}

                        data.removeAt(holder.absoluteAdapterPosition)

                        notifyDataSetChanged()

                    }
                }).setNegativeButton(holder.cardSubjectEdit.context.getText(R.string.stringRegisterActivityButtonCancel), null).create().show()

            }
        })

        holder.cardSubjectEdit.setOnClickListener {
            openDialog(data[holder.absoluteAdapterPosition],
                holder.cardSubjectName.context,
                holder.absoluteAdapterPosition
            )
        }

    }


    private fun openDialog(subject: Subject, context: Context, position: Int) {
        subjectsRecyclerViewDialogBuilder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.register_subjects_layout, null)
        subjectsRecyclerViewSubjectCode = view.findViewById(R.id.registerSubjectsEditTextCode)
        subjectsRecyclerViewSubjectName = view.findViewById(R.id.registerSubjectsEditTextName)
        subjectsRecyclerViewSubjectTeacher = view.findViewById(R.id.registerSubjectsEditTextTeacher)
        subjectsRecyclerViewSubjectStartTime = view.findViewById(R.id.registerSubjectsEditTextStartTime)
        subjectsRecyclerViewSubjectEndTime = view.findViewById(R.id.registerSubjectsEditTextEndTime)
        subjectsRecyclerViewMonday = view.findViewById(R.id.registerSubjectsCheckBoxMonday)
        subjectsRecyclerViewTuesday = view.findViewById(R.id.registerSubjectsCheckBoxTuesday)
        subjectsRecyclerViewWednesday = view.findViewById(R.id.registerSubjectsCheckBoxWednesday)
        subjectsRecyclerViewThursday = view.findViewById(R.id.registerSubjectsCheckBoxThursday)
        subjectsRecyclerViewFriday = view.findViewById(R.id.registerSubjectsCheckBoxFriday)
        subjectsRecyclerViewSubjectButtonSave = view.findViewById(R.id.registerSubjectsButtonRegister)
        subjectsRecyclerViewSubjectButtonCancel = view.findViewById(R.id.registerSubjectsButtonCancel)

        subjectsRecyclerViewSubjectCode.text = subject.code
        subjectsRecyclerViewSubjectName.text = subject.name
        subjectsRecyclerViewSubjectTeacher.text = subject.teacher
        subjectsRecyclerViewMonday.isChecked = subject.daysOfWeek!![0]
        subjectsRecyclerViewTuesday.isChecked = subject.daysOfWeek!![1]
        subjectsRecyclerViewWednesday.isChecked = subject.daysOfWeek!![2]
        subjectsRecyclerViewThursday.isChecked = subject.daysOfWeek!![3]
        subjectsRecyclerViewFriday.isChecked = subject.daysOfWeek!![4]
        subjectsRecyclerViewSubjectStartTime.text = subject.startTime
        subjectsRecyclerViewSubjectEndTime.text = subject.endTime

        val timerSetListStart = object: TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker, hourOfDay: Int, minute: Int) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val stf = SimpleDateFormat("HH:mm")
                subjectsRecyclerViewSubjectStartTime.setText(stf.format(calendar.time))

            }
        }

        subjectsRecyclerViewSubjectStartTime.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                TimePickerDialog(context, timerSetListStart, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(
                    Calendar.MINUTE), true).show()
            }
        })

        val timerSetListEnd = object: TimePickerDialog.OnTimeSetListener{
            override fun onTimeSet(p0: TimePicker, hourOfDay: Int, minute: Int) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                calendar.set(Calendar.MINUTE, minute)
                val stf = SimpleDateFormat("HH:mm")
                subjectsRecyclerViewSubjectEndTime.setText(stf.format(calendar.time))

            }
        }

        subjectsRecyclerViewSubjectEndTime.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                TimePickerDialog(context, timerSetListEnd, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(
                    Calendar.MINUTE), true).show()
            }
        })

        subjectsRecyclerViewDialogBuilder.setView(view)
        subjectsRecyclerViewDialog = subjectsRecyclerViewDialogBuilder.create()
        subjectsRecyclerViewDialog.show()

        subjectsRecyclerViewSubjectButtonSave.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {

                if (subjectsRecyclerViewSubjectName.text.toString().isEmpty() || subjectsRecyclerViewSubjectTeacher.text.toString().isEmpty() || subjectsRecyclerViewSubjectStartTime.text.toString().isEmpty() || subjectsRecyclerViewSubjectEndTime.text.toString().isEmpty() || ((!subjectsRecyclerViewMonday.isChecked) && (!subjectsRecyclerViewTuesday.isChecked) && (!subjectsRecyclerViewWednesday.isChecked) && (!subjectsRecyclerViewThursday.isChecked) && (!subjectsRecyclerViewFriday.isChecked))){
                    Toast.makeText(subjectsRecyclerViewSubjectName.context, subjectsRecyclerViewSubjectName.context.getString(R.string.fields), Toast.LENGTH_SHORT).show()
                } else{
                    db = FirebaseFirestore.getInstance()
                    auth = FirebaseAuth.getInstance()
                    userID = auth.currentUser!!.uid

                    val checkBoxMonday = subjectsRecyclerViewMonday.isChecked
                    val checkBoxTuesday = subjectsRecyclerViewTuesday.isChecked
                    val checkBoxWednesday = subjectsRecyclerViewWednesday.isChecked
                    val checkBoxThursday = subjectsRecyclerViewThursday.isChecked
                    val checkBoxFriday = subjectsRecyclerViewFriday.isChecked
                    val startTime = subjectsRecyclerViewSubjectStartTime.text.toString()
                    val endTime = subjectsRecyclerViewSubjectEndTime.text.toString()
                    val code = subjectsRecyclerViewSubjectCode.text.toString()
                    val name = subjectsRecyclerViewSubjectName.text.toString()
                    val teacher = subjectsRecyclerViewSubjectTeacher.text.toString()

                    val daysOfWeek: ArrayList<Boolean> = ArrayList()
                    daysOfWeek.add(0, checkBoxMonday)
                    daysOfWeek.add(1, checkBoxTuesday)
                    daysOfWeek.add(2, checkBoxWednesday)
                    daysOfWeek.add(3, checkBoxThursday)
                    daysOfWeek.add(4, checkBoxFriday)

                    val subjectUpdate = hashMapOf<String, Any>(
                        "name" to name,
                        "code" to code,
                        "teacher" to teacher,
                        "startTime" to startTime,
                        "endTime" to endTime,
                        "daysOfWeek" to daysOfWeek,
                    )

                    val sub = db.collection("Users").document(userID).collection("Subjects").document(subject.id as String)

                    db.collection("Users").document(userID).collection("Subjects").document(subject.id as String).collection("Grades").document()

                    sub.update(subjectUpdate).addOnSuccessListener {
                        Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(context, "failure: $it", Toast.LENGTH_SHORT).show()
                    }

                    sub.get().addOnSuccessListener {document ->
                        val getId = document.get("id").toString()
                        val getName = document.get("name").toString()
                        val getCode = document.get("code").toString()
                        val getDaysOfWeek = document.get("daysOfWeek")
                        val getTeacher = document.get("teacher").toString()
                        val getGrade = document.get("grade")
                        val getStartTime = document.get("startTime").toString()
                        val getEndTime = document.get("endTime").toString()
                        val subject = Subject(getId, getCode, getName, getTeacher, getGrade as ArrayList<String>, getDaysOfWeek as ArrayList<Boolean>,getStartTime, getEndTime)
                        data[position] = subject
                        notifyDataSetChanged()

                    }.addOnFailureListener {
                            Toast.makeText(context, "erro: $it", Toast.LENGTH_SHORT).show()
                        }
                    subjectsRecyclerViewDialog.dismiss()

                }
            }
        })

        subjectsRecyclerViewSubjectButtonCancel.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                subjectsRecyclerViewDialog.dismiss()
            }
        })
    }


    override fun getItemCount(): Int {
        return data.size
    }

}