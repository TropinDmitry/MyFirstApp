package com.tsu.myfirstapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnFocusChangeListener
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SignActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)
    }

    fun splashPage(view: View){
        val intent = Intent(this, SplashScreenActivity::class.java)
        startActivity(intent)
    }

    fun navigationPage(view: View){

        var et_user_name = findViewById(R.id.nameEdit) as EditText
        var et_email = findViewById(R.id.emailEdit) as EditText
        var et_password = findViewById(R.id.passwordEdit) as EditText

        val user_name = et_user_name.text;
        val password = et_password.text;
        val email = et_email.text;

        if (user_name.isNullOrEmpty() || password.isNullOrEmpty() || email.isNullOrEmpty()) {
            Toast.makeText(this@SignActivity, "Error, missing text", Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent(this, NavigationActivity::class.java)
            startActivity(intent)
        }
    }

}
