package com.example.projeto

import android.content.Intent
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*

class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    lateinit var homeActivityToggle: ActionBarDrawerToggle
    lateinit var homeActivityDrawerLayout: DrawerLayout
    lateinit var homeActivityNavigationVw: NavigationView
    lateinit var homeActivityRecyclerViewToDo: RecyclerView
    private var homeActivityLayoutManagerToDo: RecyclerView.LayoutManager? = null
    private var homeActivityAdapterToDo: RecyclerView.Adapter<ToDoRecyclerView.ViewHolder>? = null
    lateinit var homeActivityRecyclerViewDoing: RecyclerView
    private var homeActivityLayoutManagerDoing: RecyclerView.LayoutManager? = null
    private var homeActivityAdapterDoing: RecyclerView.Adapter<DoingRecyclerView.ViewHolder>? = null
    lateinit var homeActivityRecyclerViewDone: RecyclerView
    private var homeActivityLayoutManagerDone: RecyclerView.LayoutManager? = null
    private var homeActivityAdapterDone: RecyclerView.Adapter<DoneRecyclerView.ViewHolder>? = null
    lateinit var homeActivityDialog: AlertDialog
    lateinit var homeActivityDialogBuilder: AlertDialog.Builder
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore
    lateinit var userID: String
    var tasksListToDo: ArrayList<Task> = ArrayList()
    var tasksListDoing: ArrayList<Task> = ArrayList()
    var tasksListDone: ArrayList<Task> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        supportActionBar?.hide();

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.homeActivityToolbar)
        setSupportActionBar(toolbar)

        homeActivityDrawerLayout = findViewById(R.id.homeActivityDrawerLayout)
        homeActivityNavigationVw = findViewById(R.id.homeActivityNavigationView)

        homeActivityToggle = ActionBarDrawerToggle(this, homeActivityDrawerLayout, R.string.open, R.string.close)
        homeActivityDrawerLayout.addDrawerListener(homeActivityToggle)
        homeActivityNavigationVw.setNavigationItemSelectedListener(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.title = ""


    }

    private fun openHelp() {
        homeActivityDialogBuilder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.view_help, null)

        homeActivityDialogBuilder.setView(view)
        homeActivityDialog = homeActivityDialogBuilder.create()
        homeActivityDialog.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onStart() {
        super.onStart()

        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid

        val tasks = db.collection("Users").document(userID).collection("Tasks")
        tasks.orderBy("date").get().addOnSuccessListener {documents ->
            for (document in documents){
                if (document.get("status") == "0"){
                    val getId = document.get("id").toString()
                    val getName = document.get("name").toString()
                    val getStatus = document.get("status").toString()
                    val getDate = document.get("date").toString()
                    val getHour = document.get("hour").toString()
                    val getSubject = document.get("subject").toString()
                    val getDescription = document.get("description").toString()
                    val task = Task(getId, getName, getStatus, getDescription, getDate, getHour, getSubject)
                    tasksListToDo.add(task)
                }

                if (document.get("status") == "1"){
                    val getId = document.get("id").toString()
                    val getName = document.get("name").toString()
                    val getStatus = document.get("status").toString()
                    val getDate = document.get("date").toString()
                    val getHour = document.get("hour").toString()
                    val getSubject = document.get("subject").toString()
                    val getDescription = document.get("description").toString()
                    val task = Task(getId, getName, getStatus, getDescription, getDate, getHour, getSubject)
                    tasksListDoing.add(task)
                }

                if (document.get("status") == "2"){
                    val getId = document.get("id").toString()
                    val getName = document.get("name").toString()
                    val getStatus = document.get("status").toString()
                    val getDate = document.get("date").toString()
                    val getHour = document.get("hour").toString()
                    val getSubject = document.get("subject").toString()
                    val getDescription = document.get("description").toString()
                    val task = Task(getId, getName, getStatus, getDescription, getDate, getHour, getSubject)
                    tasksListDone.add(task)
                }

            }
            homeActivityRecyclerViewToDo = findViewById(R.id.homeActivityToDoRecyclerView)
            homeActivityRecyclerViewDoing = findViewById(R.id.homeActivityDoingRecyclerView)
            homeActivityRecyclerViewDone= findViewById(R.id.homeActivityDoneRecyclerView)


            homeActivityLayoutManagerToDo = GridLayoutManager(this, 3)
            homeActivityRecyclerViewToDo.layoutManager = homeActivityLayoutManagerToDo
            homeActivityAdapterToDo = ToDoRecyclerView(tasksListToDo, homeActivityRecyclerViewDoing)
            homeActivityRecyclerViewToDo.adapter = homeActivityAdapterToDo
            tasksListToDo = ArrayList()

            homeActivityLayoutManagerDoing = GridLayoutManager(this, 3)
            homeActivityRecyclerViewDoing.layoutManager = homeActivityLayoutManagerDoing
            homeActivityAdapterDoing = DoingRecyclerView(tasksListDoing, homeActivityRecyclerViewToDo, homeActivityRecyclerViewDone)
            homeActivityRecyclerViewDoing.adapter = homeActivityAdapterDoing
            tasksListDoing = ArrayList()

            homeActivityLayoutManagerDone = GridLayoutManager(this, 3)
            homeActivityRecyclerViewDone.layoutManager = homeActivityLayoutManagerDone
            homeActivityAdapterDone = DoneRecyclerView(tasksListDone, homeActivityRecyclerViewDoing)
            homeActivityRecyclerViewDone.adapter = homeActivityAdapterDone
            tasksListDone = ArrayList()


        }.addOnFailureListener {
            Toast.makeText(applicationContext, "erro: $it", Toast.LENGTH_SHORT).show()
        }

    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.itemMenuTasks -> startActivity(Intent(applicationContext, TasksActivity::class.java))
            R.id.itemMenuSubjects -> startActivity(Intent(applicationContext, SubjectsActivity::class.java))
            R.id.itemMenuSchedules -> startActivity(Intent(applicationContext, SchedulesActivity::class.java))
            R.id.itemMenuGrades -> startActivity(Intent(applicationContext, GradesActivity::class.java))
            R.id.itemMenuCalendar -> startActivity(Intent(applicationContext, CalendarActivity::class.java))
            R.id.itemMenuComplementaryHours -> startActivity(Intent(applicationContext, ComplementaryHoursActivityAdd::class.java))
            R.id.itemMenuProfile -> startActivity(Intent(applicationContext, ProfileActivity::class.java))
            R.id.itemMenuAbout -> startActivity(Intent(applicationContext, AboutActivity::class.java))
        }

        homeActivityDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        homeActivityToggle.syncState()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        homeActivityToggle.onConfigurationChanged(newConfig)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (homeActivityToggle.onOptionsItemSelected(item)){
            return true
        }

        when (item.itemId){
            R.id.menuHelp -> openHelp()
        }

        return super.onOptionsItemSelected(item)
    }

}
