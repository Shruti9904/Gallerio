package com.example.gallerio.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ArtworkDoa{
    @Query("SELECT * FROM 'artworks'")
    suspend fun getAllArtworks():List<ArtworkEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtwork(artwork: ArtworkEntity)

    @Delete
    suspend fun deleteArtwork(artwork: ArtworkEntity)

    @Query("SELECT * FROM artworks WHERE id = :id LIMIT 1")
    suspend fun getArtworkById(id: Int): ArtworkEntity?

    @Query("SELECT * FROM artworks WHERE isSaved = 1")
    suspend fun getSavedArtworks(): List<ArtworkEntity>
}