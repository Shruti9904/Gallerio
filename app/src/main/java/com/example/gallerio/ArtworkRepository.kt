package com.example.gallerio

import com.example.gallerio.data.ArtworkDoa
import com.example.gallerio.data.ArtworkEntity


class ArtworkRepository(private val dao: ArtworkDoa) {

    suspend fun getAllArtworks(): List<ArtworkEntity> = dao.getAllSavedArtworks()

    suspend fun insertArtwork(artwork: ArtworkEntity) = dao.insertArtwork(artwork)

    suspend fun deleteArtwork(artwork: ArtworkEntity) = dao.deleteArtwork(artwork)

    suspend fun getSavedArtworks() = dao.getSavedArtworks()

}