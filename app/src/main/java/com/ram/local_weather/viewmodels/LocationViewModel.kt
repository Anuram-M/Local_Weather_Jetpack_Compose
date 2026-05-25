package com.ram.local_weather.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.ram.core_database.mapper.toMappedWeather
import com.ram.core_database.dto.GPSandWeatherModel
import com.ram.core_database.dto.MappedForecast
import com.ram.core_database.dto.MappedWeather
import com.ram.core_database.entity.CurrentForecast
import com.ram.core_database.entity.CurrentWeather
import com.ram.core_database.mapper.toMappedForecast
import com.ram.core_database.repository.CurrentForecastRepository
import com.ram.core_database.repository.CurrentWeatherRepository
import com.ram.core_domain.NETWORK_RESULT
import com.ram.core_domain.models.ForeCastResponse
import com.ram.core_domain.models.WeatherResponse
import com.ram.core_domain.usecase.GetForecastUseCase
import com.ram.core_domain.usecase.GetLocationDataUseCase
import com.ram.core_domain.usecase.GetWeatherUseCase
import com.ram.core_firebase.repository.FirestoreRepository
import com.ram.local_weather.UILOGIC_STATE
import com.ram.local_weather.util.CheckerUtil
import com.ram.local_weather.util.PREF_KEYS
import com.ram.local_weather.util.SharedPrefUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    val fusedLocationProviderClient: FusedLocationProviderClient,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val getLocationDataUseCase: GetLocationDataUseCase,
    private val checkerUtil: CheckerUtil,
    private val currentWeatherRepository: CurrentWeatherRepository,
    private val currentForecastRepository: CurrentForecastRepository,
    private val firebaserepository: FirestoreRepository
) : ViewModel() {

    var _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private var _permissionGranted = mutableStateOf(false)

    private var _weatherData = mutableStateOf<WeatherResponse?>(null)
    val weatherData = _weatherData

    private var _mappedWeather = MutableStateFlow<MappedWeather?>(null)
    val mappedWeather = _mappedWeather.asStateFlow()

    private var _savedWeather = MutableStateFlow<MappedWeather?>(null)
    val savedWeather = _savedWeather.asStateFlow()

    private var _forecastData = mutableStateOf<List<MappedForecast>?>(null)
    val forecastData = _forecastData

    private var _noData = MutableStateFlow(false)
    val noData = _noData.asStateFlow()
    private var lastExecutedTime: Long? = null

    lateinit var locationCallback: LocationCallback


    private var _uiState = MutableStateFlow<UILOGIC_STATE>(UILOGIC_STATE.LOGIC_LOADING)
    val uiLogicState = _uiState.asStateFlow()

    var _searchWeatherData = mutableStateOf<WeatherResponse?>(null)
    val searchWeather = _searchWeatherData

    var _refreshCount = mutableStateOf(0)
    val refreshCount = _refreshCount


    private var _searchLoactions = MutableStateFlow<List<String>>(emptyList())
    var searchLocations = _searchLoactions.asStateFlow()

    init {
        fetchData()
        updateSearchLocations()
        lastExecutedTime = null
        stopLocationUpdate()
        checkAppState()
    }

    private fun fetchData() {
        viewModelScope.launch {
            launch {
                Log.d("DATAB", "fetchData: weather")
                val data = currentWeatherRepository.fetchWeather().firstOrNull()
                if (data != null) {
                    _mappedWeather.value = data.data
                }
            }
            launch {
                val data = currentForecastRepository.queryForecast().firstOrNull()
                if (data != null) {

                    Log.d("DATAB", "fetchData: ${data.data}")
                    _forecastData.value = data.data
                }
            }


        }
    }

    fun updateSearchLocations() {
        viewModelScope.launch {
            firebaserepository.fetchCity().collect {
                _searchLoactions.value = it
            }
        }
    }


    fun checkAppState(): UILOGIC_STATE {
        viewModelScope.launch {
            val newState = withContext(Dispatchers.IO) {
                val locationPermissionGranted = checkerUtil.checkLocationPermission()
                val locationPermissionAsked = SharedPrefUtil.getBoolean(
                    PREF_KEYS.PERMISSION_ALREADY_ASKED.name
                )

                if (!locationPermissionGranted && !locationPermissionAsked) {
                    SharedPrefUtil.saveBoolean(
                    PREF_KEYS.PERMISSION_ALREADY_ASKED.name, true
                )
                    return@withContext UILOGIC_STATE.LOGIC_PERMISSION_NEEDED
                }

                val gpsEnabled = checkerUtil.checkLocationEnabled(

                )
                val enableGPSShown = SharedPrefUtil.getBoolean(PREF_KEYS.ALREADY_SHOWN.name)
                if (locationPermissionGranted && !enableGPSShown && !gpsEnabled) {
                    stopLocationUpdate()
                    SharedPrefUtil.saveBoolean(PREF_KEYS.ALREADY_SHOWN.name, true)
                    return@withContext UILOGIC_STATE.LOGIC_LOCATION_NEEDED
                }
                SharedPrefUtil.saveBoolean(PREF_KEYS.ALREADY_SHOWN.name, true)
                UILOGIC_STATE.LOGIC_APP_READY
            }

            _uiState.value = newState
        }
        return _uiState.value
    }

    fun updateLoading(currentStatus: Boolean) {
        _isLoading.value = currentStatus
    }

    @SuppressLint("MissingPermission")
    fun getLocationUpdates(context: Context) {

        if (!_permissionGranted.value) {
            return
        }

        if (checkerUtil.checkLocationEnabled()) {
            updateLoading(true)
        } else {
            return
        }

        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            2000L
        ).build()

        locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let {
                    if (canExecuteFunction()) {
                        getPlaceName(context, it)
                    }
                    updateLoading(false)
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
    fun updatePermission(isGranted: Boolean): Boolean {
        _permissionGranted.value = isGranted
        return _permissionGranted.value
    }

    fun stopLocationUpdate() {
        if (::locationCallback.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    fun getPlaceName(context: Context, location: Location) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                geocoder.getFromLocation(
                    location.latitude, location.longitude, 1
                ) { addresses ->
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        fetchWeather(address)
                    }
                }
            } else {
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                if(addresses != null && addresses.isNotEmpty()) {
                    fetchWeather(addresses[0])
                }
            }
        } catch (exception: Exception) {
            Log.d("LOcaTIom", "getPlaceName: $exception")
        }
    }

    private fun fetchWeather(address: android.location.Address) {
        viewModelScope.launch {
            launch {

                getWeatherUseCase(
                    address.latitude,
                    address.longitude
                ).collect { weatherData ->

                    when (weatherData) {
                        is NETWORK_RESULT.Success -> {
                            weatherData.data?.let {
                                Log.d("DATAB", "fetchWeather: ${weatherData.data}")
                                val currentWeather = GPSandWeatherModel(
                                    address = address,
                                    weatherResponse = weatherData.data!!
                                )

                                withContext(Dispatchers.IO) {
                                    currentWeatherRepository.insertWeather(
                                        CurrentWeather(
                                            id = 0,
                                            isAvailable = true,
                                            data = currentWeather.toMappedWeather()
                                        )
                                    )
                                }

//                                            currentWeatherRepository.fetchWeather().first()
                                withContext(Dispatchers.Main) {
                                    _mappedWeather.value =
                                        currentWeather.toMappedWeather()

                                }
                            }
                        }

                        is NETWORK_RESULT.Error -> {}
                        is NETWORK_RESULT.Loading -> {}
                    }

                }
            }
            launch {
                getForecastUseCase(
                    address.latitude,
                    address.longitude
                ).collect { forecastWeather ->
                    when (forecastWeather) {
                        is NETWORK_RESULT.Success -> {


                            Log.d("DATAB", "fetchWeather: got forecast ${forecastWeather.data}")
                            withContext(Dispatchers.IO) {

                                val forecast = (forecastWeather.data as ForeCastResponse).list.map { it.toMappedForecast() }
//                                if(_forecastData.value != null) {
//                                    currentForecastRepository.updateForecast(
//                                        CurrentForecast(
//                                            id = 0,
//                                            data = forecast
//                                        )
//                                    )
//                                } else {
                                    currentForecastRepository.insertForecast(
                                        CurrentForecast(
                                            id = 1,
                                            data = forecast
                                        )
                                    )
//                                }
                            }
                            withContext(Dispatchers.Main) {
                                delay(200)
                                _isLoading.value = false
                                _forecastData.value = (forecastWeather.data as ForeCastResponse).list.map { it.toMappedForecast() }
                                _refreshCount.value = _refreshCount.value + 1
                            }
                        }

                        is NETWORK_RESULT.Error<*> -> {}
                        is NETWORK_RESULT.Loading<*> -> {}
                    }

                }
            }

        }
    }

    fun canExecuteFunction(): Boolean {
        val currentTime = System.currentTimeMillis()

        if (lastExecutedTime == null) {
            lastExecutedTime = currentTime
            return true
        }

        val diff = currentTime - lastExecutedTime!!
        if (diff > 30000) {
            lastExecutedTime = currentTime
            return true
        } else {
            return false
        }
    }

    fun getWeatherDataWithLocation(location: String) {
        viewModelScope.launch(Dispatchers.IO) {
            stopLocationUpdate()
            weatherData.value = null
            getLocationDataUseCase(location).collect { queryLocationWeather ->
                withContext(Dispatchers.Main) {

                    _searchWeatherData.value = queryLocationWeather.data
                }
            }
        }
    }

    fun resetSearchWeather() {
        _searchWeatherData.value = null
    }
}