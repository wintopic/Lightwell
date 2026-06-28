# Contributing

Thanks for helping improve Lightwell.

## Development Setup

1. Install Android Studio or Android command-line tools.
2. Make sure `ANDROID_HOME` points to your Android SDK.
3. Make sure `JAVA_HOME` points to a JDK compatible with the Android Gradle Plugin.
4. Build with:

```bash
./gradlew :app:assembleDesktopDebug --console=plain
```

## Code Style

- Keep changes scoped to the feature or bug being addressed.
- Prefer existing programmatic UI patterns in `MainActivity.java`.
- Avoid adding new dependencies unless they remove meaningful complexity.
- Keep generated files, local SDK paths, APKs, and build output out of commits.

## UI Changes

- Match the current in-app settings style: full pages, card rows, stable spacing, and clear selected states.
- Test text fit on narrow screens.
- Do not replace Dock icons or theme assets unless the change is intentional and documented.

## Verification

Before opening a pull request, run:

```bash
./gradlew :app:assembleDesktopDebug --console=plain
```

If the change affects interaction, also install and check it on a device:

```bash
./gradlew :app:installDesktopDebug --console=plain
```
