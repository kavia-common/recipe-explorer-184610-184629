package org.example.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.example.app.R
import org.example.app.data.RecipeEntity

/**
 * PUBLIC_INTERFACE
 * Adapter to render RecipeEntity items as cards. It supports click to open details and favorite toggle.
 */
class RecipeAdapter(
    private val onClick: (RecipeEntity) -> Unit,
    private val onFavoriteToggle: (RecipeEntity) -> Unit
) : RecyclerView.Adapter<RecipeAdapter.RecipeVH>() {

    private val items = mutableListOf<RecipeEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeVH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_recipe_card, parent, false)
        return RecipeVH(v)
    }

    override fun onBindViewHolder(holder: RecipeVH, position: Int) {
        holder.bind(items[position], onClick, onFavoriteToggle)
    }

    override fun getItemCount(): Int = items.size

    // PUBLIC_INTERFACE
    fun submit(data: List<RecipeEntity>) {
        items.clear()
        items.addAll(data)
        notifyDataSetChanged()
    }

    class RecipeVH(view: View) : RecyclerView.ViewHolder(view) {
        private val image: ImageView = view.findViewById(R.id.image)
        private val title: TextView = view.findViewById(R.id.title)
        private val description: TextView = view.findViewById(R.id.description)
        private val fav: ImageButton = view.findViewById(R.id.favoriteToggle)

        fun bind(
            item: RecipeEntity,
            onClick: (RecipeEntity) -> Unit,
            onFavoriteToggle: (RecipeEntity) -> Unit
        ) {
            title.text = item.title
            description.text = item.description
            image.setImageResource(android.R.drawable.ic_menu_report_image)
            fav.setImageResource(if (item.isFavorite) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off)

            itemView.setOnClickListener { onClick(item) }
            fav.setOnClickListener { onFavoriteToggle(item) }

            itemView.contentDescription = "${item.title}: ${item.description}"
        }
    }
}
