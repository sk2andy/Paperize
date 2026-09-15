package com.anthonyla.paperize.service.livewallpaper.renderer

import com.anthonyla.paperize.domain.model.WallpaperEffects
import org.junit.Assert.assertEquals
import org.junit.Test

class OffsetBlurCalculatorTest {

    @Test
    fun `disabled glass effect keeps regular blur`() {
        assertBlur(
            expected = 30f,
            effects = WallpaperEffects(enableBlur = true, blurPercentage = 30),
            offset = 1f
        )
    }

    @Test
    fun `missing launcher pages disables glass effect`() {
        assertBlur(
            expected = 0f,
            effects = glassEffects(),
            offset = 1f,
            offsetStep = 0f
        )
    }

    @Test
    fun `home page stays sharp`() {
        assertBlur(expected = 0f, effects = glassEffects(), offset = 0f)
    }

    @Test
    fun `glass blur fades in over half a page`() {
        val effects = glassEffects()

        assertBlur(expected = 25f, effects = effects, offset = 0.0625f)
        assertBlur(expected = 50f, effects = effects, offset = 0.125f)
        assertBlur(expected = 50f, effects = effects, offset = 0.25f)
    }

    @Test
    fun `invalid launcher offsets keep base blur`() {
        assertBlur(expected = 0f, effects = glassEffects(), offset = Float.NaN)
        assertBlur(
            expected = 0f,
            effects = glassEffects(),
            offset = 0.25f,
            offsetStep = Float.NaN
        )
    }

    @Test
    fun `regular blur never dips during glass transition`() {
        val effects = WallpaperEffects(
            enableBlur = true,
            blurPercentage = 30,
            enableBlurOffCenter = true
        )

        assertBlur(expected = 30f, effects = effects, offset = 0f)
        assertBlur(expected = 30f, effects = effects, offset = 0.0625f)
        assertBlur(expected = 30f, effects = effects, offset = 0.25f)
    }

    private fun glassEffects() = WallpaperEffects(enableBlurOffCenter = true)

    private fun assertBlur(
        expected: Float,
        effects: WallpaperEffects,
        offset: Float,
        offsetStep: Float = 0.25f
    ) {
        assertEquals(
            expected,
            OffsetBlurCalculator.calculateBlurPercentage(
                effects = effects,
                offset = offset,
                offsetStep = offsetStep
            ),
            0.001f
        )
    }
}
