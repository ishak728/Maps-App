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


class MapsActivity : AppCompatActivity(), OnMapReadyCallback,GoogleMap.OnMapLongClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var locationManager:LocationManager
    private lateinit var locationListener: LocationListener
    private var selectedLatitude:Double?=null
    private var selectedLongitude:Double?=null
    private lateinit var db:PlaceDatabase
    private lateinit var placeDao: PlaceDao
    private var compositeDisposable= CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        /*şöyle yaparaktan da uygulaa çalışacaktır.ama yoğun bir iş olursa çöker
        db=Room.databaseBuilder(applicationContext,PlaceDatabase::class.java,"Places")
            .allowMainThreadQueries()
            .build() */

        //PlaceDatabase::class.java hangi sınıfa bağlanılacağını yazıyoruz
        //Places:database ismidir
        db=Room.databaseBuilder(applicationContext,PlaceDatabase::class.java,"Places").build()
        placeDao=db.placeDao()


    }




    //harita hazır olduğunda çağrılır
    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap
        //GoogleMap.OnMapLongClickListener'ı kullanabilmek için güncek haritamız olan mMap'e arayüzü uygulamak gerekiyor
        mMap.setOnMapLongClickListener(this)

/*
        //LatLng:kordinatı tutar.kordinat diye bir sınıf var onu araştır o daha fazla bilgi tutuyor
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
        //konuma zoom yapar
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney,10f))*/

        //burada "getSystemService" Any döndürdüğü için casting işlemini uygulamamız gerekir
        locationManager= this.getSystemService(LOCATION_SERVICE) as LocationManager
        /*LocationManager sayesinde güncel,son konumu bulabiliriz.konum sağalayıcısı vb.bir sürü şey bulunabilir.
requestLocationUpdates sayesinde konum değiştiği her anda bize haber verilecek.
locationListener paremetresi sayesinde konum değiştiğinde bize haber verilir.konum sağlayıcısı,ne kadar sürede
haberverilsin ve ne kadar uzağa gidildiğinde haber versin parametrelerini biz ayarlıyoruz*/

/*
        //burada it Location sınıfıdır.bu sınıf sayesinde kullanıcını o andaki latitude,longitude,yüksekliğini,hızını,zamanını
        //vb.bir sürü şey bulabiliriz
        locationListener=LocationListener{
            it.
        }*/

        //LocationListener bir arayüzdür bundan dolayı böyle oluşturmak daha doğrudur.hem böyle yaparaktan farklı fonksiyonları
        // da kullanabiliriz.ör:onProviderdisabled veya enabled,onStatusChanged vb.bir kaçtanen daha fonk.var
        locationListener=object:LocationListener{
            override fun onLocationChanged(location: Location) {
                println("latitude:"+location.latitude+"longitude"+location.longitude)

            }

            //bu olmadan bazen program çalışmıyor.
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
                super.onStatusChanged(provider, status, extras)
            }

        }


        // ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
        // ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED)

        //izin verilmedi
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                Snackbar.make(binding.root,"need permission",Snackbar.LENGTH_INDEFINITE).setAction("give permission"){
                    //izin verilmedii çin izin isteniyor
                    ActivityCompat.requestPermissions(this@MapsActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
                }
            }
            else{
                //izin verilmediği için izin isteniyor
                ActivityCompat.requestPermissions(this@MapsActivity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1)
            }
        }
        else{
            //izin verilmiş.
           locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)


            //konuma mavi yuvarlak bir sembol koyar
            mMap.isMyLocationEnabled=true

        }

    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==1){
            //grantResults.size nedir araştır
            if(grantResults.size>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){

                //izin verilmiş.
                //tekrardan buradan izin almak gerekiyor.kabul etmiyor.normal yoldan izin istesek yine de eklemek gerekiyor izni
                if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0f,locationListener)
                println(1)
            }
        }
        else{
            Toast.makeText(this@MapsActivity,"not allowed",Toast.LENGTH_LONG).show()
        }
    }

    //haritaya uzun tıklama sonrası burası çalışacaktır
    override fun onMapLongClick(p0: LatLng) {
        //daha çnce var olan markerları siler
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(p0).title("Place ı liked"))
        selectedLatitude=p0.latitude
        selectedLongitude=p0.longitude

    }


    fun saveBtn(view: View){
        val place = Place(selectedLatitude!!,selectedLongitude!!,binding.placeTxt.text.toString())

        //compositeDisposable'ı güzelce araştır
        compositeDisposable.add(
            placeDao.insert(place)
                    //schedulers rxjavadan gelen bir sınıf ve io'ya erişimi sağlıyor
                .subscribeOn(Schedulers.io())
                    //mainthread sadece android schedulerste var
                .observeOn(AndroidSchedulers.mainThread())
                    //şöyle referans verebiliyoruz:"this::handleResponse"
                .subscribe(this::handleResponse)
        )
    }
    fun handleResponse(){
        val intent=Intent(this,MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
    }

    fun deleteBtn(view: View){

    }

    override fun onDestroy() {
        super.onDestroy()

        //kullanıp atar.yer tasarrufu sağlar.fazla yer kaplayan uygulamalarda kullanılır.mesela şu an rxjavada kullanasak da olur
        compositeDisposable.clear()
    }
}