package com.github.rccookie.engine2d;

import java.util.Objects;

import com.github.rccookie.engine2d.impl.Implementation;
import com.github.rccookie.util.Arguments;

import org.jetbrains.annotations.Range;

/**
 * Represents a color in RGBA space.
 */
@SuppressWarnings("StaticInitializerReferencesSubClass")
public class Color {

    /**
     * Constant for calculating a brighter or darker color.
     */
    private static final float DARKTER_FACTOR = 0.7f;

    /**
     * The color black with an alpha value of 0.
     */
    public static final Color CLEAR      = new ConstColor(0,   0,   0,   0);
    /**
     * The color white.
     */
    public static final Color WHITE      = new ConstColor(255, 255, 255, 255);
    /**
     * A light gray.
     */
    public static final Color LIGHT_GRAY = new ConstColor(192, 192, 192, 255);
    /**
     * A gray.
     */
    public static final Color GRAY       = new ConstColor(128, 128, 128, 255);
    /**
     * A dark gray.
     */
    public static final Color DARK_GRAY  = new ConstColor(64,  64,  64 , 255);
    /**
     * The color black.
     */
    public static final Color BLACK      = new ConstColor(0,   0,   0  , 255);
    /**
     * The color red.
     */
    public static final Color RED        = new ConstColor(255, 0,   0  , 255);
    /**
     * An orange.
     */
    public static final Color ORANGE     = new ConstColor(255, 200, 0  , 255);
    /**
     * The color yellow.
     */
    public static final Color YELLOW     = new ConstColor(255, 255, 0  , 255);
    /**
     * The color green.
     */
    public static final Color GREEN      = new ConstColor(0,   255, 0  , 255);
    /**
     * The color cyan.
     */
    public static final Color CYAN       = new ConstColor(0,   255, 255, 255);
    /**
     * The color blue.
     */
    public static final Color BLUE       = new ConstColor(0,   0,   255, 255);
    /**
     * The color magenta.
     */
    public static final Color MAGENTA    = new ConstColor(255, 0,   255, 255);
    /**
     * A pink.
     */
    public static final Color PINK       = new ConstColor(255, 175, 175, 255);

    /**
     * RGBA value between 0 and 255.
     */
    @Range(from = 0, to = 255)
    public final short r, g, b, a;
    /**
     * RGBA float value between 0 and 1. Equivalent to respectable int
     * value when multiplied by 255.
     */
    @Range(from = 0, to = 1)
    public final float fr, fg, fb, fa;

    /**
     * Creates a new color with the given RGBA values.
     *
     * @param r Red
     * @param g Green
     * @param b Blue
     * @param a Alpha
     */
    public Color(@Range(from = 0, to = 255) int r, @Range(from = 0, to = 255) int g, @Range(from = 0, to = 255) int b, @Range(from = 0, to = 255) int a) {
        Arguments.checkRange(r, 0, 256);
        Arguments.checkRange(g, 0, 256);
        Arguments.checkRange(b, 0, 256);
        Arguments.checkRange(a, 0, 256);
        this.r = (short) r;
        this.g = (short) g;
        this.b = (short) b;
        this.a = (short) a;
        fr = r / 255f;
        fg = g / 255f;
        fb = b / 255f;
        fa = a / 255f;
    }

    /**
     * Creates a new color with the given RGBA values.
     *
     * @param r Red
     * @param g Green
     * @param b Blue
     */
    public Color(@Range(from = 0, to = 255) int r, @Range(from = 0, to = 255) int g, @Range(from = 0, to = 255) int b) {
        this(r, g, b, 255);
    }

    /**
     * Creates a new color with the given RGBA values.
     *
     * @param fr Red in float space between 0 and 1
     * @param fg Green in float space between 0 and 1
     * @param fb Blue in float space between 0 and 1
     * @param fa Alpha in float space between 0 and 1
     */
    public Color(@Range(from = 0, to = 1) float fr, @Range(from = 0, to = 1) float fg, @Range(from = 0, to = 1) float fb, @Range(from = 0, to = 1) float fa) {
        Arguments.checkInclusive(fr, 0f, 1f);
        Arguments.checkInclusive(fg, 0f, 1f);
        Arguments.checkInclusive(fb, 0f, 1f);
        Arguments.checkInclusive(fa, 0f, 1f);
        this.fr = fr;
        this.fg = fg;
        this.fb = fb;
        this.fa = fa;
        r = (short) (fr * 255 + 0.5f);
        g = (short) (fg * 255 + 0.5f);
        b = (short) (fb * 255 + 0.5f);
        a = (short) (fa * 255 + 0.5f);
    }

