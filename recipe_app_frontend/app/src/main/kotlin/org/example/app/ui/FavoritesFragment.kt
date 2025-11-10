package org.example.app.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.app.R
import org.example.app.data.RecipeEntity
import org.example.app.data.DataRepository

/**
 * PUBLIC_INTERFACE
 * Favorites screen listing saved recipes.
 */
class FavoritesFragment : Fragment() {

    private lateinit var repo: DataRepository
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private lateinit var emptyText: TextView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        repo = DataRepository.getInstance(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_favorites, container, false)
        emptyText = v.findViewById(R.id.emptyText)
        recycler = v.findViewById(R.id.favoritesRecycler)
        recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = RecipeAdapter(
            onClick = { openDetail(it) },
            onFavoriteToggle = { toggleFavorite(it) }
        )
        recycler.adapter = adapter
        return v
    }

    override fun onResume() {
        super.onResume()
        loadData()
    }

    private fun openDetail(recipe: RecipeEntity) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, RecipeDetailFragment.newInstance(recipe.id))
            .addToBackStack("detail")
            .commit()
    }

    private fun toggleFavorite(recipe: RecipeEntity) {
        viewLifecycleOwner.lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                repo.toggleFavorite(recipe.id)
            }
            loadData()
        }
    }

    private fun loadData() {
        viewLifecycleOwner.lifecycleScope.launch {
            val data = withContext(Dispatchers.IO) { repo.getFavorites() }
            emptyText.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
            adapter.submit(data)
        }
    }

    companion object {
        // PUBLIC_INTERFACE
        fun newInstance(): FavoritesFragment = FavoritesFragment()
    }
}
