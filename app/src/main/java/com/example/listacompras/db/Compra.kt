package com.example.listacompras.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class Compra(
    @PrimaryKey(autoGenerate = true) val  id: Int,
    var compra:String,
    var realizada:Boolean
)
