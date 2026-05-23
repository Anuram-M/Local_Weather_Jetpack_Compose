package com.ram.core_firebase.repositoryimpl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.ram.core_firebase.repository.FirestoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor() : FirestoreRepository {
    override fun fetchCity(): Flow<List<String>> {
       return FirebaseFirestore.getInstance().collection("location")
            .snapshots()
            .map { querySnapshot ->
                val doc = querySnapshot.documents.firstOrNull()
                if (doc != null && doc.exists()) {
                    // Read your array field safely
                    val cities = doc.get("city list") as? List<*>

                    // Update your state flow
                    cities?.mapNotNull { it.toString() } ?: emptyList()
                } else {
                    emptyList()
                }
            }.flowOn(Dispatchers.IO)
    }
}