import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.SoundEffectConstants
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.agrizar.berriesconteo.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.journeyapps.barcodescanner.BarcodeCallback
import java.util.Timer
import java.util.TimerTask


class QRScannerFragment : Fragment(), BarcodeCallback {

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var resultTextView: TextView
    private var mListener: OnFragmentInteractionListener? = null
    private var scanningEnabled = true

    //  SE ESTABLECEN DOS SEGUNDOS DE INTERVALO PARA ESCANEAR
    private val scannerInterval = 2000L
    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            mListener = context as OnFragmentInteractionListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context debe implementar OnFragmentInteractionListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_q_r_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcodeView = view.findViewById(R.id.barcodeView)

//      SE CONFIGURA EL ESCANER PARA ADMITIR VARIOS FORMATOS
        val formats: Collection<BarcodeFormat> = listOf(BarcodeFormat.CODE_128)
        barcodeView.setDecoderFactory(DefaultDecoderFactory(formats))
        barcodeView

//      COMIENZA EL ESCANEO CUANDO SE MUESTRE EL FRAGMENT
        barcodeView.decodeContinuous(this)
    }

    private var lastScanTime = 0L
    override fun barcodeResult(result: BarcodeResult?) {
//      SE OBTIENE EL RESULTADO DEL ESCANEO DEL CODIGO DE BARRAS
        if (result != null) {
            if (scanningEnabled) {
                val currentTime = System.currentTimeMillis()
                if (currentTime - lastScanTime >= scannerInterval) {
                    lastScanTime = currentTime

                    mListener?.onValueReturned(result.text)
                }
            }
        }
    }

    override fun possibleResultPoints(resultPoints: List<ResultPoint>?) {
        // METODO PARA POSIBLES PUNTOS DE RESULTADOS
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    interface OnFragmentInteractionListener {
        //      TIPO DE DATO
        fun onValueReturned(value: String)
    }
}
