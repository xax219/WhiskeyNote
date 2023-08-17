package com.example.whiskey2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SingleMaltDAO {
    @Query("SELECT * FROM singlemalt")
    fun getAll(): Flow<List<SingleMalt>>

    @Query("SELECT * FROM singlemalt WHERE uid IN (:singlemaltIds)")
    fun loadAllByIds(singlemaltIds: IntArray): List<SingleMalt>

    @Query("SELECT * FROM singlemalt WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): SingleMalt

    @Query("SELECT price FROM singlemalt")
    fun getAllPrices(): Flow<List<Int>>

    @Insert
    fun insertAll(vararg singleMalts: SingleMalt)

    @Update
    suspend fun updateSingleMalt(singleMalt: SingleMalt)

    @Delete
    fun delete(singleMalt: SingleMalt)

}