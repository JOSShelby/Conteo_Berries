package com.example.berriesconteo

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.android.volley.toolbox.Volley.newRequestQueue
import org.json.JSONArray
import org.json.JSONObject
import java.sql.Connection
import java.sql.DriverManager


class MainActivity : AppCompatActivity() {
    private lateinit var db: SQLiteDatabase
    fun insertarRegistros() {
        val a=1;
        val urlRegistros =
            "http://" + getString(R.string.servidor) + "/kudePOO/aplicacion/berries/php/insertarRegistros.php?a=$a";
        val queueResponsivas = newRequestQueue(this)
        val stringRequestResponsivas =
            StringRequest(Request.Method.GET, urlRegistros, { response ->
                val jsonRespuesta = JSONObject(response);
                println(jsonRespuesta);
            })
            {

            }
        queueResponsivas.add(stringRequestResponsivas)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        var arrModuloTitulos : MutableList<String>? = mutableListOf()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        NOS MANDA A LA PANTALLA DE CAPTURAR CUANDO PRESIONEMOS EL BOTON
        var btn: Button = findViewById(R.id.btnCapturar)
        btn.setOnClickListener {
            val intent = Intent(this, pantalla_capturar:: class.java)
            startActivity(intent)
        }
        var btn2: Button = findViewById(R.id.btnSubir)
        btn2.setOnClickListener{
            insertarRegistros()
        }
    }
}