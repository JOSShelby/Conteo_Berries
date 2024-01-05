package com.agrizar.berriesconteo

import QRScannerFragment
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.berriesconteo.Consultar
import com.example.berriesconteo.R
import com.example.berriesconteo.databinding.ActivityPantallaCapturarBinding
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar

class pantalla_capturar : AppCompatActivity(), QRScannerFragment.OnFragmentInteractionListener {

    //  VARIABLES
    private lateinit var binding: ActivityPantallaCapturarBinding
    private lateinit var msgCorrecto: RelativeLayout
    private lateinit var msgConfirmacion: RelativeLayout
    private lateinit var imgCorrecto: ImageView
    private lateinit var txtCorrecto: TextView
    private lateinit var soundPool: SoundPool
    private lateinit var cajaEscribeEscaner: EditText
    private var beepSoundId = 0
    private var seleccionEstacion = 0
    private var seleccionModulo = 0
    private var seleccionSector = 0
    private var seleccionFruto = 0
    private var seleccionCubetas = 1
    private var seleccionVariedad = 0

    var seleccionLote = 0

    private val CAMERA_PERMISSION_CODE = 101

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El usuario concedió el permiso, puedes acceder a la cámara aquí
                println("permiso concedido")
            } else {
                // El usuario denegó el permiso, toma medidas adecuadas (mostrar mensaje, deshabilitar funciones, etc.)
                println("permiso no concedido")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_capturar)

//        SE CREA UNA INSTANCIA, EVITA USAR EL findViewId
        binding = ActivityPantallaCapturarBinding.inflate(layoutInflater)

//      ESTABLECE EL CONTENIDO DE LA ACTIVIDAD
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_DENIED
        ) {
            // Si el permiso no está concedido, solicítalo
            ActivityCompat.requestPermissions(
                this,
                arrayOf(android.Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            // Si ya se concedió el permiso, accede a la cámara aquí
            println("Permiso de la cámara ya concedido")
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container_frame_layout, QRScannerFragment())
                .commit()
        }

        val btnCheckBuckets = binding.btnCheckBuckets

        btnCheckBuckets.setOnClickListener {
            val intent = Intent(this, Consultar::class.java)
            startActivity(intent)
        }

//      ESTABLECE LA PANTALLA COMPLETA
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

//      BUSCA EL EDITTEXT DONDE SE COLOCARA EL NUMERO DE EMPLEADO CON EL ESCANER EXTERNO
        this.cajaEscribeEscaner = binding.CajaEscribeEscaner

//      DETECTARA CUANDO SE DE UN ENTER EN EL EDITTEXT
        cajaEscribeEscaner.addTextChangedListener(object : TextWatcher {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun afterTextChanged(s: Editable?) {
                // VERIFICA SI EL CARACTER DE NUEVA LINEA ('\n') ESTA PRESENTE EN EL TEXTO
                if (s?.toString()?.contains('\n') == true) {

                    addNewBuckets(s.toString())
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // NO SE UTILIZA EN ESTE EJEMPLO
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // NO SE UTILIZA EN ESTE EJEMPLO
            }
        })

//      METEMOS 1S LAYOUTS EN VARIABLES USANDO LA INSTANCIA CREADA
        msgCorrecto = binding.mensajeCorrecto
        msgConfirmacion = binding.mensajeAlert
        txtCorrecto = binding.txtCorrecto
        imgCorrecto = binding.imgCorrecto

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
        val spinnerLugar = binding.inpLugar
        val arrLugar: MutableList<String> = mutableListOf()

        arrLugar.add("LUGAR...")
        arrLugar.add("AGZ3")
        arrLugar.add("AGZ1")

//      ADAPTADOR PARA EL SPINNER DE FRUTO
        val spinnerFruto = binding.inpFruto
        val arrFrutoTitulos: MutableList<String> = mutableListOf()
        arrFrutoTitulos.add("FRUTO.....")
        arrFrutoTitulos.add("FRAMBUESA")
        arrFrutoTitulos.add("ZARZAMORA")
        val adapterFruto =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, arrFrutoTitulos)
        spinnerFruto.adapter = adapterFruto

        //      ADAPTADOR PARA EL SPINNER DE FRUTO
        val spinnerVariedades = binding.inpVariedades




        spinnerFruto.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                val arrVariedadesTitulos: MutableList<String> = mutableListOf()
                if (position == 0) {
                    seleccionFruto = 0
                }
                if (position == 1) {
                    println("entre aqui 1")
                    seleccionFruto = 13

                    arrVariedadesTitulos.add("VARIEDADES.....")
                    arrVariedadesTitulos.add("SAPPHIRE")
                    arrVariedadesTitulos.add("DJ")
                    val adapterVariedades =
                        ArrayAdapter(
                            this@pantalla_capturar,
                            android.R.layout.simple_spinner_item,
                            arrVariedadesTitulos
                        )
                    spinnerVariedades.adapter = adapterVariedades

                    spinnerVariedades.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                if (position == 0) {
                                    seleccionVariedad = 0
                                }
                                if (position == 1) {
                                    seleccionVariedad = 1
                                }
                                if (position == 2) {
                                    seleccionVariedad = 2
                                }

                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
                            }
                        }

                }
                if (position == 2) {
                    seleccionFruto = 14
                    arrVariedadesTitulos.add("VARIEDADES.....")
                    arrVariedadesTitulos.add("TP04")
                    val adapterVariedades =
                        ArrayAdapter(
                            this@pantalla_capturar,
                            android.R.layout.simple_spinner_item,
                            arrVariedadesTitulos
                        )
                    spinnerVariedades.adapter = adapterVariedades
                    spinnerVariedades.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                if (position == 0) {
                                    seleccionVariedad = 0
                                }

                                if (position == 1) {
                                    seleccionVariedad = 3
                                }

                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
                            }
                        }
                }

                if (position == 1 || position == 2) {
                    spinnerVariedades.visibility = View.VISIBLE
                } else {
                    spinnerVariedades.visibility = View.INVISIBLE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
            }
        }

