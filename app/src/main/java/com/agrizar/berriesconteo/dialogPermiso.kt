package com.agrizar.berriesconteo

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
        txtcont = rootView.findViewById(R.id.txtCont)
        txtcontt = rootView.findViewById(R.id.txtContTotal)
        txtReconexion = rootView.findViewById(R.id.txtReconexion)

        val mArgs = arguments
        val jsonArreglo = mArgs!!.getString("jsonArreglo");
        val arrCont = mArgs!!.getString("arrCont");

        txtcontt.text = arrCont

//      BOTON QUE MANDA EL USER Y PASSWORD A PHP PARA QUE INSERTE EL REGISTRO SI SON CORRECTOS LOS DATOS
        btnSubir.setOnClickListener {

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            val idCell = sharedPreferences.getInt("idCell", 0)


            val scope = CoroutineScope(Dispatchers.Default)


            tareaJob = scope.launch {
                while (isActive) {
                    checkStatusRequest(idCell,0){request,cont ->
                       txtcont.text = cont.toString()
                    }
                    delay(1000)
                }
            }


            cargaPermiso.isGone = false
            if (idCell == 0) {
                addNewCell() { statusCode ->

                    val idCell = sharedPreferences.getInt("idCell", 0)

                    if (statusCode) {
                        insertarRegistros(
                            jsonArreglo!!,
                            rootView.context,
                            txtContraseña.text.toString(),
                            txtNombre.text.toString(),
                            idCell
                        ) { errorData ->
                            if(errorData == 1){
                                tareaJob = scope.launch {
                                    while (isActive) {
                                        checkStatusRequest(idCell,1){request,cont ->
                                            txtcont.text = cont.toString()
                                        }
                                        delay(1000)
                                    }
                                }
                            }
                        }

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
                        tareaJob = scope.launch {
                            while (isActive) {
                                checkStatusRequest(idCell,1){request,cont ->
                                    txtcont.text = cont.toString()
                                }
                                delay(1000)
                            }
                        }
                    }

                }

            }
        }
        return rootView;
    }

    private fun addNewCell(callback: (Boolean) -> Unit) {

        val url =
            "http://" + getString(R.string.servidor) + "/kudePOO/aplicacion/apps/berries/addNewCell.php";
        val queue = Volley.newRequestQueue(context)
        val socketTimeout = 1200000 // 20 minutos en milisegundos

        var statusCode = -1

        val stringRequest = object : StringRequest(Method.GET, url,
            Response.Listener { response ->
                try {
                    val jsonRespuesta = JSONObject(response)
                    statusCode = jsonRespuesta.getInt("statusCode")
                    val idCell = jsonRespuesta.getInt("idCell")
                    if (statusCode == 1) {
                        val sharedPreferences =
                            PreferenceManager.getDefaultSharedPreferences(context)
                        val editor = sharedPreferences.edit()
                        editor.putInt("idCell", idCell)
                        editor.apply()
                        callback(true)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    callback(false)
                }
            },
            Response.ErrorListener { error ->
                // Manejar el error de la solicitud
                Toast.makeText(context, "Ocurrió un error en la conexión", Toast.LENGTH_SHORT)
                    .show()
                callback(false)
            }
        ) {}

        stringRequest.retryPolicy =
            DefaultRetryPolicy(
                socketTimeout,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
            )

        queue.add(stringRequest)

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
                    println(error)
                    println("tipo:$type")
                    txtReconexion.visibility = View.GONE
                    if(error==3){
                        println("entre aqui en la respuesta 3")
                        cargaPermiso.isGone = true
                        tareaJob?.cancel()
                        var dbBerries = DBBerries(context, " DBBerries", null, R.string.versionBD)
                        val db = dbBerries.writableDatabase
                        db.execSQL("DELETE FROM cubetascontadasberries")
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
    }

    fun insertarRegistros(
        json: String,
        context: Context,
        contraseña: String,
        usuario: String,
        idCell: Int,callback: (Int) -> Unit
    ) {
        // RUTA PARA MANDAR EL ARREGLO, USER Y PASSWORD - EN PRUEBAS
        val urlRegistros =
            "http://" + getString(R.string.servidor) + "/kudePOO/aplicacion/apps/berries/insertarRegistros.php";

        val queueResponsivas = Volley.newRequestQueue(context)

        // Configurar una política de reintento con un tiempo de espera de 10 minutos
        val socketTimeout = 1200000 // 20 minutos en milisegundos
        println(json)
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
                    var dbBerries = DBBerries(context, " DBBerries", null, R.string.versionBD)
                    val db = dbBerries.writableDatabase
                    db.execSQL("DELETE FROM cubetascontadasberries")
                    Toast.makeText(
                        context,
                        "Se subieron los registros exitosamente",
                        Toast.LENGTH_SHORT
                    ).show()
                    activityMain?.Resultado(true)
                    callback(0)
                    tareaJob?.cancel()
                    dismiss()
                } else {

                    // SI LA RESPUESTA DE PHP NO FUE 1, PUEDE HABER UN ERROR INESPERADO O EL USUARIO ES INCORRECTO
                    tareaJob?.cancel()
                    if (statusCode == 0) {
                        Toast.makeText(context, "Ocurrió un error", Toast.LENGTH_SHORT).show()
                        callback(0)
                    }
                    if (statusCode == 2) {
                        Toast.makeText(context, "Usuario incorrecto", Toast.LENGTH_SHORT).show()
                        callback(0)
                    }
                }
                cargaPermiso.isGone = true
            },
            Response.ErrorListener { error ->
                // Manejar el error de la solicitud
                //Toast.makeText(context, "Ocurrió un error en la conexión", Toast.LENGTH_SHORT)
                    //.show()
                //cargaPermiso.isGone = true
                tareaJob?.cancel()
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