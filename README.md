# LikeMinds-Android-Feed-SX

The LikeMinds Android Feed SX enables you to integrate personalized and engaging feeds into your Android application, enhancing user experiences 
and driving user engagement. <br> Go through this readme file to integrate Feed-SX and set up a dynamic feed into your Android application smoothly.

## Getting started

### Import Feed-SX module

Firstly, import the module into your project to get started.

1. Clone the master branch of LikeMinds-Android-Feed-SX repository.
2. Import the `feedsx` module in your Android Project.
3. Open your build.gradle file and make sure that the module is now listed under `dependencies.implementation project(":feedsx")`


### Initiate `LikeMindsFeedUI`

Once you have imported the module, initiate the Feed UI calling `LikeMindsFeedUI.initLikeMindsFeedUI()` with necessary parameters.

```kotlin
LikeMindsFeedUI.initLikeMindsFeedUI(
  application,      // instance of your application
  lmUICallback,     // LMUICallback, required to get callbacks
  brandingRequest   // branding data to apply Branding
)
```


