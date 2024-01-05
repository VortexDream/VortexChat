package com.vortex.android.vortexchat.di

import com.google.firebase.database.FirebaseDatabase
import com.vortex.android.vortexchat.firebase.BaseAuthenticator
import com.vortex.android.vortexchat.firebase.BaseDatabase
import com.vortex.android.vortexchat.firebase.FirebaseAuthenticator
import com.vortex.android.vortexchat.firebase.OnlineDatabase
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
    fun provideDatabase(firebaseDatabase : FirebaseDatabase, repository: BaseAuthRepository) : BaseDatabase {
        return OnlineDatabase(firebaseDatabase, repository)
    }
}