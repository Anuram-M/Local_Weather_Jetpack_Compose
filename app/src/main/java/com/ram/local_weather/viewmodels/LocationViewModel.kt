package com.ram.local_weather.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.ram.local_weather.MyApplication
import com.ram.local_weather.UILOGIC_STATE
import com.ram.local_weather.models.ForeCastResponse
import com.ram.local_weather.models.WeatherResponse
import com.ram.local_weather.repository.WeatherRepository
import com.ram.local_weather.util.CheckerUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    val fusedLocationProviderClient: FusedLocationProviderClient,
    private val application: MyApplication,
    private val weatherRepository: WeatherRepository
) : AndroidViewModel(application) {

    var _location = mutableStateOf<Location?>(null)
    val location = _location

    var _isLoading = mutableStateOf(true)
    val isLoading = _isLoading

    private var _permissionGranted = mutableStateOf(false)
    val permissionGranted = _permissionGranted

    private var _address = mutableStateOf<Address?>(null)
    val address = _address

    private var _weatherData = mutableStateOf<WeatherResponse?>(null)
    val weatherData = _weatherData

    private var _forecastData = mutableStateOf<ForeCastResponse?>(null)
    val forecastData = _forecastData

    private var lastExecutedTime: Long? = null

    lateinit var locationCallback: LocationCallback


    private var _uiState = MutableStateFlow<UILOGIC_STATE>(UILOGIC_STATE.LOGIC_PERMISSION_NEEDED)
    val uiLogicState = _uiState.asStateFlow()

    init {
        lastExecutedTime = null
        stopLocationUpdate()
        checkAppState()
    }

    fun checkAppState() {
        when {
           !CheckerUtil().checkLocationPermission(application) -> _uiState.value = UILOGIC_STATE.LOGIC_PERMISSION_NEEDED
            !CheckerUtil().checkLocationEnabled(application) -> {
                 stopLocationUpdate()
                _uiState.value = UILOGIC_STATE.LOGIC_LOCATION_NEEDED
            }
            else -> _uiState.value = UILOGIC_STATE.LOGIC_APP_READY
        }
    }

    @SuppressLint("MissingPermission")
    fun getLocationUpdates() {

        if(!_permissionGranted.value) {
            Log.d("OPOPOP", "getLocationUpdates: return")
            return
        }

        _isLoading.value = true

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).build()

        locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let {
                    _location.value = it
                    if(canExecuteFunction()) {
                        getPlaceName(application.applicationContext, it)
                    }
                    _isLoading.value = false
                }
            }
        }

        fusedLocationProviderClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    @SuppressLint("MissingPermission")
    fun updatePermission(isGranted: Boolean) {
        _permissionGranted.value = isGranted
    }

    fun stopLocationUpdate() {
        if(::locationCallback.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    fun updateAddress(currentAddress: Address?) {
        if(currentAddress != null) {
            _address.value = currentAddress
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun getPlaceName(context: Context, location: Location) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            geocoder.getFromLocation(location.latitude, location.longitude, 1
            ) { addresses ->
                if (addresses.isNotEmpty()) {
                    //Address[addressLines=[0:"27 A, EB Colony, Saravanampatti, Coimbatore, Tamil Nadu 641035, India"],feature=27 A,admin=Tamil Nadu,sub-admin=null,locality=Coimbatore,thoroughfare=null,postalCode=641035,countryCode=IN,countryName=India,hasLatitude=true,latitude=11.077613399999999,hasLongitude=true,longitude=76.9987225,phone=null,url=null,extras=null]
                    val address = addresses[0]
                    updateAddress(address)
                    viewModelScope.launch(Dispatchers.IO) {
                        val weatherResponse =
                            weatherRepository.getWeatherData(address.latitude, address.longitude)
                        val foreCastResponse =
                            weatherRepository.getForeCaseData(address.latitude, address.longitude)
                        delay(200)
                        _weatherData.value = weatherResponse.data
                        _forecastData.value = foreCastResponse.data
                    }
                }
            }
        } catch (exception: Exception) {
            Log.d("LOcaTIom", "getPlaceName: $exception")
        }
    }

    fun canExecuteFunction() : Boolean {
        val currentTime = System.currentTimeMillis()

        if(lastExecutedTime == null) {
            lastExecutedTime = currentTime
            return true
        }

        val diff = currentTime - lastExecutedTime!!
        if(diff > 30000) {
            lastExecutedTime = currentTime
            return true
        } else {
            return  false
        }
    }
}