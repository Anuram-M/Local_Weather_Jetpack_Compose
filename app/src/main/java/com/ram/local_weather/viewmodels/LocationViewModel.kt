package com.ram.local_weather.viewmodels

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.location.Location
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.insertSeparators
import androidx.paging.map
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.google.android.play.agesignals.AgeSignalsManager
import com.google.android.play.agesignals.AgeSignalsRequest
import com.google.android.play.agesignals.model.AgeSignalsVerificationStatus
import com.ram.core_database.dto.GPSandWeatherModel
import com.ram.core_database.entity.CurrentWeatherAndForecast
import com.ram.core_database.entity.WeatherHistory
import com.ram.core_database.mapper.toMappedForecast
import com.ram.core_database.mapper.toMappedWeather
import com.ram.core_database.repository.CurrentWeatherAndForecastRepository
import com.ram.core_database.repository.WeatherHistoryRepository
import com.ram.core_domain.NETWORK_RESULT
import com.ram.core_domain.models.ForeCastResponse
import com.ram.core_domain.models.WeatherResponse
import com.ram.core_domain.usecase.GetForecastUseCase
import com.ram.core_domain.usecase.GetLocationDataUseCase
import com.ram.core_domain.usecase.GetWeatherUseCase
import com.ram.core_firebase.repository.FirestoreRepository
import com.ram.local_weather.stateclass.HistoryUIData
import com.ram.local_weather.stateclass.NavStateClass
import com.ram.local_weather.stateclass.UIStateClass
import com.ram.local_weather.util.CheckerUtil
import com.ram.local_weather.util.DateConvertor
import com.ram.local_weather.util.PREF_KEYS
import com.ram.local_weather.util.SharedPrefUtil
import com.ram.local_weather.widget.WidgetDataHandler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    val fusedLocationProviderClient: FusedLocationProviderClient,
    private val getWeatherUseCase: GetWeatherUseCase,
    private val getForecastUseCase: GetForecastUseCase,
    private val getLocationDataUseCase: GetLocationDataUseCase,
    private val checkerUtil: CheckerUtil,
    private val currentWeatherRepository: CurrentWeatherAndForecastRepository,
    private val weatherHistoryRepository: WeatherHistoryRepository,
    private val firebaserepository: FirestoreRepository,
    private val ageSignalsManager: AgeSignalsManager,
    private val widgetDataHandler: WidgetDataHandler
) : ViewModel() {

    private var _permissionGranted = false

    private var lastExecutedTime: Long? = null

    lateinit var locationCallback: LocationCallback

    private var _weatherDataUI = MutableStateFlow(UIStateClass())
    val weatherDataUI = _weatherDataUI.asStateFlow()

    var _refreshCount = mutableStateOf(0)
    val refreshCount = _refreshCount

    val searchLocations: StateFlow<List<String>> =
        firebaserepository.fetchCity().distinctUntilChanged().stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = emptyList()
        )

