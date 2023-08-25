package com.example.listacompras.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.Room.databaseBuilder
import androidx.room.RoomDatabase


@Database(entities = [Compra::class], version = 1)
abstract class AppDataBase: RoomDatabase() {
        abstract  fun compraDao():CompraDao
    // patron singleton  una unica base de datos
    companion object {
        // Volatile asegura que sea actualizada la propiedad
        // atómicamente
        @Volatile
        private var BASE_DATOS : AppDataBase? = null
        fun getInstance(contexto: Context):AppDataBase{
            // synchronized previene el acceso de múltiples threads de manera simultánea
            return BASE_DATOS ?: synchronized(this) {
                databaseBuilder(
                    contexto.applicationContext,
                    AppDataBase::class.java,
                    "Compras.bd"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { BASE_DATOS = it }
            }
        }
    }

}