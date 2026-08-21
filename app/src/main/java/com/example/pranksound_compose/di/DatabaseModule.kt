package com.example.pranksound.di

import android.content.Context
import androidx.room.Room
import com.example.pranksound.data.db.MyRoomDatabase
import com.example.pranksound.data.db.dao.CustomSoundDao
import com.example.pranksound.data.db.dao.FavoriteSoundDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): MyRoomDatabase {
        return Room.databaseBuilder(
            appContext, MyRoomDatabase::class.java, "my_db"
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    @Singleton
    fun provideFavoriteSoundDao(appDatabase: MyRoomDatabase): FavoriteSoundDao {
        return appDatabase.favoriteSound()
    }

    @Provides
    @Singleton
    fun provideCustomSoundDao(appDatabase: MyRoomDatabase): CustomSoundDao {
        return appDatabase.customSound()
    }



}