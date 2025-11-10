package org.example.app.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.app.R
import org.example.app.data.RecipeEntity
import org.example.app.data.DataRepository

/**
 * PUBLIC_INTERFACE
 * Add Recipe screen: Users can create a recipe with validation; data saved locally.
 */
class AddRecipeFragment : Fragment() {

    private lateinit var repo: DataRepository
    private lateinit var titleInput: EditText
    private lateinit var descriptionInput: EditText
    private lateinit var ingredientsInput: EditText
    private lateinit var stepsInput: EditText
    private lateinit var imageUrlInput: EditText
    private lateinit var saveButton: MaterialButton

    override fun onAttach(context: Context) {
        super.onAttach(context)
        repo = DataRepository.getInstance(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_add_recipe, container, false)
        titleInput = v.findViewById(R.id.inputTitle)
        descriptionInput = v.findViewById(R.id.inputDescription)
        ingredientsInput = v.findViewById(R.id.inputIngredients)
        stepsInput = v.findViewById(R.id.inputSteps)
        imageUrlInput = v.findViewById(R.id.inputImageUrl)
        saveButton = v.findViewById(R.id.saveButton)

        saveButton.setOnClickListener { onSave(v) }
        return v
    }

    private fun onSave(view: View) {
        val title = titleInput.text?.toString()?.trim().orEmpty()
        val description = descriptionInput.text?.toString()?.trim().orEmpty()
        val ingredients = ingredientsInput.text?.toString()?.trim().orEmpty()
        val steps = stepsInput.text?.toString()?.trim().orEmpty()
        val imageUrl = imageUrlInput.text?.toString()?.trim()?.ifBlank { null }

        if (title.isBlank()) {
            Snackbar.make(view, R.string.title_required, Snackbar.LENGTH_SHORT).show()
            return
        }
        if (description.isBlank()) {
            Snackbar.make(view, R.string.description_required, Snackbar.LENGTH_SHORT).show()
            return
        }

        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                repo.add(
                    RecipeEntity(
                        title = title,
                        description = description,
                        ingredients = if (ingredients.isBlank()) "-" else ingredients,
                        steps = if (steps.isBlank()) "-" else steps,
                        imageUrl = imageUrl
                    )
                )
            }
            // After save, go back to home to see it listed
            parentFragmentManager.popBackStack()
            (activity as? MainActivity)?.let { main ->
                main.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, HomeFragment.newInstance())
                    .commit()
            }
            Snackbar.make(view, R.string.save, Snackbar.LENGTH_SHORT).show()
        }
    }

    companion object {
        // PUBLIC_INTERFACE
        fun newInstance(): AddRecipeFragment = AddRecipeFragment()
    }
}
