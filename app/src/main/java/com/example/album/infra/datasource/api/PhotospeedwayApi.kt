package com.example.album.infra.datasource.api

import com.example.album.infra.datasource.api.response.AlbumResponse
import retrofit2.http.GET
import retrofit2.http.Url

interface PhotospeedwayApi {

  @GET
  suspend fun getAlbum(
    @Url url : String
  ): AlbumResponse
}
