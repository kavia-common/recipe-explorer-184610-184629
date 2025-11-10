package org.example.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * PUBLIC_INTERFACE
 * Room database providing access to RecipeDao with seed data on first creation.
 */
@Database(entities = [RecipeEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        // PUBLIC_INTERFACE
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "recipe_db"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            seed(context)
                        }
                    })
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private fun seed(context: Context) {
            val scope = CoroutineScope(Dispatchers.IO)
            scope.launch {
                val dao = getInstance(context).recipeDao()
                // Seed with mock data
                val samples = listOf(
                    RecipeEntity(
                        title = "Lemon Herb Grilled Salmon",
                        description = "Citrus-forward salmon with fresh herbs.",
                        ingredients = "- Salmon fillets\n- Lemon\n- Olive oil\n- Parsley\n- Salt & pepper",
                        steps = "1. Marinate salmon.\n2. Grill 3-4 min per side.\n3. Serve with lemon wedges.",
                        imageUrl = null,
                        isFavorite = true
                    ),
                    RecipeEntity(
                        title = "Creamy Mushroom Pasta",
                        description = "Rich, savory pasta with mushrooms.",
                        ingredients = "- Pasta\n- Mushrooms\n- Cream\n- Garlic\n- Parmesan",
                        steps = "1. Sauté mushrooms.\n2. Add cream & garlic.\n3. Toss with pasta, top with parmesan.",
                        imageUrl = null
                    ),
                    RecipeEntity(
                        title = "Avocado Toast Deluxe",
                        description = "Crisp toast with smashed avocado & toppings.",
                        ingredients = "- Bread\n- Avocado\n- Chili flakes\n- Lime\n- Salt",
                        steps = "1. Toast bread.\n2. Smash avocado with lime.\n3. Spread and garnish.",
                        imageUrl = null
                    )
                )
                samples.forEach { dao.insert(it) }
            }
        }
    }
}
