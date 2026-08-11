# Connect 4 Game - Android

A modern implementation of the classic Connect 4 game built with Jetpack Compose.

## Features
- Single Player Mode: Play against a smart AI bot.
- Two Player Mode: Local multiplayer to play with friends.
- AI Bot: Powered by a Minimax algorithm with Alpha-Beta pruning.
- Difficulty Levels: Choose between Easy, Medium, and Hard.
- Score Tracking: Keeps track of wins for both players using DataStore.
- Sound Effects: Interactive feedback for game actions.
- Responsive UI: Built entirely with Jetpack Compose Material 3.

## Tech Stack
- Kotlin
- Jetpack Compose (UI)
- ViewModel & StateFlow (Architecture)
- Coroutines (Asynchronous AI calculations)
- DataStore (Local persistence)
- Firebase (Analytics & Crashlytics)

## Project Structure
- `ui/`: Contains Composables for screens and components.
- `model/`: Core game logic, bot algorithms, and state definitions.
- `data/`: Managers for local storage (Scores, Settings).

## Getting Started

### For Users (Download & Play)
1. Go to the **Releases** section on the right side of this repository.
2. Download the latest `Connect4.apk` file.
3. Install it on your Android device and enjoy!

### For Developers (Build from Source)
1. Clone the repository
2. Open the project in Android Studio (Ladybug or newer).
3. Ensure you have Android SDK 28 (Min SDK) or higher installed.
4. Wait for Android Studio to sync your Gradle files, then click Run to launch it on an emulator or device.