//      ADAPTADOR PARA EL SPINNER DE CUBETAS
        val spinnerCubetas = binding.inpCubetas
        val arrCubetas: MutableList<Int> = mutableListOf()
        arrCubetas.add(1)
        arrCubetas.add(2)
        arrCubetas.add(3)
        arrCubetas.add(4)
        val adapterCubeta = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrCubetas)

        spinnerCubetas.adapter = adapterCubeta
        spinnerCubetas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    seleccionCubetas = 1
                }
                if (position == 1) {
                    seleccionCubetas = 2
                }
                if (position == 2) {
                    seleccionCubetas = 3
                }
                if (position == 3) {
                    seleccionCubetas = 4
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
            }
        }

        val adapterLugar = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrLugar)
        adapterLugar.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLugar.adapter = adapterLugar
        spinnerLugar.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                var aparecerSpinners = false;

                if (position == 0) {
                    aparecerSpinners = false;
                }
                if (position == 1 || position == 2) {
                    aparecerSpinners = true;

                    if (position == 1) {
                        seleccionLote = 1
                    } else {
                        seleccionLote = 2
                    }

                }

                //SPINNER DE MODULOS TRAIDO DE LA BD
                val spinnermodulo = binding.inpModulo
                val arrModuloTitulos: MutableList<String> = mutableListOf()
                val dbBerries =
                    DBBerries(applicationContext, " DBBerries", null, R.string.versionBD);
                val db = dbBerries.readableDatabase

                val queryModulo =
                    "SELECT idmodulo,nombremodulo FROM modulosberries WHERE idmodulo in (SELECT idmodulo FROM relacionmodulolote WHERE idlote = $seleccionLote )  ORDER BY idmodulo ASC"
                val cursorModulo = db.rawQuery(queryModulo, null)

                arrModuloTitulos.add("MODULO...")
                while (cursorModulo.moveToNext()) {
                    val nombremodulo =
                        cursorModulo.getString(cursorModulo.getColumnIndexOrThrow("nombremodulo"))
                    println(nombremodulo)
                    arrModuloTitulos.add(nombremodulo)
                }

                //SPINNER DE SECTORES TRAIDO DE LA BD
                val spinnerSector = binding.inpSector
                val arrSectorTitulos: MutableList<String> = mutableListOf()

                val querySector =
                    "SELECT idsector,nombresector FROM sectoresberries WHERE idsector in (SELECT idsector FROM relacionsectorlote WHERE idlote = $seleccionLote )  ORDER BY idsector ASC"
                val cursorSector = db.rawQuery(querySector, null)

                arrSectorTitulos.add("SECTOR...")
                while (cursorSector.moveToNext()) {
                    val nombreSector =
                        cursorSector.getString(cursorSector.getColumnIndexOrThrow("nombresector"))
                    println(nombreSector)
                    arrSectorTitulos.add(nombreSector)
                }

