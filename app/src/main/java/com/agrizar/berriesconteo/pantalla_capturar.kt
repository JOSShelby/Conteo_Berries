package com.agrizar.berriesconteo

import QRScannerFragment
import android.database.Cursor
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.berriesconteo.R
import com.example.berriesconteo.databinding.ActivityPantallaCapturarBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class pantalla_capturar : AppCompatActivity(), QRScannerFragment.OnFragmentInteractionListener {

//  VARIABLES
    private lateinit var binding: ActivityPantallaCapturarBinding
    private lateinit var dbBerries: DBBerries
    private lateinit var msgCorrecto: RelativeLayout
    private lateinit var msgConfirmacion: RelativeLayout
    private lateinit var soundPool: SoundPool
    private var beepSoundId = 0
    var seleccionEstacion = 0
    var seleccionModulo = 0
    var seleccionSector = 0
    var seleccionFruto = 0
    var seleccionCubetas = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_capturar)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container_frame_layout, QRScannerFragment())
                .commit()
        }
        binding = ActivityPantallaCapturarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        msgCorrecto = binding.mensajeCorrecto
        msgConfirmacion = binding.mensajeAlert

        val maxStreams = 1
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()
        soundPool = SoundPool.Builder()
            .setMaxStreams(maxStreams)
            .setAudioAttributes(audioAttributes)
            .build()

//      CARGA EL SONIDO DE PIDIDO PARA ESCANEAR
        beepSoundId = soundPool.load(this, R.raw.beep, 1)

//      SPINNER DE MODULOS TRAIDO DE LA BD
        var spinnermodulo = binding.inpModulo
        var arrModuloTitulos : MutableList<String>? = mutableListOf()
        var dbBerries = DBBerries(applicationContext," DBBerries", null, R.string.versionBD);
        val db = dbBerries.readableDatabase
        val columns = arrayOf("idmodulo","nombremodulo")
        val cursorModulo: Cursor = db.query("modulosberries", columns, null, null, null, null, "idmodulo ASC")
        arrModuloTitulos!!.add("MODULO...")
        while (cursorModulo.moveToNext()) {
            val nombremodulo = cursorModulo.getString(cursorModulo.getColumnIndexOrThrow("nombremodulo"))
            println(nombremodulo)
            arrModuloTitulos!!.add(nombremodulo)
        }

        //SPINNER DE SECTORES TRAIDO DE LA BD
        var spinnerSector = binding.inpSector
        var arrSectorTitulos : MutableList<String>? = mutableListOf()
        val columnsSector = arrayOf("idsector","nombresector")
        val cursorSector: Cursor = db.query("sectoresberries", columnsSector, null, null, null, null, "idsector ASC")
        arrSectorTitulos!!.add("SECTOR...")
        while (cursorSector.moveToNext()) {
            val nombreSector = cursorSector.getString(cursorSector.getColumnIndexOrThrow("nombresector"))
            println(nombreSector)
            arrSectorTitulos!!.add(nombreSector)
        }

//       SPINNER DE ESTACIONES TRAIDO DE LA BD
        var spinnerEstacion = binding.inpEstacion
        var arrEstacionTitulos : MutableList<String>? = mutableListOf()
        val columnsEstacion = arrayOf("idestacion","nombreestacion")
        val cursorEstacion: Cursor = db.query("estacionberries", columnsEstacion, null, null, null, null, "idestacion ASC")
        arrEstacionTitulos!!.add("ESTACION...")
        while (cursorEstacion.moveToNext()) {
            val nombreEstacion = cursorEstacion.getString(cursorEstacion.getColumnIndexOrThrow("nombreestacion"))
            println(nombreEstacion)
            arrEstacionTitulos!!.add(nombreEstacion)
        }

//    SE CIERRAN
        cursorEstacion.close()
        cursorSector.close()
        cursorModulo.close()
//    SE CIERRA LA BD
        db.close()

//      ADAPTADOR PARA EL SPINNER DE MODULOS
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrModuloTitulos!!)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnermodulo.adapter = adapter
        spinnermodulo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                seleccionModulo = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
            }
        }

//      ADAPTADOR PARA EL SPINNER DE SECTORES
        val adapterSector = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrSectorTitulos!!)
        adapterSector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSector.adapter = adapterSector
        spinnerSector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                seleccionSector = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
            }
        }

//      ADAPTADOR PARA EL SPINNER DE ESTACIONES
        val adapterEstacion = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrEstacionTitulos!!)
        adapterSector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerEstacion.adapter = adapterEstacion
        spinnerEstacion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                seleccionEstacion = position
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
            }
        }

//      ADAPTADOR PARA EL SPINNER DE FRUTO
        val spinnerFruto = binding.inpFruto
        val arrFrutoTitulos : MutableList<String>? = mutableListOf()
        arrFrutoTitulos!!.add("FRUTO.....")
        arrFrutoTitulos!!.add("FRAMBUESA")
        arrFrutoTitulos!!.add("ZARZAMORA")
        val adapterFruto = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrFrutoTitulos!!)
        adapterSector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFruto.adapter = adapterFruto
        spinnerFruto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0){
                    seleccionFruto = 0
                }
                if(position == 1){
                    seleccionFruto = 13
                }
                if(position == 2){
                    seleccionFruto = 14
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
            }
        }

