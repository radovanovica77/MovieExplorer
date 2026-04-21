package com.example.movieexplorer

import android.content.Intent
import android.widget.Button
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var editTextSearch: EditText
    private lateinit var buttonSearch: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var gameAdapter: GameAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupViews()
        setupRecyclerView()
        setupSearchButton()
    }

    private fun setupViews() {
        editTextSearch = findViewById(R.id.editTextSearch)
        buttonSearch = findViewById(R.id.buttonSearch)
        recyclerView = findViewById(R.id.recyclerViewMovies)

        // Favorites dugme
        val buttonFavorites = findViewById<Button>(R.id.buttonFavorites)
        buttonFavorites.setOnClickListener {
            val intent = Intent(this, FavoritesActivity::class.java)
            startActivity(intent)
        }  // ← ZATVORENA ZAGRADA ZA setOnClickListener
    }     // ← ZATVORENA ZAGRADA ZA setupViews

    private fun setupRecyclerView() {
        val favoriteManager = FavoriteManager(this)
        gameAdapter = GameAdapter(favoriteManager)
        recyclerView.adapter = gameAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupSearchButton() {
        buttonSearch.setOnClickListener {
            val query = editTextSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchGames(query)
            } else {
                Toast.makeText(this, "Unesite naziv igre", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchGames(query: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitInstance.apiService.searchGames(
                    GameApiService.API_KEY,
                    query
                )

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful && response.body() != null) {
                        val games = response.body()!!.results
                        gameAdapter.setGames(games)

                        if (games.isEmpty()) {
                            Toast.makeText(this@MainActivity, "Nisu pronađene igre", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "Pretraga neuspešna", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Greška: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}