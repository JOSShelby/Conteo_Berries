package com.agrizar.berriesconteo

import QRScannerFragment
import android.database.Cursor
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
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
//      ESTABLECE LA PANTALLA COMPLETA
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_capturar)

//      SI EL ESTADO DE GUARDADO ES NULLO ENTRA
        if (savedInstanceState == null) {
//          INICIA LA TRANSACCION
            supportFragmentManager.beginTransaction()
//              AGREGA UN FRAGMENTO A UNA ACTIVIDAD. SE INDICA DONDE SE COLOCARA EL FRAGMENTO
                .add(R.id.container_frame_layout, QRScannerFragment())
//              APLICA LOS CAMBIOS
                .commit()
        }

//      SE CREA UNA INSTANCIA, EVITA USAR EL findViewId
        binding = ActivityPantallaCapturarBinding.inflate(layoutInflater)
//      ESTABLECE EL CONTENIDO DE LA ACTIVIDAD
        setContentView(binding.root)

//      METEMOS LOS LAYOUTS EN VARIABLES USANDO LA INSTANCIA CREADA
        msgCorrecto = binding.mensajeCorrecto
        msgConfirmacion = binding.mensajeAlert

//      INDICA QUE SE PUEDE REPRODUCIR UN SOLO EFECTO AL MISMO TIEMPO
        val maxStreams = 1
//      SE CREA UN OBJETO CON ATRIBUTOS DEL SONIDO
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)  // INDICA QUE EL SONIDO SE UTILIZA COMO MUSICA O SONIDO EN SEGUNDO PLANO
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // INDICA QUE ES UN SONIDO DE NOTIFICACION O EFECTO DE SONIDO BREVE
            .build() // FINALIZA LA CONSTRUCCION DE LOS ATRIBUTOS
        soundPool = SoundPool.Builder()
            .setMaxStreams(maxStreams)// LE ASIGNA LA VARIABLE CREADA ANTERIORMENTE
            .setAudioAttributes(audioAttributes) // LE ASIGNA LOS ATRIBUTOS CREADOS ANTERIORMENTE
            .build() // FINALIZA LA CONSTRUCCION DEL OBJETO

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

//    SE CIERRAN LAS CONEXIONES
        cursorEstacion.close()
        cursorSector.close()
        cursorModulo.close()
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

//              CONSULTA PARA OBTENER LAS ULTIMAS 4 CUBETAS
                val queryCubetas = "SELECT * FROM cubetascontadasberries WHERE numero_empleado = $num_empleado ORDER BY idcubeta DESC LIMIT 4"
                val cursor = dbVer.rawQuery(queryCubetas, null)

//              CONTADOR DE REGISTROS
                val contadorCursor = cursor.count

//              VARIABLE PARA SABER QUE LAS ULTIMAS 4 CUBETAS FUERON 0 (EL 0 INDICA QUE AUN NO SON LAS 4 CUBETAS)
//              CUANDO YA SE CONTARON LAS 4 CUBETAS, EL 0 DE LAS ULTIMAS 4 CUBETAS SE VUELVE 1, INDICANDO QUE TIENE QUE PASAR 15 MINS PARA VOLVER A CONTAR
                var puntero = true

//              PUNTERO PARA SALIR DEL WHILE
                var punt = 0

//              BANDERA BOOLEAN PARA SABER CUANDO LA CUBETA ES PARTE DE OTRAS 4 ANTERIORES
                var bandera = 0

//              CICLO PARA OBTENER LOS RESULTADOS
                while (cursor.moveToNext()) {
                    val idcubeta = cursor.getInt(cursor.getColumnIndexOrThrow("idcubeta"))

//                  SI EL PUNTERO PARA SALIR DEL WHILE ES 0 ENTRA
                    if(punt == 0){
//                      OBTIENE LA BANDERA DEL REGISTRO PARA VER SI ES 0
                        bandera = cursor.getInt(cursor.getColumnIndexOrThrow("bandera"))
                    }else{
                        bandera = 1
                    }
                    val numempleado = cursor.getInt(cursor.getColumnIndexOrThrow("numero_empleado"))

//                  IMPRIME LOS RESULTADOS
                    println("EMPLEADO: $numempleado, IDCUB: $idcubeta, BANDERA: $bandera")

//                  SI LA BANDERA DEL REGISTRO ES 0, ES PORQUE AUN NO SE HAN COMPLETADO LAS 4 CUBETAS
                    if(bandera==0){
//                      EL PUNTERO SIGUE SIENDO 0
                        punt=0
                    }else{
//                      SI LA BANDERA YA TRAE UN 1, ES PORQUE AUN NO SE HAN CONTADO LAS 4
                        punt=1
                    }
                }

//              SI EL PUNTERO SE QUEDO EN 0 AL RECORRER PASAR EL WHILE, INDICA QUE SE PUEDEN ACTUALIZAR A 1 LOS REGISTROS
//              PARA INDICAR QUE YA SE CONTARON LOS ULTIMOS 4 REGISTROS
                if(punt==0){
//                  SI TRAE LOS 4 RESULTADOS ES PORQUE YA SON LAS 4 CUBETAS, SI TRAE MENOS ES PORQUE SON LOS PRIMEROS REGISTROS
                    if(contadorCursor==4){
//                      SE ACTUALIZAN A ESTADO 1 PARA SABER QUE YA SE TOMARON EN CUENTA PARA JUNTAR LAS 4 CUBETAS
                        val subconsulta = "(SELECT idcubeta FROM cubetascontadasberries WHERE numero_empleado = $num_empleado ORDER BY idcubeta DESC LIMIT 4)"
                        val queryActualizar = "UPDATE cubetascontadasberries SET status = 1 WHERE idcubeta IN $subconsulta"
                        dbVer.execSQL(queryActualizar)
                    }else{
//                      SI ENTRA AQUI ES PORQUE APENAS SON LOS PRIMEROS REGISTROS DEL COSECHADOR, EN RESUMEN, LLEVA MENOS DE 4 CUBETAS Y PUEDE INSERTAR OTRA
                        for (i in 1..seleccionCubetas){
                            val cadenaAgregarCubeta = "INSERT INTO cubetascontadasberries(fecha,moduloid,estacion,sector,numero_empleado,fruto,bandera) " +
                                    "VALUES('${dateFormat.format(calendar.time)}',$seleccionModulo,$seleccionEstacion,$seleccionSector,'$value',$seleccionFruto, 0)"
                            dbVer.execSQL(cadenaAgregarCubeta)
                        }
                    }
                }
                if(punt==1){
//                  ENTRA AQUI CUANDO LA CONSULTA SI TRAE LOS 4 ULTIMOS RESULTADOS PERO AUN SE PUEDE INSERTAR REGISTROS PARA COMPLETAS LAS 4 CUBETAS
                    for (i in 1..seleccionCubetas){
                        val cadenaAgregarCubeta = "INSERT INTO cubetascontadasberries(fecha,moduloid,estacion,sector,numero_empleado,fruto,bandera) " +
                                "VALUES('${dateFormat.format(calendar.time)}',$seleccionModulo,$seleccionEstacion,$seleccionSector,'$value',$seleccionFruto, 0)"
                        dbVer.execSQL(cadenaAgregarCubeta)
                    }
                }


//              OCULTA LA PANTALLA DE PREGUNTA
                msgConfirmacion.isVisible = false

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