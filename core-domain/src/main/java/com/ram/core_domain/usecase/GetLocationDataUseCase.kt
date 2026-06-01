package com.ram.core_domain.usecase

import com.ram.core_domain.NETWORK_RESULT
import com.ram.core_domain.models.WeatherResponse
import com.ram.core_domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class GetLocationDataUseCase @Inject constructor(val weatherRepository: WeatherRepository) {
    suspend operator fun invoke(queryLocation: String): NETWORK_RESULT<WeatherResponse> {
        return try {
            withContext(Dispatchers.IO) {
                weatherRepository.getWeatherDataFromLocation(queryLocation)
            }
        } catch (e: Exception) {
            NETWORK_RESULT.Error(null, e.message)
        }
    }
}