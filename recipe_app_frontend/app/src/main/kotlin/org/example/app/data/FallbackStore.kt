package org.example.app.data

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject

/**
 * PUBLIC_INTERFACE
 * Simple JSON-based local store using SharedPreferences to persist recipes and favorites
 * when Room annotation processing is unavailable. It is not intended for production use.
 */
class FallbackStore private constructor(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("recipe_store", Context.MODE_PRIVATE)

    // PUBLIC_INTERFACE
    fun getAll(): List<RecipeEntity> {
        val arr = JSONArray(prefs.getString(KEY_RECIPES, "[]"))
        return (0 until arr.length()).map { fromJson(arr.getJSONObject(it)) }
    }

    // PUBLIC_INTERFACE
    fun getFavorites(): List<RecipeEntity> = getAll().filter { it.isFavorite }

    // PUBLIC_INTERFACE
    fun search(query: String): List<RecipeEntity> {
        if (query.isBlank()) return getAll()
        val q = query.lowercase()
        return getAll().filter { it.title.lowercase().contains(q) || it.description.lowercase().contains(q) }
    }

    // PUBLIC_INTERFACE
    fun getById(id: Long): RecipeEntity? = getAll().find { it.id == id }

    // PUBLIC_INTERFACE
    fun add(recipe: RecipeEntity): Long {
        val list = getAll().toMutableList()
        val nextId = (list.maxOfOrNull { it.id } ?: 0L) + 1
        list.add(recipe.copy(id = nextId))
        saveAll(list)
        return nextId
    }

    // PUBLIC_INTERFACE
    fun toggleFavorite(id: Long): RecipeEntity? {
        val list = getAll().toMutableList()
        val idx = list.indexOfFirst { it.id == id }
        if (idx == -1) return null
        val updated = list[idx].copy(isFavorite = !list[idx].isFavorite)
        list[idx] = updated
        saveAll(list)
        return updated
    }

    private fun saveAll(list: List<RecipeEntity>) {
        val arr = JSONArray()
        list.forEach { arr.put(toJson(it)) }
        prefs.edit().putString(KEY_RECIPES, arr.toString()).apply()
    }

    private fun toJson(r: RecipeEntity): JSONObject = JSONObject().apply {
        put("id", r.id)
        put("title", r.title)
        put("description", r.description)
        put("ingredients", r.ingredients)
        put("steps", r.steps)
        put("imageUrl", r.imageUrl)
        put("isFavorite", r.isFavorite)
    }

    private fun fromJson(o: JSONObject): RecipeEntity = RecipeEntity(
        id = o.optLong("id", 0),
        title = o.optString("title"),
        description = o.optString("description"),
        ingredients = o.optString("ingredients"),
        steps = o.optString("steps"),
        imageUrl = if (o.isNull("imageUrl")) null else o.optString("imageUrl"),
        isFavorite = o.optBoolean("isFavorite", false)
    )

    companion object {
        private const val KEY_RECIPES = "recipes_json"

        @Volatile private var INSTANCE: FallbackStore? = null

        // PUBLIC_INTERFACE
        fun getInstance(context: Context): FallbackStore {
            return INSTANCE ?: synchronized(this) {
                val inst = FallbackStore(context.applicationContext).apply {
                    if (getAll().isEmpty()) {
                        // seed
                        add(
                            RecipeEntity(
                                title = "Lemon Herb Grilled Salmon",
                                description = "Citrus-forward salmon with fresh herbs.",
                                ingredients = "- Salmon fillets\n- Lemon\n- Olive oil\n- Parsley\n- Salt & pepper",
                                steps = "1. Marinate salmon.\n2. Grill 3-4 min per side.\n3. Serve with lemon wedges.",
                                isFavorite = true
                            )
                        )
                        add(
                            RecipeEntity(
                                title = "Creamy Mushroom Pasta",
                                description = "Rich, savory pasta with mushrooms.",
                                ingredients = "- Pasta\n- Mushrooms\n- Cream\n- Garlic\n- Parmesan",
                                steps = "1. Sauté mushrooms.\n2. Add cream & garlic.\n3. Toss with pasta, top with parmesan."
                            )
                        )
                        add(
                            RecipeEntity(
                                title = "Avocado Toast Deluxe",
                                description = "Crisp toast with smashed avocado & toppings.",
                                ingredients = "- Bread\n- Avocado\n- Chili flakes\n- Lime\n- Salt",
                                steps = "1. Toast bread.\n2. Smash avocado with lime.\n3. Spread and garnish."
                            )
                        )
                    }
                }
                INSTANCE = inst
                inst
            }
        }
    }
}
