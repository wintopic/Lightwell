# Lightwell

Lightwell, also called 隙光 in the app, is a lightweight Android app panel for quickly finding and launching installed apps. It is designed for constrained modes such as aggressive battery saving, where the normal launcher may be inconvenient or unavailable.

Lightwell is not registered as a replacement home launcher. It opens as a regular app or from its Quick Settings tile, then presents a focused desktop-like panel with a grid, Dock, app search, app management, and theme controls.

## Features

- 12-cell and 20-cell desktop grid layouts.
- Home grid backed by a saved app list, so startup avoids scanning every installed app.
- Full app list with search, batch add, Chinese initial matching, and A-Z fast index.
- Long-press app actions for home placement, Dock pinning, hiding, uninstall, and app info.
- Dock with default phone, system settings, and messages shortcuts.
- Drag support for moving home icons and pinning apps to Dock.
- Theme system with Pantone Color of the Year themes from 2000 to 2025.
- SmartisanOS-style themes, texture-backed grid cells, and Dock backgrounds.
- Transparent/frosted wallpaper theme.
- Page switch animations, including slide, flip, shutter, and card styles.
- In-app settings pages for layout, appearance, icon style, animation, hidden apps, Dock apps, usage records, and reset actions.

## Screens

The main surfaces are:

- Home panel: app grid, page indicator, and Dock.
- App list: search, recent apps, batch add, and A-Z index.
- Panel settings: layout, appearance, animations, icon options, management, and system info.

## Project Structure

```text
.
├── app/
│   ├── build.gradle
│   └── src/
│       ├── desktop/                    # Product flavor manifest.
│       └── main/
│           ├── AndroidManifest.xml
│           ├── assets/
│           │   ├── smartisan/           # Classic texture and preview assets.
│           │   └── smartisan_themes/    # SmartisanOS-style theme textures.
│           ├── java/com/xiguang/app/
│           │   ├── MainActivity.java    # Main panel, settings, themes, app list, animations.
│           │   └── PanelTileService.java
│           └── res/                     # Strings, styles, launcher icons, Dock icons.
├── build.gradle
├── settings.gradle
├── gradle/
├── README.md
├── CHANGELOG.md
├── CONTRIBUTING.md
├── LICENSE
└── THIRD_PARTY_NOTICES.md
```

## Requirements

- Android Studio or command-line Android Gradle tooling.
- Android SDK with the configured compile SDK available.
- JDK compatible with the Android Gradle Plugin used by this project.

The current Android package id is:

```text
com.xiguang.app
```

The package id is intentionally left unchanged to avoid breaking existing installs during development.

## Build

Set your local SDK and JDK paths in the normal Android way. For example:

```bash
export ANDROID_HOME=/path/to/Android/Sdk
export JAVA_HOME=/path/to/jdk
./gradlew :app:assembleDesktopDebug --console=plain
```

The debug APK is generated at:

```text
app/build/outputs/apk/desktop/debug/app-desktop-debug.apk
```

Install to a connected device:

```bash
./gradlew :app:installDesktopDebug --console=plain
```

## Development Notes

- Most UI is built programmatically in `MainActivity.java`; there are very few XML layouts.
- The app stores user state in `SharedPreferences`, including home apps, hidden apps, Dock pins, sort mode, theme, icon options, and animation settings.
- Texture-backed themes use assets under `app/src/main/assets/`.
- If a texture asset is missing, the app falls back to generated colors and drawables instead of crashing.
- Generated build output, IDE files, local SDK paths, and APK artifacts should not be committed.

## Repository Name

The GitHub repository is intended to be named:

```text
Lightwell
```

The app display name can remain `隙光`.

## License

Project code is licensed under the Apache License 2.0 unless otherwise noted. Third-party assets and notices are listed in `THIRD_PARTY_NOTICES.md`.
