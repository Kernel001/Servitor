package com.ab.servitor

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.ab.servitor.Dao.Web1C

class MainActivity : AppCompatActivity() {
    private val reposit = Web1C()
    private val codeReceiver = BarcodeReceiver()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(findViewById(R.id.my_toolbar))

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
            !=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.INTERNET),
                0)
        }

        reposit.getProductCatalog(this)
        codeReceiver.from("android.intent.ACTION_DECODE_DATA", this)
            .subscribe(this::onNewBarcode)
    }

    private fun onNewBarcode(intent:Intent){
        val data = reposit.getProductByGTIN( intent.getStringExtra("barcode_string"))
        Toast.makeText(this, "Barcode: $data", Toast.LENGTH_SHORT).show()
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
}
