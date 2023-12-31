package com.example.whiskey2.data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

//엔티티 3개 만들어음
@Entity
data class Bourbon(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("price") val price: Int? = null,
    @ColumnInfo("year") val year: Int,
    @ColumnInfo("location") val location: String,
    @ColumnInfo("tastingNote") val tastingNote: String,
    @ColumnInfo("imageUri") val imageUri: String? = null
) {
    fun copy(
        name: String = this.name,
        price: Int? = this.price,
        year: Int = this.year,
        location: String = this.location,
        tastingNote: String = this.tastingNote,
        imageUri: String? = this.imageUri
    ): Bourbon {
        return Bourbon(uid, name, price, year, location, tastingNote, imageUri)
    }
}
@Parcelize
data class ParcelableBourbon(
    val uid: Int = 0,
    val name: String,
    val price: Int? = null,
    val year: Int,
    val location: String,
    val tastingNote: String,
    val imageUri: String? = null
) : Parcelable
