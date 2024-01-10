package com.vortex.android.vortexchat.di

import com.google.firebase.Firebase
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storage
import com.vortex.android.vortexchat.firebase.BaseAuthenticator
import com.vortex.android.vortexchat.firebase.BaseDatabase
import com.vortex.android.vortexchat.firebase.BaseStorage
import com.vortex.android.vortexchat.firebase.FirebaseAuthenticator
import com.vortex.android.vortexchat.firebase.OnlineDatabase
import com.vortex.android.vortexchat.firebase.OnlineStorage
import com.vortex.android.vortexchat.repository.AuthRepository
import com.vortex.android.vortexchat.repository.BaseAuthRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideAuthenticator(database : FirebaseDatabase) : BaseAuthenticator {
        return FirebaseAuthenticator(database)
    }

    @Singleton
    @Provides
    fun provideRepository(authenticator : BaseAuthenticator) : BaseAuthRepository {
        return AuthRepository(authenticator)
    }

    @Singleton
    @Provides
    fun provideFirebaseDatabase() : FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @Singleton
    @Provides
    fun provideFirebaseStorage() : FirebaseStorage {
        return Firebase.storage
    }

    @Singleton
    @Provides
    fun provideStorage(firebaseStorage: FirebaseStorage, repository: BaseAuthRepository) : BaseStorage {
        return OnlineStorage(firebaseStorage, repository)
    }

    @Singleton
    @Provides
    fun provideDatabase(firebaseDatabase : FirebaseDatabase, repository: BaseAuthRepository) : BaseDatabase {
        return OnlineDatabase(firebaseDatabase, repository)
    }
}