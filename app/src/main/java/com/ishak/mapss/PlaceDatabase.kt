package com.ishak.mapss

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(Place::class),version=1)
abstract class PlaceDatabase:RoomDatabase() {
    abstract fun placeDao():PlaceDao
}