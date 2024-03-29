package com.agrizar.berriesconteo

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.agrizar.berriesconteo.berries.berriesconteo.Consultar
import com.agrizar.berriesconteo.R
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import org.json.JSONException
import org.json.JSONObject

class MainActivity : AppCompatActivity(), dialogPermiso.Resultado , dialogAutorizacion.checkAuthorization{
    //  VARIABLES
    private lateinit var db: SQLiteDatabase
    private lateinit var btnSubirDatos: LinearLayout
    private lateinit var txtSubir: TextView
    private lateinit var txtCell: TextView
    private lateinit var imgSubido: LinearLayout
    private lateinit var linearMenu: LinearLayout
    private lateinit var progressMenu: ProgressBar

    override fun onResume() {
        super.onResume()
//      LEE LA BASE DE DATOS
        val dbBerries = DBBerries(applicationContext, " DBBerries", null, R.string.versionBD);
        val db = dbBerries.readableDatabase
        val columnsCubetas = arrayOf("idcubeta")
        val cursorCubetas: Cursor = db.query(
            "cubetascontadasberries",
            columnsCubetas,
            null,
            null,
            null,
            null,
            "idcubeta ASC"
        )

//      MUESTRA EN EL BOTON DE SUBIR UN CONTEO DE LOS REGISTROS GENERALES
        txtSubir.text = "SUBIR : ${cursorCubetas.count}"
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

//      COMPRUEBA LA VERSION DEL SISTEMA OPERATIVO DEL ANDROID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//          SI LA VERSION ES 6.0 O SUPERIOR, SE UTILIZA NETWORKCAPABILITIES PARA CONOCER LA CONEXION ACTIVA
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
//          PROPORCIONA INFORMACION SOBRE LOS ATRIBUTOS DE LA RED (WIFI, DATOS)
            val actNetwork =
                connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false

//          RETORNA SI ES DATOS MOVILES O WIFI SI ES TRUE
            return actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                    actNetwork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        } else {

//          SE UTILIZA UNA API MAS ANTIGUA SI LA VERSION ES ANTERIOR A 6.0

//          OBTIENE LA CONEXION ACTIVA ACTUAL
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false

//          SI HAY CONEXION RETORNA TRUE
            return networkInfo.isConnected
        }
    }

    fun pruebasInsert(numCont: Int){
        val dbB = DBBerries(applicationContext, " DBBerries", null, R.string.versionBD);
        val dbVer = dbB.readableDatabase

        for (i in 0..numCont) {
            val cadenaAgregarCubeta =
                "INSERT INTO cubetascontadasberries(fecha,moduloid,estacion,sector,numero_empleado,fruto,variedad,bandera,loteid) " +
                        "VALUES('2024-02-20',1,1,1,'49019',13,1, 0,1)"
            dbVer.execSQL(cadenaAgregarCubeta)
        }

        dbVer.close()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

//      ESTABLECE LA PANTALLA COMPLETA
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        linearMenu = findViewById(R.id.linearMenu)
        progressMenu = findViewById(R.id.progressMenu)

        //pruebasInsert(2000)

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val idCell = sharedPreferences.getInt("idCell", 0)

        if (idCell == 0) {
            addNewCell() { _ ->
                println("registrado");
                val idCell = sharedPreferences.getInt("idCell", 0)
                txtCell.text = "Cell $idCell"
            }
        }

        btnSubirDatos = findViewById(R.id.btnSubir)
        txtSubir = findViewById(R.id.txtSubir)
        txtCell = findViewById(R.id.txtCell)
        imgSubido = findViewById(R.id.imgSubido)
        val btnSelectSectors: LinearLayout = findViewById(R.id.btnSelectSectors)

        txtCell.text = "Cell $idCell"

//      NOS MANDA A LA PANTALLA DE CAPTURAR CUANDO PRESIONEMOS EL BOTON
        val btn: LinearLayout = findViewById(R.id.btnCapturar)
        btn.setOnClickListener {
            val intent = Intent(this, pantalla_capturar::class.java)
            startActivity(intent)
        }

//      LEE LA BASE DE DATOS
        val dbBerries = DBBerries(applicationContext, " DBBerries", null, R.string.versionBD);
        val db = dbBerries.readableDatabase

//      NOS MANDA A LA PANTALLA DE CONSULTA CUANDO PRESIONEMOS EL BOTON
        val btn2: LinearLayout = findViewById(R.id.btnConsultar)
        btn2.setOnClickListener {
            val intent = Intent(this, Consultar::class.java)
            startActivity(intent)
        }

        btnSelectSectors.setOnClickListener {

            var dialogopermiso = dialogAutorizacion();
            dialogopermiso.show(supportFragmentManager, "titulo")

        }

//      MANDA LOS DATOS DE SQLITE A PHP AL PRESIONAR EL BOTON DE SUBIR
        btnSubirDatos.setOnClickListener {
//          TRAE LAS CUBETAS
            val arrEstacionTitulos: MutableList<String>? = mutableListOf()
            val arrCont: MutableList<MutableList<String>>? = mutableListOf()
            val columnsCubetas =
                arrayOf("fecha", "moduloid", "estacion", "sector", "numero_empleado", "fruto", "variedad","loteid")
            val cursorCubetas: Cursor = db.query(
                "cubetascontadasberries",
                columnsCubetas,
                null,
                null,
                null,
                null,
                "idcubeta ASC"
            )

//          RECORRE LA BD SI HAY REGISTROS EN LA BASE DE DATOS
            if (cursorCubetas.count != 0) {
                while (cursorCubetas.moveToNext()) {
//                  SE DECLARA LA MUTABLELIST
                    var arrayDatos: MutableList<String>? = mutableListOf()

//                  TRAE LOS REGISTROS DE LA COLUMNA FECHA
                    val fecha =
                        cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("fecha"))
                    arrayDatos!!.add(fecha)

//                  TRAE LOS REGISTROS DE LA COLUMNA MODULO
                    val moduloid =
                        cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("moduloid"))
                    arrayDatos!!.add(moduloid)

//                  TRAE LOS REGISTROS DE LA COLUMNA ESTACION
                    val estacion =
                        cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("estacion"))
                    arrayDatos!!.add(estacion)

//                  TRAE LOS REGISTROS DE LA COLUMNA SECTOR
                    val sector =
                        cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("sector"))
                    arrayDatos!!.add(sector)