//       SPINNER DE ESTACIONES TRAIDO DE LA BD
                val spinnerEstacion = binding.inpEstacion
                val arrEstacionTitulos: MutableList<String> = mutableListOf()

                val queryEstacion =
                    "SELECT idestacion,nombreestacion FROM estacionberries WHERE idestacion in (SELECT idestacion FROM relacionestacionlote WHERE idlote = $seleccionLote )  ORDER BY idestacion ASC"
                val cursorEstacion = db.rawQuery(queryEstacion, null)


                arrEstacionTitulos.add("ESTACION...")
                while (cursorEstacion.moveToNext()) {
                    val nombreEstacion =
                        cursorEstacion.getString(cursorEstacion.getColumnIndexOrThrow("nombreestacion"))
                    println(nombreEstacion)
                    arrEstacionTitulos.add(nombreEstacion)
                }

//    SE CIERRAN LAS CONEXIONES
                cursorEstacion.close()
                cursorSector.close()
                cursorModulo.close()
                db.close()

//      ADAPTADOR PARA EL SPINNER DE MODULOS
                val adapter = ArrayAdapter(
                    this@pantalla_capturar,
                    android.R.layout.simple_spinner_item,
                    arrModuloTitulos
                )
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnermodulo.adapter = adapter
                spinnermodulo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        seleccionModulo = position
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
                    }
                }

//      ADAPTADOR PARA EL SPINNER DE SECTORES
                val adapterSector = ArrayAdapter(
                    this@pantalla_capturar,
                    android.R.layout.simple_spinner_item,
                    arrSectorTitulos
                )
                adapterSector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerSector.adapter = adapterSector
                spinnerSector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        seleccionSector = position
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
                    }
                }

