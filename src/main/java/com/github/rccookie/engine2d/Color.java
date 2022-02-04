package com.github.rccookie.engine2d;

import java.util.Objects;

import com.github.rccookie.util.Arguments;

@SuppressWarnings("StaticInitializerReferencesSubClass")
public class Color {

    private static final float FACTOR = 0.7f;

    public static final Color CLEAR      = new ConstColor(0,   0,   0,   0);
    public static final Color WHITE      = new ConstColor(255, 255, 255, 255);
    public static final Color LIGHT_GRAY = new ConstColor(192, 192, 192, 255);
    public static final Color GRAY       = new ConstColor(128, 128, 128, 255);
    public static final Color DARK_GRAY  = new ConstColor(64,  64,  64 , 255);
    public static final Color BLACK      = new ConstColor(0,   0,   0  , 255);
    public static final Color RED        = new ConstColor(255, 0,   0  , 255);
    public static final Color ORANGE     = new ConstColor(255, 200, 0  , 255);
    public static final Color YELLOW     = new ConstColor(255, 255, 0  , 255);
    public static final Color GREEN      = new ConstColor(0,   255, 0  , 255);
    public static final Color CYAN       = new ConstColor(0,   255, 255, 255);
    public static final Color BLUE       = new ConstColor(0,   0,   255, 255);
    public static final Color MAGENTA    = new ConstColor(255, 0,   255, 255);
    public static final Color PINK       = new ConstColor(255, 175, 175, 255);

    public final short r, g, b, a;
    public final float fr, fg, fb, fa;

    public Color(int r, int g, int b, int a) {
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

    public Color(int r, int g, int b) {
        this(r, g, b, 255);
    }

    public Color(float fr, float fg, float fb, float fa) {
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

    public Color(float fr, float fg, float fb) {
        this(fr, fg, fb, 1f);
    }

    public Color(int brightness, int a) {
        this(brightness, brightness, brightness, a);
    }

    public Color(float fBrightness, float fa) {
        this(fBrightness, fBrightness, fBrightness, fa);
    }

    public Color(int brightness) {
        this(brightness, 255);
    }

    public Color(float fBrightness) {
        this(fBrightness, 1f);
    }



    public Color setRed(int r) {
        return new Color(r, g, b, a);
    }

    public Color setGreen(int g) {
        return new Color(r, g, b, a);
    }

    public Color setBlue(int b) {
        return new Color(r, g, b, a);
    }

    public Color setAlpha(int a) {
        return new Color(r, g, b, a);
    }

    public Color setRed(float fr) {
        return new Color(fr, fg, fb, fa);
    }

    public Color setGreen(float fg) {
        return new Color(fr, fg, fb, fa);
    }

    public Color setBlue(float fb) {
        return new Color(fr, fg, fb, fa);
    }

    public Color setAlpha(float fa) {
        return new Color(fr, fg, fb, fa);
    }



    public Color brighter() {

        // From java.awt.Color

        int i = (int) (1/(1-FACTOR));
        if(r == 0 && g == 0 && b == 0)
            return new Color(i, i, i, a);

        return new Color(Math.min((int) (Math.max(r, i)/FACTOR), 255),
                Math.min((int) (Math.max(g, i)/FACTOR), 255),
                Math.min((int) (Math.max(b, i)/FACTOR), 255),
                a);
    }

    public Color darker() {
        return new Color(Math.max((int) (r * FACTOR), 0),
                Math.max((int) (g * FACTOR), 0),
                Math.max((int) (b * FACTOR), 0),
                a);
    }



    public Color getComplement() {
        return new Color(255 - r, 255 - g, 255 - b, a);
    }

    public Color getContrast() {
        return new Color(
                r > 127 ? 0 : 255,
                g > 127 ? 0 : 255,
                b > 127 ? 0 : 255,
                a
        );
    }


    public int getRGB() {
        return  ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8)  |
                 (b & 0xFF)         |
                ((a & 0xFF) << 24);
    }


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

    @Override
    public String toString() {
        String rgb = Integer.toHexString(
                ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF) | 0x01000000
        ).substring(1).toUpperCase();
        return "#" + (a == 255 ? rgb : rgb + Integer.toHexString((a & 0xFF) | 0x0100).substring(1).toUpperCase());
    }


    public static Color fromRGB(int rgb) {
        return new Color((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, (rgb >> 24) & 0xFF);
    }



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
