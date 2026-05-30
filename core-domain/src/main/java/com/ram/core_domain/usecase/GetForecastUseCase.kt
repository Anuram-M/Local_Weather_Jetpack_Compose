package com.ram.core_domain.usecase

import com.ram.core_domain.NETWORK_RESULT
import com.ram.core_domain.models.ForeCastResponse
import com.ram.core_domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(lat: Double, lon: Double): NETWORK_RESULT<ForeCastResponse> {
        return try {
            withContext(Dispatchers.IO) {
                weatherRepository.getForeCaseData(lat, lon)
            }
        } catch (e: IOException) {
            NETWORK_RESULT.Error(null, e.message)
        }

    }
}