//      ADAPTADOR PARA EL SPINNER DE ESTACIONES
                val adapterEstacion = ArrayAdapter(
                    this@pantalla_capturar,
                    android.R.layout.simple_spinner_item,
                    arrEstacionTitulos
                )
                adapterSector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerEstacion.adapter = adapterEstacion
                spinnerEstacion.onItemSelectedListener =
                    object : AdapterView.OnItemSelectedListener {
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            seleccionEstacion = position
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
                        }
                    }

                spinnerCubetas.isVisible = aparecerSpinners
                spinnerFruto.isVisible = aparecerSpinners
                spinnerEstacion.isVisible = aparecerSpinners
                spinnerSector.isVisible = aparecerSpinners
                spinnermodulo.isVisible = aparecerSpinners

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)

    fun addNewBuckets(Nempleado: String) {
        //REALIZA LA ACCION AL DETECTAR EL SALTO DE LINEA
        val scannedText = Nempleado.trim() // OBTIENE EL TEXTO SIN ESPACIOS

//                  BORRA EL CONTENIDO ESCANEADO DEL EDITTEXT PARA QUE SE MANTENGA VACIO
        cajaEscribeEscaner.setText("")

        //      MUESTRA EL NUMERO DE EMPLEADO EN EL LABEL
        val num_empleadoMostrar = findViewById<TextView>(R.id.inpNumEmpleado)
        num_empleadoMostrar.text = "Número de empleado: $scannedText"

//      TRAE EL CONTENIDO DEL XML ACTIVITY_PANTALLA_CAPTURAR
        val btnConfirmar = findViewById<Button>(R.id.botonConfirmacion)
        val btnRechazar = findViewById<Button>(R.id.botonCancelacion)

        // println("$seleccionModulo,$seleccionEstacion,$seleccionSector,$seleccionFruto");

//      REPRODUCE EL SONIDO DE PITADO
        soundPool.play(beepSoundId, 1.0f, 1.0f, 0, 0, 1.0f)

//      VALIDA QUE LOS SPINNERS NO ESTEN VACIOS
        if (seleccionModulo != 0 && seleccionEstacion != 0 && seleccionSector != 0 && seleccionFruto != 0) {

//          HACE VISIBLE EL ALERT PARA QUE SE ACEPTEN O NO LAS CUBETAS
            msgConfirmacion.isVisible = true

//          GUARDA EL NUMERO DE EMPLEADO EN UNA VARIABLE
            val num_empleado = scannedText

//          OBTIENE LA FECHA Y HORA ACTUAL
            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

//          SE DECLARAN VARIABLES PARA VER O INSERTAR EN LA BASE DE DATOS
            val dbBerries = DBBerries(applicationContext, " DBBerries", null, R.string.versionBD);
            val db = dbBerries.writableDatabase
            val dbVer = dbBerries.readableDatabase

//          CONSULTA PARA OBTENER LAS CUBETAS QUE LLEVA EL COSECHADOR
            val conlumnsCubetas = arrayOf(
                "fecha",
                "moduloid",
                "estacion",
                "sector",
                "numero_empleado",
                "fruto",
                "status"
            )
            val seleccion = "numero_empleado = ?" //CONDICION WHERE
            val seleccionArgum = arrayOf(scannedText) //VALOR DEL NUMERO DE EMPLEADO
            val cursorCubetas: Cursor = db.query(
                "cubetascontadasberries",
                conlumnsCubetas,
                seleccion,
                seleccionArgum,
                null,
                null,
                "idcubeta ASC"
            )
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
                val queryCubetas =
                    "SELECT * FROM cubetascontadasberries WHERE numero_empleado = '$num_empleado' AND bandera = 0 "
                val cursor = dbVer.rawQuery(queryCubetas, null)

//              CONTADOR DE REGISTROS
                val contadorCursor = cursor.count

                val sumaTotalCubetas = cursor.count + seleccionCubetas

//              VARIABLE PARA SABER QUE LAS ULTIMAS 4 CUBETAS FUERON 0 (EL 0 INDICA QUE AUN NO SON LAS 4 CUBETAS)
//              CUANDO YA SE CONTARON LAS 4 CUBETAS, EL 0 DE LAS ULTIMAS 4 CUBETAS SE VUELVE 1, INDICANDO QUE TIENE QUE PASAR 15 MINS PARA VOLVER A CONTAR

//              PUNTERO PARA SALIR DEL WHILE


//              BANDERA BOOLEAN PARA SABER CUANDO LA CUBETA ES PARTE DE OTRAS 4 ANTERIORES
                var bandera = 0
                var contCuatroMas = 0

                if (contadorCursor == 4) {

                    bandera = 1;// la cuenta de cubetas esta en el limite

                }

                if (sumaTotalCubetas > 4) {

                    contCuatroMas = 1 // la seccion y el conteo son mas de 4
                }

                if (bandera == 1) {

                    //CONSULTA PARA OBTENER LAS ULTIMA hora de su ultimo registro
                    val queryCubetas =
                        "SELECT idcubeta,fecha FROM cubetascontadasberries WHERE numero_empleado = '$num_empleado' AND bandera = 0 ORDER BY idcubeta DESC LIMIT 1"
                    val cursor = dbVer.rawQuery(queryCubetas, null)


                    while (cursor.moveToNext()) {
                        val fecha = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))

                        val inputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        val outputFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

                        // Convertir la cadena de fecha en LocalDateTime
                        val inputDateTime = LocalDateTime.parse(fecha, inputFormat)

                        // Sumar 15 minutos
                        val newDateTime = inputDateTime.plus(10, ChronoUnit.MINUTES)

                        val currentDateTime = LocalDateTime.now()

                        if (currentDateTime > newDateTime) {

                            val subconsulta =
                                "(SELECT idcubeta FROM cubetascontadasberries WHERE numero_empleado = '$num_empleado' AND bandera = 0 )"
                            val queryActualizar =
                                "UPDATE cubetascontadasberries SET status = 1 , bandera = 1 WHERE idcubeta IN $subconsulta"
                            dbVer.execSQL(queryActualizar)


                            for (i in 1..seleccionCubetas) {
                                val cadenaAgregarCubeta =
                                    "INSERT INTO cubetascontadasberries(fecha,moduloid,estacion,sector,numero_empleado,fruto,variedad,bandera) " +
                                            "VALUES('${dateFormat.format(calendar.time)}',$seleccionModulo,$seleccionEstacion,$seleccionSector,'$scannedText',$seleccionFruto,$seleccionVariedad, 0)"
                                dbVer.execSQL(cadenaAgregarCubeta)
                            }
                            pantallaMensaje(1)
                        } else {
                            txtCorrecto.text =
                                "Error ya fueron pitadas 4 cubetas al empleado $num_empleado \n la ultima cubeta fue pitada a las $fecha \n podra volver a pitar el empleado dentro de 10 min. "
                            pantallaMensaje(3)
                        }
                    }
                } else {
                    if (contCuatroMas == 1) {
                        println("la suma del contador y las cubetas registradas da mas de 4")
                        //funcion para traer el mensaje
                        pantallaMensaje(2)
                    } else {

                        for (i in 1..seleccionCubetas) {
                            val cadenaAgregarCubeta =
                                "INSERT INTO cubetascontadasberries(fecha,moduloid,estacion,sector,numero_empleado,fruto,variedad,bandera) " +
                                        "VALUES('${dateFormat.format(calendar.time)}',$seleccionModulo,$seleccionEstacion,$seleccionSector,'$scannedText',$seleccionFruto,$seleccionVariedad, 0)"
                            dbVer.execSQL(cadenaAgregarCubeta)
                        }
                        //funcion para traer el mensaje
                        pantallaMensaje(1)
                    }
                }
