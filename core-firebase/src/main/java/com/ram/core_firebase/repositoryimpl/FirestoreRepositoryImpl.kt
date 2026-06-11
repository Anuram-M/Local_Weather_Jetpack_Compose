package com.ram.core_firebase.repositoryimpl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.google.firebase.messaging.FirebaseMessaging
import com.ram.core_domain.models.ReleaseData
import com.ram.core_firebase.repository.FirestoreRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
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

    override fun fetchReleaseNotes(): Flow<List<ReleaseData>> = callbackFlow {
        val listener = FirebaseFirestore.getInstance().collection("update_notes")
            .addSnapshotListener { snapshots, exception ->
                if (exception != null) {
                    close(exception)
                    return@addSnapshotListener
                }
                val docs = snapshots?.documents?.map { documentSnapshot ->
                    ReleaseData(
                        version = documentSnapshot.get("version").toString(),
                        notes = documentSnapshot.get("notes").toString()
                    )
                } ?: emptyList()
                trySend(docs.sortedByDescending { it.version })
            }

        awaitClose {
            listener.remove()
        }
    }.flowOn(Dispatchers.IO)

    override suspend fun topicSubscriptionStatus(subPreference: Boolean): Flow<String> = callbackFlow{
            if(subPreference.equals(true)) {
                FirebaseMessaging.getInstance().subscribeToTopic("app_update")
                    .addOnCompleteListener { result ->
                        if(result.isSuccessful) {
                            trySend("Subscribed")
                        } else {
                            trySend("")
                        }
                        close()
                    }.addOnFailureListener {
                        trySend("")
                        close()
                    }
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic("app_update")
                    .addOnCompleteListener { result ->
                        if (result.isSuccessful) {
                            trySend("UnSubscribed")
                        } else {
                            trySend("")
                        }
                        close()
                    }.addOnFailureListener {
                        trySend("")
                        close()
                    }
            }

        awaitClose {  }

    }.flowOn(Dispatchers.IO)
}