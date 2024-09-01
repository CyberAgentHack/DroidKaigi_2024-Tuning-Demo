package com.example.album.di

import android.content.Context
import androidx.room.Room
import com.example.album.infra.datasource.db.PhotospeedwayDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
internal object DatabaseModule {

  @Provides
  @Singleton
  fun provideDatabase(
    @ApplicationContext context: Context
  ): PhotospeedwayDatabase {
    return Room.databaseBuilder(
      context,
      PhotospeedwayDatabase::class.java,
      "photospeedway_database"
    ).build()
  }
}
