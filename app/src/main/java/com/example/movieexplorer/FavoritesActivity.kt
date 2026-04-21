package com.example.movieexplorer

import android.os.Bundle
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Button

class FavoritesActivity : ComponentActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var favoriteManager: FavoriteManager
    private lateinit var gameAdapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites)

        setupViews()
        loadFavorites()
    }

    private fun setupViews() {
        recyclerView = findViewById(R.id.recyclerViewFavorites)
        emptyText = findViewById(R.id.textViewEmpty)

        // Nazad dugme
        val buttonBack = findViewById<Button>(R.id.buttonBack)
        buttonBack.setOnClickListener {
            finish()
        }

        favoriteManager = FavoriteManager(this)
        gameAdapter = GameAdapter(favoriteManager)

        recyclerView.adapter = gameAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadFavorites() {
        val favorites = favoriteManager.getFavorites()

        if (favorites.isEmpty()) {
            emptyText.visibility = android.view.View.VISIBLE
            recyclerView.visibility = android.view.View.GONE
        } else {
            emptyText.visibility = android.view.View.GONE
            recyclerView.visibility = android.view.View.VISIBLE
            gameAdapter.setGames(favorites)
        }
    }

    override fun onResume() {
        super.onResume()
        loadFavorites() // Refresh kad se vrati na ekran
    }
}