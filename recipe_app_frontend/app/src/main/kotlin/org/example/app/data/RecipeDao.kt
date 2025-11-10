package org.example.app.data

import androidx.room.*

/**
 * PUBLIC_INTERFACE
 * Data access operations for recipes.
 */
@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY id DESC")
    suspend fun getAll(): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE isFavorite = 1 ORDER BY id DESC")
    suspend fun getFavorites(): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY id DESC")
    suspend fun search(query: String): List<RecipeEntity>

    @Query("SELECT * FROM recipes WHERE id=:id")
    suspend fun getById(id: Long): RecipeEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(recipe: RecipeEntity): Long

    @Update
    suspend fun update(recipe: RecipeEntity)

    @Delete
    suspend fun delete(recipe: RecipeEntity)
}
