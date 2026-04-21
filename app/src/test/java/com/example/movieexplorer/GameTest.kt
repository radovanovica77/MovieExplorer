package com.example.movieexplorer

import org.junit.Test
import org.junit.Assert.*

class GameTest {

    // Test za Game klasa
    @Test
    fun game_creation_and_properties_test() {
        // Arrange
        val platform = Platform(PlatformInfo("PC"))
        val testGame = Game(
            id = 1234,
            name = "Minecraft",
            backgroundImage = "https://media.rawg.io/media/games/456.jpg",
            released = "2011-11-18",
            rating = 4.5,
            platforms = listOf(platform)
        )

        assertEquals(1234, testGame.id)
        assertEquals("Minecraft", testGame.name)
        assertEquals("2011-11-18", testGame.released)
        assertEquals(4.5, testGame.rating, 0.0)
        assertNotNull(testGame.platforms)
        assertEquals("PC", testGame.platforms!![0].platform.name)
        assertTrue("Background image should be valid URL",
            testGame.backgroundImage!!.startsWith("https://"))
    }

    // Test za PLATFORM i PLATFORMINFO strukture
    @Test
    fun platform_structure_test() {
        // Arrange
        val platformInfo = PlatformInfo("PlayStation 5")
        val platform = Platform(platformInfo)
        // Assert
        assertEquals("PlayStation 5", platform.platform.name)
        assertNotNull("Platform info should not be null", platform.platform)
    }
    // Test za API
    @Test
    fun gameResponse_parsing_test() {
        // Arrange
        val game1 = Game(1, "Game 1", null, "2024", 4.2, null)
        val game2 = Game(2, "Game 2", null, "2023", 3.8, null)
        val game3 = Game(3, "Game 3", null, "2022", 4.9, null)
        val games = listOf(game1, game2, game3)
        val gameResponse = GameResponse(games)
        // Assert
        assertEquals(3, gameResponse.results.size)
        assertEquals("Game 1", gameResponse.results[0].name)
        assertTrue("Response should contain all games",
            gameResponse.results.any { it.name == "Game 3" })

        // Test sortiranja
        val sortedGames = gameResponse.results.sortedByDescending { it.rating }
        assertEquals("Game 3", sortedGames[0].name) // Highest rating
    }

    // Test za SEARCH VALIDATION
    @Test
    fun search_query_validation_test() {
        // Valid queries
        val validQueries = listOf(
            "minecraft", "gta", "fortnite", "call of duty",
            "assassin's creed", "the witcher 3", "cyberpunk 2077"
        )

        validQueries.forEach { query ->
            assertTrue("Query '$query' should be valid", query.isNotEmpty())
            assertTrue("Query '$query' should be long enough", query.length >= 2)
            assertFalse("Query should not be blank", query.isBlank())
        }

        // Invalid queries
        val invalidQueries = listOf("", " ", "a", "  ", "\t", "\n")

        invalidQueries.forEach { query ->
            val isInvalid = query.isBlank() || query.trim().length < 2
            assertTrue("Query '$query' should be invalid", isInvalid)
        }

        // Edge cases
        assertEquals("Trimmed query should be valid", "gta", "  gta  ".trim())
        assertTrue("Query with numbers should be valid", "gta5".length >= 2)
    }

    // Test za RATING VALIDATION
    @Test
    fun game_rating_boundaries_test() {

        val testCases = mapOf(
            0.0 to true,
            2.5 to true,
            5.0 to true,
            4.99 to true,
            0.01 to true
        )

        testCases.forEach { (rating, shouldBeValid) ->
            val game = Game(1, "Test", null, "2024", rating, null)

            if (shouldBeValid) {
                assertTrue("Rating $rating should be valid",
                    game.rating >= 0.0 && game.rating <= 5.0)
            }
        }
        val preciseGame = Game(1, "Precise", null, "2024", 4.567, null)
        assertEquals(4.567, preciseGame.rating, 0.001)
    }

