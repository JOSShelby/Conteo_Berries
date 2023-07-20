package com.example.berriesconteo

import android.database.Cursor
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class subirDatosBD:AppCompatActivity() {

    var arrModuloTitulos : MutableList<String>? = mutableListOf()

    var dbBerries = DBBerries(applicationContext," DBBerries", null, 9);
    val db = dbBerries.readableDatabase

    val columns = arrayOf("idmodulo","nombremodulo")

    val cursorModulo: Cursor = db.query("modulosberries", columns, null, null, null, null, "idmodulo ASC")

}