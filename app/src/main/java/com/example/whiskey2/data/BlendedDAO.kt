package com.example.whiskey2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BlendedDAO {
    @Query("SELECT * FROM blended")
    fun getAll(): Flow<List<Blended>>

    @Query("SELECT * FROM blended WHERE uid IN (:blendedIds)")
    fun loadAllByIds(blendedIds: IntArray): List<Blended>

    @Query("SELECT * FROM blended WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Blended

    @Query("SELECT price FROM blended")
    fun getAllPrices(): Flow<List<Int>>

    @Insert
    fun insertAll(vararg blendeds: Blended)

    @Update
    suspend fun updateBlended(blended: Blended)

    @Delete
    fun delete(blended: Blended)

}