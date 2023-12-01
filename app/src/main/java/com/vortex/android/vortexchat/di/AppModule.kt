package com.vortex.android.vortexchat.di

import com.vortex.android.vortexchat.firebase.BaseAuthenticator
import com.vortex.android.vortexchat.firebase.FirebaseAuthenticator
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

    /**All of our application dependencies shall be provided here*/

    //this means that anytime we need an authenticator Dagger will provide a Firebase authenticator.
    //in future if you want to swap out Firebase authentication for your own custom authenticator
    //you will simply come and swap here.
    @Singleton
    @Provides
    fun provideAuthenticator() : BaseAuthenticator {
        return FirebaseAuthenticator()
    }

    //this just takes the same idea as the authenticator. If we create another repository class
    //we can simply just swap here
    @Singleton
    @Provides
    fun provideRepository(authenticator : BaseAuthenticator) : BaseAuthRepository {
        return AuthRepository(authenticator)
    }
}