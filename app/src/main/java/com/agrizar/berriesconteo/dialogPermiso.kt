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
//      VARIABLES TRAIDAS DEL XML
        val rootView: View = inflater.inflate(R.layout.dialogpermiso, container, false);
        val txtNombre = rootView.findViewById<EditText>(R.id.txtnombre)
        val txtContraseña = rootView.findViewById<EditText>(R.id.txtcontraseña)
        val btnSubir = rootView.findViewById<Button>(R.id.btnSubirDatos)
        cargaPermiso = rootView.findViewById(R.id.cargaPermiso)
        val mArgs = arguments
        val jsonArreglo = mArgs!!.getString("jsonArreglo");

//      BOTON QUE MANDA EL USER Y PASSWORD A PHP PARA QUE INSERTE EL REGISTRO SI SON CORRECTOS LOS DATOS
        btnSubir.setOnClickListener {
            cargaPermiso.isGone = false
            insertarRegistros(jsonArreglo!!,rootView.context,txtContraseña.text.toString(),txtNombre.text.toString())
        }
        return rootView;
    }
    fun insertarRegistros(json: String,context: Context,contraseña:String ,usuario:String ){
//        RUTA PARA MANDAR EL ARREGLO, USER Y PASSWORD - EN PRUEBAS
        val urlRegistros = "http://" + getString(R.string.servidor) + "/kudePOO/aplicacion/apps/berries/insertarRegistros.php?array=$json&usuario=$usuario&password=$contraseña";

        val queueResponsivas = Volley.newRequestQueue(context)

//      DECLARAMOS EL ESTADO EN -1 PARA SABER DESPUES SI HUBO UN ERROR
        var statusCode = -1
        val stringRequestResponsivas = StringRequest(Request.Method.GET, urlRegistros, { response ->
            println(response)
            val jsonRespuesta = JSONObject(response)
            statusCode = jsonRespuesta.getInt("statusCode")

//          SI LA RESPUESTA DEL PHP INSERTO LOS DATOS A LA BD DEL KUDE, NOS REGRESA EL ESTADO 1
            if(statusCode==1){
                var dbBerries = DBBerries(context," DBBerries", null, R.string.versionBD);
                val db = dbBerries.writableDatabase
                db.execSQL("DELETE FROM cubetascontadasberries")
                Toast.makeText(context,"Se subieron los registros Exitosamente",Toast.LENGTH_SHORT).show()
                activityMain?.Resultado(true)
                dismiss()
            }else{

//              SI LA RESPUESTA DE PHP NO FUE 1, PUEDE HABER UN ERROR INESPERADO O EL USUARIO ES INCORRECTO
                if(statusCode==0){
                    Toast.makeText(context,"ocurrio un error",Toast.LENGTH_SHORT).show()
                }
                if(statusCode==2){
                    Toast.makeText(context,"Usuario incorrecto",Toast.LENGTH_SHORT).show()
                }
            }
            cargaPermiso.isGone = true
        },{
//          SI NO HAY CONEXION SALTA EL SIGUIENTE MENSAJE
            Toast.makeText(context,"Ocurrio un error en la conexion",Toast.LENGTH_SHORT).show()
            cargaPermiso.isGone = true
        })
        queueResponsivas.add(stringRequestResponsivas)
    }
}