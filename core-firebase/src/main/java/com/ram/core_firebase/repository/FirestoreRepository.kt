package com.ram.core_firebase.repository

import kotlinx.coroutines.flow.Flow

interface FirestoreRepository {
    fun fetchCity() : Flow<List<String>>
}