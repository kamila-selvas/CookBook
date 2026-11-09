package com.example.cookbook.domain.model

data class Recipe(
    val id: Int = 0,
    val nombre: String,
    val descripcion: String? = null,
    val ingredientes: String,
    val instrucciones: String,
    val categoria: String? = null,
    val imagenUrl: String? = null,
    val tiempoPreparacion: Int? = null,
    val porciones: Int? = null,
    val esFavorita: Boolean = false
)
