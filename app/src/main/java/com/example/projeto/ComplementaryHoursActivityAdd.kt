package com.example.projeto

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ComplementaryHoursActivityAdd : AppCompatActivity() {
    private var TAG = "ComplementaryHoursActivityAdd"
    lateinit var complementaryHoursActivityAddSpinner: Spinner
    lateinit var complementaryHoursActivityAddDescription: TextInputEditText
    lateinit var complementaryHoursActivityAddWorkload: TextInputEditText
    lateinit var complementaryHoursActivityAddButtonRegister: Button
    lateinit var complementaryHoursActivityAddButtonCancel: Button
    lateinit var complementaryHoursActivityAddButtonAdd: Button
    lateinit var complementaryHoursActivityAddButtonStatistics: Button
    lateinit var complementaryHoursActivityAddDialogBuilder: AlertDialog.Builder
    lateinit var complementaryHoursActivityAddDialog: AlertDialog
    lateinit var complementaryHoursActivityAddRecyclerView: RecyclerView
    private var complementaryHoursActivityAddLayoutManager: RecyclerView.LayoutManager? = null
    private var complementaryHoursActivityAddAdapter: RecyclerView.Adapter<ComplementaryHoursRecyclerView.ViewHolder>? = null
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var userID: String
    var complementaryHoursList: ArrayList<ComplementaryHour> = ArrayList()
    var id: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complementary_hours_add)

        complementaryHoursActivityAddButtonStatistics = findViewById(R.id.complementaryHoursActivityStatistics)
        complementaryHoursActivityAddButtonAdd = findViewById(R.id.complementaryHoursActivityAddButtonAdd)

        complementaryHoursActivityAddButtonAdd.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                openDialog()
            }
        })

        complementaryHoursActivityAddButtonStatistics.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                startActivity(Intent(applicationContext, ComplementaryHoursActivity::class.java))
                finish()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid

        val complementaryHours = db.collection("Users").document(userID).collection("ComplementaryHours")

        complementaryHours.orderBy("order").get().addOnSuccessListener {documents ->
            for (document in documents){
                val getId = document.get("id").toString()
                val getType = document.get("type").toString()
                val getDescription = document.get("description").toString()
                val getWorkload = document.get("workload").toString()
                val complementaryHour = ComplementaryHour(getId, getType, getDescription, getWorkload)
                complementaryHoursList.add(complementaryHour)
            }
            complementaryHoursActivityAddRecyclerView = findViewById(R.id.complementaryHoursActivityAddRecyclerView)
            complementaryHoursActivityAddLayoutManager = LinearLayoutManager(this)
            complementaryHoursActivityAddRecyclerView.layoutManager = complementaryHoursActivityAddLayoutManager
            complementaryHoursActivityAddAdapter = ComplementaryHoursRecyclerView(complementaryHoursList)
            complementaryHoursActivityAddRecyclerView.adapter = complementaryHoursActivityAddAdapter
            complementaryHoursList = ArrayList()
        }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "erro: $it", Toast.LENGTH_SHORT).show()
            }
    }

    private fun openDialog() {
        complementaryHoursActivityAddDialogBuilder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.register_complementary_hours_layout, null)
        complementaryHoursActivityAddSpinner = view.findViewById(R.id.registerComplementaryHourSpinner)
        complementaryHoursActivityAddDescription = view.findViewById(R.id.registerComplementaryHoursDescription)
        complementaryHoursActivityAddWorkload = view.findViewById(R.id.registerComplementaryHoursWorkload)
        complementaryHoursActivityAddButtonRegister = view.findViewById(R.id.registerComplementaryHoursButtonRegister)
        complementaryHoursActivityAddButtonCancel = view.findViewById(R.id.registerComplementaryHoursButtonCancel)


        val complementaryHoursTypes = arrayOf(getString(R.string.stringComplementaryHoursTypesTeaching), getString(R.string.stringComplementaryHoursTypesResearch), getString(R.string.stringComplementaryHoursTypesExtension))
        complementaryHoursActivityAddSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, complementaryHoursTypes)

        complementaryHoursActivityAddButtonRegister.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View) {
                val position = complementaryHoursActivityAddSpinner.selectedItemPosition
                val type = complementaryHoursTypes[position]
                val description = complementaryHoursActivityAddDescription.text.toString()
                val workload = complementaryHoursActivityAddWorkload.text.toString()


                if(type.isEmpty() || description.isEmpty() || workload.isEmpty()){
                    val snackbar: Snackbar = Snackbar.make(p0, "Preencha os campos", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()
                }else if(workload.toInt() !in 1 .. 240){
                    val snackbar: Snackbar = Snackbar.make(p0, "Hora complemetar deve ser entre 1 e 240", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()
                }else{
                    id += 1
                    saveComplementaryHours(type, description, workload, id)
                }

            }
        })

        complementaryHoursActivityAddButtonCancel.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                complementaryHoursActivityAddDialog.dismiss()
            }
        })

        complementaryHoursActivityAddDialogBuilder.setView(view)
        complementaryHoursActivityAddDialog = complementaryHoursActivityAddDialogBuilder.create()
        complementaryHoursActivityAddDialog.show()
    }

    private fun saveComplementaryHours(type: String, description: String, workload: String, id: Int) {
        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid

        var complementaryHoursType = 0

        val complementaryHours = db.collection("Users").document(userID).collection("ComplementaryHours")

        complementaryHours.orderBy("order").get().addOnSuccessListener { documents ->
            for (document in documents) {
                if (document.get("type") == type) {
                    val hoursType = document.get("workload").toString()
                    complementaryHoursType += hoursType.toInt()
                }
            }
            complementaryHoursType += workload.toInt()

            if (complementaryHoursType > 240){
                Toast.makeText(applicationContext, "irá ultrapassar as horas", Toast.LENGTH_SHORT).show()
            }else{
                val documentReference = db.collection("Users").document(userID).collection("ComplementaryHours").document()

                val complementaryHour = hashMapOf(
                    "id" to documentReference.id,
                    "order" to id,
                    "type" to type,
                    "description" to description,
                    "workload" to workload,
                )

                documentReference.set(complementaryHour).addOnSuccessListener(OnSuccessListener {
                    Log.d(TAG, "Success by saving the data")
                    complementaryHoursActivityAddDialog.dismiss()

                    val complementaryHoursLists0: ArrayList<ComplementaryHour> = ArrayList()
                    val complementaryHours = db.collection("Users").document(userID).collection("ComplementaryHours")

                    complementaryHours.orderBy("order").get().addOnSuccessListener {documents ->
                        for (document in documents){
                            val getId = document.get("id").toString()
                            val getType = document.get("type").toString()
                            val getDescription = document.get("description").toString()
                            val getWorkload = document.get("workload").toString()
                            val getComplementaryHour = ComplementaryHour(getId, getType, getDescription, getWorkload)
                            complementaryHoursLists0.add(getComplementaryHour)
                        }
                        complementaryHoursActivityAddRecyclerView = findViewById(R.id.complementaryHoursActivityAddRecyclerView)
                        complementaryHoursActivityAddLayoutManager = LinearLayoutManager(this)
                        complementaryHoursActivityAddRecyclerView.layoutManager = complementaryHoursActivityAddLayoutManager
                        complementaryHoursActivityAddAdapter = ComplementaryHoursRecyclerView(complementaryHoursLists0)
                        complementaryHoursActivityAddRecyclerView.adapter = complementaryHoursActivityAddAdapter
                    }
                        .addOnFailureListener {
                            Toast.makeText(applicationContext, "erro: $it", Toast.LENGTH_SHORT).show()
                        }


                }).addOnFailureListener(OnFailureListener {
                    Log.d(TAG, "Failure by saving the data ${it.toString()}")
                })
            }
        }
    }
}