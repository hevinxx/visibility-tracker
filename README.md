# VisibilityTracker for Jetpack Compose [![](https://jitpack.io/v/hevinxx/visibility-tracker.svg)](https://jitpack.io/#hevinxx/visibility-tracker)

`VisibilityTracker` is a Jetpack Compose library designed to track the visibility ratio of composables within your app. It's particularly useful in complex UIs with nested scrollable elements, like `LazyColumn` and `LazyRow`, allowing you to execute logic based on how much of a component is visible on screen. Additionally, it supports treating components as invisible during the `onStop` lifecycle event, making it ideal for managing visibility-related logic efficiently.

## Features
- **Dynamic Visibility Detection**: Automatically detects and reports when the visibility ratio of composables crosses a specified threshold, allowing for targeted actions based on the visibility state. This is particularly effective in complex UIs with nested scrollable elements like LazyColumn and LazyRow.
- **Threshold-Based Callbacks**: Triggers a callback with a Boolean parameter indicating whether the composable has become more visible than the specified threshold ratio (true) or less visible (false). This allows for precise control over visibility-dependent functionality.
- **Lifecycle Integration**: Offers the option to automatically treat composables as invisible (setting the visibility ratio to 0) when the onStop lifecycle event is triggered, optimizing behavior for scenarios where components are not in view.

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
    threshold = 0.5f,
    onVisibilityChanged = { isVisible ->
        if (isVisible) {
            println("More than a half of this composable is visible.")
        } else {
            println("Less than a half of this composable is visible.")
        }
    },
    treatOnStopAsInvisible = true
) {
    // Your composable content here
}
```

## Parameters
- `threshold`: A float value representing the visibility threshold to trigger the callback. The threshold should be a value between 0 and 1, where 0 indicates completely invisible and 1 indicates fully visible. The callback is invoked when the visibility ratio crosses this threshold, either by becoming more or less visible. Default value is 1f, meaning the callback will only be triggered when the composable becomes fully visible or not.
- `onVisibilityChanged`: A callback function that is invoked when the visibility ratio of the content surpasses the specified threshold. The Boolean parameter signifies the visibility state, with true indicating that the composable has become more visible than the threshold ratio, and false indicating it has become less visible. This callback is triggered whenever the visibility ratio crosses the threshold in either direction.
- `treatOnStopAsInvisible`: A Boolean indicating whether to treat the composable as invisible when the `onStop` lifecycle event occurs.
- `content`: The composable content to track.

## Notes
- Ensure that `VisibilityTracker` is used in a composable context where its size and position can be accurately determined.
- The visibility ratio calculation accounts for the visible area of the content relative to its total area, adjusting for any overlap with parent composables or the edge of the screen.

## Limitations
The Visibility Tracker comes with certain limitations. We welcome pull requests if you have solutions to these problems or any enhancements.
- Tracking precision is compromised when a popup overlays part of a view. This is due to the lack of a parent-child relationship between the popup and the underlying view, making it challenging to accurately detect visibility changes.
- Setting the threshold to 0f may not accurately capture the moment a composable becomes fully invisible. This limitation arises from the way visibility changes are detected and reported, particularly in scenarios where onGloballyPositioned does not get called as the view disappears.

## Contributing
We welcome contributions to `VisibilityTracker`! Please feel free to submit issues or pull requests for bugs, feature requests, or documentation improvements.

## License
VisibilityTracker is open-source software licensed under the MIT license.
