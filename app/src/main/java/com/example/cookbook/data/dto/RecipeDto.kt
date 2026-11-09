package com.example.cookbook.data.dto

import com.google.gson.annotations.SerializedName

data class RecipeDto(
    @SerializedName("id")
    val id: Int,

    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String? = null,

    @SerializedName("ingredientes")
    val ingredientes: String,

    @SerializedName("instrucciones")
    val instrucciones: String,

    @SerializedName("categoria")
    val categoria: String? = null,

    @SerializedName("imagen_url")
    val imagenUrl: String? = null,

    @SerializedName("tiempo_preparacion")
    val tiempoPreparacion: Int? = null,

    @SerializedName("porciones")
    val porciones: Int? = null,

    @SerializedName("es_favorita")
    val esFavorita: Boolean = false
)

data class RecipeRequestDto(
    @SerializedName("nombre")
    val nombre: String,

    @SerializedName("descripcion")
    val descripcion: String? = null,

    @SerializedName("ingredientes")
    val ingredientes: String,

    @SerializedName("instrucciones")
    val instrucciones: String,

    @SerializedName("categoria")
    val categoria: String? = null,

    @SerializedName("imagen_url")
    val imagenUrl: String? = null,

    @SerializedName("tiempo_preparacion")
    val tiempoPreparacion: Int? = null,

    @SerializedName("porciones")
    val porciones: Int? = null,

    @SerializedName("es_favorita")
    val esFavorita: Boolean = false
)
