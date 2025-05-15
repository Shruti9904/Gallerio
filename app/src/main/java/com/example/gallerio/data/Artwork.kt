package com.example.gallerio.data

import androidx.room.Entity
import androidx.room.PrimaryKey


data class Artwork(
    val id: Int,
    val title: String,
    val artist_title: String?,
    val image_id: String?,
    val artist_display: String?,
    val medium_display: String?
) {
    fun toArtworkEntity(isSaved: Boolean = false): ArtworkEntity {
        return ArtworkEntity(
            id = id,
            title = title,
            imageId = image_id,
            artistTitle = artist_title,
            isSaved = isSaved,
            artistDisplay = artist_display,
            mediumDisplay = medium_display
        )
    }

}

@Entity(tableName = "artworks")
data class ArtworkEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val artistTitle: String?,
    val imageId: String?,
    var isSaved: Boolean = false,
    var artistDisplay: String?,
    var mediumDisplay: String?
)


