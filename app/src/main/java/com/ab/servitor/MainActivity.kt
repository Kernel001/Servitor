package com.ab.servitor

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.preference.PreferenceManager
import com.ab.servitor.dao.Web1C
import com.ab.servitor.databinding.ActivityMainBinding
import com.ab.servitor.frag.FragLogin
import com.ab.servitor.util.OnFragmentInteractionListener
import com.ab.servitor.frag.MainMenu
import com.ab.servitor.frag.Scanning
import com.ab.servitor.util.GlobalStatus
import java.lang.Exception

class MainActivity : AppCompatActivity(), OnFragmentInteractionListener {
    private val reposit = Web1C
    private val global = GlobalStatus

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        PreferenceManager.setDefaultValues(this, R.xml.root_preferences, false)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.INTERNET),
                0)
        }

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
            !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                0)
        }


        if (savedInstanceState == null) {
            val dbUser: String = PreferenceManager.getDefaultSharedPreferences(this).getString("c1_user", "")?:""
            val dbPass: String = PreferenceManager.getDefaultSharedPreferences(this).getString("c1_pass","")?:""
            val termID: String = PreferenceManager.getDefaultSharedPreferences(this).getString("terminalID","")?:""

            if (dbUser.isEmpty() || dbPass.isEmpty() || termID.isEmpty()){
                //нужна авторизация!
                supportFragmentManager.beginTransaction()
                    .add(R.id.main_frame, FragLogin.newInstance())
                    .commit()
            }
            else {
                activate1Creposit(dbUser, dbPass)
                if (!GlobalStatus.http1CConnectio.get())
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frame, MainMenu.newInstance())
                        .commit()
                else
                    supportFragmentManager.beginTransaction()
                        .add(R.id.main_frame, MainMenu.newInstance())
                        .commit()
            }
        }
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.global = global
        setSupportActionBar(findViewById(R.id.my_toolbar))

        binding.myToolbar.title="Servitor. ${applicationContext.packageManager.getPackageInfo(packageName, 0).versionName}"
    }

    fun activate1Creposit(dbUser:String, dbPass:String){
        try {
            reposit.init(this, dbUser, dbPass)
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Ошибка подключения к серверу ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
        reposit.getProductCatalog(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            val intent = Intent(this, SettingsActivity::class.java)
            startActivity(intent)
        }
        return true
    }

    @SuppressLint("HardwareIds")
    override fun onFragmentInteraction(view: View) {
        if (view.tag == "SCAN" || view.tag == "INVENT"){
            GlobalStatus.currentOper.set(view.tag.toString())
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, Scanning.newInstance())
                .addToBackStack(null)
                .commit()
        }

        if (view.tag == "LOGIN"){
            val temMng:TelephonyManager = getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
            val imei = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                temMng.imei
            } else {
                temMng.deviceId
            }

            if (imei.isEmpty())
                Toast.makeText(this,"IMEI не определен. Идентификация устройства невозможна", Toast.LENGTH_LONG).show()
            val pref = PreferenceManager.getDefaultSharedPreferences(this)
            val editor = pref.edit()
            editor.putString("terminalID", imei)
            editor.apply()

            val dbUser: String = PreferenceManager.getDefaultSharedPreferences(this).getString("c1_user", "")?:""
            val dbPass: String = PreferenceManager.getDefaultSharedPreferences(this).getString("c1_pass","")?:""

            activate1Creposit(dbUser, dbPass)

            if (GlobalStatus.http1CConnectio.get())
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_frame, MainMenu.newInstance())
                    .commit()
        }
    }
 }


