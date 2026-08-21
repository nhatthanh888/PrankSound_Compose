package com.example.pranksound.di

import android.content.Context
import com.example.pranksound.data.db.dao.CustomSoundDao
import com.example.pranksound.data.db.dao.FavoriteSoundDao
import com.example.pranksound.data.dto.prank.FavoriteSound
import com.example.pranksound.data.local.LocalData
import com.example.pranksound.utils.Network
import com.example.pranksound.utils.NetworkConnectivity
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import javax.inject.Singleton
import kotlin.coroutines.CoroutineContext

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideLocalRepository(
        @ApplicationContext context: Context,
        favoriteSoundDao: FavoriteSoundDao,
        customSoundDao: CustomSoundDao,
        ): LocalData {
        return LocalData(context, favoriteSoundDao, customSoundDao)
    }

    @Provides
    @Singleton
    fun provideCoroutineContext(): CoroutineContext {
        return Dispatchers.IO
    }

    @Provides
    @Singleton
    fun provideNetworkConnectivity(@ApplicationContext context: Context): NetworkConnectivity {
        return Network(context)
    }
}
