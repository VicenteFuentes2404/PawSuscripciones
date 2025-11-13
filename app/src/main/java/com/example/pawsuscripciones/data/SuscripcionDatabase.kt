package com.example.pawsuscripciones.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pawsuscripciones.data.SuscripcionDao
import com.example.pawsuscripciones.data.Suscripcion


// Se incrementa la versión de la base de datos de 3 a 4
@Database(entities = [Suscripcion::class], version = 4, exportSchema = false)
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
                )
                    // Esto es importante: al detectar un cambio de versión,
                    // destruirá la base de datos vieja y creará una nueva.
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = inst
                inst
            }
        }
    }
}