package com.ishak.mapss

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable

@Dao
interface PlaceDao {
    @Insert
    fun insert(place:Place):Completable
    @Delete
    fun delete(place:Place):Completable
    //burada tablo adına ne verdiysek onu yazıyoruz
    @Query("SELECT * FROM Place")
    fun getAll():Flowable <List<Place>>
}