package com.example.movieexplorer
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Path
interface GameApiService {

    @GET("games")
    suspend fun searchGames(
        @Query("key") apiKey: String,
        @Query("search") query: String
    ): Response<GameResponse>

    @GET("games/{id}")
    suspend fun getGameDetails(
        @Path("id") gameId: Int,
        @Query("key") apiKey: String
    ): Response<GameDetail>

    companion object {
        const val BASE_URL = "https://api.rawg.io/api/"
        const val API_KEY = "fbb8788e7c1a416ea403e1eae9546886"
    }
}