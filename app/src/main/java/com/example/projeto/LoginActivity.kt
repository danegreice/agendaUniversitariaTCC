package com.example.projeto

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore

class LoginActivity : AppCompatActivity() {
    private var TAG = "MainActivity"
    lateinit var loginActivityButtonLogin: Button
    lateinit var loginActivityNoRegistration: TextView
    lateinit var loginActivityEditTextLogin: TextInputEditText
    lateinit var loginActivityEditTextPassword: TextInputEditText
    lateinit var auth: FirebaseAuth
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()

        loginActivityEditTextLogin = findViewById(R.id.loginActivityEditLogin)
        loginActivityEditTextPassword = findViewById(R.id.loginActivityEditPassword)
        loginActivityButtonLogin = findViewById(R.id.loginActivityButtonLogin)
        loginActivityNoRegistration = findViewById(R.id.loginActivityNoRegistration)


        loginActivityButtonLogin.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View) {
                val email: String = loginActivityEditTextLogin.text.toString()
                val password: String = loginActivityEditTextPassword.text.toString()

                if(email.isEmpty() || password.isEmpty()){
                    val snackbar: Snackbar = Snackbar.make(p0, "Preencha todos os campos", Snackbar.LENGTH_SHORT)
                    snackbar.setBackgroundTint(Color.WHITE)
                    snackbar.setTextColor(Color.BLACK)
                    snackbar.show()
                } else{
                    authenticateUser(email, password, p0)
                }
            }
        } )

        loginActivityNoRegistration.setOnClickListener(object: View.OnClickListener{
            override fun onClick(p0: View?){
                val intent = Intent(applicationContext, RegisterActivity::class.java)
                startActivity(intent)
                finish()
                }
            }
        )
    }

    private fun authenticateUser(email: String, password: String, view: View) {
        auth = FirebaseAuth.getInstance()

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(OnCompleteListener {
            if (it.isSuccessful){
                startActivity(Intent(applicationContext, HomeActivity::class.java))
                finish()
            }else{
                val error: String
                try{
                    throw it.exception!!
                } catch (e: Exception){
                    error = "Erro ao logar usuario"
                }

                val snackbar: Snackbar = Snackbar.make(view, error, Snackbar.LENGTH_SHORT)
                snackbar.setBackgroundTint(Color.WHITE)
                snackbar.setTextColor(Color.BLACK)
                snackbar.show()
            }
        })
    }

    public override fun onStart() {
        super.onStart()

        auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        if (currentUser != null){
            startActivity(Intent(applicationContext, HomeActivity::class.java))
            finish()
        }
    }

}