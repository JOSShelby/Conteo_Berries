import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.berriesconteo.R
import com.google.zxing.BarcodeFormat
import com.google.zxing.ResultPoint
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import com.journeyapps.barcodescanner.DefaultDecoderFactory
import com.journeyapps.barcodescanner.BarcodeCallback


class QRScannerFragment : Fragment(), BarcodeCallback  {

    private lateinit var barcodeView: DecoratedBarcodeView
    private lateinit var resultTextView: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_q_r_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcodeView = view.findViewById(R.id.barcodeView)
        resultTextView = view.findViewById(R.id.codigoQR)

        // Configura el escáner para admitir múltiples formatos
        val formats: Collection<BarcodeFormat> = listOf(BarcodeFormat.CODE_128)
        barcodeView.setDecoderFactory(DefaultDecoderFactory(formats))

        // Comienza el escaneo cuando el Fragment se muestre
        barcodeView.decodeContinuous(this)



    }

    override fun barcodeResult(result: BarcodeResult?) {
        // Aquí puedes obtener el resultado del escaneo del código de barras
        if (result != null) {
            resultTextView.text  = result.text
            // Maneja el resultado del escaneo como desees
        }
    }

    override fun possibleResultPoints(resultPoints: List<ResultPoint>?) {
        // Método opcional para manejar posibles puntos de resultado (no es necesario para el escaneo de códigos de barras)
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }


}