    /**
     * Creates a new color with the given RGBA values.
     *
     * @param fr Red in float space between 0 and 1
     * @param fg Green in float space between 0 and 1
     * @param fb Blue in float space between 0 and 1
     */
    public Color(@Range(from = 0, to = 1) float fr, @Range(from = 0, to = 1) float fg, @Range(from = 0, to = 1) float fb) {
        this(fr, fg, fb, 1f);
    }

    /**
     * Creates a new color with all RGB values set to the same value.
     *
     * @param brightness The value for r,g and b
     * @param a Alpha
     */
    public Color(@Range(from = 0, to = 255) int brightness, @Range(from = 0, to = 255) int a) {
        this(brightness, brightness, brightness, a);
    }

    /**
     * Creates a new color with all RGB values set to the same value.
     *
     * @param fBrightness The value for r,g and b in float space between 0 and 1
     * @param fa Alpha in float space between 0 and 1
     */
    public Color(@Range(from = 0, to = 1) float fBrightness, @Range(from = 0, to = 1) float fa) {
        this(fBrightness, fBrightness, fBrightness, fa);
    }

    /**
     * Creates a new color with all RGB values set to the same value.
     *
     * @param fBrightness The value for r,g and b in float space between 0 and 1
     */
    public Color(@Range(from = 0, to = 1) float fBrightness) {
        this(fBrightness, 1f);
    }

    /**
     * Creates a new color from the given RGB value. Aplha will be ignored and set to 255.
     * <p>The main purpose of this method is to be used with hexadecimal notation
     * (0x00000000) to input a hex color value directly.</p>
     *
     * @param rgb RGB value to set
     */
    public Color(int rgb) {
        this(rgb, false);
    }

