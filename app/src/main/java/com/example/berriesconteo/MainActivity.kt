package com.example.berriesconteo

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley.newRequestQueue
import com.google.gson.Gson
import org.json.JSONArray
import org.json.JSONObject
import java.sql.DriverManager



class MainActivity : AppCompatActivity() {
    private lateinit var db: SQLiteDatabase

    private lateinit var btnSubirDatos:LinearLayout
    private lateinit var txtSubir:TextView
    private lateinit var imgSubido:LinearLayout
    private lateinit var cargaDatos:ProgressBar
    fun insertarRegistros(json: String){

//        RUTA EN PRUEBAS
        val urlRegistros = "http://" + getString(R.string.servidor) + "/kudePOO/aplicacion/apps/berries/insertarRegistros.php?array=$json";

//        RUTA EN MI LOCAL JOSS
//        val urlRegistros = "http://" + getString(R.string.servidor) + "/kudePOO/aplicacion/berries/php/insertarRegistros.php?array=$json";

        val queueResponsivas = newRequestQueue(this)
        var statusCode = -1
        val stringRequestResponsivas = StringRequest(Request.Method.GET, urlRegistros, { response ->
            val jsonRespuesta = JSONObject(response);
           statusCode = jsonRespuesta.getInt("statusCode")

            if(statusCode==1){

                var dbBerries = DBBerries(applicationContext," DBBerries", null, R.string.versionBD);

                val db = dbBerries.writableDatabase

                db.execSQL("DELETE FROM cubetascontadasberries")

                Toast.makeText(this,"Se subieron los registros Exitosamente",Toast.LENGTH_SHORT).show()

                txtSubir.text = "SUBIR : 0"


                cargaDatos.visibility = View.GONE

                // Define la animación de aparición
                val fadeInAnimation = AlphaAnimation(5f, 1f)
                fadeInAnimation.duration = 3000 // Duración en milisegundos

                val waitAnimation = AlphaAnimation(1f, 1f)
                fadeInAnimation.duration = 1000 // Duración en milisegundos

                // Define la animación de desaparición
                val fadeOutAnimation = AlphaAnimation(1f, 0f)
                fadeOutAnimation.duration = 500 // Duración en milisegundos

                imgSubido.startAnimation(fadeInAnimation)
                imgSubido.postDelayed({
                    imgSubido.startAnimation(fadeOutAnimation)
                },1000)

            }else{
                Toast.makeText(this,"ocurrio un error",Toast.LENGTH_SHORT).show()
            }
        },{
            Toast.makeText(this,"ocurrio un error en la conexion",Toast.LENGTH_SHORT).show()
        })
        queueResponsivas.add(stringRequestResponsivas)

    }

    override fun onResume() {
        super.onResume()

        var dbBerries = DBBerries(applicationContext," DBBerries", null, R.string.versionBD);
        val db = dbBerries.readableDatabase

        val columnsCubetas = arrayOf("idcubeta")
        val cursorCubetas: Cursor = db.query("cubetascontadasberries", columnsCubetas, null, null, null, null, "idcubeta ASC")

        txtSubir.text = "SUBIR : ${cursorCubetas.count}"
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // Utilizar NetworkCapabilities para versiones de Android 6.0 (Marshmallow) y posteriores
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNetwork = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

            return actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {
            // Utilizar la versión anterior de la API para versiones de Android anteriores a 6.0 (Marshmallow)
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSubirDatos = findViewById(R.id.btnSubir)
        txtSubir = findViewById(R.id.txtSubir)

        imgSubido = findViewById(R.id.imgSubido)

        cargaDatos = findViewById<ProgressBar>(R.id.cargaSubir)


//        NOS MANDA A LA PANTALLA DE CAPTURAR CUANDO PRESIONEMOS EL BOTON
        var btn: LinearLayout = findViewById(R.id.btnCapturar)
        btn.setOnClickListener {
            val intent = Intent(this, pantalla_capturar:: class.java)
            startActivity(intent)
        }
        var dbBerries = DBBerries(applicationContext," DBBerries", null, R.string.versionBD);
        val db = dbBerries.readableDatabase

//        MANDA LOS DATOS DE SQLITE A PHP

        btnSubirDatos.setOnClickListener{
            //        TRAE LAS CUBETAS


            cargaDatos.visibility = View.VISIBLE




            var arrEstacionTitulos : MutableList<String>? = mutableListOf()
            var arrCont : MutableList<MutableList<String>>? = mutableListOf()
            val columnsCubetas = arrayOf("fecha","moduloid","estacion","sector","numero_empleado","fruto")
            val cursorCubetas: Cursor = db.query("cubetascontadasberries", columnsCubetas, null, null, null, null, "idcubeta ASC")

            println("Numeros de cubetas ${cursorCubetas.count}");
//          RECORRE LA BD

            if(cursorCubetas.count!=0){
            while (cursorCubetas.moveToNext()) {

                var arrayDatos:MutableList<String>? =  mutableListOf()


//              TRAE LA FECHAs
                val fecha = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("fecha"))
//                println(fecha)
                arrayDatos!!.add(fecha)

//              TRAE EL MODULO
                val moduloid = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("moduloid"))
//                println(moduloid)
                arrayDatos!!.add(moduloid)

//              TRAE LA ESTACION
                val estacion = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("estacion"))
//                println(estacion)
                arrayDatos!!.add(estacion)

//              TRAE EL SECTOR
                val sector = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("sector"))
//                println(sector)
                arrayDatos!!.add(sector)

//              TRAE EL NUMERO DE EMPLEADO
                val numero_empleado = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("numero_empleado"))
//                println(numero_empleado)
                arrayDatos!!.add(numero_empleado)

//              TRAE EL FRUTO
                val fruto = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("fruto"))
//                println(fruto)
                arrayDatos!!.add(fruto)

                arrCont!!.add(arrayDatos)
//                println(arrCont)

//                println("-----------------------")
            }
            val gson = Gson()
            val jsonArreglo = gson.toJson(arrCont)
//                println(jsonArreglo);


                if (isNetworkAvailable()) {
                    // Hay conexión a Internet
                    insertarRegistros(jsonArreglo)
                } else {
                    // No hay conexión a Internet
                    Toast.makeText(this,"No hay internet disponible",Toast.LENGTH_SHORT).show()
                }




        }else{
            cargaDatos.visibility = View.GONE
            Toast.makeText(this,"No se encuentra ningun registro",Toast.LENGTH_SHORT).show()
        }
        }
    }
}

