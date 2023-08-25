package com.example.listacompras.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update


@Dao
interface CompraDao {
    @Query("SELECT * FROM compra  ORDER BY realizada")
    fun getAll(): List<Compra>

    @Query("SELECT COUNT(*) FROM compra")
    fun  contar():Int


    @Insert
    fun  insertCompra(compra: Compra):Long

    @Update
    fun updateCompra(compra:Compra)


    @Delete
    fun deleteCompra(compra: Compra)

}