package com.example.berriesconteo

import QRScannerFragment
import android.database.Cursor
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.berriesconteo.databinding.ActivityPantallaCapturarBinding
import java.text.SimpleDateFormat
import java.util.Calendar

class pantalla_capturar : AppCompatActivity(), QRScannerFragment.OnFragmentInteractionListener {


    private lateinit var binding: ActivityPantallaCapturarBinding

    private lateinit var dbBerries: DBBerries

    private lateinit var txtEmpleadoid: EditText

    var seleccionEstacion=0
    var seleccionModulo=0
    var seleccionSector=0
    var seleccionFruto = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_capturar)


        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container_frame_layout, QRScannerFragment())
                .commit()
        }

        binding = ActivityPantallaCapturarBinding.inflate(layoutInflater)
        setContentView(binding.root)


        txtEmpleadoid = binding.inpNumEmpleado





        var spinnermodulo = binding.inpModulo
        var arrModuloTitulos : MutableList<String>? = mutableListOf()


        var dbBerries = DBBerries(applicationContext," DBBerries", null, 9);
        val db = dbBerries.readableDatabase

        val columns = arrayOf("idmodulo","nombremodulo")
        val cursorModulo: Cursor = db.query("modulosberries", columns, null, null, null, null, "idmodulo ASC")
        arrModuloTitulos!!.add("MODULO...")
        while (cursorModulo.moveToNext()) {

            val nombremodulo = cursorModulo.getString(cursorModulo.getColumnIndexOrThrow("nombremodulo"))
            println(nombremodulo)

            arrModuloTitulos!!.add(nombremodulo)
        }


        //spinner de sectores




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

        //spinner de estaciones



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



        cursorEstacion.close()
        cursorSector.close()
        cursorModulo.close()
        db.close()


        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrModuloTitulos!!)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnermodulo.adapter = adapter


        spinnermodulo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                seleccionModulo = position



            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Acciones cuando no se selecciona ningún elemento
            }
        }


        val adapterSector = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrSectorTitulos!!)
        adapterSector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerSector.adapter = adapterSector


        spinnerSector.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                seleccionSector = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Acciones cuando no se selecciona ningún elemento
            }
        }



        val adapterEstacion = ArrayAdapter(this, android.R.layout.simple_spinner_item, arrEstacionTitulos!!)
        adapterSector.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinnerEstacion.adapter = adapterEstacion


        spinnerEstacion.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                seleccionEstacion = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Acciones cuando no se selecciona ningún elemento
            }
        }


        var spinnerFruto = binding.inpFruto
        var arrFrutoTitulos : MutableList<String>? = mutableListOf()

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
                // Acciones cuando no se selecciona ningún elemento
            }
        }





    }

    override fun onValueReturned(value: String) {

        if(seleccionModulo!=0 && seleccionEstacion!=0 && seleccionSector!=0 && seleccionFruto!=0 ){

            txtEmpleadoid.setText(value)

            val calendar = Calendar.getInstance()
            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

            var dbBerries = DBBerries(applicationContext," DBBerries", null, 9);

            val db = dbBerries.writableDatabase

            val dbVer = dbBerries.readableDatabase

            val cadenaAgregarCubeta =
                "INSERT INTO cubetascontadasberries(fecha,moduloid,estacion,sector,numero_empleado,fruto) VALUES('${dateFormat.format(calendar.time)}',$seleccionModulo,$seleccionEstacion,$seleccionSector,'$value',$seleccionFruto)"

            dbVer.execSQL(cadenaAgregarCubeta)

            val columnsEstacion = arrayOf("idcubeta","fecha")
            val cursorEstacion: Cursor = dbVer.query("cubetascontadasberries", columnsEstacion, null, null, null, null, null)


            while (cursorEstacion.moveToNext()) {

                val idcubeta = cursorEstacion.getString(cursorEstacion.getColumnIndexOrThrow("idcubeta"))
                println(idcubeta)

                val fecha = cursorEstacion.getString(cursorEstacion.getColumnIndexOrThrow("fecha"))
                println(fecha)

            }

            cursorEstacion.close()
            dbVer.close()
            db.close()

            Toast.makeText(this ,"Registrada Nueva Cubeta",Toast.LENGTH_SHORT).show()


        }else{
            Toast.makeText(this ,"Ponga los Datos Minimos",Toast.LENGTH_SHORT).show()
        }


    }
}