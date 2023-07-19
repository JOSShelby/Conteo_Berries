package com.example.berriesconteo

import android.app.backup.BackupAgentHelper
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.berriesconteo.databinding.ActivityPantallaCapturarBinding

class pantalla_capturar : AppCompatActivity() {


    private lateinit var binding: ActivityPantallaCapturarBinding

    private lateinit var dbBerries: DBBerries

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_capturar)

        binding = ActivityPantallaCapturarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var seleccionModulo=0

        var spinnermodulo = binding.inpModulo
        var arrModuloTitulos : MutableList<String>? = mutableListOf()


        var dbBerries = DBBerries(applicationContext," DBBerries", null, 9);
        val db = dbBerries.readableDatabase

        val columns = arrayOf("idmodulo","nombremodulo")




        val cursorModulo: Cursor = db.query("modulosberries", columns, null, null, null, null, "idmodulo ASC")



        while (cursorModulo.moveToNext()) {

            val nombremodulo = cursorModulo.getString(cursorModulo.getColumnIndexOrThrow("nombremodulo"))
            println(nombremodulo)

            arrModuloTitulos!!.add(nombremodulo)
        }

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


    }
}