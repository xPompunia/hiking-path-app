# Szlaczki

Android app for browsing US national park hiking trails, built with Jetpack Compose.

## Features

- Trail list fetched from the [National Park Service API](https://www.nps.gov/subjects/developer/api-documentation.htm), cached locally with Room
- Detail view with full descriptions and images (Coil)
- Per-trail hike stopwatch with saved times and a floating timer overlay
- Material 3 theming with a custom animated logo

## Running

Open the project in Android Studio and add your NPS API key to `local.properties`:

```properties
NPS_API_KEY=your_key_here
```

Then build and run the `app` configuration (minSdk 24).

## Tech stack

Kotlin, Jetpack Compose, Material 3, Room, Retrofit + Gson, Coil, Navigation Compose.
