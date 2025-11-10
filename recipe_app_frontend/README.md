# Recipe Explorer (Ocean Professional)

A Kotlin Android app for browsing, searching, viewing, saving, and sharing recipes. It uses a modern Ocean Professional theme (primary #2563EB, secondary #F59E0B) with rounded corners, subtle shadows, and smooth transitions. Local persistence is provided via Room; seed data is added on first run.

## Features
- Browse: Grid of recipe cards with hero image placeholder, title, description, and favorite toggle.
- Search: Top search field filters recipes by title/description. Pull-to-refresh available.
- Detail: Hero image placeholder, title, ingredients, steps, Share and Save/Unsave buttons.
- Favorites: List of saved recipes.
- Add Recipe: Form with validation (title, description required), optional image URL.
- Share: Native share intent for title and description.
- Accessibility: Content descriptions for key images and actions; responsive layouts.

## Project layout
- app/src/main/kotlin/org/example/app/ui: Activities, Fragments, Adapters
- app/src/main/kotlin/org/example/app/data: Room entities, DAO, Database, Repository
- app/src/main/res/layout: XML layouts for screens and items
- app/src/main/res/values: Colors, Styles, Strings
- Declarative Gradle DSL is used for the build configuration

## Build and Run

Prerequisites: JDK 17, Android SDK with API 34.

Build:
```bash
./gradlew build
```

Install debug:
```bash
./gradlew :app:installDebug
```

Run: Launch "Recipe Explorer" on your device/emulator.

## Notes and Next Steps
- Image loading uses a placeholder; consider integrating Glide/Picasso for remote URLs.
- Add instrumented/unit tests for DAO and repository when required.
- No external APIs; data is local only.
- Storage auto-selects: Room when available; otherwise a JSON SharedPreferences fallback is used so the app still runs under this Declarative DSL without KSP/annotationProcessor.
- If you plan to use Room code generation, configure the proper KSP or annotation processor support in the Declarative Gradle DSL and switch DataRepository to always use Room.