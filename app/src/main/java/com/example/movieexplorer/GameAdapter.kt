package com.example.movieexplorer

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class GameAdapter(private val favoriteManager: FavoriteManager) : RecyclerView.Adapter<GameAdapter.GameViewHolder>() {

    private var games = listOf<Game>()

    fun setGames(newGames: List<Game>) {
        games = newGames
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GameViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.game_item, parent, false)
        return GameViewHolder(view)
    }

    override fun onBindViewHolder(holder: GameViewHolder, position: Int) {
        val game = games[position]
        holder.bind(game, favoriteManager)
    }

    override fun getItemCount(): Int = games.size

    class GameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val titleTextView: TextView = itemView.findViewById(R.id.textViewTitle)
        private val dateTextView: TextView = itemView.findViewById(R.id.textViewDate)
        private val ratingTextView: TextView = itemView.findViewById(R.id.textViewRating)
        private val posterImageView: ImageView = itemView.findViewById(R.id.imageViewPoster)
        private val favoriteImageView: ImageView = itemView.findViewById(R.id.imageViewFavorite)

        fun bind(game: Game, favoriteManager: FavoriteManager) {
            titleTextView.text = game.name

            // Datum i platforme
            val platforms = game.platforms?.take(2)?.joinToString(", ") { it.platform.name } ?: ""
            dateTextView.text = if (platforms.isNotEmpty()) {
                "${game.released ?: "Nepoznat"} • $platforms"
            } else {
                game.released ?: "Nepoznat datum"
            }

            // Rating
            ratingTextView.text = "${game.rating}"

            // Load game image
            if (!game.backgroundImage.isNullOrEmpty()) {
                Glide.with(itemView.context)
                    .load(game.backgroundImage)
                    .into(posterImageView)
            }

            // Favorite ikona
            updateFavoriteIcon(game.id, favoriteManager)

            // Favorite click
            favoriteImageView.setOnClickListener {
                if (favoriteManager.isFavorite(game.id)) {
                    favoriteManager.removeFromFavorites(game.id)
                } else {
                    favoriteManager.addToFavorites(game)
                }
                updateFavoriteIcon(game.id, favoriteManager)
            }

            // Klik na celu karticu
            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, DetailActivity::class.java)
                intent.putExtra("game_id", game.id)
                intent.putExtra("game_name", game.name)
                intent.putExtra("game_image", game.backgroundImage)
                intent.putExtra("game_rating", game.rating)
                intent.putExtra("game_released", game.released)
                // Dodaj platforme kao string
                val platforms = game.platforms?.joinToString(", ") { it.platform.name } ?: "Nepoznato"
                intent.putExtra("game_platforms", platforms)
                context.startActivity(intent)
            }
        }

        private fun updateFavoriteIcon(gameId: Int, favoriteManager: FavoriteManager) {
            if (favoriteManager.isFavorite(gameId)) {
                favoriteImageView.setImageResource(android.R.drawable.btn_star_big_on) // Puna zvezda
            } else {
                favoriteImageView.setImageResource(android.R.drawable.btn_star_big_off) // Prazna zvezda
            }
        }
    }
}