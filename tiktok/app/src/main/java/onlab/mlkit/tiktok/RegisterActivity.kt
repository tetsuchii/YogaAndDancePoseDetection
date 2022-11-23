package onlab.mlkit.tiktok

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    lateinit var regEmail : TextInputEditText
    lateinit var regPass : TextInputEditText
    lateinit var regBtn : Button
    lateinit var loginSwitch : TextView

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        regEmail=findViewById(R.id.etRegEmail)
        regPass=findViewById(R.id.etRegPass)
        regBtn=findViewById(R.id.regBtn)
        loginSwitch=findViewById(R.id.loginSwitch)

        auth = FirebaseAuth.getInstance()

        regBtn.setOnClickListener {
            createUser()
        }

        loginSwitch.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun createUser() {
        val email = regEmail.text.toString()
        val pass = regPass.text.toString()

        if(TextUtils.isEmpty(email)){
            regEmail.error = "Email cannot be empty"
            regEmail.requestFocus()
        }else if(TextUtils.isEmpty(pass)) {
            regPass.error = "Password cannot be empty"
            regPass.requestFocus()
        } else {
            auth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, LoginActivity::class.java))
                }else {
                    Toast.makeText(this,"Registration Error: " + it.exception!!.message,Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}