package com.sghore.chimtubeworld.core.data.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object FirestoreModule {

    @Singleton
    @Provides
    fun provideFirestore() =
        FirebaseFirestore.getInstance()
}
