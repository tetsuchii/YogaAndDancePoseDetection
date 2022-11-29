package onlab.mlkit.tiktok

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import onlab.mlkit.tiktok.databinding.ActivityLoginBinding
import onlab.mlkit.tiktok.databinding.ActivityMainBinding

class LoginActivity : AppCompatActivity() {
    lateinit var viewBinding: ActivityLoginBinding
    lateinit var logEmail : TextInputEditText
    lateinit var  logPass : TextInputEditText
    lateinit var registerSwitch : TextView
    lateinit var logBtn : Button

    lateinit var auth : FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        val animDrawable = viewBinding.rootLayout.background as AnimationDrawable
        animDrawable.setEnterFadeDuration(10)
        animDrawable.setExitFadeDuration(5000)
        animDrawable.start()

        logEmail=findViewById(R.id.etLogEmail)
        logPass=findViewById(R.id.etLogPass)
        logBtn=findViewById(R.id.logBtn)
        registerSwitch=findViewById(R.id.registerSwitch)

        auth = FirebaseAuth.getInstance()

        logBtn.setOnClickListener {
            loginUser()
        }

        registerSwitch.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun loginUser() {
        val email = logEmail.text.toString()
        val pass = logPass.text.toString()

        if(TextUtils.isEmpty(email)){
            logEmail.error = "Email cannot be empty"
            logEmail.requestFocus()
        }else if(TextUtils.isEmpty(pass)) {
            logPass.error = "Password cannot be empty"
            logPass.requestFocus()
        } else {
            auth.signInWithEmailAndPassword(email,pass).addOnCompleteListener {
                if(it.isSuccessful){
                    Toast.makeText(this, "User logged in successfully", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                }else {
                    Toast.makeText(this,"Log in Error: " + it.exception!!.message, Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}