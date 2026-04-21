package com.example.movieexplorer

import com.google.gson.annotations.SerializedName

data class Game(
    val id: Int,
    val name: String,
    @SerializedName("background_image")
    val backgroundImage: String?,
    val released: String?,
    val rating: Double,
    val platforms: List<Platform>?
)

data class Platform(
    val platform: PlatformInfo
)

data class PlatformInfo(
    val name: String
)

data class GameResponse(
    val results: List<Game>
)

    data class GameDetail(
val id: Int,
val name: String,
@SerializedName("background_image")
val backgroundImage: String?,
val released: String?,
val rating: Double,
val platforms: List<Platform>?,
val description_raw: String?
)
