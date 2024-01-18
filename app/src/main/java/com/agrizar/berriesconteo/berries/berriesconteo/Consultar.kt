package com.agrizar.berriesconteo.berries.berriesconteo

import android.content.pm.ActivityInfo
import android.database.Cursor
import android.os.Bundle
import android.view.Gravity
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.agrizar.berriesconteo.DBBerries
import com.agrizar.berriesconteo.R
import com.google.android.material.textfield.TextInputEditText
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanIntentResult
import com.journeyapps.barcodescanner.ScanOptions

class Consultar : AppCompatActivity() {
    private lateinit var imageButton: ImageButton
    override fun onCreate(savedInstanceState: Bundle?) {

//      COLOCA LA PANTALLA COMPLETA EN EL DISPOSITIVO
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

//      EVITA QUE SE ROTE LA PANTALLA
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_consultar)


//      TRAE EL LINEARLAYOUT
        val linearlistaCosechadores: LinearLayout = findViewById(R.id.linearlistaCosechadores)
//      TRAE EL LINEARLAYOUT DE LOS TITULOS
        val linearTituloTabla: LinearLayout = findViewById(R.id.linearTituloTabla)
        val nEmpleado: TextInputEditText = findViewById(R.id.txtEmpleado)

//      VARIABLE QUE SE LE ASIGNA EL BOTON
        imageButton = findViewById(R.id.botonBuscarCosechadores)
        val btnLector: ImageButton = findViewById(R.id.btnCodeBar)

        fun getEmployee(nEmpleado: String) {
            linearlistaCosechadores.removeAllViewsInLayout()
            linearTituloTabla.isVisible = true

//          SE DECLARAN VARIABLES PARA VER O INSERTAR EN LA BASE DE DATOS
            val dbBerries = DBBerries(applicationContext, " DBBerries", null, R.string.versionBD);
            val db = dbBerries.writableDatabase
            val dbVer = dbBerries.readableDatabase

//          CONSULTA PARA OBTENER TODOS LOS COSECHADORES EXISTENTES EN LA BASE DE DATOS
            val conlumnsCubetas = arrayOf("numero_empleado", "COUNT(*) AS num_registros")
            val cursorCubetas: Cursor = db.query(
                "cubetascontadasberries",
                conlumnsCubetas,
                "numero_empleado = ?",
                arrayOf(nEmpleado),
                "numero_empleado",
                null,
                null
            )

//          RECORREMOS LOS RESULTADOS DE LA BUSQUEDA DE LA BD
            while (cursorCubetas.moveToNext()) {
                val numEmp =
                    cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("numero_empleado"))
                val numCub =
                    cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("num_registros"))

//              SE CREA UN LAYOUT PARA METER LOS RESULTADOS
                val linearLayoutRegistro = LinearLayout(this);
                linearLayoutRegistro.layoutParams =
                    LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT,
                    )
                linearLayoutRegistro.setPadding(0, 10, 0, 0)
                linearLayoutRegistro.weightSum = 2f
                linearLayoutRegistro.orientation = LinearLayout.HORIZONTAL

//              PINTAMOS LOS RESULTADOS DE NUMERO DE EMPLEADO EN EL LAYOUT
                val txtNumEmpleado = TextView(this)
                txtNumEmpleado.text = numEmp
                txtNumEmpleado.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                txtNumEmpleado.setBackgroundResource(R.drawable.celdas)
                txtNumEmpleado.textSize = 18f
                val llpNumEmpleado = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f,
                )
                txtNumEmpleado.gravity = Gravity.CENTER_HORIZONTAL
                llpNumEmpleado.setMargins(0, 0, 0, 0);
                txtNumEmpleado.layoutParams = llpNumEmpleado
                linearLayoutRegistro.addView(txtNumEmpleado)

//              PINTAMOS LOS RESULTADOS DE LAS CUBETAS EN EL LAYOUT
                val txtCubetas = TextView(this)
                txtCubetas.text = numCub
                txtCubetas.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                txtCubetas.setBackgroundResource(R.drawable.celdas)
                txtCubetas.textSize = 18f
                val llpCubetas = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    1f
                )
                txtCubetas.gravity = Gravity.CENTER_HORIZONTAL
                llpCubetas.setMargins(0, 0, 0, 0);
                txtCubetas.layoutParams = llpCubetas
                linearLayoutRegistro.addView(txtCubetas)

//              METEMOS LOS LINEAR LAYOUT EN EL LAYOUT DEL XML QUE MANDAMOS LLAMAR
                linearlistaCosechadores.addView(linearLayoutRegistro)
            }

//          CERRAMOS CONEXIONES
            cursorCubetas.close()
            db.close()
            dbVer.close()
        }

        val lectorcodigo = registerForActivityResult(ScanContract()) { result: ScanIntentResult ->
            if (result.contents == null) {
                Toast.makeText(this, "Se cancelo el escaneo", Toast.LENGTH_LONG).show()
            } else {
                getEmployee(result.contents)
            }
        }

        btnLector.setOnClickListener {
            val options = ScanOptions()
            options.setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES)
            options.setPrompt("escanne el codigo");
            options.setCameraId(0)
            options.setOrientationLocked(true);
            options.setBarcodeImageEnabled(true);
            lectorcodigo.launch(options);
        }

//      ACCION QUE REALIZARA EL BOTON AL DARLE CLICK
        imageButton.setOnClickListener {
            getEmployee(nEmpleado.text.toString())
        }
    }
}