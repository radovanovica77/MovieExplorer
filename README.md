MovieExplorer
An Android application built with Kotlin for browsing and exploring movies with a clean, modern UI.

Features
- Browse popular movies
- View movie details (title, description, rating, poster)
- Search functionality
- Clean and responsive UI
- API-based movie data integration

Tech Stack
- Language: Kotlin
- IDE: Android Studio
- Architecture: MVVM (ViewModel & LiveData)
- Networking: Retrofit
- UI: RecyclerView

API Key Setup
This project uses an external movie API (e.g. TMDB). The API key is not included in the repository for security reasons.

Steps:

1. Open (or create) the `local.properties` file in the root of the project
2. Add the following line:

```properties
MOVIE_API_KEY=your_api_key_here
```

3. In `app/build.gradle.kts`, the key is read like this:

```kotlin
import java.util.Properties

val localProperties = Properties()
localProperties.load(rootProject.file("local.properties").inputStream())

android {
    ...
    buildFeatures { buildConfig = true }

    defaultConfig {
        ...
        buildConfigField(
            "String",
            "MOVIE_API_KEY",
            "\"${localProperties["MOVIE_API_KEY"]}\""
        )
    }
}
```

4. Use the key in your code via:

```kotlin
BuildConfig.MOVIE_API_KEY
```

> ⚠️ `local.properties` is listed in `.gitignore` and will never be pushed to GitHub.

Getting Started
1. Clone the repository
2. Open in Android Studio
3. Set up your API key (see above)
4. Build and run on an emulator or physical device
