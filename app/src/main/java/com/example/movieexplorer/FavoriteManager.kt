package com.example.movieexplorer

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoriteManager(context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("favorites", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun addToFavorites(game: Game) {
        val favorites = getFavorites().toMutableList()
        if (!favorites.any { it.id == game.id }) {
            favorites.add(game)
            saveFavorites(favorites)
        }
    }

    fun removeFromFavorites(gameId: Int) {
        val favorites = getFavorites().toMutableList()
        favorites.removeAll { it.id == gameId }
        saveFavorites(favorites)
    }

    fun isFavorite(gameId: Int): Boolean {
        return getFavorites().any { it.id == gameId }
    }

    fun getFavorites(): List<Game> {
        val json = sharedPreferences.getString("favorites_list", "[]")
        val type = object : TypeToken<List<Game>>() {}.type
        return gson.fromJson(json, type) ?: emptyList()
    }

    private fun saveFavorites(favorites: List<Game>) {
        val json = gson.toJson(favorites)
        sharedPreferences.edit().putString("favorites_list", json).apply()
    }
}