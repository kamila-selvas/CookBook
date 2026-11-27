package com.example.cookbook.data.mapper

import com.example.cookbook.data.dto.RecipeDto
import com.example.cookbook.data.dto.RecipeRequestDto
import com.example.cookbook.data.local.RecipeEntity
import com.example.cookbook.domain.model.Recipe

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

fun RecipeDto.toEntity(): RecipeEntity {
    return RecipeEntity(
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

fun List<RecipeDto>.toEntities(): List<RecipeEntity> {
    return map { it.toEntity() }
}

fun RecipeEntity.toDomain(): Recipe {
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

fun List<RecipeEntity>.entitiesToDomain(): List<Recipe> {
    return map { it.toDomain() }
}

fun Recipe.toEntity(): RecipeEntity {
    return RecipeEntity(
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
