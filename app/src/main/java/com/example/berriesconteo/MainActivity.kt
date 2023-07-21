package com.example.berriesconteo

import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.sql.DriverManager



class MainActivity : AppCompatActivity() {
    private lateinit var db: SQLiteDatabase
    fun insertarRegistros(arreglo: Array<String>):Int{

        val gson = Gson()
        val arr = gson.toJson(arreglo)
        val urlRegistros = "http://" + getString(R.string.servidor) + "/kudePOO/aplicacion/berries/php/insertarRegistros.php?array=$arr";
        val queueResponsivas = newRequestQueue(this)
        var statusCode = -1
        val stringRequestResponsivas = StringRequest(Request.Method.GET, urlRegistros, { response ->
            val jsonRespuesta = JSONObject(response);
            println(jsonRespuesta);
            statusCode = jsonRespuesta.getInt("statusCode")
        },{
            statusCode = -1
        })
        queueResponsivas.add(stringRequestResponsivas)
        return statusCode
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


//        MANDA LOS DATOS DE SQLITE A PHP
        var btn2: Button = findViewById(R.id.btnSubir)
        btn2.setOnClickListener{
            //        TRAE LAS CUBETAS
            var dbBerries = DBBerries(applicationContext," DBBerries", null, 1);
            val db = dbBerries.readableDatabase

            var arrEstacionTitulos : MutableList<String>? = mutableListOf()
            val columnsCubetas = arrayOf("fecha","moduloid","estacion","sector","numero_empleado","fruto","cubetas_contadas","status")
            val cursorCubetas: Cursor = db.query("cubetascontadasberries", columnsCubetas, null, null, null, null, "idcubeta ASC")

//          RECORRE LA BD
            while (cursorCubetas.moveToNext()) {

//              TRAE LA FECHA
                val fecha = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("fecha"))
//                println(fecha)
                arrEstacionTitulos!!.add(fecha)

//              TRAE EL MODULO
                val moduloid = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("moduloid"))
//                println(moduloid)
                arrEstacionTitulos!!.add(moduloid)

//              TRAE LA ESTACION
                val estacion = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("estacion"))
//                println(estacion)
                arrEstacionTitulos!!.add(estacion)

//              TRAE EL SECTOR
                val sector = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("sector"))
//                println(sector)
                arrEstacionTitulos!!.add(sector)

//              TRAE EL NUMERO DE EMPLEADO
                val numero_empleado = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("numero_empleado"))
//                println(numero_empleado)
                arrEstacionTitulos!!.add(numero_empleado)

//              TRAE EL FRUTO
                val fruto = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("fruto"))
//                println(fruto)
                arrEstacionTitulos!!.add(fruto)

//              TRAE LAS CUBETAS CONTADAS
                val cubetas_contadas = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("cubetas_contadas"))
//                println(cubetas_contadas)
                arrEstacionTitulos!!.add(cubetas_contadas)

//              TRAE LAS CUBETAS CONTADAS
                val status = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("status"))
//                println(status)
                arrEstacionTitulos!!.add(status)

                val separador = "||"
//                arrEstacionTitulos!!.add(separador)
//                println("-----------------------")
            }
                println(arrEstacionTitulos);
//            insertarRegistros(arrEstacionTitulos)
        }
    }
}