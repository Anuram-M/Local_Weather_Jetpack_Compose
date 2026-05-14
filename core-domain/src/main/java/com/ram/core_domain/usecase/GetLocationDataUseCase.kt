package com.ram.core_domain.usecase

import com.ram.core_domain.NETWORK_RESULT
import com.ram.core_domain.models.WeatherResponse
import com.ram.core_domain.repository.WeatherRepository
import javax.inject.Inject

class GetLocationDataUseCase @Inject constructor(val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(queryLocation: String) : NETWORK_RESULT<WeatherResponse> {
        return weatherRepository.getWeatherDataFromLocation(location = queryLocation)
    }
}