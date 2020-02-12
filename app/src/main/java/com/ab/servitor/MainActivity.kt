package com.ab.servitor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.preference.PreferenceManager
import com.ab.servitor.dao.Web1C
import com.ab.servitor.databinding.ActivityMainBinding
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

        if (savedInstanceState == null) {
            try {
                reposit.init(this)
            } catch (e: Exception){
                Toast.makeText(this, "Ошибка подключения к серверу ${e.message}", Toast.LENGTH_LONG).show()
            }
            reposit.getProductCatalog(this)

            supportFragmentManager.beginTransaction()
                .add(R.id.main_frame, MainMenu.newInstance())
                .commit()
        }
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        binding.global = global
        setSupportActionBar(findViewById(R.id.my_toolbar))

        binding.myToolbar.title="Servitor. ${applicationContext.packageManager.getPackageInfo(packageName, 0).versionName}"
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

    override fun onFragmentInteraction(view: View) {
        if (view.tag == "SCAN"){
            supportFragmentManager.beginTransaction()
                .replace(R.id.main_frame, Scanning.newInstance())
                .addToBackStack(null)
                .commit()
        }
    }
}
