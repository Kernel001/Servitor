package com.ab.servitor.frag

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.databinding.*
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.ab.servitor.BarcodeReceiver
import com.ab.servitor.dao.Web1C
import com.ab.servitor.data.ProdItem
import com.ab.servitor.data.ScanResult
import com.ab.servitor.R
import com.ab.servitor.util.OnFragmentInteractionListener
import com.ab.servitor.databinding.FragmentScanBinding
import com.ab.servitor.util.GlobalStatus
import io.reactivex.disposables.Disposable
import java.lang.NumberFormatException
import java.util.*

class Scanning: Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private var viewModel: ScanResult = ScanResult()
    private var barCodeObservable: Disposable? = null
    private val reposit = Web1C
    private var dataBinding: FragmentScanBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_scan, container, false)
        dataBinding?.data = viewModel
        dataBinding?.controller = this

        context?.let {
            barCodeObservable = BarcodeReceiver().from("android.intent.ACTION_DECODE_DATA", it)
                .subscribe(this::onNewBarcode)
        }
        return dataBinding?.root
    }

    private fun onNewBarcode(intent: Intent){
        val data: ProdItem? = reposit.getProductByGTIN( intent.getStringExtra("barcode_string"))
        data?.let{
            viewModel.setDeviceID(PreferenceManager.getDefaultSharedPreferences(context).getString("terminalID",""))
            viewModel.setDevicePrefix(PreferenceManager.getDefaultSharedPreferences(context).getString("terminalPrefix", ""))
            viewModel.setGtin(it.GTIN)
            viewModel.setName("${it.Name} (${it.DopName}), ${it.ei}")
            viewModel.setDateProd(Calendar.getInstance().get(Calendar.DAY_OF_WEEK))
            viewModel.setKol(1.0f)
            Log.d("SCAN","new code scanned: ${it.GTIN}")
        }
    }

    fun onSendButtonClick(v: View){
        context?.let{
            v.isEnabled = false
            reposit.postScanResult(it, viewModel)
                ?.subscribe({
                    Toast.makeText(context, "Сообщение отправлено!", Toast.LENGTH_SHORT).show()
                    GlobalStatus.lastError.set("ОК")
                    GlobalStatus.http1CConnectio.set(true)
                    viewModel = ScanResult()
                    dataBinding?.data = viewModel
                    v.isEnabled = true
                },
                {
                    error-> GlobalStatus.lastError.set(error.message)
                    GlobalStatus.http1CConnectio.set(false)
                    v.isEnabled = true
                })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
        barCodeObservable?.dispose()
    }

    companion object {
        @JvmStatic
        fun newInstance() = Scanning()

//        @JvmStatic
//        @BindingAdapter("android:text")
//        fun setInt(view:TextView, date: Int?) {
//            if (date == null)
//                view.text = ""
//            else
//                view.text = date.toString()
//        }
//
//        @JvmStatic
//        @InverseBindingAdapter(attribute = "android:text")
//        fun getInt(view: TextView): Int{
//            return (view.text as String).toInt()
//        }
    }
}

@BindingAdapter("android:text")
fun setFloat(view:TextView, v: Float?) {
    if (v.toString() != view.text) {
        if (v == null)
            return
        view.text = v.toString()
    }
}

@InverseBindingAdapter(attribute = "android:text", event = "android:textAttrChanged")
fun getFloat(view: TextView): Float{
    val num = view.text.toString()
    if (num.isEmpty()) return 0.0f
    try{
        return num.toFloat()
    } catch (e: NumberFormatException){
        return 0.0f
    }
}
