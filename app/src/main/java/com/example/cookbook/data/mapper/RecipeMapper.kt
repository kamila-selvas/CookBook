package com.example.cookbook.data.mapper

import com.example.cookbook.data.dto.RecipeDto
import com.example.cookbook.data.dto.RecipeRequestDto
import com.example.cookbook.domain.model.Recipe

/**
 * Mappers para convertir DTOs de recetas a modelos de dominio y viceversa.
 */
fun RecipeDto.toDomain(): Recipe {
    return Recipe(
        id = id,
        nombre = nombre,
        descripcion = descripcion,
        ingredientes = ingredientes,
        instrucciones = instrucciones,
        categoria = categoria,
        imagenUrl = imagenUrl,
        tiempoPreparacion = tiempoPreparacion,
        porciones = porciones,
        esFavorita = esFavorita
    )
}

fun List<RecipeDto>.toDomain(): List<Recipe> {
    return map { it.toDomain() }
}

fun Recipe.toRequestDto(): RecipeRequestDto {
    return RecipeRequestDto(
        nombre = nombre.trim(),
        descripcion = descripcion?.trim(),
        ingredientes = ingredientes.trim(),
        instrucciones = instrucciones.trim(),
        categoria = categoria?.trim(),
        imagenUrl = imagenUrl?.trim(),
        tiempoPreparacion = tiempoPreparacion,
        porciones = porciones,
        esFavorita = esFavorita
    )
}