//    val historyData = weatherHistoryRepository.fetchHistory().stateIn(
//        scope = viewModelScope,
//        started = SharingStarted.WhileSubscribed(3000),
//        initialValue = emptyList()
//    )

    var _navState = Channel<NavStateClass>(Channel.BUFFERED)
    val navEvents = _navState.receiveAsFlow()

    private var lastProcessedLocation: Location? = null

    private val _notificationPermission = MutableStateFlow(false)
    val notificationPermission = _notificationPermission.asStateFlow()

    private val _isAlreadyAsked = MutableStateFlow(false)
    val alreadyAskedNotificationPermission = _isAlreadyAsked.asStateFlow()

    val releaseNotes = firebaserepository.fetchReleaseNotes().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(3000),
        initialValue = emptyList()
    )

    val subscriptionStatusChange = MutableSharedFlow<Boolean>(replay = 1)

    @OptIn(ExperimentalCoroutinesApi::class)
    val subscriptionStatus = subscriptionStatusChange
        .flatMapLatest { status ->
            firebaserepository.topicSubscriptionStatus(status)
        }
        .distinctUntilChanged()
        .onEach { staus ->
            when(staus) {
                 "Subscribed" -> SharedPrefUtil.saveBoolean(PREF_KEYS.NOTIFICATION_TOPIC_SUBSCRIPTION.name, true)
                 "UnSubscribed" -> SharedPrefUtil.saveBoolean(PREF_KEYS.NOTIFICATION_TOPIC_SUBSCRIPTION.name, false)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(3000),
            initialValue = if(SharedPrefUtil.getBoolean(PREF_KEYS.NOTIFICATION_TOPIC_SUBSCRIPTION.name)) "Subscribed" else "UnSubscribed"
        )

    val historyContentPageSize = MutableStateFlow(10)

    private val historyListingType = MutableStateFlow("Timeline")
    val historyType = historyListingType.asStateFlow()

    fun updatePageSize(newValue: Int) {
        historyContentPageSize.value = newValue
    }

    fun resetPageSize() {
        historyContentPageSize.value = 10
    }

    fun updatePageListing(newType: String) {
        resetPageSize()
        historyListingType.value = newType
    }

    val pagedHistory: Flow<PagingData<HistoryUIData>> =
        historyContentPageSize
            .flatMapLatest {
                historyListingType.flatMapLatest { type ->
                    if(type.equals("Timeline")) {
                        weatherHistoryRepository.fetchHistoryP(it)
                    } else {
                        weatherHistoryRepository.fetchHistoryPByPlace(it)
                    }
                }.map { pagingData ->
                    pagingData.map {  HistoryUIData.Item(it) }
                }.map { pagingData ->
                    pagingData.insertSeparators { before: HistoryUIData.Item?, after: HistoryUIData.Item? ->
                        if(historyListingType.value.equals("Timeline")) {
                            val currentMonth = after?.let {
                                DateConvertor.getMonthGroup(after?.data?.lastChecked!!)
                            }
                            val previousMonth = before?.let {
                                DateConvertor.getMonthGroup(before?.data?.lastChecked!!)
                            }
                            if(currentMonth != null && currentMonth != previousMonth) {
                                HistoryUIData.Header(currentMonth)
                            } else null
                        } else {
                            val currentLetter = after?.let {
                                it.data.place[0].uppercase()
                            }
                            val previousLetter = before?.let {
                                it.data.place[0].uppercase()
                            }
                            if(currentLetter != null && currentLetter != previousLetter) {
                                HistoryUIData.Header(currentLetter)
                            } else {
                                null
                            }
                        }
                    }
                }
            }.cachedIn(viewModelScope)

    init {
        fetchData()
        lastExecutedTime = null
        initializeAgeSignalManager()
        fetchPermissionState()
    }

    fun fetchPermissionState() {
        viewModelScope.launch {
            _notificationPermission.value = checkerUtil.checkNotificationPermission()
            _isAlreadyAsked.value = SharedPrefUtil.getBoolean(PREF_KEYS.ALREADY_ASKED_NOTIFICATION_PERMISSION.name)
            SharedPrefUtil.saveBoolean(PREF_KEYS.NOTIFICATION_PERMISSION.name, _notificationPermission.value)
            modifyTopicSubscription()
        }
    }

    private fun modifyTopicSubscription() {
        val topicStatus = SharedPrefUtil.getBoolean(PREF_KEYS.NOTIFICATION_TOPIC_SUBSCRIPTION.name)
        if(_notificationPermission.value && !topicStatus) {
            updateTopicSubscription(true)
        } else if(!_notificationPermission.value && topicStatus) {
            updateTopicSubscription(false)
        }
    }

    fun updateTopicSubscription(subPreference: Boolean) {
        subscriptionStatusChange.tryEmit(subPreference)
    }

    fun checkNotificationPermission(): Boolean {
        return checkerUtil.checkNotificationPermission()
    }

    fun openAppSettings(context: Context) {
        val intent =  Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            // Forms a deep-link directly to your specific app package configuration page
            data = Uri.fromParts("package", context.packageName, null)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)
    }

    fun updateAlreadyAsked() {
        viewModelScope.launch {
            SharedPrefUtil.saveBoolean(PREF_KEYS.ALREADY_ASKED_NOTIFICATION_PERMISSION.name, true)
        }
    }

    fun updateNotificationPermission(permissionGranted: Boolean) {
        viewModelScope.launch {
            _notificationPermission.value = permissionGranted
            SharedPrefUtil.saveBoolean(PREF_KEYS.NOTIFICATION_PERMISSION.name, _notificationPermission.value)
        }
    }

    fun initializeAgeSignalManager() {
        ageSignalsManager.checkAgeSignals(AgeSignalsRequest.builder().build())
            .addOnSuccessListener { result ->
                if (result.userStatus() == null) {
                    checkAppState()
                } else {
                    when (result.userStatus()) {
                        AgeSignalsVerificationStatus.SUPERVISED_APPROVAL_DENIED,
                        AgeSignalsVerificationStatus.SUPERVISED_APPROVAL_PENDING -> {
                            triggerNavigation(NavStateClass.NavigateToAppRestricted)
                        }

                        AgeSignalsVerificationStatus.UNKNOWN -> {
                            checkAppState()
                        }

                        else -> {
                            checkAppState()
                        }
                    }
                }
            }
            .addOnFailureListener { error ->
                checkAppState()
            }

//        val fakeManager = FakeAgeSignalsManager()
//
//        // 2. Mock a simulated Texas minor who has been DENIED parental approval
//        val mockTexasBlockedUser = AgeSignalsResult.builder()
//            .setUserStatus(AgeSignalsVerificationStatus.VERIFIED)
//            .setAgeLower(13)
//            .setAgeUpper(17)
//            .build()
//
//        // 3. Inject the fake result state into the manager's queue
//        fakeManager.setNextAgeSignalsResult(mockTexasBlockedUser)
//
//        // 4. Run the check (it will immediately intercept and return your mock user)
//        fakeManager.checkAgeSignals(AgeSignalsRequest.builder().build())
//            .addOnSuccessListener { result ->
//                Log.d("AGELIST", "checkAppState: ${result.userStatus()}")
//
//                if(result.userStatus() == null) {
//                    checkAppState()
//                } else {
//                    when (result.userStatus()) {
//                        AgeSignalsVerificationStatus.SUPERVISED_APPROVAL_DENIED,
//                        AgeSignalsVerificationStatus.SUPERVISED_APPROVAL_PENDING -> {
//                            triggerNavigation(NavStateClass.NavigateToAppRestricted)
//                        }
//
//                        AgeSignalsVerificationStatus.UNKNOWN -> {
//                            checkAppState()
//                        }
//                        else -> {
//                            checkAppState()
//                        }
//                    }
//                }
//            }
    }

    private fun fetchData() {
        viewModelScope.launch(Dispatchers.IO) {
            val data = currentWeatherRepository.fetchData().firstOrNull()
            data?.let {
                _weatherDataUI.update {
                    it.copy(
                        isLoading = false,
                        isCurrent = true,
                        weatherData = data.weather,
                        forecastData = data.forecast
                    )
                }
            }
        }
    }

    fun checkAppState() {
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
                    return@withContext NavStateClass.NavigateToPermission
                }
                val gpsEnabled = checkerUtil.checkLocationEnabled()
                val enableGPSShown = SharedPrefUtil.getBoolean(PREF_KEYS.ALREADY_SHOWN.name)
                if (locationPermissionGranted && !enableGPSShown && !gpsEnabled) {
                    stopLocationUpdate()
                    SharedPrefUtil.saveBoolean(PREF_KEYS.ALREADY_SHOWN.name, true)
                    return@withContext NavStateClass.NavigateToGPS
                }
                SharedPrefUtil.saveBoolean(PREF_KEYS.ALREADY_SHOWN.name, true)
                NavStateClass.NavigateToHome
            }
            triggerNavigation(newState)
        }
    }

    fun triggerNavigation(event: NavStateClass) {
        viewModelScope.launch {
            _navState.send(event)
        }
    }

    fun updateLoading(currentStatus: Boolean) {
        _weatherDataUI.update { it.copy(isLoading = currentStatus) }
    }


    @SuppressLint("MissingPermission")
    fun getLocationUpdates(context: Context) {
        if (!_permissionGranted) {
            return
        }
        if (!checkerUtil.checkLocationEnabled() || _weatherDataUI.value.searchWeather != null) {
            updateLoading(false)
            return
        }
        stopLocationUpdate()
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            20000L
        ).apply {
            // 🔑 STEP 2: Allow a slightly lower accuracy fallback if GPS isn't matching
            setMinUpdateDistanceMeters(10f)
        }.build()

        locationCallback = object : LocationCallback() {
            @RequiresApi(Build.VERSION_CODES.TIRAMISU)
            override fun onLocationResult(result: LocationResult) {
                super.onLocationResult(result)
                result.lastLocation?.let {
                    val newLocation = result.lastLocation
                    // Guard clause: Only query geocoder if the user moved more than 20 meters
                    val distance =
                        lastProcessedLocation?.distanceTo(newLocation!!) ?: Float.MAX_VALUE
                    if (canExecuteFunction()) {
                        getPlaceName(context, it)
                    }
                }
            }
        }
        fusedLocationProviderClient.requestLocationUpdates(
            request,
            Executors.newSingleThreadExecutor(),
            locationCallback,
        )
    }

    @SuppressLint("MissingPermission")
    fun updatePermission(isGranted: Boolean): Boolean {
        _permissionGranted = isGranted
        return _permissionGranted
    }

    fun stopLocationUpdate() {
        if (::locationCallback.isInitialized) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    fun getPlaceName(context: Context, location: Location) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
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
                if (addresses != null && addresses.isNotEmpty()) {
                    fetchWeather(addresses[0])
                }
            }
        } catch (exception: Exception) {
            Log.d("LOcaTIom", "getPlaceName: $exception")
        }
    }

    private fun fetchWeather(address: android.location.Address) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val weatherData = async {
                    getWeatherUseCase(
                        address.latitude,
                        address.longitude
                    )
                }
                val forecastData = async {
                    getForecastUseCase(
                        address.latitude,
                        address.longitude
                    )
                }
                val weatherResult = weatherData.await()
                val forecastResult = forecastData.await()
                if (weatherResult is NETWORK_RESULT.Success && forecastResult is NETWORK_RESULT.Success) {
                    val currentWeather = GPSandWeatherModel(
                        subLocality = address.subLocality,
                        locality = address.locality,
                        weatherResponse = weatherResult.data!!
                    ).toMappedWeather()
                    val currentForecast =
                        (forecastResult.data as ForeCastResponse).list.map { it.toMappedForecast() }

                    widgetDataHandler.updateWidgetData(address.subLocality ?: address.locality, currentWeather.mainTemp.toInt().toString(), currentWeather.description!!)
                    currentWeatherRepository.insertData(
                        CurrentWeatherAndForecast(
                            id = 1,
                            weather = currentWeather,
                            forecast = currentForecast
                        )
                    )
                    _refreshCount.value += 1
                    _weatherDataUI.update {
                        it.copy(
                            isLoading = false,
                            isCurrent = true,
                            weatherData = currentWeather,
                            forecastData = currentForecast,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _weatherDataUI.update {
                    it.copy(
                        isLoading = false,
                        isCurrent = false,
                        weatherData = null,
                        forecastData = null,
                        error = "${e.message}"
                    )
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
        if (diff > 60000) {
            lastExecutedTime = currentTime
            return true
        } else {
            return false
        }
    }

    fun getWeatherDataWithLocation(location: String) {
        viewModelScope.launch(Dispatchers.IO) {
            stopLocationUpdate()
            _weatherDataUI.update { it.copy(isLoading = true, isCurrent = false) }
            val searchedWeather = async { getLocationDataUseCase(location) }
            val searchResult = searchedWeather.await()
            if (searchResult is NETWORK_RESULT.Success) {
                val mappedWeather = GPSandWeatherModel(
                    locality = searchResult.data?.name,
                    weatherResponse = searchResult.data!!
                ).toMappedWeather()
                (searchResult.data as WeatherResponse)
                weatherHistoryRepository.insertWeather(
                    WeatherHistory(
                        place = searchResult.data?.name ?: "Unknown",
                        temp = searchResult.data?.main?.temp ?: 0.0,
                        lastChecked = System.currentTimeMillis()
                    )
                )
                _refreshCount.value += 1
                _weatherDataUI.update {
                    it.copy(
                        isLoading = false,
                        searchWeather = mappedWeather,
                        error = null
                    )
                }
            } else if (searchResult is NETWORK_RESULT.Error) {
                _weatherDataUI.update { it.copy(isLoading = false, error = searchResult.message) }
            }
        }
    }

    fun resetSearchWeather() {
        lastExecutedTime = null
        _weatherDataUI.update { it.copy(isLoading = true, searchWeather = null) }
    }
}