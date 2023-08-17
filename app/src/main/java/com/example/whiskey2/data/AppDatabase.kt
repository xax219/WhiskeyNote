package com.example.whiskey2.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

//[User::class, ] 여기서 , 로 나눠서 받음
@Database(entities = [SingleMalt::class], version = 1 )
abstract class AppDatabase : RoomDatabase() {
    abstract fun singleMaltDAO(): SingleMaltDAO

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "contacts.db"
                ).addMigrations(
//                    MIGRATION_0_1
                ).build()
            }
        }

    }
}

//val MIGRATION_0_1 = object : Migration(0,1) {
//    override fun migrate(database: SupportSQLiteDatabase) {
//        database.execSQL("ALTER TABLE User ADD COLUMN name TEXT")
//        database.execSQL("ALTER TABLE User ADD COLUMN location TEXT")
//        database.execSQL("ALTER TABLE User ADD COLUMN tastingNote TEXT")
//        database.execSQL("ALTER TABLE User ADD COLUMN year INTEGER")
//        database.execSQL("ALTER TABLE User ADD COLUMN price INTEGER")
//        database.execSQL("ALTER TABLE User ADD COLUMN imageUri TEXT")
//    }
//}
