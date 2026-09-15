package com.anthonyla.paperize.service.livewallpaper.renderer

/**
 * GLSL shader programs for live wallpaper rendering.
 * Includes vertex shaders and fragment shaders for two-pass blur and color effects.
 */
object GLShaders {

    /**
     * Simple vertex shader that passes through positions and texture coordinates.
     * Used for all rendering passes.
     */
    const val VERTEX_SHADER = """
        uniform mat4 u_mvpMatrix;
        attribute vec4 a_position;
        attribute vec2 a_texCoord;
        varying vec2 v_texCoord;
        varying vec2 v_imageCoord;

        void main() {
            gl_Position = u_mvpMatrix * a_position;
            v_texCoord = a_texCoord;
            // a_position spans the complete picture even when its texture is split
            // into tiles. Keep a global image coordinate so image-wide effects do
            // not restart at every tile boundary.
            v_imageCoord = vec2(a_position.x * 0.5 + 0.5, 0.5 - a_position.y * 0.5);
        }
    """

    /**
     * Simple fragment shader for basic texture rendering without effects.
     * Used for testing or when no effects are enabled.
     */
    const val SIMPLE_FRAGMENT_SHADER = """
        precision mediump float;
        uniform sampler2D u_texture;
        uniform float u_alpha;
        varying vec2 v_texCoord;

        void main() {
            vec4 color = texture2D(u_texture, v_texCoord);
            gl_FragColor = vec4(color.rgb, color.a * u_alpha);
        }
    """

    /**
     * Fragment shader for horizontal blur and off-home reeded glass.
     * Blends the smooth 17-tap kernel with Paperize's historical coarse 9-tap
     * kernel. Its separated samples create the original fluted-glass look.
     */
    const val BLUR_HORIZONTAL_FRAGMENT_SHADER = """
        precision mediump float;
        uniform sampler2D u_texture;
        uniform vec2 u_resolution;
        uniform float u_blurRadius;
        uniform float u_glassFactor;
        varying vec2 v_texCoord;

        void main() {
            vec2 pixelSize = 1.0 / u_resolution;
            float r = u_blurRadius;

            vec4 center = texture2D(u_texture, v_texCoord);
            vec4 smoothColor = center * 0.1120;
            vec4 glassColor = center * 0.2210;

            vec4 pair = texture2D(u_texture, v_texCoord + vec2(-0.5 * pixelSize.x * r, 0.0)) +
                        texture2D(u_texture, v_texCoord + vec2( 0.5 * pixelSize.x * r, 0.0));
            smoothColor += pair * 0.1078;
            pair = texture2D(u_texture, v_texCoord + vec2(-1.0 * pixelSize.x * r, 0.0)) +
                   texture2D(u_texture, v_texCoord + vec2( 1.0 * pixelSize.x * r, 0.0));
            smoothColor += pair * 0.0962;
            glassColor += pair * 0.1899;
            pair = texture2D(u_texture, v_texCoord + vec2(-1.5 * pixelSize.x * r, 0.0)) +
                   texture2D(u_texture, v_texCoord + vec2( 1.5 * pixelSize.x * r, 0.0));
            smoothColor += pair * 0.0796;
            pair = texture2D(u_texture, v_texCoord + vec2(-2.0 * pixelSize.x * r, 0.0)) +
                   texture2D(u_texture, v_texCoord + vec2( 2.0 * pixelSize.x * r, 0.0));
            smoothColor += pair * 0.0610;
            glassColor += pair * 0.1215;
            pair = texture2D(u_texture, v_texCoord + vec2(-2.5 * pixelSize.x * r, 0.0)) +
                   texture2D(u_texture, v_texCoord + vec2( 2.5 * pixelSize.x * r, 0.0));
            smoothColor += pair * 0.0434;
            pair = texture2D(u_texture, v_texCoord + vec2(-3.0 * pixelSize.x * r, 0.0)) +
                   texture2D(u_texture, v_texCoord + vec2( 3.0 * pixelSize.x * r, 0.0));
            smoothColor += pair * 0.0286;
            glassColor += pair * 0.0577;
            pair = texture2D(u_texture, v_texCoord + vec2(-3.5 * pixelSize.x * r, 0.0)) +
                   texture2D(u_texture, v_texCoord + vec2( 3.5 * pixelSize.x * r, 0.0));
            smoothColor += pair * 0.0175;
            pair = texture2D(u_texture, v_texCoord + vec2(-4.0 * pixelSize.x * r, 0.0)) +
                   texture2D(u_texture, v_texCoord + vec2( 4.0 * pixelSize.x * r, 0.0));
            smoothColor += pair * 0.0099;
            glassColor += pair * 0.0204;

            gl_FragColor = mix(smoothColor, glassColor, u_glassFactor);
        }
    """