    // Test za NULL SAFETY
    @Test
    fun game_null_values_handling_test() {
        val gameWithNulls = Game(
            id = 999,
            name = "Test Game",
            backgroundImage = null,
            released = null,
            rating = 4.0,
            platforms = null
        )
        assertNotNull("Game ID should never be null", gameWithNulls.id)
        assertNotNull("Game name should never be null", gameWithNulls.name)
        assertNotNull("Rating should never be null", gameWithNulls.rating)

        assertNull("Background image can be null", gameWithNulls.backgroundImage)
        assertNull("Released date can be null", gameWithNulls.released)
        assertNull("Platforms can be null", gameWithNulls.platforms)
        val gameWithEmptyStrings = Game(
            id = 888,
            name = "",
            backgroundImage = "",
            released = "",
            rating = 3.0,
            platforms = emptyList()
        )
        assertTrue("Empty name should be handled", gameWithEmptyStrings.name.isEmpty())
        assertTrue("Empty platforms list should be handled",
            gameWithEmptyStrings.platforms!!.isEmpty())
    }

    // Test za MULTIPLE PLATFORMS
    @Test
    fun game_multiple_platforms_test() {
        // Arrange
        val platforms = listOf(
            Platform(PlatformInfo("PC")),
            Platform(PlatformInfo("PlayStation 5")),
            Platform(PlatformInfo("Xbox Series X")),
            Platform(PlatformInfo("Nintendo Switch"))
        )

        val multiPlatformGame = Game(
            id = 2024,
            name = "Multi-Platform Game",
            backgroundImage = null,
            released = "2024-02-22",
            rating = 4.8,
            platforms = platforms
        )

        // Assert
        assertEquals(4, multiPlatformGame.platforms!!.size)
        assertTrue("Should contain PC platform",
            multiPlatformGame.platforms!!.any { it.platform.name == "PC" })
        assertTrue("Should contain PlayStation 5",
            multiPlatformGame.platforms!!.any { it.platform.name == "PlayStation 5" })

        // Test platform names
        val platformNames = multiPlatformGame.platforms!!.map { it.platform.name }
        assertTrue("Platform names should contain expected values",
            platformNames.containsAll(listOf("PC", "PlayStation 5", "Xbox Series X")))
    }
    // Test za DATE PARSING i VALIDATION
    @Test
    fun game_release_date_test() {
        // Test različitih formata datuma
        val dateFormats = listOf(
            "2024-02-22",    // Standard format
            "2023-12-31",    // End of year
            "2020-01-01",    // Start of year
            "1999-05-15"     // Older game
        )

        dateFormats.forEach { date ->
            val game = Game(1, "Date Test", null, date, 4.0, null)
            assertEquals("Date should match", date, game.released)
            assertTrue("Date should follow YYYY-MM-DD pattern",
                date.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
        }
    }

    // za sortiranje
    @Test
    fun game_comparison_test() {
        // Arrange
        val games = listOf(
            Game(1, "Low Rated", null, "2024", 2.1, null),
            Game(2, "High Rated", null, "2024", 4.8, null),
            Game(3, "Mid Rated", null, "2024", 3.5, null)
        )

        // Test sortiranja po rating-u
        val sortedByRating = games.sortedByDescending { it.rating }
        assertEquals("High Rated", sortedByRating[0].name)
        assertEquals("Low Rated", sortedByRating[2].name)

        // Test sortiranja po imenu
        val sortedByName = games.sortedBy { it.name }
        assertEquals("High Rated", sortedByName[0].name)
        assertEquals("Mid Rated", sortedByName[2].name)
    }

    // Test za granicne
    @Test
    fun game_edge_cases_test() {
        // Vrlo dugačko ime igre
        val longName = "A".repeat(500)
        val longNameGame = Game(1, longName, null, "2024", 4.0, null)
        assertEquals(500, longNameGame.name.length)

        // Decimalni rating sa više cifara
        val preciseRating = 3.14159
        val preciseGame = Game(2, "Pi Game", null, "2024", preciseRating, null)
        assertEquals(preciseRating, preciseGame.rating, 0.00001)

        // Vrlo star datum
        val oldGame = Game(3, "Pong", null, "1972-11-29", 3.0, null)
        assertNotNull("Old games should have valid dates", oldGame.released)

        // Maksimalni ID
        val maxIdGame = Game(Int.MAX_VALUE, "Max ID", null, "2024", 5.0, null)
        assertEquals(Int.MAX_VALUE, maxIdGame.id)
    }
}