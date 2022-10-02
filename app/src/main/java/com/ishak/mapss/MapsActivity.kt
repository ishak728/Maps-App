package com.ishak.mapkotlin

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Database
import androidx.room.Room
import com.google.android.gms.maps.CameraUpdateFactory

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.ishak.mapkotlin.databinding.ActivityMapsBinding
import com.ishak.mapss.Place
import com.ishak.mapss.PlaceDao
import com.ishak.mapss.PlaceDatabase
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private var selectedLatitude: Double? = null
    private var selectedLongitude: Double? = null
    private lateinit var db: PlaceDatabase
    private lateinit var placeDao: PlaceDao
    private var compositeDisposable = CompositeDisposable()
    private var placeFromMain: Place? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        db = Room.databaseBuilder(applicationContext, PlaceDatabase::class.java, "Places").build()
        placeDao = db.placeDao()

        binding.saveBtn.isEnabled = false


    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        mMap.setOnMapLongClickListener(this)
        val intent = intent
        val info = intent.getStringExtra("info")

        if (info == "new") {
            binding.saveBtn.visibility = View.VISIBLE
            binding.deleteBtn.visibility = View.GONE

          /* *//* locationManager = this.getSystemService(LOCATION_SERVICE) as LocationManager

           locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {
                    println("latitude:" + location.latitude + "longitude" + location.longitude)

                }

                //bu olmadan bazen bazı uygulamalar çalışmıyor.
                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                    super.onStatusChanged(provider, status, extras)
                }

            }*//*



            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    )
                ) {
                    Snackbar.make(binding.root, "need permission", Snackbar.LENGTH_INDEFINITE)
                        .setAction("give permission") {
                            //izin verilmedii çin izin isteniyor
                            ActivityCompat.requestPermissions(
                                this@MapsActivity,
                                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                                1
                            )
                        }
                } else {
                    //izin verilmediği için izin isteniyor
                    ActivityCompat.requestPermissions(
                        this@MapsActivity,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        1
                    )
                }
            } else {
               *//* //izin verilmiş.
                locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0f,
                    locationListener
                )


                //konuma mavi yuvarlak bir sembol koyar
                mMap.isMyLocationEnabled = true*//*

            }*/
        }
        //daha önce seçilmiş place'e gidiyor
        else {

            placeFromMain = intent.getSerializableExtra("SelectedPlace") as Place
            placeFromMain?.let {
                val latlng = LatLng(it.latitude, it.longitude)
                mMap.addMarker(MarkerOptions().position(latlng).title(it.place))
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 3f))
                binding.placeTxt.setText(it.place)
                binding.saveBtn.visibility = View.GONE
                binding.deleteBtn.visibility = View.VISIBLE
            }

        }


    }

/*

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {

            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                )
                    */
/*locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        0,
                        0f,
                        locationListener
                    )*//*
 println()

            }
        } else {
            Toast.makeText(this@MapsActivity, "not allowed", Toast.LENGTH_LONG).show()
        }
    }
*/


    override fun onMapLongClick(p0: LatLng) {
        //daha önce var olan markerları siler
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0).title("Place ı liked"))
        selectedLatitude = p0.latitude
        selectedLongitude = p0.longitude

        binding.saveBtn.isEnabled = true

    }


    fun saveBtn(view: View) {
        val place = Place(selectedLatitude!!, selectedLongitude!!, binding.placeTxt.text.toString())


        compositeDisposable.add(
            placeDao.insert(place)
                //schedulers rxjavadan gelen bir sınıf ve io'ya erişimi sağlıyor
                .subscribeOn(Schedulers.io())
                //mainthread sadece android schedulerste var
                .observeOn(AndroidSchedulers.mainThread())

                .subscribe(this::handleResponse)
        )
    }

    fun handleResponse() {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun deleteBtn(view: View) {
        compositeDisposable.add(
            placeDao.delete(placeFromMain!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::handleResponse)
        )
    }

    override fun onDestroy() {
        super.onDestroy()

        //kullanıp atar.yer tasarrufu sağlar
        //büyük uygulamalarda ve özllikle internetten veri çekerken çok fazla yer kaplıyor bundan dolayı composite disposable kullanılır
        compositeDisposable.clear()
    }
}