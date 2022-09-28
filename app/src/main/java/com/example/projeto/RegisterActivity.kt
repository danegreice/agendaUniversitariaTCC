package com.example.projeto

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.*
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import java.util.*


open class RegisterActivity : AppCompatActivity() {

    private var TAG = "CadastroActivity"
    lateinit var registerActivityButtonCancel: Button
    lateinit var registerActivityButtonRegister: Button
    lateinit var registerActivityEditTextName: TextInputEditText
    lateinit var registerActivityEditTextEmail: TextInputEditText
    lateinit var registerActivityEditTextPassword: TextInputEditText
    lateinit var db: FirebaseFirestore
    lateinit var auth: FirebaseAuth
    lateinit var userID: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        supportActionBar?.hide()
        db = FirebaseFirestore.getInstance()


        registerActivityButtonCancel = findViewById(R.id.registerActivityButtonCancel)
        registerActivityButtonRegister = findViewById(R.id.registerActivityButtonRegister)
        registerActivityEditTextName = findViewById(R.id.registerActivityEditName)
        registerActivityEditTextEmail = findViewById(R.id.registerActivityEditEmail)
        registerActivityEditTextPassword = findViewById(R.id.registerActivityEditPassword)

        registerActivityButtonRegister.setOnClickListener(object: View.OnClickListener{
            @SuppressLint("ShowToast")
            override fun onClick(p0: View) {
                val name = registerActivityEditTextName.text.toString()
                val email = registerActivityEditTextEmail.text.toString()
                val password = registerActivityEditTextPassword.text.toString()

                if(name.isEmpty() || email.isEmpty() || password.isEmpty()){
                    val snackbar: Snackbar = Snackbar.make(p0, "Preencha os campos", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()
                } else{
                    registerUser(email, password, p0)
                }

            }
        })

        registerActivityButtonCancel.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?) {
                onBackPressed()
            }
        })

    }

    private fun registerUser(email: String, password: String, view: View){

        auth = FirebaseAuth.getInstance()
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener( OnCompleteListener {

            if (it.isSuccessful){

                saveUserData()

                val snackbar: Snackbar = Snackbar.make(view, "Cadastro realizado com sucesso", Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.WHITE)
                snackbar.setTextColor(Color.BLACK)
                snackbar.show()
                auth.signOut()
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            } else{
                val error: String
                try{
                    throw it.exception!!
                } catch (e: FirebaseAuthWeakPasswordException){
                    error = "Digite uma senha com no minimo 6 caracteres"
                } catch (e: FirebaseAuthUserCollisionException){
                    error = "Esta conta ja foi cadastrada"
                } catch (e: FirebaseAuthInvalidCredentialsException){
                    error = "E-mail invalido"
                } catch (e: Exception){
                    error = "Erro ao cadastrar usuario"
                }

                val snackbar: Snackbar = Snackbar.make(view, error, Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.WHITE)
                snackbar.setTextColor(Color.BLACK)
                snackbar.show()
            }
        })
    }

    private fun saveUserData(){

        val name = registerActivityEditTextName.text.toString()

        auth = FirebaseAuth.getInstance()
        userID = auth.currentUser!!.uid

        val users = hashMapOf(
            "id" to userID,
            "name" to name,
            "photo" to "",
        )

        val documentReference: DocumentReference = db.collection("Users").document(userID)
        documentReference.set(users).addOnSuccessListener(OnSuccessListener {
            Log.d(TAG, "Success by saving the data")
        }).addOnFailureListener(OnFailureListener {
            Log.d(TAG, "Failure by saving the data ${it.toString()}")
        })
    }


}