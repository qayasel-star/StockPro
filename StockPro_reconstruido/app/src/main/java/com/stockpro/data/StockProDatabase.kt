package com.stockpro.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [ProductEntity::class, CategoryEntity::class, StockMovementEntity::class], version = 1, exportSchema = true)
abstract class StockProDatabase : RoomDatabase() {
    abstract fun dao(): StockProDao

    companion object {
        @Volatile private var INSTANCE: StockProDatabase? = null
        fun get(context: Context): StockProDatabase = INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(context.applicationContext, StockProDatabase::class.java, "stockpro.db")
                // No borrar datos automáticamente. Cada cambio de esquema requiere una migración explícita.
                .build().also { INSTANCE = it }
        }
    }
}
