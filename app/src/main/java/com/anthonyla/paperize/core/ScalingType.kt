package com.anthonyla.paperize.core

/**
 * Wallpaper scaling options
 */
enum class ScalingType {
    /**
     * Fill the screen, cropping if necessary
     */
    FILL,

    /**
     * Fit the entire image, adding letterboxing/pillarboxing if necessary
     */
    FIT,

    /**
     * Stretch the image to fill the screen
     */
    STRETCH,

    /**
     * Display at original size
     */
    NONE,

    /**
     * Fill the screen like FILL, but center the image on the first screen
     */
    CENTER;

    companion object {
        fun fromString(value: String?): ScalingType {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: FILL
        }
    }
}