//      ADAPTADOR PARA EL SPINNER DE CUBETAS
        val spinnerCubetas = binding.inpCubetas
        val arrCubetas : MutableList<Int>? = mutableListOf()
        arrCubetas!!.add(1)
        arrCubetas!!.add(2)
        arrCubetas!!.add(3)
        arrCubetas!!.add(4)
        val adapterCubeta = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrCubetas!!)
        adapterSector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCubetas.adapter = adapterCubeta
        spinnerCubetas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if(position == 0){
                    seleccionCubetas = 1
                }
                if(position == 1){
                    seleccionCubetas = 2
                }
                if(position == 2){
                    seleccionCubetas = 3
                }
                if(position == 3){
                    seleccionCubetas = 4
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
            }
        }
    }
    override fun onValueReturned(value: String) {
//      MUESTRA EL NUMERO DE EMPLEADO EN EL LABEL
        val num_empleadoMostrar = findViewById<TextView>(R.id.inpNumEmpleado)
        num_empleadoMostrar.text = "Número de empleado: $value"

//      TRAE EL CONTENIDO DEL XML ACTIVITY_PANTALLA_CAPTURAR
        val btnConfirmar = findViewById<Button>(R.id.botonConfirmacion)
        val btnRechazar = findViewById<Button>(R.id.botonCancelacion)

        println("$seleccionModulo,$seleccionEstacion,$seleccionSector,$seleccionFruto");

//      REPRODUCE EL SONIDO DE PITADO
        soundPool.play(beepSoundId, 1.0f, 1.0f, 0, 0, 1.0f)

//      VALIDA QUE LOS SPINNERS NO ESTEN VACIOS
        if(seleccionModulo!=0 && seleccionEstacion!=0 && seleccionSector!=0 && seleccionFruto!=0 ){

//          HACE VISIBLE EL ALERT PARA QUE SE ACEPTEN O NO LAS CUBETAS
            msgConfirmacion.isVisible=true

//          GUARDA EL NUMERO DE EMPLEADO EN UNA VARIABLE
            val num_empleado = value

//          OBTIENE LA FECHA Y HORA ACTUAL
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

//          SE DECLARAN VARIABLES PARA VER O INSERTAR EN LA BASE DE DATOS
            var dbBerries = DBBerries(applicationContext," DBBerries", null, R.string.versionBD);
            val db = dbBerries.writableDatabase
            val dbVer = dbBerries.readableDatabase

//          CONSULTA PARA OBTENER LAS CUBETAS QUE LLEVA EL COSECHADOR
            val conlumnsCubetas = arrayOf("fecha", "moduloid", "estacion", "sector", "numero_empleado", "fruto", "status")
            val seleccion = "numero_empleado = ?" //CONDICION WHERE
            val seleccionArgum = arrayOf(value) //VALOR DEL NUMERO DE EMPLEADO
            val cursorCubetas: Cursor = db.query("cubetascontadasberries", conlumnsCubetas, seleccion, seleccionArgum, null, null, "idcubeta ASC")
            val count = cursorCubetas.count

//          MUESTRA EL NUMERO DE CUBETAS CONTADAS POR ESE EMPLEADO
            val resultadoCount = findViewById<TextView>(R.id.mensajeConteoEmpleado)
            resultadoCount.text = "El empleado ha contado: $count cubetas"

//          MUESTRA EL NUMERO DE CUBETAS QUE SE LE VAN A AGREGAR
            val resultadoCubetasAgregadas = findViewById<TextView>(R.id.mensajeCubetasAgregar)
            resultadoCubetasAgregadas.text = "Se le agregarán: $seleccionCubetas cubetas"

//          EN ESPERA DE QUE SE LE DE CLICK AL BOTON DE CONFIRMAR CUBETAS
            btnConfirmar.setOnClickListener {
//              OCULTA LA PANTALLA DE PREGUNTA
                msgConfirmacion.isVisible = false
                for (i in 1..seleccionCubetas){
                    val cadenaAgregarCubeta = "INSERT INTO cubetascontadasberries(fecha,moduloid,estacion,sector,numero_empleado,fruto) " +
                            "VALUES('${dateFormat.format(calendar.time)}',$seleccionModulo,$seleccionEstacion,$seleccionSector,'$value',$seleccionFruto)"
                    dbVer.execSQL(cadenaAgregarCubeta)
                }
//              CIERRA LA BD AL TERMINAR EL CICLO FOR
                dbVer.close()
                db.close()

//              ANIMACION PARA APARECER EL MENSAJE DE EXITO DE INSERSION
                val fadeInAnimation = AlphaAnimation(5f, 1f)
                fadeInAnimation.duration = 1500 // DURACION EN MILISEGUNDOS

//              ANIMACION PARA ESPERAR
                val waitAnimation = AlphaAnimation(1f, 1f)
                fadeInAnimation.duration = 1000 // DURACION EN MILISEGUNDOS

//              ANIMACION PARA DESAPARECER EL MENSAJE DE EXITO DE INSERSION
                val fadeOutAnimation = AlphaAnimation(1f, 0f)
                fadeOutAnimation.duration = 500 // DURACION EN MILISEGUNDOS

//              SE PINTA EL MENSAJE DE EXITO DE INSERSION CON SU ANIMACION DE ENTRADA Y SALIDA
                msgCorrecto.startAnimation(fadeInAnimation)
                msgCorrecto.postDelayed({
                    msgCorrecto.startAnimation(fadeOutAnimation)
                },1000)
            }

//          EN ESPERA DE QUE SE LE DE CLICK AL BOTON DE RECHAZAR CUBETAS
            btnRechazar.setOnClickListener {
                msgConfirmacion.isVisible = false
            }
        }else{
            Toast.makeText(this ,"Ponga los Datos Minimos",Toast.LENGTH_SHORT).show()
        }
    }
}