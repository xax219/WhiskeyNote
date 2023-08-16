package com.example.whiskey2.data

import androidx.compose.ui.text.input.TextFieldValue
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class User(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo("name") val name: String,
    @ColumnInfo("price") val price: Int ? = null,
    @ColumnInfo("year") val year: Int,
    @ColumnInfo("location") val location: String,
    @ColumnInfo("tastingNote") val tastingNote: String,
    @ColumnInfo("imageUri") val imageUri: String? = null
)
