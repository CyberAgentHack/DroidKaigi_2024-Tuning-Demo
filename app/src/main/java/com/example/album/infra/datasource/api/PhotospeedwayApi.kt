package com.example.album.infra.datasource.api

import com.example.album.infra.datasource.api.request.FavoriteRequest
import com.example.album.infra.datasource.api.response.AlbumResponse
import com.example.album.infra.datasource.api.response.ExifInfoResponse
import com.example.album.infra.datasource.api.response.PhotoResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface PhotospeedwayApi {

  @GET
  suspend fun getAlbum(
    @Url url: String
  ): AlbumResponse

  @GET
  suspend fun getFavorite(
    @Url url: String
  ): List<PhotoResponse>

  @POST
  suspend fun addFavorite(
    @Url url: String,
    @Body request: FavoriteRequest
  ): PhotoResponse

  @GET
  suspend fun getExifInfo(
    @Url url: String
  ): ExifInfoResponse
}
