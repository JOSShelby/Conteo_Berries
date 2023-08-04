package com.agrizar.berriesconteo

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.berriesconteo.R
import org.json.JSONObject

class dialogPermiso: DialogFragment() {

    private lateinit var cargaPermiso: ProgressBar

    interface Resultado {
        fun Resultado(resultado: Boolean)
    }

    private var activityMain: Resultado? = null;
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is MainActivity) {
            activityMain = context
        } else throw RuntimeException(
            context.toString() + "DEBE IMPLEMENTAR COMUCADORFRAGMENT"
        )
    }

    override fun onDetach() {
        super.onDetach()
        activityMain = null
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView: View = inflater.inflate(R.layout.dialogpermiso, container, false);
        var txtNombre = rootView.findViewById<EditText>(R.id.txtnombre)
        var txtContraseña = rootView.findViewById<EditText>(R.id.txtcontraseña)
        var btnSubir = rootView.findViewById<Button>(R.id.btnSubirDatos)
        cargaPermiso = rootView.findViewById(R.id.cargaPermiso)
        val mArgs = arguments
        val jsonArreglo = mArgs!!.getString("jsonArreglo");

        btnSubir.setOnClickListener {
            cargaPermiso.isGone = false
            insertarRegistros(jsonArreglo!!,rootView.context,txtContraseña.text.toString(),txtNombre.text.toString())
        }
        return rootView;
    }
    fun insertarRegistros(json: String,context: Context,contraseña:String ,usuario:String ){
//        RUTA EN PRUEBAS
        val urlRegistros = "http://" + getString(R.string.servidor) + "/kudePOO/aplicacion/apps/berries/insertarRegistros.php?array=$json&usuario=$usuario&password=$contraseña";
//        RUTA EN MI LOCAL JOSS
//        val urlRegistros = "http://" + getString(R.string.servidor) + "/kudePOO/aplicacion/berries/php/insertarRegistros.php?array=$json";

        val queueResponsivas = Volley.newRequestQueue(context)
        var statusCode = -1
        val stringRequestResponsivas = StringRequest(Request.Method.GET, urlRegistros, { response ->
            val jsonRespuesta = JSONObject(response);
            statusCode = jsonRespuesta.getInt("statusCode")
            if(statusCode==1){
                var dbBerries = DBBerries(context," DBBerries", null, R.string.versionBD);
                val db = dbBerries.writableDatabase
                db.execSQL("DELETE FROM cubetascontadasberries")
                Toast.makeText(context,"Se subieron los registros Exitosamente",Toast.LENGTH_SHORT).show()
                activityMain?.Resultado(true)
                dismiss()
//                txtSubir.text = "SUBIR : 0"
//
//
//                cargaDatos.visibility = View.GONE
//
//                // Define la animación de aparición
//                val fadeInAnimation = AlphaAnimation(5f, 1f)
//                fadeInAnimation.duration = 3000 // Duración en milisegundos
//
//                val waitAnimation = AlphaAnimation(1f, 1f)
//                fadeInAnimation.duration = 1000 // Duración en milisegundos
//
//                // Define la animación de desaparición
//                val fadeOutAnimation = AlphaAnimation(1f, 0f)
//                fadeOutAnimation.duration = 500 // Duración en milisegundos
//
//                imgSubido.startAnimation(fadeInAnimation)
//                imgSubido.postDelayed({
//                    imgSubido.startAnimation(fadeOutAnimation)
//                },1000)
            }else{
                if(statusCode==0){
                    Toast.makeText(context,"ocurrio un error",Toast.LENGTH_SHORT).show()
                }
                if(statusCode==2){
                    Toast.makeText(context,"Usuario incorrecto",Toast.LENGTH_SHORT).show()
                }
            }
            cargaPermiso.isGone = true
        },{
            Toast.makeText(context,"ocurrio un error en la conexion",Toast.LENGTH_SHORT).show()
            cargaPermiso.isGone = true
        })
        queueResponsivas.add(stringRequestResponsivas)
    }
}