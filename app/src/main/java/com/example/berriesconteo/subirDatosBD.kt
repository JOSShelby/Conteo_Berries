package com.example.berriesconteo

import android.database.Cursor
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class subirDatosBD:AppCompatActivity() {

<<<<<<< HEAD
    var arrModuloTitulos : MutableList<String>? = mutableListOf()

    var dbBerries = DBBerries(applicationContext," DBBerries", null, 9);
    val db = dbBerries.readableDatabase

    val columns = arrayOf("idmodulo","nombremodulo")

    val cursorModulo: Cursor = db.query("modulosberries", columns, null, null, null, null, "idmodulo ASC")

=======
        val btnsubirDatos = findViewById<Button>(R.id.btnSubir)
        btnsubirDatos.setOnClickListener{
            exportarDatos()
        }
    }
    private fun exportarDatos() {
        val dbHandler = DBBerries((this)," DBBerries", null,9 )
        //val data = dbHandler.obtenerDatosComoCSV()

        //val csvFile = File(filesDir, "data.csv")
        //csvFile.writeText(data)
    }
>>>>>>> 0c94dae5a46a238d5e195b4c30d7404457760157
}