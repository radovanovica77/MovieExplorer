package com.example.movieexplorer

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.bumptech.glide.Glide
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailActivity : ComponentActivity() {

    private lateinit var favoriteManager: FavoriteManager
    private var currentGame: Game? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        setupViews()
        loadGameDetails()
    }

    private fun setupViews() {
        // Get basic data from intent
        val gameName = intent.getStringExtra("game_name") ?: "Unknown Game"
        val gameImage = intent.getStringExtra("game_image")
        val gameRating = intent.getDoubleExtra("game_rating", 0.0)
        val gameReleased = intent.getStringExtra("game_released")
        val gamePlatforms = intent.getStringExtra("game_platforms")
        val gameId = intent.getIntExtra("game_id", 0)

        // Setup favorite manager
        favoriteManager = FavoriteManager(this)

        // Create current game object
        currentGame = Game(
            id = gameId,
            name = gameName,
            backgroundImage = gameImage,
            released = gameReleased,
            rating = gameRating,
            platforms = null
        )

        // Setup basic views
        findViewById<TextView>(R.id.textViewGameTitle).text = gameName
        findViewById<TextView>(R.id.textViewGameRating).text = "⭐ $gameRating"
        findViewById<TextView>(R.id.textViewGameReleased).text = "Datum: ${gameReleased ?: "Nepoznat"}"
        findViewById<TextView>(R.id.textViewGamePlatforms).text = "Platforme: $gamePlatforms"

        // Load large image
        val imageView = findViewById<ImageView>(R.id.imageViewGamePoster)
        if (!gameImage.isNullOrEmpty()) {
            Glide.with(this)
                .load(gameImage)
                .into(imageView)
        }

        // Back button
        findViewById<Button>(R.id.buttonBack).setOnClickListener {
            finish()
        }

        // Favorite zvezda funkcionalnost
        val favoriteStar = findViewById<ImageView>(R.id.imageViewFavoriteStar)
        updateFavoriteStar(favoriteStar)

        favoriteStar.setOnClickListener {
            currentGame?.let { game ->
                if (favoriteManager.isFavorite(game.id)) {
                    favoriteManager.removeFromFavorites(game.id)
                } else {
                    favoriteManager.addToFavorites(game)
                }
                updateFavoriteStar(favoriteStar)
            }
        }
    }

    private fun updateFavoriteStar(star: ImageView) {
        currentGame?.let { game ->
            if (favoriteManager.isFavorite(game.id)) {
                star.setImageResource(android.R.drawable.btn_star_big_on)
            } else {
                star.setImageResource(android.R.drawable.btn_star_big_off)
            }
        }
    }

    private fun loadGameDetails() {
        val gameId = intent.getIntExtra("game_id", 0)
        if (gameId == 0) return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.apiService.getGameDetails(
                    gameId,
                    GameApiService.API_KEY
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val gameDetail = response.body()!!

                        // Update description
                        val descriptionView = findViewById<TextView>(R.id.textViewGameDescription)
                        val description = gameDetail.description_raw

                        if (!description.isNullOrBlank()) {
                            // Očisti HTML tagove
                            val cleanDescription = description.replace(Regex("<[^>]*>"), "")
                            descriptionView.text = cleanDescription
                        } else {
                            descriptionView.text = "Opis nije dostupan za ovu igru."
                        }

                    } else {
                        findViewById<TextView>(R.id.textViewGameDescription).text = "Greška pri učitavanju opisa."
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    findViewById<TextView>(R.id.textViewGameDescription).text = "Greška: ${e.message}"
                }
            }
        }
    }
}