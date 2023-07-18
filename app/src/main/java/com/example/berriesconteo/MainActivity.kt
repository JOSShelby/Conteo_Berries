package com.example.berriesconteo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        NOS MANDA A LA PANTALLA DE CAPTURAR CUANDO PRESIONEMOS EL BOTON
        var btn: Button = findViewById(R.id.btnCapturar)
        btn.setOnClickListener {
            val intent: Intent = Intent(this, pantalla_capturar:: class.java)
            startActivity(intent)
        }
    }
}