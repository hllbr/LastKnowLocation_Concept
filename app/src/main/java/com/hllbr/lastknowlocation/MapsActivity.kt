package com.hllbr.lastknowlocation

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.hllbr.lastknowlocation.databinding.ActivityMapsBinding

import java.util.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager : LocationManager
    private lateinit var locationListener : LocationListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMapLongClickListener(myListener)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        locationListener = object : LocationListener{
            override fun onLocationChanged(location: Location) {

                    mMap.clear()
                    val userLocation = LatLng(location.latitude,location.longitude)
                    mMap.addMarker(MarkerOptions().position(userLocation).title("Your Location"))
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation,15f))
                    /*
                    GeoCoder ile harita üzerinde tıklanan alanın enlem ve boylamını alabiliyoruz bu alınıan verielri ister yazdırır ister işler ister bir veritabanına kaydederim vb...
                     */
                    val geocoder =  Geocoder(this@MapsActivity,Locale.getDefault())
                    try{
                        val addressList = geocoder.getFromLocation(location.latitude,location.longitude,1)
                        if(addressList != null && addressList.size > 0){
                                Toast.makeText(this@MapsActivity,addressList.get(0).toString(),Toast.LENGTH_LONG).show()
                                println(addressList.get(0).toString())

                        }
                    }catch (e: Exception){
                        e.printStackTrace()
                    }

                }



        }
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)

        }else{
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,15f,locationListener)
            val userLastLocation =locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if(userLastLocation != null){
                //şuan userLastLocation içerisinde herhangi bir latitude yada longitutde bulunmuyor eğer harita üzerinde bir işaretleme işlemi yapacaksam gerekli verileri vermem gerekiyor.
                val lastLocation = LatLng(userLastLocation.latitude,userLastLocation.longitude)
                mMap.addMarker(MarkerOptions().position(lastLocation).title("Your Location"))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastLocation,15f))
                //eğer onLocationChanged devreye girmezse ben burada kurduğum yapı sayesinde kullanıcıya elimdeki senaryolar gereğince son bilinen konumunu göstermiş olacğım

            }

        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == 1 ){

            if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1,15f,locationListener)
            }

        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    val myListener = object : GoogleMap.OnMapLongClickListener {
        override fun onMapLongClick(p0: LatLng?) {

            mMap.clear()

            val geocoder = Geocoder(this@MapsActivity,Locale.getDefault())

            if (p0 != null) {

                var address = ""

                try {


                    val addressList = geocoder.getFromLocation(p0.latitude,p0.longitude,1)
                    if (addressList != null && addressList.size > 0) {

                        if (addressList[0].thoroughfare != null) {
                            address += addressList[0].thoroughfare

                            if (addressList[0].subThoroughfare != null) {
                                address += addressList[0].subThoroughfare
                            }
                        }


                    }


                } catch (e: Exception) {
                    e.printStackTrace()
                }

                mMap.addMarker(MarkerOptions().position(p0).title(address))


            } else {
                Toast.makeText(applicationContext,"Try Again",Toast.LENGTH_LONG).show()

            }

        }

    }
}