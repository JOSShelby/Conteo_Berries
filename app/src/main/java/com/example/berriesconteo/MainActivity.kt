package com.example.berriesconteo

import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        NOS MANDA A LA PANTALLA DE CAPTURAR CUANDO PRESIONEMOS EL BOTON
        var btn: Button = findViewById(R.id.btnCapturar)


        btn.setOnClickListener {
            val intent = Intent(this, pantalla_capturar:: class.java)
            startActivity(intent)
        }
    }
}