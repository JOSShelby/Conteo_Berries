package com.example.berriesconteo

import QRScannerFragment
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.berriesconteo.databinding.ActivityPantallaCapturarBinding

class pantalla_capturar : AppCompatActivity() {


    private lateinit var binding: ActivityPantallaCapturarBinding

    private lateinit var dbBerries: DBBerries

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

        var seleccionModulo=0

        var spinnermodulo = binding.inpModulo
        var arrModuloTitulos : MutableList<String>? = mutableListOf()


        var dbBerries = DBBerries(applicationContext," DBBerries", null, 16);
        val db = dbBerries.readableDatabase

        val columns = arrayOf("idmodulo","nombremodulo")
        val cursorModulo: Cursor = db.query("modulosberries", columns, null, null, null, null, "idmodulo ASC")

        while (cursorModulo.moveToNext()) {

            val nombremodulo = cursorModulo.getString(cursorModulo.getColumnIndexOrThrow("nombremodulo"))
            println(nombremodulo)

            arrModuloTitulos!!.add(nombremodulo)
        }


        //spinner de sectores

        var seleccionSector=0


        var spinnerSector = binding.inpSector
        var arrSectorTitulos : MutableList<String>? = mutableListOf()

        val columnsSector = arrayOf("idsector","nombresector")
        val cursorSector: Cursor = db.query("sectoresberries", columnsSector, null, null, null, null, "idsector ASC")

        while (cursorSector.moveToNext()) {

            val nombreSector = cursorSector.getString(cursorSector.getColumnIndexOrThrow("nombresector"))
            println(nombreSector)
            arrSectorTitulos!!.add(nombreSector)
        }

        //spinner de estaciones

        var seleccionEstacion=0

        var spinnerEstacion = binding.inpEstacion
        var arrEstacionTitulos : MutableList<String>? = mutableListOf()

        val columnsEstacion = arrayOf("idestacion","nombreestacion")
        val cursorEstacion: Cursor = db.query("estacionberries", columnsEstacion, null, null, null, null, "idestacion ASC")

        while (cursorEstacion.moveToNext()) {

            val nombreEstacion = cursorEstacion.getString(cursorEstacion.getColumnIndexOrThrow("nombreestacion"))
            println(nombreEstacion)
            arrEstacionTitulos!!.add(nombreEstacion)
        }

        val columnsCubetas = arrayOf("fecha","moduloid","estacion","sector","numero_empleado","fruto","cubetas_contadas","status")
        val cursorCubetas: Cursor = db.query("cubetascontadasberries", columnsCubetas, null, null, null, null, "idcubeta ASC")
        while (cursorCubetas.moveToNext()) {

            val numero_empleado = cursorCubetas.getString(cursorCubetas.getColumnIndexOrThrow("numero_empleado"))
            println(numero_empleado)
            arrEstacionTitulos!!.add(numero_empleado)
        }

        cursorCubetas.close()
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
                seleccionSector = position
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Acciones cuando no se selecciona ningún elemento
            }
        }


    }
}