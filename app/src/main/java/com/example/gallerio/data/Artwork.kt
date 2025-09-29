package com.example.gallerio.data

import androidx.room.Entity
import androidx.room.PrimaryKey


data class Artwork(
    val id: Int,
    val title: String,
    val artist_title: String?,
    val image_id: String?,
    val artist_display: String?,
    val medium_display: String?,
    val date_display: String?,
    val place_of_origin:String?,
    val artwork_type_title: String?,
    val style_title: String?,
    val description: String?
) {
    fun toArtworkEntity(isSaved: Boolean = false): ArtworkEntity {
        return ArtworkEntity(
            id = id,
            title = title,
            imageId = image_id,
            artistTitle = artist_title,
            isSaved = isSaved,
            artistDisplay = artist_display,
            mediumDisplay = medium_display,
            dateDisplay = date_display,
            placeOfOrigin = place_of_origin,
            artworkTypeTitle = artwork_type_title,
            styleTitle = style_title,
            description = description
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
    var mediumDisplay: String?,
    val dateDisplay: String?,
    val placeOfOrigin:String?,
    val artworkTypeTitle: String?,
    val styleTitle: String?,
    val description: String?
)


//place_of_origin artwork_type_title style_title description