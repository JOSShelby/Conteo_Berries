package com.example.berriesconteo

import android.app.backup.BackupAgentHelper
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

class pantalla_capturar : AppCompatActivity() {

    lateinit var edtFecha: EditText
    lateinit var edtEstacion: EditText
    lateinit var edtSector: EditText
    lateinit var edtInvernadero: EditText
    lateinit var edtNumeroEmpleado: EditText
    lateinit var edtFruto: EditText
    lateinit var edtCubetas: EditText
    lateinit var btnAgregar: Button


    private lateinit var dbBerries: DBBerries

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_capturar)

        edtFecha = findViewById(R.id.inpFecha)
        edtEstacion = findViewById(R.id.inpEstacion)
        edtSector = findViewById(R.id.inpSector)
        edtInvernadero = findViewById(R.id.inpInvenadero)
        edtNumeroEmpleado = findViewById(R.id.inpNumEmpleado)
        edtFruto = findViewById(R.id.inpFruto)
        edtCubetas = findViewById(R.id.inpCubetas)
        btnAgregar = findViewById(R.id.btnGuardar)

        dbBerries = DBBerries(applicationContext," DBBerries", null, 1);

        btnAgregar.setOnClickListener{
            dbBerries.agregarCubetas(edtFecha.text.toString(), edtEstacion.text.toString(), edtSector.text.toString(), edtInvernadero.text.toString(), edtNumeroEmpleado.text.toString().toInt(), edtFruto.text.toString(), edtCubetas.text.toString().toInt())
            Toast.makeText(applicationContext, "Se agrego con exito", Toast.LENGTH_SHORT).show()
        }
    }
}