package com.agrizar.berriesconteo

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.berriesconteo.R
import com.google.gson.Gson


class MainActivity : AppCompatActivity(), dialogPermiso.Resultado {
    private lateinit var db: SQLiteDatabase

    private lateinit var btnSubirDatos:LinearLayout
    private lateinit var txtSubir:TextView
    private lateinit var imgSubido:LinearLayout

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
                    var dialogopermiso = dialogPermiso();
                    val args = Bundle()
                    args.putString("jsonArreglo", jsonArreglo)

                    dialogopermiso.setArguments(args)

                    //Toast.makeText(this,pallet,Toast.LENGTH_SHORT).show();
                    dialogopermiso.show(supportFragmentManager, "titulo")

                    //insertarRegistros(jsonArreglo)
                } else {
                    // No hay conexión a Internet
                    Toast.makeText(this,"No hay internet disponible",Toast.LENGTH_SHORT).show()
                }




        }else{

            Toast.makeText(this,"No se encuentra ningun registro",Toast.LENGTH_SHORT).show()
        }
        }
    }

    override fun Resultado(resultado: Boolean) {
        txtSubir.text = "SUBIR : 0"


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
    }
}

