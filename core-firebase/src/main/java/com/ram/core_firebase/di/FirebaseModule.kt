package com.ram.core_firebase.di

import com.ram.core_firebase.repository.FirestoreRepository
import com.ram.core_firebase.repositoryimpl.FirestoreRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FirebaseModule {

    @Singleton
    @Binds
    abstract fun provideFirebaseRespository(
        firestoreRepositoryImpl: FirestoreRepositoryImpl
    ): FirestoreRepository
}