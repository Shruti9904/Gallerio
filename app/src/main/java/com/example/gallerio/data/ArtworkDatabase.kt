package com.example.gallerio.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [ArtworkEntity::class], version = 1)
abstract class AppDatabase: RoomDatabase(){
    abstract fun artworkDao() : ArtworkDoa
}