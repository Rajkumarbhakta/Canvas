# 🎨Canvas

<p align="center">
    <img height="100" src="/icons/android/play_store_512.png" alt="icon" title="Icon"/>
</p>

### Paint & Draw

**Sketch, Doodle & Create Art Easily!**

The simplest way to express your creativity – just tap, draw, and create!

Unleash your inner artist with **Paint & Draw**, the ultimate lightweight and user-friendly drawing app for everyone — from beginners to pros. Whether you want to doodle for fun, sketch ideas, or create digital art masterpieces, this app gives you the tools to make it happen in seconds.


# ✨Features
- **Simple & Clean Interface:** Start drawing instantly with a distraction-free design.
- **Customizable Brushes & Colors:** Select from various brush types, sizes, and a full-color palette.
- **Intuitive Canvas:** Draw, erase, and edit with precision on a responsive canvas.
- **Undo & Redo:** Effortlessly step backward or forward through your drawing actions.
- **Save & Share:** Export your creations as images and share them easily.
- **Dark Mode Support:** Draw comfortably in any lighting condition.
- **Offline Functionality:** Create art anytime, even without an internet connection.
- **SQLite Database:** Fast and reliable local storage for all your drawings with proper database structure.

# Tech Stack

- **Kotlin** – Kotlin is a modern, statically typed, cross-platform programming language developed by JetBrains.
- **Compose Multiplatform** – Compose Multiplatform is a declarative framework for building beautiful shared UIs across Android, iOS, desktop, and web – powered by Kotlin Multiplatform.
- **Coroutines** – Asynchronous programming for smooth UI interactions
- **SQLite Database** – Cross-platform SQLite database for efficient local storage of drawings
- **Material Design** – UI/UX guidelines and components

## Database Implementation

Canvas uses SQLite database for storing drawings across all platforms:

- **Android**: Uses `SQLiteOpenHelper` with Android's built-in SQLite
- **Desktop**: Uses SQLite JDBC driver for Windows, Mac, and Linux
- **iOS**: Uses SQLite3 C API through Kotlin/Native
- **Web**: Uses `localStorage` as fallback (IndexedDB for larger datasets)

### Database Schema
```sql
CREATE TABLE drawings (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    time TEXT NOT NULL,
    state TEXT NOT NULL -- Serialized DrawingState
);
```

# Supported Platforms


<p align="center">
    <img src="https://img.icons8.com/color/48/000000/android-os.png" alt="Android" title="Android"/>
    <img src="https://img.icons8.com/color/48/000000/ios-logo.png" alt="iOS" title="iOS"/>
    <img src="https://img.icons8.com/color/48/000000/windows-10.png" alt="Windows" title="Windows"/>
    <img src="https://img.icons8.com/color/48/000000/mac-logo.png" alt="Mac" title="Mac"/>
    <img src="https://img.icons8.com/color/48/000000/linux.png" alt="Linux" title="Linux"/>
    <img src="https://img.icons8.com/color/48/000000/web.png" alt="Web" title="Web"/>
</p>

<p align="center">
    <b>Android</b> &nbsp; | &nbsp; <b>iOS</b> &nbsp; | &nbsp; <b>Desktop (Windows, Mac, Linux)</b> &nbsp; | &nbsp; <b>Web</b>
</p>


# Releases

## Android App

[![Get it on Google Play](https://play.google.com/intl/en_us/badges/static/images/badges/en_badge_web_generic.png)](https://play.google.com/store/apps/details?id=com.rkbapps.canvas)

## iOS App

To run the application on an iPhone device or simulator:

- Open `iosApp/iosApp.xcproject` in Xcode and run the standard configuration.
- Or use the [Kotlin Multiplatform Mobile plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform-mobile) for Android Studio.

To run iOS simulator UI tests:
```sh
./gradlew :composeApp:iosSimulatorArm64Test
```

## Desktop

To run the desktop application:

- Start the app: `./gradlew :composeApp:run`
- Start with **hot reload**: `./gradlew :composeApp:runHot`
- Run desktop UI tests: `./gradlew :composeApp:jvmTest`

The database file is stored at: `%USERPROFILE%\canvas_database.db` (Windows) or `~/canvas_database.db` (Mac/Linux)

## Web

Access the app directly in your browser: [Canvas Web Version](https://me.rkbapps.in/canvas_build/)

# Development

## Database Location

- **Desktop**: `%USERPROFILE%\canvas_database.db` (Windows) or `~/canvas_database.db` (Mac/Linux)
- **Android**: Internal app storage
- **iOS**: App documents directory
- **Web**: Browser localStorage

## Building

```bash
# Build for all platforms
./gradlew build

# Run desktop version
./gradlew :composeApp:run

# Build Android APK
./gradlew :composeApp:assembleDebug
```

# Contribution

We welcome contributions from the community! If you'd like to help improve Canvas, please follow these steps:

1. **Fork the repository** and create your branch from `main`.
2. **Open an issue** to discuss your proposed changes or features.
3. **Make your changes** with clear, descriptive commit messages.
4. **Test your changes** to ensure stability across supported platforms.
5. **Submit a pull request** with a detailed description of your changes.

Please review our [Code of Conduct](CODE_OF_CONDUCT.md) before getting started.

Thank you for helping make Canvas better!

# Licence

This project is licensed under the [MIT License](LICENSE).  
You are free to use, modify, and distribute this software for personal or commercial purposes, provided that you include the original copyright and license notice in any copies or substantial portions of the software.

See the [LICENSE](LICENSE) file for full license text.