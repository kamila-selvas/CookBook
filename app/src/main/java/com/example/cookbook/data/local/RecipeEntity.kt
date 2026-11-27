package com.example.cookbook.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey
    val id: Int,

    @ColumnInfo(name = "nombre")
    val nombre: String,

    @ColumnInfo(name = "descripcion")
    val descripcion: String? = null,

    @ColumnInfo(name = "ingredientes")
    val ingredientes: String,

    @ColumnInfo(name = "instrucciones")
    val instrucciones: String,

    @ColumnInfo(name = "categoria")
    val categoria: String? = null,

    @ColumnInfo(name = "imagen_url")
    val imagenUrl: String? = null,

    @ColumnInfo(name = "tiempo_preparacion")
    val tiempoPreparacion: Int? = null,

    @ColumnInfo(name = "porciones")
    val porciones: Int? = null,

    @ColumnInfo(name = "es_favorita")
    val esFavorita: Boolean = false
)
