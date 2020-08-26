package com.ab.servitor.frag

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.ab.servitor.BuildConfig
import com.ab.servitor.R
import com.ab.servitor.databinding.FragmentLoginBinding
import com.ab.servitor.util.OnFragmentInteractionListener

class FragLogin:Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private var binding: FragmentLoginBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: FragmentLoginBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        view.handlers = this
        binding = view
        return view.root
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener)
            listener = context
        else
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    fun getVersionText() = BuildConfig.VERSION_NAME

    @SuppressLint("ApplySharedPref")
    fun onAuthButtonClick(v:View){
        val pref = PreferenceManager.getDefaultSharedPreferences(v.context)
        val editor = pref.edit()
        editor.putString("c1_user", binding?.loginEditText?.text.toString())
        editor.putString("c1_pass", binding?.passEditText?.text.toString())
        editor.commit()

        v.tag="LOGIN"
        listener?.onFragmentInteraction(v)
    }

    companion object{
        @JvmStatic
        fun newInstance() = FragLogin()
    }
}