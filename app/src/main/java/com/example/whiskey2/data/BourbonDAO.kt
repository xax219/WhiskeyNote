package com.example.whiskey2.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface BourbonDAO {
    @Query("SELECT * FROM bourbon")
    fun getAll(): Flow<List<Bourbon>>

    @Query("SELECT * FROM bourbon WHERE uid IN (:bourbonIds)")
    fun loadAllByIds(bourbonIds: IntArray): List<Bourbon>

    @Query("SELECT * FROM bourbon WHERE name LIKE :name LIMIT 1")
    fun findByName(name: String): Bourbon

    @Query("SELECT price FROM bourbon")
    fun getAllPrices(): Flow<List<Int>>

    @Insert
    fun insertAll(vararg bourbon: Bourbon)

    @Update
    suspend fun updateBourbon(bourbon: Bourbon)

    @Delete
    fun delete(bourbon: Bourbon)

}