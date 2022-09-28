package com.example.projeto

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ComplementaryHoursActivity : AppCompatActivity() {
    lateinit var complementaryHoursActivityPieChart: PieChart
    lateinit var complementaryHoursActivityProgressBarTeaching: ProgressBar
    lateinit var complementaryHoursActivityProgressBarResearch: ProgressBar
    lateinit var complementaryHoursActivityProgressBarExtension: ProgressBar
    lateinit var complementaryHoursActivityButtonReturn: Button
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complementary_hours)

        complementaryHoursActivityButtonReturn = findViewById(R.id.complementaryHoursActivityReturn)
        complementaryHoursActivityButtonReturn.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                onBackPressed()
            }
        })

        }

    override fun onStart() {
        super.onStart()

        var complementaryHoursTeaching = 0f
        var complementaryHoursResearch = 0f
        var complementaryHoursExtension = 0f
        var complementaryHoursTeachingProgressBar = 0f
        var complementaryHoursResearchProgressBar = 0f
        var complementaryHoursExtensionProgressBar = 0f
        var complementaryHoursTotal = 0f

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid
        val complementaryHoursTypes = arrayOf(getString(R.string.stringComplementaryHoursTypesTeaching), getString(R.string.stringComplementaryHoursTypesResearch), getString(R.string.stringComplementaryHoursTypesExtension))
        val complementaryHours = db.collection("Users").document(userID).collection("ComplementaryHours")

        complementaryHours.orderBy("order").get().addOnSuccessListener {documents ->
            for (document in documents){
                if (document.get("type") == complementaryHoursTypes[0]){
                    val teaching = document.get("workload").toString()
                    complementaryHoursTeaching += teaching.toFloat()
                }
                if (document.get("type") == complementaryHoursTypes[1]){
                    val research = document.get("workload").toString()
                    complementaryHoursResearch += research.toFloat()

                }
                if (document.get("type") == complementaryHoursTypes[2]){
                    val extension = document.get("workload").toString()
                    complementaryHoursExtension += extension.toFloat()
                }
            }



            complementaryHoursActivityPieChart = findViewById(R.id.complementaryHoursActivityPieChart)
            complementaryHoursActivityProgressBarTeaching = findViewById(R.id.complementaryHoursActivityProgressBarTeaching)
            complementaryHoursActivityProgressBarResearch = findViewById(R.id.complementaryHoursActivityProgressBarResearch)
            complementaryHoursActivityProgressBarExtension = findViewById(R.id.complementaryHoursActivityProgressBarExtension)

            complementaryHoursActivityPieChart.setUsePercentValues(true)
            complementaryHoursActivityPieChart.description.isEnabled = false
            complementaryHoursActivityPieChart.legend.isEnabled = false

            complementaryHoursTeachingProgressBar = complementaryHoursTeaching * (0.416666667f)
            complementaryHoursResearchProgressBar = complementaryHoursResearch * (0.416666667f)
            complementaryHoursExtensionProgressBar = complementaryHoursExtension * (0.416666667f)

            complementaryHoursActivityProgressBarTeaching.progress = complementaryHoursTeachingProgressBar.toInt()
            complementaryHoursActivityProgressBarResearch.progress = complementaryHoursResearchProgressBar.toInt()
            complementaryHoursActivityProgressBarExtension.progress = complementaryHoursExtensionProgressBar.toInt()

                complementaryHoursActivityPieChart.animateY(1400, Easing.EaseInOutQuad)
                complementaryHoursTeaching *= (0.138888889f)
                complementaryHoursResearch *= (0.138888889f)
                complementaryHoursExtension *= (0.138888889f)
                complementaryHoursTotal = 100f - (complementaryHoursTeaching + complementaryHoursResearch + complementaryHoursExtension)


                val entries: ArrayList<PieEntry> = ArrayList()
                entries.add(PieEntry(complementaryHoursTeaching))
                entries.add(PieEntry(complementaryHoursResearch))
                entries.add(PieEntry(complementaryHoursExtension))
                entries.add(PieEntry(complementaryHoursTotal))

                val dataSet = PieDataSet(entries, "")

                val colors: ArrayList<Int> = ArrayList()
                colors.add(resources.getColor(R.color.green))
                colors.add(resources.getColor(R.color.pink))
                colors.add(resources.getColor(R.color.red))
                colors.add(resources.getColor(R.color.white))

                dataSet.colors = colors

                val data = PieData(dataSet)
                data.setValueTextSize(0f)
                complementaryHoursActivityPieChart.data = data

        }
            .addOnFailureListener {
                Toast.makeText(applicationContext, "erro: $it", Toast.LENGTH_SHORT).show()
            }
    }
}