    /**
     * Fragment shader for vertical blur and off-home reeded glass.
     * Uses the same smooth-to-coarse blend as the horizontal pass.
     */
    const val BLUR_VERTICAL_FRAGMENT_SHADER = """
        precision mediump float;
        uniform sampler2D u_texture;
        uniform vec2 u_resolution;
        uniform float u_blurRadius;
        uniform float u_glassFactor;
        varying vec2 v_texCoord;

        void main() {
            vec2 pixelSize = 1.0 / u_resolution;
            float r = u_blurRadius;

            vec4 center = texture2D(u_texture, v_texCoord);
            vec4 smoothColor = center * 0.1120;
            vec4 glassColor = center * 0.2210;

            vec4 pair = texture2D(u_texture, v_texCoord + vec2(0.0, -0.5 * pixelSize.y * r)) +
                        texture2D(u_texture, v_texCoord + vec2(0.0,  0.5 * pixelSize.y * r));
            smoothColor += pair * 0.1078;
            pair = texture2D(u_texture, v_texCoord + vec2(0.0, -1.0 * pixelSize.y * r)) +
                   texture2D(u_texture, v_texCoord + vec2(0.0,  1.0 * pixelSize.y * r));
            smoothColor += pair * 0.0962;
            glassColor += pair * 0.1899;
            pair = texture2D(u_texture, v_texCoord + vec2(0.0, -1.5 * pixelSize.y * r)) +
                   texture2D(u_texture, v_texCoord + vec2(0.0,  1.5 * pixelSize.y * r));
            smoothColor += pair * 0.0796;
            pair = texture2D(u_texture, v_texCoord + vec2(0.0, -2.0 * pixelSize.y * r)) +
                   texture2D(u_texture, v_texCoord + vec2(0.0,  2.0 * pixelSize.y * r));
            smoothColor += pair * 0.0610;
            glassColor += pair * 0.1215;
            pair = texture2D(u_texture, v_texCoord + vec2(0.0, -2.5 * pixelSize.y * r)) +
                   texture2D(u_texture, v_texCoord + vec2(0.0,  2.5 * pixelSize.y * r));
            smoothColor += pair * 0.0434;
            pair = texture2D(u_texture, v_texCoord + vec2(0.0, -3.0 * pixelSize.y * r)) +
                   texture2D(u_texture, v_texCoord + vec2(0.0,  3.0 * pixelSize.y * r));
            smoothColor += pair * 0.0286;
            glassColor += pair * 0.0577;
            pair = texture2D(u_texture, v_texCoord + vec2(0.0, -3.5 * pixelSize.y * r)) +
                   texture2D(u_texture, v_texCoord + vec2(0.0,  3.5 * pixelSize.y * r));
            smoothColor += pair * 0.0175;
            pair = texture2D(u_texture, v_texCoord + vec2(0.0, -4.0 * pixelSize.y * r)) +
                   texture2D(u_texture, v_texCoord + vec2(0.0,  4.0 * pixelSize.y * r));
            smoothColor += pair * 0.0099;
            glassColor += pair * 0.0204;

            gl_FragColor = mix(smoothColor, glassColor, u_glassFactor);
        }
    """

    /**
     * Fragment shader for color effects (darken, vignette, grayscale).
     * Applied after blur passes. Uses branchless math for optimal GPU performance.
     */
    const val EFFECTS_FRAGMENT_SHADER = """
        precision mediump float;
        uniform sampler2D u_texture;
        uniform float u_alpha;
        uniform float u_darkenFactor;
        uniform float u_vignetteFactor;
        uniform float u_grayscaleFactor;
        uniform float u_adaptiveBrightnessFactor;
        varying vec2 v_texCoord;
        varying vec2 v_imageCoord;
 
        void main() {
            vec4 color = texture2D(u_texture, v_texCoord);
 
            // 1. Apply darken (branchless - when factor is 0, multiplier is 1.0)
            color.rgb *= (1.0 - u_darkenFactor);
 
            // 2. Apply vignette — matches CPU vignetteBitmap gradient stops:
            //    [0% dark at center] → [10% dark at 70% of radius] → [80% dark at edge]
            //    radius is normalized to 0.5 UV (image edge along shorter axis)
            vec2 vignetteCenter = v_imageCoord - 0.5;
            float dist = length(vignetteCenter);
            float t = clamp(dist / 0.5, 0.0, 1.0);
            // Inner segment: 0% → 10% over [0, 0.7]
            float innerDark = smoothstep(0.0, 0.7, t) * 0.1;
            // Outer segment: 10% → 80% over [0.7, 1.0]
            float outerDark = smoothstep(0.7, 1.0, t) * 0.7;
            float darkAmount = innerDark + outerDark;
            color.rgb *= (1.0 - darkAmount * u_vignetteFactor);
 
            // 3. Apply grayscale (branchless - mix handles factor 0 correctly)
            // ITU-R BT.709 standard luminance calculation
            float gray = dot(color.rgb, vec3(0.2126, 0.7152, 0.0722));
            color.rgb = mix(color.rgb, vec3(gray), u_grayscaleFactor);
 
            // 4. Apply adaptive brightness multiplier
            color.rgb *= u_adaptiveBrightnessFactor;

            gl_FragColor = vec4(color.rgb, color.a * u_alpha);
        }
    """

    /**
     * Solid color fragment shader for debugging and UI overlays.
     */
    const val COLOR_FRAGMENT_SHADER = """
        precision mediump float;
        uniform vec4 u_color;

        void main() {
            gl_FragColor = u_color;
        }
    """
}
