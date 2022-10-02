package com.ishak.mapss

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Place")
class Place(
    @ColumnInfo(name ="Latitude") var latitude:Double,
    @ColumnInfo(name="Longitude") var longitude:Double,
    @ColumnInfo(name="Place") var place:String
):Serializable {
    @PrimaryKey(autoGenerate = true) var id=0

}