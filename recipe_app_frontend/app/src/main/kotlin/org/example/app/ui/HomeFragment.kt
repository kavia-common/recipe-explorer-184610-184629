package org.example.app.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.example.app.R
import org.example.app.data.RecipeEntity
import org.example.app.data.DataRepository

/**
 * PUBLIC_INTERFACE
 * Home/Browse Fragment that shows a grid of recipe cards with a search bar and pull-to-refresh.
 */
class HomeFragment : Fragment() {

    private lateinit var repo: DataRepository
    private lateinit var recycler: RecyclerView
    private lateinit var adapter: RecipeAdapter
    private lateinit var swipe: SwipeRefreshLayout
    private lateinit var searchEdit: EditText

    override fun onAttach(context: Context) {
        super.onAttach(context)
        repo = DataRepository.getInstance(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        recycler = v.findViewById(R.id.recipesRecycler)
        swipe = v.findViewById(R.id.swipeRefresh)
        searchEdit = v.findViewById(R.id.searchEdit)

        recycler.layoutManager = GridLayoutManager(requireContext(), 2)
        adapter = RecipeAdapter(
            onClick = { openDetail(it) },
            onFavoriteToggle = { toggleFavorite(it) }
        )
        recycler.adapter = adapter

        swipe.setOnRefreshListener { loadData(searchEdit.text.toString()) }

        v.findViewById<View>(R.id.searchInputLayout).setOnClickListener {
            // no-op, focus handled by edit text
        }

        searchEdit.setOnEditorActionListener { _, _, _ ->
            loadData(searchEdit.text.toString())
            true
        }

        return v
    }

    override fun onResume() {
        super.onResume()
        loadData(searchEdit.text.toString())
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
            loadData(searchEdit.text.toString())
        }
    }

    private fun loadData(query: String) {
        swipe.isRefreshing = true
        viewLifecycleOwner.lifecycleScope.launch {
            val data = withContext(Dispatchers.IO) {
                if (query.isBlank()) repo.getAll() else repo.search(query)
            }
            adapter.submit(data)
            swipe.isRefreshing = false
        }
    }

    companion object {
        // PUBLIC_INTERFACE
        fun newInstance(): HomeFragment = HomeFragment()
    }
}
