package com.example.pawsuscripciones.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pawsuscripciones.data.SuscripcionDao
import com.example.pawsuscripciones.data.Suscripcion

@Database(entities = [Suscripcion::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun suscripcionDao(): SuscripcionDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val inst = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "pawsuscripciones.db"
                ).build()
                INSTANCE = inst
                inst
            }
        }
    }
}