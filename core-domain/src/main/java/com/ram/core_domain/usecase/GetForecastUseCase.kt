package com.ram.core_domain.usecase

import com.ram.core_domain.NETWORK_RESULT
import com.ram.core_domain.models.ForeCastResponse
import com.ram.core_domain.repository.WeatherRepository
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(lat: Double, lon: Double) : NETWORK_RESULT<ForeCastResponse> {
        return weatherRepository.getForeCaseData(lat, lon)
    }
}