    /**
     * Creates a new color from the given RGBA value. If set aplha will be ignored and
     * set to 255.
     * <p>The main purpose of this method is to be used with hexadecimal notation
     * (0x00000000) to input a hex color value directly.</p>
     *
     * @param rgb The RGB or ARGB to set
     * @param a Whether alpha should be used or set to 255
     */
    public Color(int rgb, boolean a) {
        this((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, a ? (rgb >> 24) & 0xFF : 0xFF);
    }

    /**
     * Creates a new color from the given hex string. The hex string may include a
     * leading '#' or not, but must not have any whitespaces or other characters.
     *
     * @param hex The hex string
     */
    public Color(String hex) {
        this(getRGB(hex), true);
    }

    /**
     * Calculates a ARGB value from the given hex string.
     *
     * @param hex The string to parse
     * @return The ARGB value
     */
    private static int getRGB(String hex) {
        if(hex.startsWith("#"))
            hex = hex.substring(1);
        int rgb = Integer.parseInt(hex, 16);
        if(hex.length() <= 6)
            rgb |= 0xFF000000;
        return rgb;
    }





    /**
     * Returns a new color with the given red value.
     *
     * @param r Red
     * @return A copy of this color with the specified red value
     */
    public Color setRed(@Range(from = 0, to = 255) int r) {
        return new Color(r, g, b, a);
    }

    /**
     * Returns a new color with the given green value.
     *
     * @param g Green
     * @return A copy of this color with the specified green value
     */
    public Color setGreen(@Range(from = 0, to = 255) int g) {
        return new Color(r, g, b, a);
    }

    /**
     * Returns a new color with the given blue value.
     *
     * @param b Blue
     * @return A copy of this color with the specified blue value
     */
    public Color setBlue(@Range(from = 0, to = 255) int b) {
        return new Color(r, g, b, a);
    }

    /**
     * Returns a new color with the given alpha value.
     *
     * @param a Alpha
     * @return A copy of this color with the specified alpha value
     */
    public Color setAlpha(@Range(from = 0, to = 255) int a) {
        return new Color(r, g, b, a);
    }

    /**
     * Returns a new color with the given red value.
     *
     * @param fr Red in float space between 0 and 1
     * @return A copy of this color with the specified red value
     */
    public Color setRed(@Range(from = 0, to = 1) float fr) {
        return new Color(fr, fg, fb, fa);
    }

    /**
     * Returns a new color with the given green value.
     *
     * @param fg Green in float space between 0 and 1
     * @return A copy of this color with the specified green value
     */
    public Color setGreen(@Range(from = 0, to = 1) float fg) {
        return new Color(fr, fg, fb, fa);
    }

    /**
     * Returns a new color with the given blue value.
     *
     * @param fb Blue in float space between 0 and 1
     * @return A copy of this color with the specified blue value
     */
    public Color setBlue(@Range(from = 0, to = 1) float fb) {
        return new Color(fr, fg, fb, fa);
    }

    /**
     * Returns a new color with the given alpha value.
     *
     * @param fa Alpha in float space between 0 and 1
     * @return A copy of this color with the specified alpha value
     */
    public Color setAlpha(@Range(from = 0, to = 1) float fa) {
        return new Color(fr, fg, fb, fa);
    }


    /**
     * Returns a new color that is brighter than this color, if possible.
     * Note that darker may not be an exact reverse of this operation.
     *
     * @return A color brighter than this color
     */
    public Color brighter() {

        // From java.awt.Color

        int i = (int) (1/(1- DARKTER_FACTOR));
        if(r == 0 && g == 0 && b == 0)
            return new Color(i, i, i, a);

        return new Color(Math.min((int) (Math.max(r, i)/ DARKTER_FACTOR), 255),
                Math.min((int) (Math.max(g, i)/ DARKTER_FACTOR), 255),
                Math.min((int) (Math.max(b, i)/ DARKTER_FACTOR), 255),
                a);
    }

    /**
     * Returns a new color that is darker than this color, if possible.
     * Note that brighter may not be an exact reverse of this operation.
     *
     * @return A color darker than this color
     */
    public Color darker() {
        return new Color(Math.max((int) (r * DARKTER_FACTOR), 0),
                Math.max((int) (g * DARKTER_FACTOR), 0),
                Math.max((int) (b * DARKTER_FACTOR), 0),
                a);
    }


    /**
     * Returns the RGB complement of this color, i.e. every component (except alhpa)
     * gets inverted.
     *
     * @return This color's complement
     */
    public Color getComplement() {
        return new Color(255 - r, 255 - g, 255 - b, a);
    }

    /**
     * Returns the color with the most contrast (the biggest possible component-wise
     * difference).
     *
     * @return A contrast color to this color
     */
    public Color getContrast() {
        return new Color(
                r > 127 ? 0 : 255,
                g > 127 ? 0 : 255,
                b > 127 ? 0 : 255,
                a
        );
    }

    /**
     * Returns the ARGB of this color.
     *
     * @return The ARGB of this color
     */
    public int getRGB() {
        return  ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                 (b & 0xFF)         |
                ((a & 0xFF) << 24);
    }


    /**
     * Returns a new {@link java.awt.Color} with the same RGBA values as this
     * color. Note that this operation may not be supported on all implementations.
     * Test {@link Implementation#supportsAWT()} first.
     *
     * @return A equivalent java.awt.Color
     */
    public java.awt.Color getAwtColor() {
        return new java.awt.Color(r, g, b, a);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return r == color.r && g == color.g && b == color.b && a == color.a;
    }

    @Override
    public int hashCode() {
        return Objects.hash(r, g, b, a);
    }

    /**
     * Returns this color as hex value with a leading '#'. If alpha is 255
     * it will not be included.
     *
     * @return A string representation of this color
     */
    @Override
    public String toString() {
        String rgb = Integer.toHexString(
                ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF) | 0x01000000
        ).substring(1).toUpperCase();
        return "#" + (a == 255 ? rgb : rgb + Integer.toHexString((a & 0xFF) | 0x0100).substring(1).toUpperCase());
    }


    /**
     * Class for frequently used color constants with cached values.
     */
    private static class ConstColor extends Color {

        private final String toString;
        private final Object awtColor;

        ConstColor(int r, int g, int b, int a) {
            super(r, g, b, a);
            toString = super.toString();
            if(Application.getImplementation().supportsAWT())
                awtColor = super.getAwtColor();
            else awtColor = null;
        }

        @Override
        public String toString() {
            return toString;
        }

        @Override
        public java.awt.Color getAwtColor() {
            return (java.awt.Color) awtColor;
        }
    }
}
