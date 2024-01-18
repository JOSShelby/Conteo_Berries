package com.agrizar.berriesconteo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.Toast
import com.agrizar.berriesconteo.R
import com.agrizar.berriesconteo.databinding.ActivityPantallaCapturarBinding
import com.agrizar.berriesconteo.databinding.ActivitySelectionSectorsBinding

class selectionSectors : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionSectorsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection_sectors)
        binding = ActivitySelectionSectorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var loteid = 0
        var moduloid = 0

        val spinnerLugar = binding.inpLugar

        val arrLugarTitulos: MutableList<String> = mutableListOf()
        arrLugarTitulos.add("LUGAR.....")
        arrLugarTitulos.add("AGZ3")
        arrLugarTitulos.add("AGZ1")
        val adapterLugar =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, arrLugarTitulos)
        spinnerLugar.adapter = adapterLugar

        val dbBerries =
            DBBerries(applicationContext, " DBBerries", null, R.string.versionBD);
        val db = dbBerries.readableDatabase



        spinnerLugar.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val spinnerModulo = binding.inpModulo
                    val arrModulos: MutableList<String> = mutableListOf()
                    if (position == 1) {
                        loteid = 1
                        arrModulos.add("MODULO.....")
                        arrModulos.add("MOD1")
                        arrModulos.add("MOD2")
                        arrModulos.add("MOD3")


                    }
                    if (position == 2) {
                        loteid = 2
                        arrModulos.add("MODULO.....")
                        arrModulos.add("MOD1")
                    }

                    val adapterModulo =
                        ArrayAdapter(
                            this@selectionSectors,
                            android.R.layout.simple_spinner_item,
                            arrModulos
                        )
                    spinnerModulo.adapter = adapterModulo

                    spinnerModulo.onItemSelectedListener =
                        object : AdapterView.OnItemSelectedListener {
                            override fun onItemSelected(
                                parent: AdapterView<*>?,
                                view: View?,
                                position: Int,
                                id: Long
                            ) {
                                val spinnerEstacion = binding.inpEstacion
                                val arrEstacion: MutableList<String> = mutableListOf()
                                val arrIdRelation: MutableList<Int> = mutableListOf()

                                val dbWrite = dbBerries.readableDatabase

                                moduloid = position

                                val queryEstacion =
                                    "SELECT idestacion,nombreestacion FROM estacionberries WHERE idestacion in (SELECT idestacion FROM relacionlotemoduloestacionsector WHERE idlote = $loteid and idmodulo = $moduloid )  ORDER BY idestacion ASC"
                                val cursorSector = db.rawQuery(queryEstacion, null)

                                arrEstacion.add("Estacion...")
                                arrIdRelation.add(0)
                                while (cursorSector.moveToNext()) {
                                    val nombreEstacion =
                                        cursorSector.getString(cursorSector.getColumnIndexOrThrow("nombreestacion"))
                                    arrEstacion.add(nombreEstacion)
                                    val idestacion =
                                        cursorSector.getInt(cursorSector.getColumnIndexOrThrow("idestacion"))
                                    arrIdRelation.add(idestacion)
                                }

                                val adapterEstacion =
                                    ArrayAdapter(
                                        this@selectionSectors,
                                        android.R.layout.simple_spinner_item,
                                        arrEstacion
                                    )
                                spinnerEstacion.adapter = adapterEstacion

                                spinnerEstacion.onItemSelectedListener =
                                    object : AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(
                                            parent: AdapterView<*>?,
                                            view: View?,
                                            position: Int,
                                            id: Long
                                        ) {
                                            val linearSectors = binding.sectores

                                            linearSectors.removeAllViews()

                                            if (position != 0) {

                                                val queryAllSectors =
                                                    "SELECT idsector,nombresector FROM sectoresberries"
                                                val cursorAllSectors =
                                                    db.rawQuery(queryAllSectors, null)
                                                while (cursorAllSectors.moveToNext()) {
                                                    val nombreSector =
                                                        cursorAllSectors.getString(
                                                            cursorAllSectors.getColumnIndexOrThrow(
                                                                "nombresector"
                                                            )
                                                        )
                                                    val idSector =
                                                        cursorAllSectors.getInt(
                                                            cursorAllSectors.getColumnIndexOrThrow(
                                                                "idsector"
                                                            )
                                                        )

                                                    val switchSector = Switch(this@selectionSectors)

                                                    val layoutParams = LinearLayout.LayoutParams(
                                                        LinearLayout.LayoutParams.WRAP_CONTENT, // Ancho wrap_content
                                                        LinearLayout.LayoutParams.WRAP_CONTENT  // Altura wrap_content (o el valor que desees)
                                                    )

                                                    layoutParams.setMargins(0, 16, 0, 0)

                                                    switchSector.layoutParams = layoutParams
                                                    switchSector.text = nombreSector

                                                    val querySectors =
                                                        "SELECT idsector,nombresector FROM sectoresberries WHERE idsector in (SELECT idsector FROM relacioncompletasector WHERE  idrelacion in (SELECT id FROM relacionlotemoduloestacionsector WHERE idestacion = ${arrIdRelation[position]} and idlote = $loteid ) )  ORDER BY idsector ASC"
                                                    val cursorSector =
                                                        db.rawQuery(querySectors, null)
                                                    while (cursorSector.moveToNext()) {
                                                        val idsectorFound = cursorSector.getInt(
                                                            cursorSector.getColumnIndexOrThrow("idsector")
                                                        )
                                                        if (idsectorFound == idSector) {
                                                            switchSector.isChecked = true
                                                        }
                                                    }

                                                    switchSector.setOnCheckedChangeListener { buttonView, isChecked ->
                                                        // Aquí puedes realizar acciones basadas en el cambio de estado del Switch
                                                        if (isChecked) {

                                                            val queryRelacion =
                                                                "SELECT id FROM relacionlotemoduloestacionsector WHERE idlote = $loteid and idmodulo = $moduloid and idestacion = ${arrIdRelation[position]} "
                                                            val cursorRelacion =
                                                                db.rawQuery(queryRelacion, null)
                                                            while (cursorRelacion.moveToNext()) {
                                                                val id = cursorRelacion.getInt(
                                                                    cursorRelacion.getColumnIndexOrThrow(
                                                                        "id"
                                                                    )
                                                                )
                                                                val cadenaInsert =
                                                                    "INSERT INTO relacioncompletasector(idrelacion,idsector) " +
                                                                            "VALUES($id,$idSector)"
                                                                dbWrite.execSQL(cadenaInsert)
                                                                Toast.makeText(
                                                                    this@selectionSectors,
                                                                    "Sector asignado ala estacion correctamente",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()

                                                            }
                                                        } else {
                                                            val queryRelacion =
                                                                "SELECT id FROM relacionlotemoduloestacionsector WHERE idlote = $loteid and idmodulo = $moduloid and idestacion = ${arrIdRelation[position]} "
                                                            val cursorRelacion =
                                                                db.rawQuery(queryRelacion, null)
                                                            while (cursorRelacion.moveToNext()) {
                                                                val id = cursorRelacion.getInt(
                                                                    cursorRelacion.getColumnIndexOrThrow(
                                                                        "id"
                                                                    )
                                                                )
                                                                val cadenaDelete =
                                                                    "DELETE FROM relacioncompletasector WHERE idrelacion = $id and idsector = $idSector "
                                                                dbWrite.execSQL(cadenaDelete)
                                                                Toast.makeText(
                                                                    this@selectionSectors,
                                                                    "Sector eliminado ala estacion correctamente",
                                                                    Toast.LENGTH_SHORT
                                                                ).show()
                                                            }
                                                        }
                                                    }
                                                    linearSectors.addView(switchSector)
                                                }
                                            }
                                        }

                                        override fun onNothingSelected(parent: AdapterView<*>?) {
                                            // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
                                        }
                                    }

                            }

                            override fun onNothingSelected(parent: AdapterView<*>?) {
                                // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
                            }
                        }
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
                }
            }
    }
}