package com.ram.core_domain.usecase

import com.ram.core_domain.NETWORK_RESULT
import com.ram.core_domain.models.WeatherResponse
import com.ram.core_domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(lat: Double, lon: Double): NETWORK_RESULT<WeatherResponse> {
        return try {
            withContext(Dispatchers.IO) {
                weatherRepository.getWeatherData(lat, lon)
            }
        } catch (e: IOException) {
            NETWORK_RESULT.Error(null, e.message)
        }

    }
}