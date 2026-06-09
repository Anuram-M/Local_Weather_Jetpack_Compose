package com.ram.core_firebase.repository

import com.ram.core_domain.models.ReleaseData
import kotlinx.coroutines.flow.Flow

interface FirestoreRepository {
    fun fetchCity() : Flow<List<String>>
    fun fetchReleaseNotes(): Flow<List<ReleaseData>>
}