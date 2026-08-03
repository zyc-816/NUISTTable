# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project overview
- This repository is a single-module Android app (`:app`) named `NUISTTable`.
- The current implementation is centered on opening the NUIST timetable site inside an embedded `WebView`, letting the user complete the site login flow there, and then extracting the authenticated cookie back into the app.
- The app is built with Gradle Kotlin DSL, Jetpack Compose, and Material 3.

## Common commands
Use `gradlew.bat` on Windows or `./gradlew` in a Unix-like shell.

- Build debug APK: `gradlew.bat :app:assembleDebug`
- Install debug APK to a connected device/emulator: `gradlew.bat :app:installDebug`
- Run local unit tests: `gradlew.bat :app:testDebugUnitTest`
- Run instrumented tests: `gradlew.bat :app:connectedDebugAndroidTest`
- Run a broad build verification: `gradlew.bat build`

`connectedDebugAndroidTest` requires a running Android device or emulator.

## Repository structure
- `settings.gradle.kts` includes only the `:app` module.
- `app/build.gradle.kts` contains the Android app configuration, Compose enablement, and dependencies.
- `app/src/main/java/com/example/nuisttable/MainActivity.kt` contains the app entry point and top-level Compose UI flow.
- `app/src/main/java/com/example/nuisttable/ui/asset/WebPage.kt` contains the embedded `WebView` implementation.
- `app/src/main/java/com/example/nuisttable/web/request.kt` currently exists as a placeholder and does not yet implement a native request/data layer.

## Current architecture
- `MainActivity` calls `NUISTTableApp()`, which owns the top-level UI state.
- The main screen is a Compose `Scaffold` with a theme toggle and an action that opens the timetable/login page.
- When the user chooses to fetch the timetable, the app switches to `CreateWebView(...)` from `ui/asset/WebPage.kt`.
- `CreateWebView` embeds a platform `WebView` through Compose `AndroidView`, configures cookies and browser settings, and watches page loads to determine when login/timetable navigation has succeeded.
- Login success is currently inferred by reaching the timetable page and finding a cookie containing `GS_SESSIONID`; that cookie string is then passed back to the Compose screen.
- There is no implemented native API client, repository layer, or timetable parsing flow yet; current behavior is still WebView-first.

## WebView-specific behavior
- `WebPage.kt` sets a desktop Chrome user agent. This appears to be important for compatibility with the target site.
- The WebView enables JavaScript, DOM storage, multiple windows, zoom support, and mixed content.
- Back navigation is handled inside the WebView first; if the WebView cannot go back, the app closes the WebView screen.
- `onPageFinished` is the key hook for detecting a successful return to the timetable page and extracting cookies.
- `onReceivedSslError` currently calls `handler?.proceed()`, so do not assume SSL failures are being blocked.

## Android/runtime notes
- `app/src/main/AndroidManifest.xml` declares `INTERNET` and sets `android:usesCleartextTraffic="true"`.
- The app currently targets a modern Android baseline (`minSdk = 33`) and uses Compose instead of XML layouts.
- Release optimization is currently disabled in `app/build.gradle.kts`.

## Testing status
- `app/src/test/java/com/example/nuisttable/ExampleUnitTest.kt` is still the template arithmetic test.
- `app/src/androidTest/java/com/example/nuisttable/ExampleInstrumentedTest.kt` is still the template package-name test.
- Do not assume meaningful functional coverage exists yet; verify behavior through the app flow when changing WebView/login logic.

## Guidance for future changes
- Prefer keeping UI work in the existing Compose style used by `MainActivity.kt`.
- Treat `ui/asset/WebPage.kt` as the current source of truth for WebView behavior unless the project is intentionally refactored.
- If a native networking layer is added later, update this file to reflect whether `web/request.kt` is still a placeholder or has become the real request entry point.
- Ignore generated build outputs when reasoning about the codebase; focus on Gradle config and `app/src/...` source files.