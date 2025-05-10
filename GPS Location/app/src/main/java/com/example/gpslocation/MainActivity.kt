package com.example.gpslocation

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import android.content.Context
import android.location.Address
import android.location.Geocoder

import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.Location
import android.location.LocationManager
import android.widget.Button
import android.widget. TextView
import android.widget.Toast

import java.util.Locale

class MainActivity: AppCompatActivity(), LocationListener {

    private lateinit var tl: TextView
    private lateinit var t2: TextView
    private lateinit var bl:Button
    private lateinit var LM: LocationManager

    @Override
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tl = findViewById(R.id.t1)
        t2 = findViewById(R.id.t2)
        bl = findViewById(R.id.b1)

        if (ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                101
            )
        }
        bl.setOnClickListener {
            getLocation()
        }
    }

    private fun getLocation() = try {
        LM = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        LM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5f, this)
    } catch (e: SecurityException) {
        e.printStackTrace()
    }

    override fun onLocationChanged(location: Location) {
        tl.text = "Latitude: " + location.getLatitude() + "nLongitude: " + location.getLongitude()
        try {
            val geocoder = Geocoder(this, Locale.getDefault())
            val addresses =
                geocoder.getFromLocation(location.latitude, location.longitude, 1) as List<Address>
            t2.text = addresses[0].getAddressLine(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onProviderDisabled(provider: String) {
        Toast.makeText(this, "Please enable GPS and Internet", Toast.LENGTH_SHORT).show()
    }
    override fun onProviderEnabled(provider: String) {
        Toast.makeText(this, "GPS is enabled", Toast.LENGTH_SHORT).show()
    }
    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Toast.makeText(this, "Status Changed", Toast.LENGTH_SHORT).show()
    }
}