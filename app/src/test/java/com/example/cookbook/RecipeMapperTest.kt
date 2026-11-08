package com.example.cookbook

import com.example.cookbook.data.dto.RecipeDto
import com.example.cookbook.data.mapper.toDomain
import com.example.cookbook.data.mapper.toRequestDto
import com.example.cookbook.domain.model.Recipe
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class RecipeMapperTest {

    @Test
    fun recipeDto_toDomain_mapsAllFieldsCorrectly() {
        val dto = RecipeDto(
            id = 101,
            nombre = "Pasta Carbonara",
            descripcion = "Receta clásica italiana",
            ingredientes = "Pasta, Huevo, Panceta, Queso Pecorino, Pimienta",
            instrucciones = "1. Cocinar la pasta. 2. Mezclar huevos y queso. 3. Dorar panceta y mezclar.",
            categoria = "Almuerzo",
            imagenUrl = "http://example.com/pasta.jpg",
            tiempoPreparacion = 25,
            porciones = 2,
            esFavorita = true
        )

        val domainModel = dto.toDomain()

        assertEquals(101, domainModel.id)
        assertEquals("Pasta Carbonara", domainModel.nombre)
        assertEquals("Receta clásica italiana", domainModel.descripcion)
        assertEquals("Pasta, Huevo, Panceta, Queso Pecorino, Pimienta", domainModel.ingredientes)
        assertEquals("1. Cocinar la pasta. 2. Mezclar huevos y queso. 3. Dorar panceta y mezclar.", domainModel.instrucciones)
        assertEquals("Almuerzo", domainModel.categoria)
        assertEquals("http://example.com/pasta.jpg", domainModel.imagenUrl)
        assertEquals(25, domainModel.tiempoPreparacion)
        assertEquals(2, domainModel.porciones)
        assertTrue(domainModel.esFavorita)
    }

    @Test
    fun recipe_toRequestDto_trimsAndMapsFieldsCorrectly() {
        val recipe = Recipe(
            id = 0,
            nombre = "  Ensalada César  ",
            descripcion = "  Lechuga fresca con aderezo  ",
            ingredientes = "  Lechuga, Pollo, Queso, Crutones  ",
            instrucciones = "  Mezclar todo en un bowl  ",
            categoria = "  Cena  ",
            imagenUrl = "  http://example.com/ensalada.jpg  ",
            tiempoPreparacion = 15,
            porciones = 1,
            esFavorita = false
        )

        val requestDto = recipe.toRequestDto()

        assertEquals("Ensalada César", requestDto.nombre)
        assertEquals("Lechuga fresca con aderezo", requestDto.descripcion)
        assertEquals("Lechuga, Pollo, Queso, Crutones", requestDto.ingredientes)
        assertEquals("Mezclar todo en un bowl", requestDto.instrucciones)
        assertEquals("Cena", requestDto.categoria)
        assertEquals("http://example.com/ensalada.jpg", requestDto.imagenUrl)
        assertEquals(15, requestDto.tiempoPreparacion)
        assertEquals(1, requestDto.porciones)
        assertEquals(false, requestDto.esFavorita)
    }

    @Test
    fun recipeDtoList_toDomain_mapsAllItems() {
        val dtoList = listOf(
            RecipeDto(id = 1, nombre = "Receta 1", ingredientes = "Ing 1", instrucciones = "Inst 1"),
            RecipeDto(id = 2, nombre = "Receta 2", ingredientes = "Ing 2", instrucciones = "Inst 2")
        )

        val domainList = dtoList.toDomain()

        assertEquals(2, domainList.size)
        assertEquals(1, domainList[0].id)
        assertEquals(2, domainList[1].id)
    }
}
