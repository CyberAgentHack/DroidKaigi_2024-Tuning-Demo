package com.example.album.di

import com.example.album.infra.datasource.api.PhotospeedwayApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class ApiModule {

  @Singleton
  @Provides
  fun providePhotospeedwayApi(): PhotospeedwayApi {
    val retrofit = Retrofit.Builder()
      .baseUrl("https://photospeedway.dokup.dev")
      .addConverterFactory(
        Json.asConverterFactory("application/json; charset=UTF-8".toMediaType())
      )
      .client(
        OkHttpClient
          .Builder()
          .build()
      )
      .build()

    return retrofit.create(PhotospeedwayApi::class.java)
  }
}
