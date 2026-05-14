package com.ram.local_weather.viewmodels

import android.annotation.SuppressLint
import android.app.Application
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
import com.ram.core_domain.models.ForeCastResponse
import com.ram.core_domain.models.WeatherResponse
import com.ram.core_domain.usecase.GetForecastUseCase
import com.ram.core_domain.usecase.GetLocationDataUseCase
import com.ram.core_domain.usecase.GetWeatherUseCase
import com.ram.local_weather.UILOGIC_STATE
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
    private val application: Application,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val getLocationDataUseCase: GetLocationDataUseCase,
    private val checkerUtil: CheckerUtil
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


    private var _uiState = MutableStateFlow<UILOGIC_STATE>(UILOGIC_STATE.LOGIC_LOADING)
    val uiLogicState = _uiState.asStateFlow()

    var _searchWeatherData = mutableStateOf<WeatherResponse?>(null)
    val searchWeather = _searchWeatherData

    var _refreshCount = mutableStateOf(0)
    val refreshCount = _refreshCount

    init {
        lastExecutedTime = null
        stopLocationUpdate()
        checkAppState()
    }

    fun checkAppState() : UILOGIC_STATE{
        when {
           !checkerUtil.checkLocationPermission(application) -> _uiState.value = UILOGIC_STATE.LOGIC_PERMISSION_NEEDED
            !checkerUtil.checkLocationEnabled(application) -> {
                 stopLocationUpdate()
                _uiState.value = UILOGIC_STATE.LOGIC_LOCATION_NEEDED
            }
            else -> _uiState.value = UILOGIC_STATE.LOGIC_APP_READY
        }
        return _uiState.value
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
            2000L
        ).build()

        locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let {
                    _location.value = it
                    Log.d("REMREM", "onLocationResult: before checking")
                    if(canExecuteFunction()) {
                        Log.d("REMREM", "onLocationResult: after checking")
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
    fun updatePermission(isGranted: Boolean) : Boolean {
        _permissionGranted.value = isGranted
        return _permissionGranted.value
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
                    val address = addresses[0]
                    updateAddress(address)
                    viewModelScope.launch(Dispatchers.IO) {
                        val weatherResponse = getWeatherUseCase(address.latitude, address.longitude)
                        val foreCastResponse =
                            getForecastUseCase(address.latitude, address.longitude)
                        delay(200)
                        _weatherData.value = weatherResponse.data
                        _forecastData.value = foreCastResponse.data
                        _refreshCount.value = _refreshCount.value + 1
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

    fun getWeatherDataWithLocation(location: String) {
        viewModelScope.launch(Dispatchers.IO) {
            stopLocationUpdate()
            weatherData.value = null
            Log.d("RESTP", "getWeatherDataWithLocation: calling the data")
            val result = getLocationDataUseCase(location)
            Log.d("RESTP", "getWeatherDataWithLocation: result of the data : $result")
            _searchWeatherData.value = result.data

        }
    }

    fun resetSearchWeather() {
        _searchWeatherData.value = null
    }
}