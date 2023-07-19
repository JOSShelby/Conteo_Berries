package com.example.berriesconteo

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class subirDatosBD:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.activity_main)

        val btnsubirDatos = findViewById<Button>(R.id.btnSubir)
        btnsubirDatos.setOnClickListener{
            exportarDatos()
        }
    }
    private fun exportarDatos() {
        val dbHandler = DBBerries((this)," DBBerries", null,9 )
        val data = dbHandler.obtenerDatosComoCSV()

        val csvFile = File(filesDir, "data.csv")
        csvFile.writeText(data)
    }
}