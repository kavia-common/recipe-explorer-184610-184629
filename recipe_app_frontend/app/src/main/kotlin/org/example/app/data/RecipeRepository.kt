package org.example.app.data

import android.content.Context

/**
 * PUBLIC_INTERFACE
 * Repository for managing recipes and favorites via Room.
 */
class RecipeRepository private constructor(context: Context) {

    private val dao = AppDatabase.getInstance(context).recipeDao()

    // PUBLIC_INTERFACE
    suspend fun getAll(): List<RecipeEntity> = dao.getAll()

    // PUBLIC_INTERFACE
    suspend fun getFavorites(): List<RecipeEntity> = dao.getFavorites()

    // PUBLIC_INTERFACE
    suspend fun search(query: String): List<RecipeEntity> = if (query.isBlank()) dao.getAll() else dao.search(query)

    // PUBLIC_INTERFACE
    suspend fun getById(id: Long): RecipeEntity? = dao.getById(id)

    // PUBLIC_INTERFACE
    suspend fun add(recipe: RecipeEntity): Long = dao.insert(recipe)

    // PUBLIC_INTERFACE
    suspend fun toggleFavorite(id: Long): RecipeEntity? {
        val current = dao.getById(id) ?: return null
        val updated = current.copy(isFavorite = !current.isFavorite)
        dao.update(updated)
        return updated
    }

    companion object {
        @Volatile private var INSTANCE: RecipeRepository? = null

        // PUBLIC_INTERFACE
        fun getInstance(context: Context): RecipeRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = RecipeRepository(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
