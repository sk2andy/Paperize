package com.anthonyla.paperize.service.livewallpaper.renderer

import com.anthonyla.paperize.domain.model.WallpaperEffects
import kotlin.math.abs
import kotlin.math.max

/** Calculates smooth live-wallpaper blur while leaving the first launcher page. */
internal object OffsetBlurCalculator {
    const val MIN_OFFSET_STEP = 0.01f
    private const val FULL_BLUR_DISTANCE_IN_SCREENS = 0.5f
    private const val DEFAULT_OFF_HOME_BLUR_PERCENTAGE = 50f

    fun calculateBlurPercentage(
        effects: WallpaperEffects,
        offset: Float,
        offsetStep: Float
    ): Float {
        val baseBlurPercentage = if (effects.enableBlur) {
            effects.blurPercentage.coerceIn(0, 100).toFloat()
        } else {
            0f
        }

        if (!effects.enableBlurOffCenter ||
            !offset.isFinite() ||
            !offsetStep.isFinite() ||
            offsetStep <= MIN_OFFSET_STEP
        ) {
            return baseBlurPercentage
        }

        val distanceFromHome = abs(offset.coerceIn(0f, 1f))
        val fadeDistance = FULL_BLUR_DISTANCE_IN_SCREENS * offsetStep
        val fadeProgress = (distanceFromHome / fadeDistance).coerceIn(0f, 1f)
        val offHomeBlurPercentage = if (effects.enableBlur) {
            baseBlurPercentage
        } else {
            DEFAULT_OFF_HOME_BLUR_PERCENTAGE
        }

        return max(baseBlurPercentage, offHomeBlurPercentage * fadeProgress)
    }
}
