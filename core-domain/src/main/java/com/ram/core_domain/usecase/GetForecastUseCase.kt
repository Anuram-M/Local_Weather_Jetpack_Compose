package com.ram.core_domain.usecase

import com.ram.core_domain.NETWORK_RESULT
import com.ram.core_domain.repository.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class GetForecastUseCase @Inject constructor(val weatherRepository: WeatherRepository) {
    operator fun invoke(lat: Double, lon: Double) = flow {
        emit(weatherRepository.getForeCaseData(lat, lon))
    }.flowOn(Dispatchers.IO)
        .catch { exception ->
            emit(
                NETWORK_RESULT.Error(
                    data = null,
                    exception.message ?: "Exception occurred in fetching forecast data"
                )
            )
        }
}