package com.agrizar.berriesconteo

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.berriesconteo.Consultar
import com.example.berriesconteo.R
import com.google.gson.Gson

class MainActivity : AppCompatActivity(), dialogPermiso.Resultado {
//  VARIABLES
    private lateinit var db: SQLiteDatabase
    private lateinit var btnSubirDatos:LinearLayout
    private lateinit var txtSubir:TextView
    private lateinit var imgSubido:LinearLayout

    override fun onResume() {
        super.onResume()
//      LEE LA BASE DE DATOS
        val dbBerries = DBBerries(applicationContext," DBBerries", null, R.string.versionBD);
        val db = dbBerries.readableDatabase
        val columnsCubetas = arrayOf("idcubeta")
        val cursorCubetas: Cursor = db.query("cubetascontadasberries", columnsCubetas, null, null, null, null, "idcubeta ASC")

//      MUESTRA EN EL BOTON DE SUBIR UN CONTEO DE LOS REGISTROS GENERALES
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

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnSubirDatos = findViewById(R.id.btnSubir)
        txtSubir = findViewById(R.id.txtSubir)
        imgSubido = findViewById(R.id.imgSubido)

//        NOS MANDA A LA PANTALLA DE CAPTURAR CUANDO PRESIONEMOS EL BOTON
        val btn: LinearLayout = findViewById(R.id.btnCapturar)
        btn.setOnClickListener {
            val intent = Intent(this, pantalla_capturar:: class.java)
            startActivity(intent)
        }
//      LEE LA BASE DE DATOS
        val dbBerries = DBBerries(applicationContext," DBBerries", null, R.string.versionBD);
        val db = dbBerries.readableDatabase

//      NOS MANDA A LA PANTALLA DE CONSULTA CUANDO PRESIONEMOS EL BOTON
        val btn2: LinearLayout = findViewById(R.id.btnConsultar)
        btn2.setOnClickListener {
            val intent = Intent(this, Consultar:: class.java)
            startActivity(intent)
        }

//        MANDA LOS DATOS DE SQLITE A PHP AL PRESIONAR EL BOTON DE SUBIR
        btnSubirDatos.setOnClickListener{
            //        TRAE LAS CUBETAS
            val arrEstacionTitulos : MutableList<String>? = mutableListOf()
            val arrCont : MutableList<MutableList<String>>? = mutableListOf()
            val columnsCubetas = arrayOf("fecha","moduloid","estacion","sector","numero_empleado","fruto")
            val cursorCubetas: Cursor = db.query("cubetascontadasberries", columnsCubetas, null, null, null, null, "idcubeta ASC")

//          RECORRE LA BD SI HAY REGISTROS EN LA BASE DE DATOS
            if(cursorCubetas.count!=0){
                while (cursorCubetas.moveToNext()) {
//                  SE DECLARA LA MUTABLELIST
                    var arrayDatos:MutableList<String>? =  mutableListOf()

//                  TRAE LOS REGISTROS DE LA COLUMNA FECHA
                    val fecha = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("fecha"))
                    arrayDatos!!.add(fecha)

//                  TRAE LOS REGISTROS DE LA COLUMNA MODULO
                    val moduloid = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("moduloid"))
                    arrayDatos!!.add(moduloid)

//                  TRAE LOS REGISTROS DE LA COLUMNA ESTACION
                    val estacion = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("estacion"))
                    arrayDatos!!.add(estacion)

//                  TRAE LOS REGISTROS DE LA COLUMNA SECTOR
                    val sector = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("sector"))
                    arrayDatos!!.add(sector)

//                  TRAE LOS REGISTROS DE LA COLUMNA NUMERO DE EMPLEADO
                    val numero_empleado = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("numero_empleado"))
                    arrayDatos!!.add(numero_empleado)

//                  TRAE LOS REGISTROS DE LA COLUMNA FRUTO
                    val fruto = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("fruto"))
                    arrayDatos!!.add(fruto)

//                  METE LOS ARREGLOS QUE SE FORMARON DE LAS COLUMNAS, DENTRO DE UN ARREGLO
                    arrCont!!.add(arrayDatos)
                }
//              CONVIERTE EL ARREGLO A JSON
                val gson = Gson()
                val jsonArreglo = gson.toJson(arrCont)

                if (isNetworkAvailable()) {
//                  SE EJECUTA SI HAY CONEXION A INTERNET
                    var dialogopermiso = dialogPermiso();
                    val args = Bundle()
                    args.putString("jsonArreglo", jsonArreglo)
                    dialogopermiso.setArguments(args)
                    dialogopermiso.show(supportFragmentManager, "titulo")
                }else{
//                  SE EJECUTA SI NO HAY CONEXION A INTERNET
                    Toast.makeText(this,"No hay internet disponible",Toast.LENGTH_SHORT).show()
                }
            }else{
//              SE EJECUTA SI NO HAY REGISTROS EN LA BASE DE DATOS
                Toast.makeText(this,"No se encuentra ningun registro",Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun Resultado(resultado: Boolean) {
        txtSubir.text = "SUBIR: 0"
//      ANIMACION DE APARICION
        val fadeInAnimation = AlphaAnimation(5f, 1f)
        fadeInAnimation.duration = 3000 // DURACION EN MILISEGUNDOS

        val waitAnimation = AlphaAnimation(1f, 1f)
        fadeInAnimation.duration = 1000 // DURACION EN MILISEGUNDOS

//      ANIMACION DE SALIDA
        val fadeOutAnimation = AlphaAnimation(1f, 0f)
        fadeOutAnimation.duration = 500 // DURACION EN MILISEGUNDOS

//      SE EJECUTA LA ANIMACION
        imgSubido.startAnimation(fadeInAnimation)
        imgSubido.postDelayed({
            imgSubido.startAnimation(fadeOutAnimation)
        },1000)
    }
}

