package com.example.gallerio.retrofit

import com.example.gallerio.data.Artwork
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

data class ArtworkResponse(
    val data: List<Artwork>
)

data class SearchArtworkResponse(
    val data: Artwork
)

interface ArtApiService {
    @GET("artworks")
    suspend fun getArtworks(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): ArtworkResponse

    @GET("artworks/search")
    suspend fun searchArtworks(
        @Query("q") query: String
    ): ArtworkResponse

    @GET("artworks/{id}")
    suspend fun getArtworkById(
        @Path("id") id:Int
    ):SearchArtworkResponse
}
