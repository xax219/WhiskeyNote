package com.example.whiskey2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [SingleMalt::class, Blended::class,Bourbon::class], version = 1 )
abstract class AppDatabase : RoomDatabase() {
    abstract fun singleMaltDAO(): SingleMaltDAO
    abstract fun blendedDAO(): BlendedDAO
    abstract fun bourbonDAO(): BourbonDAO
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "contacts.db"
                ).addMigrations(
                ).build()
            }
        }

    }
}
