package com.example.cookbook

import com.example.cookbook.data.dto.RecipeDto
import com.example.cookbook.data.local.RecipeEntity
import com.example.cookbook.data.repository.RecipeRepositoryImpl
import com.example.cookbook.domain.model.Recipe
import com.example.cookbook.domain.model.ResourceResult
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RecipeRepositoryOfflineFirstTest {

    private lateinit var fakeDao: FakeRecipeDao
    private lateinit var fakeApi: FakeRecipeApiService
    private lateinit var repository: RecipeRepositoryImpl

    @Before
    fun setUp() {
        fakeDao = FakeRecipeDao()
        fakeApi = FakeRecipeApiService()
        repository = RecipeRepositoryImpl(fakeApi, fakeDao)
    }

    @Test
    fun `observeRecipes emits cached entities from Room immediately`() = runBlocking {
        fakeDao.upsert(
            RecipeEntity(
                id = 1,
                nombre = "Pasta Carbonara",
                ingredientes = "Pasta, Huevo, Guanciale, Queso",
                instrucciones = "Cocinar y mezclar",
                esFavorita = true
            )
        )

        val recipes = repository.observeRecipes().first()
        assertEquals(1, recipes.size)
        assertEquals("Pasta Carbonara", recipes.first().nombre)
        assertTrue(recipes.first().esFavorita)
    }

    @Test
    fun `refresh updates Room with remote recipes and deletes obsolete local items`() = runBlocking {
        // Room tiene receta 1 y 2
        fakeDao.upsert(RecipeEntity(id = 1, nombre = "Vieja 1", ingredientes = "ing1", instrucciones = "inst1"))
        fakeDao.upsert(RecipeEntity(id = 2, nombre = "Vieja 2", ingredientes = "ing2", instrucciones = "inst2"))

        // La API solo devuelve receta 2 y receta 3
        fakeApi.remoteRecipes.add(RecipeDto(id = 2, nombre = "Vieja 2 Actualizada", ingredientes = "ing2", instrucciones = "inst2"))
        fakeApi.remoteRecipes.add(RecipeDto(id = 3, nombre = "Nueva 3", ingredientes = "ing3", instrucciones = "inst3"))

        val result = repository.refresh()
        assertTrue(result is ResourceResult.Success)

        val localEntities = fakeDao.getAll()
        val localIds = localEntities.map { it.id }.toSet()

        // 1 debió ser eliminada de Room; 2 y 3 deben estar presentes
        assertEquals(setOf(2, 3), localIds)
        assertEquals("Vieja 2 Actualizada", fakeDao.getById(2)?.nombre)
    }

    @Test
    fun `refresh when network fails returns NetworkError without wiping local cache`() = runBlocking {
        fakeDao.upsert(RecipeEntity(id = 1, nombre = "Enchiladas", ingredientes = "Pollo", instrucciones = "Hornear"))
        fakeApi.shouldThrowNetworkError = true

        val result = repository.refresh()
        assertTrue(result is ResourceResult.NetworkError)

        // Los datos locales se conservan intactos
        val cached = fakeDao.getAll()
        assertEquals(1, cached.size)
        assertEquals("Enchiladas", cached.first().nombre)
    }

    @Test
    fun `getRecipeById returns cached recipe when network request fails`() = runBlocking {
        fakeDao.upsert(RecipeEntity(id = 5, nombre = "Pozole", ingredientes = "Maiz, Cerdo", instrucciones = "Hervir"))
        fakeApi.shouldThrowNetworkError = true

        val result = repository.getRecipeById(5)
        assertTrue(result.isSuccess)
        val recipe = result.getOrNull()
        assertNotNull(recipe)
        assertEquals("Pozole", recipe?.nombre)
    }

    @Test
    fun `createRecipe calls API and saves directly into Room`() = runBlocking {
        val newRecipe = Recipe(
            id = 0,
            nombre = "Guacamole",
            ingredientes = "Aguacate, Limon, Sal",
            instrucciones = "Triturar y mezclar"
        )

        val result = repository.createRecipe(newRecipe)
        assertTrue(result.isSuccess)

        val savedInRoom = fakeDao.getAll()
        assertEquals(1, savedInRoom.size)
        assertEquals("Guacamole", savedInRoom.first().nombre)
    }

    @Test
    fun `clearLocalData clears all entries in Room`() = runBlocking {
        fakeDao.upsert(RecipeEntity(id = 1, nombre = "Taco", ingredientes = "Tortilla", instrucciones = "Servir"))
        assertEquals(1, fakeDao.getAll().size)

        repository.clearLocalData()
        assertEquals(0, fakeDao.getAll().size)
    }
}
