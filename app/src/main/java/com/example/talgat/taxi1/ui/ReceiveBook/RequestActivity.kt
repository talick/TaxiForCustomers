package com.example.talgat.taxi1.ui.ReceiveBook

import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.support.design.widget.NavigationView
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.GravityCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RelativeLayout
import com.example.talgat.taxi1.R
import com.example.talgat.taxi1.ui.login.LoginActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.android.synthetic.main.activity_request.*
import kotlinx.android.synthetic.main.content_request.*
import kotlinx.android.synthetic.main.nav_header_request.*
import java.io.IOException
import java.util.*


class RequestActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, GoogleMap.OnMyLocationButtonClickListener, GoogleMap.OnCameraIdleListener {


    private val TAG: String = this.javaClass.simpleName
    var PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: Int = 1
    var locationPermissionGranted: Boolean = false
    private var map: GoogleMap? = null

    private val BISHKEK_CENTER = LatLng(42.87928194419922, 74.59752839058638)


    private val viewModel: RequestViewModel by lazy {
        ViewModelProviders.of(this).get(RequestViewModel::class.java)
    }

    lateinit var mapView: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_request)

        getLocationPermission()
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        updateUI(FirebaseAuth.getInstance().currentUser)

        mapView = supportFragmentManager.findFragmentById(R.id.map2) as SupportMapFragment
        mapView.getMapAsync(this)
    }

    private fun getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.applicationContext,
                        android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        locationPermissionGranted = false
        when (requestCode) {
            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                }
            }
        }

        updateLocationUI()
    }

    private fun updateLocationUI() {
        if (map == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                map!!.isMyLocationEnabled = true
                map!!.uiSettings.isMyLocationButtonEnabled = true
            } else {
                map!!.isMyLocationEnabled = false
                map!!.uiSettings.isMyLocationButtonEnabled = false
            }
        } catch (e: SecurityException) {
            Log.e("Exceptioin: ", e.message)
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.recieve_book, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_settings -> return true
            else -> return super.onOptionsItemSelected(item)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_phone -> {
                startActivity(Intent(this, LoginActivity::class.java))
            }
            R.id.nav_payment -> {

            }
            R.id.nav_info -> {

            }
            R.id.nav_manage -> {

            }
            R.id.nav_support_service -> {

            }
            R.id.nav_signout -> {

                FirebaseAuth.getInstance().signOut()
                updateUI(FirebaseAuth.getInstance().currentUser)
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun updateUI(user: FirebaseUser?) {
        val header= nav_view.getHeaderView(0)
        val menu = nav_view.menu

        if (user == null) {
            menu.getItem(5).setVisible(false)
            menu.getItem(0).setVisible(true)
            current_phone_number.visibility = View.GONE
        } else {
            menu.getItem(0).setVisible(false)
            menu.getItem(5).setVisible(true)
            current_phone_number.visibility = View.VISIBLE
        }

    }


    fun onClickOpenDrawer(view: View) {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            drawer_layout.openDrawer(GravityCompat.START)
        }
    }


    override fun onMapReady(p0: GoogleMap?) {
        map = p0

        map!!.setPadding(16, 16, 16, 16)
        map!!.uiSettings.isRotateGesturesEnabled = false

        val locationButton: View = (mapView.view!!.findViewById<View>(Integer.parseInt("1")).parent as View).findViewById<View>(Integer.parseInt("2"))

        val rlp=locationButton.layoutParams as (RelativeLayout.LayoutParams)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP,0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_LEFT, 0)
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
        rlp.bottomMargin = 100


        map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(BISHKEK_CENTER, 14f))
        updateLocationUI()
        map!!.setOnMyLocationButtonClickListener(this)

        map!!.setOnCameraIdleListener(this)

    }

    override fun onCameraIdle() {
        val latLng: LatLng = map!!.cameraPosition.target

        val geocoder = Geocoder(this, Locale.getDefault())

        var addresses: List<Address>? = null

        try {
            addresses = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    1
            )
        } catch (e: IOException) {
            Log.e(TAG, "errorMessage: ", e)
        } catch (e: IllegalArgumentException) {
            Log.e(TAG, "errorMessage", e)
        }

        if (addresses == null || addresses.isEmpty()) {
            Log.e(TAG, "No Address found")
        } else {
            val address: Address = addresses[0]

            infoText.text = address.getAddressLine(0)

        }
    }


    override fun onMyLocationButtonClick(): Boolean {
        val manager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
        return false
    }

    private fun buildAlertMessageNoGps() {
        AlertDialog.Builder(this)
                .setMessage("Разрешите использовать  GPS")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id -> startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                .setNegativeButton("No") { dialog, id -> dialog.cancel() }
                .show()

    }


}
