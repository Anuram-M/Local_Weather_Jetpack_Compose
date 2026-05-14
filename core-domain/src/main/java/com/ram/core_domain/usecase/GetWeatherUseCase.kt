package com.ram.core_domain.usecase

import com.ram.core_domain.NETWORK_RESULT
import com.ram.core_domain.models.WeatherResponse
import com.ram.core_domain.repository.WeatherRepository
import javax.inject.Inject

class GetWeatherUseCase @Inject constructor(val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(lat: Double, lon: Double) : NETWORK_RESULT<WeatherResponse> {
       return weatherRepository.getWeatherData(lat, lon)
    }
}