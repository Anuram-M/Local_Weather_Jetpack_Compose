package com.ram.core_firebase.repositoryimpl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.ram.core_firebase.repository.FirestoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreRepositoryImpl @Inject constructor() : FirestoreRepository {
    override fun fetchCity(): Flow<List<String>> = flow {

       try {
           val snapshot = FirebaseFirestore.getInstance().collection("location").get().await()
           val doc = snapshot.documents.firstOrNull()
           val cities = if (doc != null && doc.exists()) {
               // Read your array field safely
               (doc.get("city list") as? List<*>)?.mapNotNull { it.toString() } ?: emptyList()
           } else {
               emptyList()
           }
           emit(cities)
       } catch (e: Exception) {
         emit(emptyList())
       }

    }.flowOn(Dispatchers.IO)
}