package com.agrizar.berriesconteo

import android.annotation.SuppressLint
import android.database.sqlite.SQLiteDatabase
import android.util.Log

class subirDatosBD(db: SQLiteDatabase) {
    @SuppressLint("Range")
    fun showDataFromCubetasContadas(db: SQLiteDatabase) {
        val cursor = db.rawQuery("SELECT * FROM cubetascontadasberries", null)
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndex("idcubeta"))
            val fecha = cursor.getString(cursor.getColumnIndex("fecha"))
            val moduloId = cursor.getInt(cursor.getColumnIndex("moduloid"))
            val estacion = cursor.getInt(cursor.getColumnIndex("estacion"))
            val sector = cursor.getInt(cursor.getColumnIndex("sector"))
            val numeroEmpleado = cursor.getInt(cursor.getColumnIndex("numero_empleado"))
            val fruto = cursor.getString(cursor.getColumnIndex("fruto"))
            val variedad = cursor.getString(cursor.getColumnIndex("variedad"))
            val status = cursor.getInt(cursor.getColumnIndex("status"))

//          IMPRIMIR LOS DATOS EN LOGCAT
            Log.d(
                "Datos",
                "ID: $id, Fecha: $fecha, Modulo ID: $moduloId, Estacion: $estacion, Sector: $sector, Empleado: $numeroEmpleado, Fruto: $fruto, Status: $status"
            )
        }
        cursor.close()
    }
}