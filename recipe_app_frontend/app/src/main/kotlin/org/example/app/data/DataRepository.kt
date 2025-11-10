package org.example.app.data

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * PUBLIC_INTERFACE
 * DataRepository abstracts storage and selects the best available backend:
 * - Room (if database can be created)
 * - FallbackStore (SharedPreferences JSON)
 */
class DataRepository private constructor(context: Context) {
    private val roomRepo: RecipeRepository? = try {
        // Attempt to instantiate Room; if this fails for any reason, fallback will be used.
        RecipeRepository.getInstance(context)
    } catch (_: Throwable) {
        null
    }
    private val fallback = FallbackStore.getInstance(context)

    private fun useRoom(): Boolean = roomRepo != null

    // PUBLIC_INTERFACE
    suspend fun getAll(): List<RecipeEntity> = withContext(Dispatchers.IO) {
        if (useRoom()) roomRepo!!.getAll() else fallback.getAll()
    }

    // PUBLIC_INTERFACE
    suspend fun getFavorites(): List<RecipeEntity> = withContext(Dispatchers.IO) {
        if (useRoom()) roomRepo!!.getFavorites() else fallback.getFavorites()
    }

    // PUBLIC_INTERFACE
    suspend fun search(query: String): List<RecipeEntity> = withContext(Dispatchers.IO) {
        if (useRoom()) roomRepo!!.search(query) else fallback.search(query)
    }

    // PUBLIC_INTERFACE
    suspend fun getById(id: Long): RecipeEntity? = withContext(Dispatchers.IO) {
        if (useRoom()) roomRepo!!.getById(id) else fallback.getById(id)
    }

    // PUBLIC_INTERFACE
    suspend fun add(recipe: RecipeEntity): Long = withContext(Dispatchers.IO) {
        if (useRoom()) roomRepo!!.add(recipe) else fallback.add(recipe)
    }

    // PUBLIC_INTERFACE
    suspend fun toggleFavorite(id: Long): RecipeEntity? = withContext(Dispatchers.IO) {
        if (useRoom()) roomRepo!!.toggleFavorite(id) else fallback.toggleFavorite(id)
    }

    companion object {
        @Volatile private var INSTANCE: DataRepository? = null

        // PUBLIC_INTERFACE
        fun getInstance(context: Context): DataRepository {
            return INSTANCE ?: synchronized(this) {
                val inst = DataRepository(context.applicationContext)
                INSTANCE = inst
                inst
            }
        }
    }
}