//                  TRAE LOS REGISTROS DE LA COLUMNA NUMERO DE EMPLEADO
                    val numero_empleado =
                        cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("numero_empleado"))
                    arrayDatos!!.add(numero_empleado)

//                  TRAE LOS REGISTROS DE LA COLUMNA FRUTO
                    val fruto =
                        cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("fruto"))
                    arrayDatos!!.add(fruto)

                    //                  TRAE LOS REGISTROS DE LA COLUMNA VARIEDAD
                    val variedad =
                        cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("variedad"))
                    arrayDatos!!.add(variedad)

                    val loteid =
                        cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("loteid"))
                    arrayDatos!!.add(loteid)

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
                    args.putString("arrCont", arrCont?.count().toString())
                    dialogopermiso.setArguments(args)
                    dialogopermiso.show(supportFragmentManager, "titulo")
                } else {
//                  SE EJECUTA SI NO HAY CONEXION A INTERNET
                    Toast.makeText(this, "No hay internet disponible", Toast.LENGTH_SHORT).show()
                }
            } else {

//              SE EJECUTA SI NO HAY REGISTROS EN LA BASE DE DATOS
                Toast.makeText(this, "No se encuentra ningun registro", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun checkAuthorization(resultado: Boolean){
        if(resultado){
            val intent = Intent(this, selectionSectors::class.java)
            startActivity(intent)
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
        }, 1000)
    }

    private fun addNewCell(callback: (Boolean) -> Unit) {

        val url =
            "http://" + getString(R.string.servidor) + "/kudePOO/aplicacion/apps/berries/addNewCell.php";
        val queue = Volley.newRequestQueue(this)
        val socketTimeout = 1200000 // 20 minutos en milisegundos

        var statusCode = -1

        linearMenu.visibility = View.GONE
        progressMenu.visibility = View.VISIBLE

        val stringRequest = object : StringRequest(
            Method.GET, url,
            Response.Listener { response ->
                try {
                    val jsonRespuesta = JSONObject(response)
                    statusCode = jsonRespuesta.getInt("statusCode")
                    val idCell = jsonRespuesta.getInt("idCell")
                    if (statusCode == 1) {
                        val sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(this)
                        val editor = sharedPreferences.edit()
                        editor.putInt("idCell", idCell)
                        editor.apply()
                        linearMenu.visibility = View.VISIBLE

                        progressMenu.visibility = View.GONE
                        callback(true)

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    linearMenu.visibility = View.VISIBLE
                    progressMenu.visibility = View.GONE
                    callback(false)
                }
            },
            Response.ErrorListener { error ->
                // Manejar el error de la solicitud
                Toast.makeText(this, "Ocurrió un error en la conexión", Toast.LENGTH_SHORT)
                    .show()
                callback(false)
            }
        ) {}

        stringRequest.retryPolicy =
            DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queue.add(stringRequest)
    }
}

