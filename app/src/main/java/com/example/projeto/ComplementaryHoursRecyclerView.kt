package com.example.projeto

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ComplementaryHoursRecyclerView(private var data: ArrayList<ComplementaryHour>):
    RecyclerView.Adapter<ComplementaryHoursRecyclerView.ViewHolder>() {
    private var TAG = "ComplementaryHoursRecyclerView"
    lateinit var complementaryHoursRecyclerViewDialogBuilder: AlertDialog.Builder
    lateinit var complementaryHoursRecyclerViewDialog: AlertDialog
    lateinit var complementaryHoursRecyclerViewType: Spinner
    lateinit var complementaryHoursRecyclerViewDescription: TextView
    lateinit var complementaryHoursRecyclerViewWorkload: TextView
    lateinit var complementaryHoursRecyclerViewButtonSave: Button
    lateinit var complementaryHoursRecyclerViewButtonCancel: Button
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardComplementaryHoursIcon: ImageView
        val cardComplementaryHoursType: TextView
        val cardComplementaryHoursWorkload: TextView
        val cardComplementaryHoursDelete: ImageView
        val cardComplementaryHoursEdit: ImageView

        init {
            cardComplementaryHoursIcon = view.findViewById(R.id.cardComplementaryHoursIcon)
            cardComplementaryHoursType = view.findViewById(R.id.cardComplementaryHoursType)
            cardComplementaryHoursWorkload = view.findViewById(R.id.cardComplementaryHoursWorkload)
            cardComplementaryHoursDelete = view.findViewById(R.id.cardComplementaryHoursDelete)
            cardComplementaryHoursEdit = view.findViewById(R.id.cardComplementaryHoursEdit)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        Log.i(TAG, "onCreateViewHolder()")
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_complementary_hours, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.i(TAG, "onBindViewHolder()")

        holder.cardComplementaryHoursType.text = data[position].type
        holder.cardComplementaryHoursWorkload.text = data[position].workload

        holder.cardComplementaryHoursDelete.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                complementaryHoursRecyclerViewDialogBuilder =
                    AlertDialog.Builder(holder.cardComplementaryHoursType.context)
                complementaryHoursRecyclerViewDialogBuilder.setTitle(
                    holder.cardComplementaryHoursType.context.getString(
                        R.string.confirmation
                    )
                ).setMessage(holder.cardComplementaryHoursType.context.getString(R.string.isSure))
                    .setPositiveButton(
                        holder.cardComplementaryHoursType.context.getText(R.string.buttonDelete),
                        object : DialogInterface.OnClickListener {
                            override fun onClick(p0: DialogInterface?, p1: Int) {
                                db = FirebaseFirestore.getInstance()
                                auth = FirebaseAuth.getInstance()
                                userID = auth.currentUser!!.uid

                                db.collection("Users").document(userID)
                                    .collection("ComplementaryHours")
                                    .document(data[holder.absoluteAdapterPosition].id as String)
                                    .delete().addOnSuccessListener {
                                    Toast.makeText(
                                        holder.cardComplementaryHoursType.context,
                                        "sucess",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(
                                            holder.cardComplementaryHoursType.context,
                                            "falhou: $e",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }

                                data.removeAt(holder.absoluteAdapterPosition)

                                notifyDataSetChanged()

                            }
                        }).setNegativeButton(
                    holder.cardComplementaryHoursType.context.getText(R.string.stringRegisterActivityButtonCancel),
                    null
                ).create().show()

            }
        })

        holder.cardComplementaryHoursEdit.setOnClickListener {
            openDialog(
                data[holder.absoluteAdapterPosition],
                holder.cardComplementaryHoursType.context,
                holder.absoluteAdapterPosition
            )
        }

    }

    override fun getItemCount(): Int {
        return data.size
    }


    private fun openDialog(complementaryHour: ComplementaryHour, context: Context, position: Int) {
        complementaryHoursRecyclerViewDialogBuilder = AlertDialog.Builder(context)
        val view = LayoutInflater.from(context).inflate(R.layout.register_complementary_hours_layout, null)
        complementaryHoursRecyclerViewType = view.findViewById(R.id.registerComplementaryHourSpinner)
        complementaryHoursRecyclerViewDescription = view.findViewById(R.id.registerComplementaryHoursDescription)
        complementaryHoursRecyclerViewWorkload = view.findViewById(R.id.registerComplementaryHoursWorkload)
        complementaryHoursRecyclerViewButtonSave = view.findViewById(R.id.registerComplementaryHoursButtonRegister)
        complementaryHoursRecyclerViewButtonCancel = view.findViewById(R.id.registerComplementaryHoursButtonCancel)

        val complementaryHoursTypes = arrayOf(context.getString(R.string.stringComplementaryHoursTypesTeaching), context.getString(R.string.stringComplementaryHoursTypesResearch), context.getString(R.string.stringComplementaryHoursTypesExtension))
        complementaryHoursRecyclerViewType.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_dropdown_item, complementaryHoursTypes)

         if (complementaryHour.type == complementaryHoursTypes[0]){
             complementaryHoursRecyclerViewType.setSelection(0)
         }
        if (complementaryHour.type == complementaryHoursTypes[1]){
            complementaryHoursRecyclerViewType.setSelection(1)
        }
        if (complementaryHour.type == complementaryHoursTypes[2]){
            complementaryHoursRecyclerViewType.setSelection(2)
        }
        complementaryHoursRecyclerViewDescription.text = complementaryHour.description
        complementaryHoursRecyclerViewWorkload.text = complementaryHour.workload



        complementaryHoursRecyclerViewDialogBuilder.setView(view)
        complementaryHoursRecyclerViewDialog = complementaryHoursRecyclerViewDialogBuilder.create()
        complementaryHoursRecyclerViewDialog.show()

        complementaryHoursRecyclerViewButtonSave.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View) {
                val pos = complementaryHoursRecyclerViewType.selectedItemPosition
                val type = complementaryHoursTypes[pos]
                val description = complementaryHoursRecyclerViewDescription.text.toString()
                val workload = complementaryHoursRecyclerViewWorkload.text.toString()


                if(type.isEmpty() || description.isEmpty() || workload.isEmpty()){
                    val snackbar: Snackbar = Snackbar.make(p0, "Preencha os campos", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()
                } else if(workload.toInt() !in 1 .. 240){
                    val snackbar: Snackbar = Snackbar.make(p0, "Hora complemetar deve ser entre 1 e 240", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()
                }else{
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

                        complementaryHoursType -= complementaryHour.workload!!.toInt()
                        complementaryHoursType += workload.toInt()

                        if (complementaryHoursType > 240){
                            val snackbar: Snackbar = Snackbar.make(p0, "Vai ultrapassar a quantidade de horas", Snackbar.LENGTH_SHORT)
                            snackbar.setBackgroundTint(Color.WHITE)
                            snackbar.setTextColor(Color.BLACK)
                            snackbar.show()
                        }else{
                            val complementaryHourUpdate = hashMapOf<String, Any>(
                                "type" to type,
                                "description" to description,
                                "workload" to workload,
                            )

                            val documentReference = db.collection("Users").document(userID).collection("ComplementaryHours").document(complementaryHour.id as String)
                            documentReference.update(complementaryHourUpdate).addOnSuccessListener {
                                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
                            }.addOnFailureListener {
                                Toast.makeText(context, "failure: $it", Toast.LENGTH_SHORT).show()
                            }

                            documentReference.get().addOnSuccessListener {document ->
                                val getId = document.get("id").toString()
                                val getType = document.get("type").toString()
                                val getDescription = document.get("description").toString()
                                val getWorkload = document.get("workload").toString()
                                val getComplementaryHour = ComplementaryHour(getId, getType, getDescription, getWorkload)
                                data[position] = getComplementaryHour
                                notifyDataSetChanged()

                            }.addOnFailureListener {
                                Toast.makeText(context, "erro: $it", Toast.LENGTH_SHORT).show()
                            }
                            complementaryHoursRecyclerViewDialog.dismiss()
                        }
                        }                        }


            }
        })

        complementaryHoursRecyclerViewButtonCancel.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                complementaryHoursRecyclerViewDialog.dismiss()
            }
        })
    }
}