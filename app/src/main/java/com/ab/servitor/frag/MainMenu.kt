package com.ab.servitor.frag

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.ab.servitor.R
import com.ab.servitor.util.OnFragmentInteractionListener
import com.ab.servitor.databinding.FragmentStartBinding

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [MainMenu.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [MainMenu.newInstance] factory method to
 * create an instance of this fragment.
 */
class MainMenu : Fragment() {
    private var listener: OnFragmentInteractionListener? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: FragmentStartBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_start, container, false)
        view.handlers = this
        return view.root
    }

    fun onScanButtonPressed(view: View) {
        view.tag = "SCAN"
        listener?.onFragmentInteraction(view)
    }

    fun onInventButtonPressed(view: View){
        view.tag = "INVENT"
        listener?.onFragmentInteraction(view)
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
    }

    companion object {
        @JvmStatic
        fun newInstance() = MainMenu()
    }
}