//              OCULTA LA PANTALLA DE PREGUNTA
                msgConfirmacion.isVisible = false

//              CIERRA LA BD AL TERMINAR EL CICLO FOR
                dbVer.close()
                db.close()
            }

//          EN ESPERA DE QUE SE LE DE CLICK AL BOTON DE RECHAZAR CUBETAS
            btnRechazar.setOnClickListener {
                msgConfirmacion.isVisible = false
            }
        } else {
            Toast.makeText(applicationContext, "Ponga los Datos Minimos", Toast.LENGTH_SHORT).show()
            num_empleadoMostrar.setText("Número de empleado: ")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onValueReturned(value: String) {
        addNewBuckets(value)
    }

    fun pantallaMensaje(tipo: Int) {

        var delay: Long = 0;

        //mensaje de correcto
        if (tipo == 1) {
            txtCorrecto.text = "Registrado Correctamente"
            imgCorrecto.setImageResource(R.drawable.correcto)
            delay = 1;

        }
        //mensaje de mas de 4 cubetas que fueron intentadas meter
        if (tipo == 2) {
            txtCorrecto.text = "Error se trato de meter mas de 4 cubetas "
            imgCorrecto.setImageResource(R.drawable.error)
            delay = 3;
        }
        //mensaje de mas de 4 cubetas que fueron intentadas meter
        if (tipo == 3) {

            imgCorrecto.setImageResource(R.drawable.error)
            delay = 3;

        }

//              ANIMACION PARA APARECER EL MENSAJE DE EXITO DE INSERSION
        val fadeInAnimation = AlphaAnimation(5f, 1f)
        fadeInAnimation.duration = 1500 * delay // DURACION EN MILISEGUNDOS

//              ANIMACION PARA ESPERAR
        fadeInAnimation.duration = 1000 * delay// DURACION EN MILISEGUNDOS

//              ANIMACION PARA DESAPARECER EL MENSAJE DE EXITO DE INSERSION
        val fadeOutAnimation = AlphaAnimation(1f, 0f)
        fadeOutAnimation.duration = 500 * delay // DURACION EN MILISEGUNDOS

        //SE PINTA EL MENSAJE DE EXITO DE INSERSION CON SU ANIMACION DE ENTRADA Y SALIDA
        msgCorrecto.startAnimation(fadeInAnimation)
        msgCorrecto.postDelayed({
            msgCorrecto.startAnimation(fadeOutAnimation)
        }, 1000)
    }
}