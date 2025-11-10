package org.example.app.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.app.R
import org.example.app.data.RecipeEntity
import org.example.app.data.DataRepository

/**
 * PUBLIC_INTERFACE
 * Recipe detail screen showing hero image, title, ingredients, steps, share and save actions.
 */
class RecipeDetailFragment : Fragment() {

    private lateinit var repo: DataRepository
    private var recipeId: Long = 0
    private var current: RecipeEntity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        repo = DataRepository.getInstance(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        recipeId = requireArguments().getLong(ARG_ID)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_recipe_detail, container, false)
        val hero: ImageView = v.findViewById(R.id.heroImage)
        val title: TextView = v.findViewById(R.id.title)
        val ingredients: TextView = v.findViewById(R.id.ingredients)
        val steps: TextView = v.findViewById(R.id.steps)
        val share: Button = v.findViewById(R.id.shareButton)
        val save: Button = v.findViewById(R.id.saveButton)

        viewLifecycleOwner.lifecycleScope.launch {
            current = withContext(Dispatchers.IO) { repo.getById(recipeId) }
            current?.let { r ->
                title.text = r.title
                ingredients.text = r.ingredients
                steps.text = r.steps
                save.text = if (r.isFavorite) getString(R.string.unsave) else getString(R.string.save)
                // Image loading placeholder already set; integrate Glide/Picasso in future if desired
            }
        }

        share.setOnClickListener {
            current?.let { r ->
                (activity as? MainActivity)?.shareText(r.title, "${r.title}\n\n${r.description}")
            }
        }

        save.setOnClickListener {
            viewLifecycleOwner.lifecycleScope.launch {
                val updated = withContext(Dispatchers.IO) { repo.toggleFavorite(recipeId) }
                updated?.let { r ->
                    save.text = if (r.isFavorite) getString(R.string.unsave) else getString(R.string.save)
                }
            }
        }

        return v
    }

    companion object {
        private const val ARG_ID = "id"

        // PUBLIC_INTERFACE
        fun newInstance(id: Long): RecipeDetailFragment {
            val f = RecipeDetailFragment()
            f.arguments = Bundle().apply { putLong(ARG_ID, id) }
            return f
        }
    }
}
