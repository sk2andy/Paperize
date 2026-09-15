package com.anthonyla.paperize.service.livewallpaper.renderer

import com.anthonyla.paperize.domain.model.WallpaperEffects
import org.junit.Assert.assertEquals
import org.junit.Test

class OffsetBlurCalculatorTest {

    @Test
    fun `disabled glass effect keeps regular blur`() {
        assertOffsetBlur(
            expectedBlur = 30f,
            expectedGlass = 0f,
            effects = WallpaperEffects(enableBlur = true, blurPercentage = 30),
            offset = 1f
        )
    }

    @Test
    fun `missing launcher pages disables glass effect`() {
        assertOffsetBlur(
            expectedBlur = 0f,
            expectedGlass = 0f,
            effects = glassEffects(),
            offset = 1f,
            offsetStep = 0f
        )
    }

    @Test
    fun `home page stays sharp`() {
        assertOffsetBlur(
            expectedBlur = 0f,
            expectedGlass = 0f,
            effects = glassEffects(),
            offset = 0f
        )
    }

    @Test
    fun `glass blur fades in over half a page`() {
        val effects = glassEffects()

        assertOffsetBlur(
            expectedBlur = 25f,
            expectedGlass = 0.5f,
            effects = effects,
            offset = 0.0625f
        )
        assertOffsetBlur(
            expectedBlur = 50f,
            expectedGlass = 1f,
            effects = effects,
            offset = 0.125f
        )
        assertOffsetBlur(
            expectedBlur = 50f,
            expectedGlass = 1f,
            effects = effects,
            offset = 0.25f
        )
    }

    @Test
    fun `invalid launcher offsets keep base blur`() {
        assertOffsetBlur(
            expectedBlur = 0f,
            expectedGlass = 0f,
            effects = glassEffects(),
            offset = Float.NaN
        )
        assertOffsetBlur(
            expectedBlur = 0f,
            expectedGlass = 0f,
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

        assertOffsetBlur(
            expectedBlur = 30f,
            expectedGlass = 0f,
            effects = effects,
            offset = 0f
        )
        assertOffsetBlur(
            expectedBlur = 30f,
            expectedGlass = 0.5f,
            effects = effects,
            offset = 0.0625f
        )
        assertOffsetBlur(
            expectedBlur = 30f,
            expectedGlass = 1f,
            effects = effects,
            offset = 0.25f
        )
    }

    @Test
    fun `zero regular blur still allows off-home glass`() {
        val effects = WallpaperEffects(
            enableBlur = true,
            blurPercentage = 0,
            enableBlurOffCenter = true
        )

        assertOffsetBlur(
            expectedBlur = 50f,
            expectedGlass = 1f,
            effects = effects,
            offset = 0.25f
        )
    }

    private fun glassEffects() = WallpaperEffects(enableBlurOffCenter = true)

    private fun assertOffsetBlur(
        expectedBlur: Float,
        expectedGlass: Float,
        effects: WallpaperEffects,
        offset: Float,
        offsetStep: Float = 0.25f
    ) {
        val result = OffsetBlurCalculator.calculate(
            effects = effects,
            offset = offset,
            offsetStep = offsetStep
        )

        assertEquals(expectedBlur, result.blurPercentage, 0.001f)
        assertEquals(expectedGlass, result.glassFactor, 0.001f)
    }
}
