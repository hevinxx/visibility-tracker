# VisibilityTracker for Jetpack Compose [![](https://jitpack.io/v/hevinxx/visibility-tracker.svg)](https://jitpack.io/#hevinxx/visibility-tracker)

`VisibilityTracker` is a Jetpack Compose library designed to track the visibility ratio of composables within your app. It's particularly useful in complex UIs with nested scrollable elements, like `LazyColumn` and `LazyRow`, allowing you to execute logic based on how much of a component is visible on screen. Additionally, it supports treating components as invisible during the `onStop` lifecycle event, making it ideal for managing visibility-related logic efficiently.

## Features
- **Visibility Ratio Tracking**: Dynamically monitor the visibility ratio of composables, with a value between 0 (completely invisible) and 1 (fully visible).
- **Customizable Thresholds**: Invoke callbacks based on customized visibility thresholds, allowing for granular control over visibility-related actions.
- **Lifecycle Awareness**: Optionally treat composables as invisible (`visibilityRatio = 0`) when the `onStop` lifecycle event is triggered, allowing for more precise control over component visibility.
- **Nested Scrollable Support**: Accurately calculate visibility in complex layouts, including nested `LazyColumn` and `LazyRow` scenarios.
- **Flexibility**: Supports both specific threshold-based callbacks and general visibility changes, accommodating a wide range of use cases.

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
Wrap your composable content within VisibilityTracker, specifying visibility thresholds and a callback function for visibility ratio changes. You can define precise points at which to trigger actions based on the visibility of the component.

```kotlin
VisibilityTracker(
    thresholds = listOf(0.5f, 1f),
    onVisibleRatioChanged = { ratio ->
        if (value >= 1f) {
            println("This component is fully visible")
        } else if (value >= 0.5f) {
            println("More than a half of this component is visible")
        } else if (value > 0f) {
            println("Less than a half of this component is visible")
        } else {
            println("This component is invisible")
        }
    },
    treatOnStopAsInvisible = true // Optional: Treat the composable as invisible on onStop
) {
    // Your composable content here
}

```

## Parameters
- `thresholds`: Optional list of float values defining specific visibility thresholds. If null, the callback is invoked for any change in visibility. Default value is listOf(1f) which means the callback is only invoked when a composable becomes fully visible (visibility ratio of 1) or not.
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
