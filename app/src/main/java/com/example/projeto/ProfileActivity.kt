package com.example.projeto

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore

class ProfileActivity : AppCompatActivity() {

    private val TAG = "PerfilActivity"
    lateinit var profileActivityUserPhoto: ImageView
    lateinit var profileActivityReturnHome: Button
    lateinit var profileActivityTakePhoto: Button
    lateinit var profileActivityLogout: Button
    lateinit var profileActivityTextViewName: TextView
    lateinit var profileActivityTextViewEmail: TextView
    private var uri_Imagem: Uri? = null
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var userID: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        supportActionBar?.hide();


        profileActivityTextViewName = findViewById(R.id.profileActivityUserName)
        profileActivityTextViewEmail = findViewById(R.id.profileActivityUserEmail)


        Log.i(TAG, "camera permission")

        if (ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0)
        }

        profileActivityUserPhoto = findViewById(R.id.profileActivityUserPhoto)
        profileActivityTakePhoto = findViewById(R.id.profileActivityButtonUserPhoto)
        profileActivityLogout = findViewById(R.id.profileActivityButtonLogout)
        profileActivityReturnHome = findViewById(R.id.profileActivityButtonReturn)


        profileActivityTakePhoto.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                Log.i(TAG, "take a picture")
                takePhoto()
            }
        }
        )

        profileActivityLogout.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                auth = FirebaseAuth.getInstance()
                auth.signOut()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
                finish()
            }
        })

        profileActivityReturnHome.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                onBackPressed()
            }
        })

    }

    override fun onStart() {
        super.onStart()


        db = FirebaseFirestore.getInstance()
        auth = FirebaseAuth.getInstance()
        val email: String = auth.currentUser!!.email!!
        userID = auth.currentUser!!.uid


        val documentReference: DocumentReference = db.collection("Users").document(userID)
        documentReference.get().addOnSuccessListener {
            if (it != null){
                profileActivityTextViewName.text = it.getString("name")
                profileActivityTextViewEmail.text = email
                if (it.getString("photo") != null){
                    val uriPhoto: Uri = Uri.parse(it.getString("photo"))
                    profileActivityUserPhoto.setImageURI(uriPhoto)
                }
            }
        }

    }


    val REQUEST_IMAGE_CAPTURE = 1
    private fun takePhoto(){
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also{

                val contentValues = ContentValues()
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                val resolver = contentResolver

                uri_Imagem = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

                takePictureIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri_Imagem)
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode== REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
            if (uri_Imagem != null) {
                db = FirebaseFirestore.getInstance()
                auth = FirebaseAuth.getInstance()
                userID = auth.currentUser!!.uid
                val documentReference: DocumentReference = db.collection("Users").document(userID)
                documentReference.update("photo", uri_Imagem.toString())
                profileActivityUserPhoto.setImageURI(uri_Imagem)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}