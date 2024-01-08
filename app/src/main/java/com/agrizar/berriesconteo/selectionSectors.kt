package com.agrizar.berriesconteo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.berriesconteo.R
import com.example.berriesconteo.databinding.ActivityPantallaCapturarBinding
import com.example.berriesconteo.databinding.ActivitySelectionSectorsBinding

class selectionSectors : AppCompatActivity() {
    private lateinit var binding: ActivitySelectionSectorsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_selection_sectors)
        binding = ActivitySelectionSectorsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val spinnerLugar = binding.inpLugar

        val arrLugarTitulos: MutableList<String> = mutableListOf()
        arrLugarTitulos.add("LUGAR.....")
        arrLugarTitulos.add("AGZ3")
        arrLugarTitulos.add("AGZ1")
        val adapterLugar =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, arrLugarTitulos)
        spinnerLugar.adapter = adapterLugar



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

                        arrModulos.add("MODULO.....")
                        arrModulos.add("MOD1")
                        arrModulos.add("MOD2")
                        arrModulos.add("MOD3")


                    }
                    if (position == 2) {
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

                    if (position == 1) {

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
                                    if (position == 1) {
                                        arrEstacion.add("ESTACION.....")
                                        arrEstacion.add("ESTACION 1")
                                        arrEstacion.add("ESTACION 2")
                                        arrEstacion.add("ESTACION 3")
                                        arrEstacion.add("ESTACION 4")
                                        arrEstacion.add("ESTACION 5")
                                        arrEstacion.add("ESTACION 6")
                                        arrEstacion.add("ESTACION 7")
                                        arrEstacion.add("ESTACION 8")
                                    }
                                    if (position == 2) {
                                        arrEstacion.add("ESTACION.....")
                                        arrEstacion.add("ESTACION 9")
                                        arrEstacion.add("ESTACION 10")
                                        arrEstacion.add("ESTACION 11")
                                        arrEstacion.add("ESTACION 12")
                                        arrEstacion.add("ESTACION 13")
                                        arrEstacion.add("ESTACION 14")
                                    }
                                    if (position == 3) {
                                        arrEstacion.add("ESTACION.....")
                                        arrEstacion.add("ESTACION 15")
                                        arrEstacion.add("ESTACION 16")
                                        arrEstacion.add("ESTACION 17")
                                        arrEstacion.add("ESTACION 18")
                                        arrEstacion.add("ESTACION 19")
                                    }

                                    val adapterEstacion =
                                        ArrayAdapter(
                                            this@selectionSectors,
                                            android.R.layout.simple_spinner_item,
                                            arrEstacion
                                        )
                                    spinnerEstacion.adapter = adapterEstacion

                                }


                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
                                }
                            }

                    }

                    if (position == 2) {

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
                                    if (position == 1) {
                                        arrEstacion.add("ESTACION.....")
                                        arrEstacion.add("ESTACION 1")
                                        arrEstacion.add("ESTACION 2")
                                        arrEstacion.add("ESTACION 3")
                                        arrEstacion.add("ESTACION 4")
                                        arrEstacion.add("ESTACION 5")
                                    }
                                    val adapterEstacion =
                                        ArrayAdapter(
                                            this@selectionSectors,
                                            android.R.layout.simple_spinner_item,
                                            arrEstacion
                                        )
                                    spinnerEstacion.adapter = adapterEstacion

                                }



                                override fun onNothingSelected(parent: AdapterView<*>?) {
                                    // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
                                }
                            }
                    }




                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // ESTO SE EJECUTA CUANDO NO SE SELECCIONA NADA
                }
            }
    }
}