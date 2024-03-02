# VisibilityTracker for Jetpack Compose [![](https://jitpack.io/v/hevinxx/visibility-tracker.svg)](https://jitpack.io/#hevinxx/visibility-tracker)

`VisibilityTracker` is a Jetpack Compose library designed to track the visibility ratio of composables within your app. It's particularly useful in complex UIs with nested scrollable elements, like `LazyColumn` and `LazyRow`, allowing you to execute logic based on how much of a component is visible on screen. Additionally, it supports treating components as invisible during the `onStop` lifecycle event, making it ideal for managing visibility-related logic efficiently.

## Features
- **Visibility Ratio Tracking**: Dynamically monitor the visibility ratio of composables, with a value between 0 (completely invisible) and 1 (fully visible).
- **Lifecycle Awareness**: Optionally treat composables as invisible (`visibilityRatio = 0`) when the `onStop` lifecycle event is triggered, allowing for more precise control over component visibility.
- **Nested Scrollable Support**: Accurately calculate visibility in complex layouts, including nested `LazyColumn` and `LazyRow` scenarios.

## Installation
Step1. Add it in your root build.gradle at the end of repositories:
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```
Step2. Add the dependency:
```gradle
dependencies {
    implementation 'com.github.hevinxx:visibility-tracker:Tag'
}
```

## Usage
To use `VisibilityTracker`, wrap your composable content within it, providing a callback for visibility ratio changes and specifying behavior during the `onStop` event if needed.

```kotlin
VisibilityTracker(
    onVisibleRatioChanged = { visibilityRatio ->
        println("Current visibility ratio: $visibilityRatio")
    },
    treatOnStopAsInvisible = true // Set to false by default
) {
    // Your composable content here
}
```

## Parameters
- `onVisibleRatioChanged`: A callback function that receives the current visibility ratio as a Float.
- `treatOnStopAsInvisible`: A Boolean indicating whether to treat the composable as invisible when the `onStop` lifecycle event occurs.
- `content`: The composable content to track.

## Notes
- Ensure that `VisibilityTracker` is used in a composable context where its size and position can be accurately determined.
- The visibility ratio calculation accounts for the visible area of the content relative to its total area, adjusting for any overlap with parent composables or the edge of the screen.

## Contributing
We welcome contributions to `VisibilityTracker`! Please feel free to submit issues or pull requests for bugs, feature requests, or documentation improvements.

## License
VisibilityTracker is open-source software licensed under the MIT license.
