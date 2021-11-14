package com.github.rccookie.engine2d;

import com.github.rccookie.util.ArgumentOutOfRangeException;

public class Color {

    public static final Color CLEAR      = new Color(0,   0,   0, 0);
    public static final Color WHITE      = new Color(255, 255, 255);
    public static final Color LIGHT_GRAY = new Color(192, 192, 192);
    public static final Color GRAY       = new Color(182, 128, 128);
    public static final Color DARK_GRAY  = new Color(64,  64,  64 );
    public static final Color BLACK      = new Color(0,   0,   0  );
    public static final Color RED        = new Color(255, 0,   0  );
    public static final Color ORANGE     = new Color(255, 200, 0  );
    public static final Color YELLOW     = new Color(255, 255, 0  );
    public static final Color GREEN      = new Color(0,   255, 0  );
    public static final Color CYAN       = new Color(0,   255, 255);
    public static final Color BLUE       = new Color(0,   0,   255);
    public static final Color MAGENTA    = new Color(255, 0,   255);
    public static final Color PINK       = new Color(255, 175, 175);

    public final short r, g, b, a;
    public final float fr, fg, fb, fa;

    public Color(int r, int g, int b, int a) {
        if(r < 0 || r > 255) throw new ArgumentOutOfRangeException(r, 0, 255);
        if(g < 0 || g > 255) throw new ArgumentOutOfRangeException(g, 0, 255);
        if(b < 0 || b > 255) throw new ArgumentOutOfRangeException(b, 0, 255);
        if(a < 0 || a > 255) throw new ArgumentOutOfRangeException(a, 0, 255);
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
        if(fr < 0 || fr > 255) throw new ArgumentOutOfRangeException(fr, 0f, 1f);
        if(fg < 0 || fg > 255) throw new ArgumentOutOfRangeException(fg, 0f, 1f);
        if(fb < 0 || fb > 255) throw new ArgumentOutOfRangeException(fb, 0f, 1f);
        if(fa < 0 || fa > 255) throw new ArgumentOutOfRangeException(fa, 0f, 1f);
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
        this(fr, fg, fb, 0f);
    }

    public Color(int rgb) {
        this((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, (rgb >> 24) & 0xFF);
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
}
