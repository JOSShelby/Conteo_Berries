package com.agrizar.berriesconteo

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [dialogAutorizacion.newInstance] factory method to
 * create an instance of this fragment.
 */
class dialogAutorizacion : DialogFragment() {

    interface checkAuthorization {
        fun checkAuthorization(resultado: Boolean)
    }

    private var activityMain: dialogAutorizacion.checkAuthorization? = null;
    private var cambio = 0
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            // Verifica que la Activity implemente la interfaz
            activityMain = context as dialogAutorizacion.checkAuthorization
        } catch (e: ClassCastException) {
            throw ClassCastException("$context debe implementar la interfaz ComunicadorFragment")
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView: View = inflater.inflate(R.layout.fragment_dialog_autorizacion, container, false);
        val txtPassword = rootView.findViewById<EditText>(R.id.txtPassword)
        val btnCheckPassword = rootView.findViewById<Button>(R.id.btnCheckPassword)

        btnCheckPassword.setOnClickListener {
            if(txtPassword.text.toString() != ""){
                if(txtPassword.text.toString() == "123456"){
                    cambio=1
                    activityMain?.checkAuthorization(true)
                    dismiss()
                }else{
                    Toast.makeText(context,"Contraseña incorrecta",Toast.LENGTH_LONG).show()
                }
            }else{
                Toast.makeText(context,"Introduzca la contraseña",Toast.LENGTH_LONG).show()
            }
        }

        return rootView
    }

    override fun onDestroy() {
        super.onDestroy()
        if(cambio==0){
            activityMain?.checkAuthorization(false)
        }
    }

}