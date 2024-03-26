package com.agrizar.berriesconteo

import android.content.Context
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isGone
import androidx.fragment.app.DialogFragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.agrizar.berriesconteo.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
class dialogPermiso : DialogFragment() {
    private lateinit var cargaPermiso: LinearLayout
    private lateinit var txtcont: TextView
    private lateinit var txtcontt: TextView
    private lateinit var  txtReconexion: TextView

    private var contTemporal = 0

    private var tareaJob: Job? = null
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
        cargaPermiso = rootView.findViewById(R.id.linearProgress)


        val mArgs = arguments
        val jsonArreglo = mArgs!!.getString("jsonArreglo");
        val arrCont = mArgs!!.getString("arrCont");

        //txtcontt.text = arrCont

//      BOTON QUE MANDA EL USER Y PASSWORD A PHP PARA QUE INSERTE EL REGISTRO SI SON CORRECTOS LOS DATOS
        btnSubir.setOnClickListener {

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val idCell = sharedPreferences.getInt("idCell", 0)
            val scope = CoroutineScope(Dispatchers.IO)

            cargaPermiso.isGone = false
            if (idCell == 0) {

                    val idCell = sharedPreferences.getInt("idCell", 0)

                        insertarRegistros(
                            jsonArreglo!!,
                            rootView.context,
                            txtContraseña.text.toString(),
                            txtNombre.text.toString(),
                            idCell
                        ) { errorData ->
                            if(errorData == 1){

                            }
                        }
            } else {
                val idCell = sharedPreferences.getInt("idCell", 0)

                insertarRegistros(
                    jsonArreglo!!,
                    rootView.context,
                    txtContraseña.text.toString(),
                    txtNombre.text.toString(),
                    idCell
                ){errorData ->
                    if(errorData == 1){
                    }
                }
            }
        }
        return rootView;
    }

    private fun checkStatusRequest(idCell: Int, type: Int, callback: (Boolean,Int) -> Unit){
        val url =
            "http://" + getString(R.string.servidor) + "/kudePOO/aplicacion/apps/berries/checkTemporalRequest.php?idCell=$idCell&type=$type";
        val queue = Volley.newRequestQueue(context)
        val socketTimeout = 1200000 // 20 minutos en milisegundos
        val stringRequest = object : StringRequest(Method.GET, url,
            Response.Listener { response ->
                try {
                    val jsonRespuesta = JSONObject(response)
                    val error = jsonRespuesta.getInt("error")
                    val cuenta = jsonRespuesta.getInt("cuenta")

                    txtReconexion.visibility = View.GONE
                    if(contTemporal>10 && error==1){
                        cargaPermiso.isGone = true
                        tareaJob?.cancel()
                        Toast.makeText(
                            context,
                            "Se subieron los registros exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        activityMain?.Resultado(true)
                        dismiss()
                    }

                    if(error==3){
                        cargaPermiso.isGone = true
                        tareaJob?.cancel()
                        Toast.makeText(
                            context,
                            "Se subieron los registros exitosamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        activityMain?.Resultado(true)
                        dismiss()
                    }
                        callback(true,cuenta)

                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(false,0)
                }
            },
            Response.ErrorListener { error ->

                txtReconexion.visibility = View.VISIBLE
                callback(false,0)
            }
        ) {}

        stringRequest.retryPolicy =
            DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

        queue.add(stringRequest)
        contTemporal += 1
    }

    fun insertarRegistros(
        json: String,
        context: Context,
        contraseña: String,
        usuario: String,
        idCell: Int,callback: (Int) -> Unit
    ) {

        var dbBerries = DBBerries(context, " DBBerries", null, R.string.versionBD)
        val db = dbBerries.writableDatabase
        db.execSQL("DELETE FROM cubetascontadasberries")
        // RUTA PARA MANDAR EL ARREGLO, USER Y PASSWORD - EN PRUEBAS
        val urlRegistros =
            "http://" + getString(R.string.servidor) + "/kudePOO/aplicacion/apps/berries/insertarRegistros.php";

        val queueResponsivas = Volley.newRequestQueue(context)

        // Configurar una política de reintento con un tiempo de espera de 10 minutos
        val socketTimeout = 7200000 // 2 horas en milisegundos

        // Parámetros a enviar en la solicitud POST
        val params = HashMap<String, String>()
        params["array"] = json
        params["usuario"] = usuario
        params["password"] = contraseña
        params["idCell"] = idCell.toString()

        // DECLARAMOS EL ESTADO EN -1 PARA SABER DESPUES SI HUBO UN ERROR
        var statusCode = -1
        val stringRequestResponsivas = object : StringRequest(Method.POST, urlRegistros,
            Response.Listener { response ->
                val jsonRespuesta = JSONObject(response)
                statusCode = jsonRespuesta.getInt("statusCode")

                // SI LA RESPUESTA DEL PHP INSERTÓ LOS DATOS A LA BD DEL KUDE, NOS REGRESA EL ESTADO 1
                if (statusCode == 1) {

                    Toast.makeText(
                        context,
                        "Se subieron los registros exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    activityMain?.Resultado(true)
                    callback(0)
                    //tareaJob?.cancel()
                    dismiss()
                } else {
                    println("entre aqui")
                    // SI LA RESPUESTA DE PHP NO FUE 1, PUEDE HABER UN ERROR INESPERADO O EL USUARIO ES INCORRECTO
                    //tareaJob?.cancel()
                    if (statusCode == 0) {
                        Toast.makeText(context, "Ocurrió un error", Toast.LENGTH_SHORT).show()
                        callback(0)
                        cargaPermiso.isGone = true
                    }
                    if (statusCode == 2) {
                        Toast.makeText(context, "Usuario incorrecto", Toast.LENGTH_SHORT).show()
                        callback(0)
                        cargaPermiso.isGone = true
                    }
                    if (statusCode == 3) {
                        //tareaJob?.cancel()
                        Toast.makeText(context, "sucedio una llamada", Toast.LENGTH_SHORT).show()
                        callback(0)
                    }
                }
            },
            Response.ErrorListener { error ->
                //tareaJob?.cancel()
                callback(1)
            }) {

            override fun getParams(): Map<String, String> {
                return params
            }

            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                // Puedes agregar encabezados personalizados si es necesario
                return headers
            }
        }

        stringRequestResponsivas.retryPolicy =
            DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )
        queueResponsivas.add(stringRequestResponsivas)
    }

}