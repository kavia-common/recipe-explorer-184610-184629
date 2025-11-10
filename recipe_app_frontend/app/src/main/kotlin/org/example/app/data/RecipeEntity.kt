package org.example.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * PUBLIC_INTERFACE
 * Represents a recipe entity for local storage via Room.
 * Fields:
 * - id: Long primary key
 * - title: Recipe title
 * - description: Short description or summary
 * - ingredients: Multiline text of ingredients
 * - steps: Multiline text of steps
 * - imageUrl: Optional image URL
 * - isFavorite: Whether the recipe is in favorites
 */
@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val ingredients: String,
    val steps: String,
    val imageUrl: String? = null,
    val isFavorite: Boolean = false
)
