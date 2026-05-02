# Lucknow Water Bowl Drive - Android App

Android app for tracking water bowl distributions for street animals in Lucknow.

## Features

- **Dashboard**: View distribution statistics and recent activity
- **Drives**: Create and manage distribution drives
- **Record Distribution**: Capture photos, GPS coordinates, and details at each location
- **Route Planner**: Enter addresses, geocode them, and get an optimized driving route
- **Gallery**: Browse all distribution photos

## Tech Stack

- Kotlin + Jetpack Compose
- Material 3 Design
- MVVM Architecture
- Retrofit + OkHttp for networking
- Coil for image loading
- OSMDroid for maps (OpenStreetMap)
- CameraX for camera

## Setup

1. Open in Android Studio (Hedgehog or later)
2. Let Gradle sync
3. Update `BASE_URL` in `app/build.gradle.kts` to point to your backend
4. Build and run on a device or emulator

## Backend

The app connects to the FastAPI backend. When running on an emulator, `10.0.2.2` maps to `localhost` on your host machine.

## Build APK

```bash
./gradlew assembleDebug
```

The APK will be at `app/build/outputs/apk/debug/app-debug.apk`.
