package com.github.rccookie.engine2d.image;

import java.util.function.UnaryOperator;

import com.github.rccookie.engine2d.Application;
import com.github.rccookie.engine2d.impl.ImageImpl;
import com.github.rccookie.engine2d.util.Num;
import com.github.rccookie.engine2d.util.RuntimeIOException;
import com.github.rccookie.geometry.performance.ILine2;
import com.github.rccookie.geometry.performance.float2;
import com.github.rccookie.geometry.performance.int2;
import com.github.rccookie.util.Arguments;
import com.github.rccookie.util.Cloneable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

/**
 * Represents an image.
 */
public class Image implements Cloneable<Image> {

    /**
     * The image's size, in pixels. Do not modify!
     */
    @NotNull
    public final int2 size;

    /**
     * The image's center coordinates. Do not modify!
     */
    @NotNull
    public final int2 center;

    /**
     * The underlying image implementation.
     */
    @NotNull
    private final ImageImpl impl;

    /**
     * Saves whether this image is definitely blank, meaning that drawing it somewhere
     * has no effect.
     */
    private boolean definitelyBlank = true;


    /**
     * Creates a new image.
     *
     * @param width The image's with, in pixels
     * @param height The image's height, in pixels
     */
    public Image(@Range(from = 0, to = Integer.MAX_VALUE) int width, @Range(from = 0, to = Integer.MAX_VALUE) int height) {
        this(new int2(width, height));
    }

    /**
     * Creates a new image.
     *
     * @param size The image's size, in pixels
     */
    public Image(@NotNull int2 size) {
        this(createImpl(size), size.clone());
    }

    /**
     * Creates a new image filled with the given color.
     *
     * @param width The image's width, in pixels
     * @param height The image's height, in pixels
     * @param color The base color for the image
     */
    public Image(@Range(from = 0, to = Integer.MAX_VALUE) int width, @Range(from = 0, to = Integer.MAX_VALUE) int height, @NotNull Color color) {
        this(new int2(width, height), color);
    }

    /**
     * Creates a new image filled with the given color.
     *
     * @param size The image's size, in pixels
     * @param color The base color for the image
     */
    public Image(@NotNull int2 size, @NotNull Color color) {
        this(size);
        fill(color);
    }

    /**
     * Creates a new image from the given implementation with the specified size.
     *
     * @param impl The image implementation to use
     * @param size The size, must be equal to the implementation's size
     */
    private Image(@NotNull ImageImpl impl, @NotNull int2 size) {
        Application.checkSetup();
        this.impl = Arguments.checkNull(impl, "impl");
        this.size = Arguments.checkNull(size, "size");
        this.center = size.dived(2);
    }

    /**
     * Creates a new image from the given implementation.
     *
     * @param impl The image implementation to use
     */
    Image(@NotNull ImageImpl impl) {
        this(impl, impl.getSize());
        definitelyBlank = false;
    }

    @Override
    @NotNull
    public String toString() {
        return "Image(" + size.x + "x" + size.y + ")";
    }

    /**
     * Returns a copy of this image.
     *
     * @return A new, identical image
     */
    @Override
    @NotNull
    @Contract("->new")
    public Image clone() {
        return new Image(impl.clone());
    }


    /**
     * Returns the current transparency of this image. 0 means transparent, 255 means
     * opaque.
     *
     * @return The current transparency
     */
    @Range(from = 0, to = 255)
    public int getAlpha() {
        return impl.getAlpha();
    }

    /**
     * Returns the current transparency of this image in float space. 0 means transparent,
     * 1 means opaque.
     *
     * @return The current transparency
     */
    @Range(from = 0, to = 1)
    public float getAlphaF() {
        return Num.clamp((getAlpha() + 0.5f) / 255, 0, 1);
    }

    /**
     * Sets the transparency of this image. The transparency defines the transparency
     * when drawing this image onto another image, and when displaying it. It does function
     * as a "post-processing" instruction and is independent of previous and subsequent
     * drawing operations onto this image.
     *
     * @param a The alpha value to set. 0 means transparent, 255 means opaque
     */
    public void setAlpha(@Range(from = 0, to = 255) int a) {
        Arguments.checkRange(a, 0, 256);
        impl.setAlpha(a);
    }

    /**
     * Sets the transparency of this image. The transparency defines the transparency
     * when drawing this image onto another image, and when displaying it. It does function
     * as a "post-processing" instruction and is independent of previous and subsequent
     * drawing operations onto this image.
     *
     * @param fa The alpha value to set. 0 means transparent, 1 means opaque
     */
    public void setAlpha(@Range(from = 0, to = 1) float fa) {
        Arguments.checkInclusive(fa, 0f, 1f);
        setAlpha(Num.clamp(Num.round(fa), 0, 255));
    }

    /**
     * Fills the specified rectangle with the given color.
     *
     * @param topLeft The top left of the rectangle
     * @param size Size of the rectangle
     * @param color Color to use
     */
    public void fillRect(@NotNull int2 topLeft, @NotNull int2 size, @NotNull Color color) {
        Arguments.checkNull(topLeft, "topLeft");
        Arguments.checkNull(size, "size");
        Arguments.checkNull(color, "color");

        if(color.a == 0 || size.x * size.y == 0) return;
        impl.fillRect(topLeft, size, color);
        definitelyBlank = false;
    }

    /**
     * Fills the whole image with the given color.
     *
     * @param color The color to use
     */
    public void fill(@NotNull Color color) {
        fillRect(int2.zero, size, color);
    }

    /**
     * Draws the outline of the specified rectangle.
     *
     * @param topLeft The top left of the rectangle
     * @param size The size of the rectangle
     * @param color The color to use
     */
    public void drawRect(@NotNull int2 topLeft, @NotNull int2 size, @NotNull Color color) {
        Arguments.checkNull(topLeft, "topLeft");
        Arguments.checkNull(size, "size");
        Arguments.checkNull(color, "color");

        if(color.a == 0) return;
        impl.drawRect(topLeft, size, color);
        definitelyBlank = false;
    }

    /**
     * Sets the color of the specified pixel.
     *
     * @param location The location to set
     * @param color The color to set
     */
    public void setPixel(@NotNull int2 location, @NotNull Color color) {
        Arguments.checkNull(location, "location");
        Arguments.checkNull(color, "color");
        Arguments.checkRange(location.x, 0, size.x);
        Arguments.checkRange(location.y, 0, size.y);

        impl.setPixel(location, color);
        definitelyBlank &= (color.a == 0);
    }

    /**
     * Returns the color at the specified pixel.
     *
     * @param location The location of the pixel to get
     * @return The color at that pixel
     */
    @NotNull
    @Contract(pure = true)
    public Color getPixel(@NotNull int2 location) {
        Arguments.checkNull(location, "location");
        Arguments.checkRange(location.x, 0, size.x);
        Arguments.checkRange(location.y, 0, size.y);

        return impl.getPixel(location);
    }

    /**
     * Draws the given image onto this image.
     *
     * @param image The image to draw onto this image
     * @param topLeft The pixel where the top left of the drawn image should
     *                be. Can be out of bounds of this image
     */
    public void drawImage(@NotNull Image image, @NotNull int2 topLeft) {
        Arguments.checkNull(image, "image");
        Arguments.checkNull(topLeft, "topLeft");

        if(image.definitelyBlank || image.impl instanceof ImageImpl.ZeroSizeImageImpl) return;
        impl.drawImage(image.impl, topLeft);
        definitelyBlank = false;
    }

    /**
     * Draws the given image onto this image.
     *
     * @param image The image to draw onto this image
     * @param center The pixel where the center of the drawn image should
     *               be. Can be out of bounds of this image
     */
    public void drawImageCr(@NotNull Image image, @NotNull int2 center) {
        Arguments.checkNull(image, "image");
        Arguments.checkNull(center, "center");
        drawImage(image, image.size.dived(-2).add(center));
    }

    /**
     * Draws a line from a to b.
     *
     * @param from Start pixel
     * @param to End pixel
     * @param color The color to use
     */
    public void drawLine(@NotNull int2 from, @NotNull int2 to, @NotNull Color color) {
        Arguments.checkNull(from, "from");
        Arguments.checkNull(to, "to");
        Arguments.checkNull(color, "color");

        if(color.a == 0) return;
        impl.drawLine(from, to, color);
        definitelyBlank = false;
    }

    /**
     * Draws a line from a to b.
     *
     * @param line The line to draw
     * @param color The color to use
     */
    public void drawLine(@NotNull ILine2 line, @NotNull Color color) {
        Arguments.checkNull(line, "line");
        drawLine(line.a, line.b, color);
    }

    /**
     * Draws the outline of an oval onto this image.
     *
     * @param topLeft The top left coordinate of the oval's bounding box.
     * @param size Width and height of the oval
     * @param color The color to use
     */
    public void drawOval(@NotNull int2 topLeft, @NotNull int2 size, @NotNull Color color) {
        Arguments.checkNull(topLeft, "topLeft");
        Arguments.checkNull(size, "size");
        Arguments.checkNull(color, "color");

        if(color.a == 0) return;
        impl.drawOval(topLeft, size, color);
        definitelyBlank = false;
    }

    /**
     * Draws the outline of an oval onto this image.
     *
     * @param center The center of the oval
     * @param size The size of the oval
     * @param color The color to use
     */
    public void drawOvalCr(@NotNull int2 center, @NotNull int2 size, @NotNull Color color) {
        Arguments.checkNull(center, "center");
        Arguments.checkNull(size, "size");
        drawOval(center.added(-size.x / 2, -size.y / 2), size, color);
    }

    /**
     * Draws the outline of an oval onto this image.
     *
     * @param topLeft The top left coordinate of the oval's bounding box.
     * @param diameter Diameter of the oval
     * @param color The color to use
     */
    public void drawCircle(@NotNull int2 topLeft, int diameter, @NotNull Color color) {
        drawOval(topLeft, new int2(diameter, diameter), color);
    }

    /**
     * Draws the outline of a circle onto this image.
     *
     * @param center The center of the oval
     * @param radius The radius of the circle
     * @param color The color to use
     */
    public void drawCircleCr(@NotNull float2 center, float radius, @NotNull Color color) {
        Arguments.checkNull(center, "center");
        drawCircle(new int2((int) (center.x - radius), (int) (center.y - radius)), (int) (radius * 2), color);
    }

    /**
     * Draws the outline of a circle onto this image.
     *
     * @param center The center of the oval
     * @param radius The radius of the circle
     * @param color The color to use
     */
    public void drawCircleCr(@NotNull int2 center, float radius, @NotNull Color color) {
        Arguments.checkNull(center, "center");
        drawCircleCr(center.toF(), radius, color);
    }

    /**
     * Draws an oval onto this image.
     *
     * @param topLeft The coordinates of the top left corner of the oval's
     *                bounding box
     * @param size Width and height of the oval
     * @param color The color to use
     */
    public void fillOval(@NotNull int2 topLeft, @NotNull int2 size, @NotNull Color color) {
        Arguments.checkNull(topLeft, "topLeft");
        Arguments.checkNull(size, "size");
        Arguments.checkNull(color, "color");

        if(color.a == 0) return;
        impl.fillOval(topLeft, size, color);
        definitelyBlank = false;
    }

    /**
     * Draws an oval onto this image.
     *
     * @param center The center of the oval
     * @param size The size of the oval
     * @param color The color to use
     */
    public void fillOvalCr(@NotNull int2 center, @NotNull int2 size, @NotNull Color color) {
        Arguments.checkNull(center, "center");
        Arguments.checkNull(size, "size");
        fillOval(center.added(-size.x / 2, -size.y / 2), size, color);
    }

    /**
     * Draws a circle onto this image.
     *
     * @param topLeft The coordinates of the top left corner of the circle's
     *                bounding box
     * @param diameter The circle's diameter
     * @param color The color to use
     */
    public void fillCircle(@NotNull int2 topLeft, int diameter, @NotNull Color color) {
        fillOval(topLeft, new int2(diameter, diameter), color);
    }

    /**
     * Draws a circle onto this image.
     *
     * @param center The center of the oval
     * @param radius The radius of the circle
     * @param color The color to use
     */
    public void fillCircleCr(@NotNull float2 center, float radius, @NotNull Color color) {
        Arguments.checkNull(center, "center");
        fillCircle(new int2((int) (center.x - radius), (int) (center.y - radius)), (int) (radius * 2), color);
    }

    /**
     * Draws a circle onto this image.
     *
     * @param center The center of the oval
     * @param radius The radius of the circle
     * @param color The color to use
     */
    public void fillCircleCr(@NotNull int2 center, float radius, @NotNull Color color) {
        Arguments.checkNull(center, "center");
        fillCircleCr(center.toF(), radius, color);
    }

    public void round(@Range(from = 0, to = Long.MAX_VALUE) float radius) {
        roundTopLeft(radius);
        roundTopRight(radius);
        roundBottomLeft(radius);
        roundBottomRight(radius);
    }

    public void roundTopLeft(@Range(from = 0, to = Long.MAX_VALUE) float radius) {
        roundCorner(radius, p->p, p->p);
    }

    public void roundTopRight(@Range(from = 0, to = Long.MAX_VALUE) float radius) {
        roundCorner(radius, p -> p.set(size.x - p.x - 1, p.y), p -> p.set(size.x - p.x - 1, p.y));
    }

    public void roundBottomLeft(@Range(from = 0, to = Long.MAX_VALUE) float radius) {
        roundCorner(radius, p -> p.set(p.x, size.y - p.y - 1), p -> p.set(p.x, size.y - p.y - 1));
    }

    public void roundBottomRight(@Range(from = 0, to = Long.MAX_VALUE) float radius) {
        roundCorner(radius, p -> p.set(size.x - p.x - 1, size.y - p.y - 1), p -> p.set(size.x - p.x - 1, size.y - p.y - 1));
    }

    private void roundCorner(@Range(from = 0, to = Long.MAX_VALUE) float radius, UnaryOperator<int2> transform, UnaryOperator<float2> fTransform) {
        Arguments.checkInclusive(radius, 0f, Num.min(size.x, (float) size.y));
        if(definitelyBlank) return;

        float sqrRadius = radius * radius;
        float sqrRadius1 = (radius+1) * (radius+1);

        float2 center = fTransform.apply(new float2(radius, radius));
        int max = Num.round(radius);
        for(int i=0; i<max; i++) {
            for(int j=i; j<max; j++) {
                int2 pixel = transform.apply(new int2(i,j));
                float sqrDist = float2.sqrDist(center, pixel.toF());
                if(sqrDist < sqrRadius) {
                    max = j;
                    break;
                }
                if(sqrDist >= sqrRadius1) {
                    setPixel(pixel, Color.CLEAR);
                    setPixel(transform.apply(pixel.set(j,i)), Color.CLEAR);
                }
                else {
                    float dist = Num.sqrt(sqrDist);
                    Color old = getPixel(pixel);
                    Color now = old.setAlpha(Num.clamp(old.fa * (1 + radius - dist), 0, 1));
                    setPixel(pixel, now);
                    pixel = transform.apply(pixel.set(j,i));
                    old = getPixel(pixel);
                    now = old.setAlpha(Num.clamp(old.fa * (1 + radius - dist), 0, 1));
                    setPixel(pixel, now);
                }
            }
        }
    }

    /**
     * Clears this image.
     */
    public void clear() {
        impl.clear();
        definitelyBlank = true;
    }

    /**
     * Returns a scaled copy of this image.
     *
     * @param newSize The size to scale to
     * @return The scaled copy
     */
    @NotNull
    @Contract("_->new")
    public Image scaled(@NotNull int2 newSize) {
        return scaled(newSize, AntialiasingMode.OFF);
    }

    /**
     * Returns a scaled copy of this image.
     *
     * @param newSize The size to scale to
     * @param aaMode The anti aliasing mode, if available
     * @return The scaled copy
     */
    @NotNull
    @Contract("_,_->new")
    public Image scaled(@NotNull int2 newSize, @NotNull AntialiasingMode aaMode) {
        Arguments.checkNull(newSize, "newSize");
        Arguments.checkNull(aaMode, "aaMode");
        Arguments.checkRange(newSize.x, 0, null);
        Arguments.checkRange(newSize.y, 0, null);

        if(newSize.equals(size)) return clone();
        Image scaled = new Image(impl.scaled(newSize.clone(), aaMode));
        scaled.definitelyBlank = definitelyBlank;
        return scaled;
    }

    /**
     * Returns a scaled copy of this image.
     *
     * @param factor The factor to scale by
     * @param aaMode The anti aliasing mode, if available
     * @return The scaled copy
     */
    @NotNull
    @Contract("_,_->new")
    public Image scaled(@Range(from = 0, to = Long.MAX_VALUE) float factor, @NotNull AntialiasingMode aaMode) {
        Arguments.checkRange(factor, 0f, null);
        return scaled(size.scaled(factor).toI(), aaMode);
    }

    /**
     * Returns a scaled copy of this image.
     *
     * @param factor The factor to scale by
     */
    @NotNull
    @Contract("_->new")
    public Image scaled(@Range(from = 0, to = Long.MAX_VALUE)float factor) {
        return scaled(factor, AntialiasingMode.OFF);
    }

    /**
     * Returns a scaled copy of this image scaled to the given width
     * while remaining the aspect ratio (as good as possible, in int space).
     *
     * @param width The target width
     * @return The scaled copy
     */
    @NotNull
    @Contract("_->new")
    public Image scaledToWidth(@Range(from = 0, to = Integer.MAX_VALUE) int width) {
        Arguments.checkRange(width, 0, null);
        return scaled(new int2(width, Math.max(1, size.y * width / size.x)));
    }

    /**
     * Returns a scaled copy of this image scaled to the given height
     * while remaining the aspect ratio (as good as possible, in int space).
     *
     * @param height The target height
     * @return The scaled copy
     */
    @NotNull
    @Contract("_->new")
    public Image scaledToHeight(@Range(from = 0, to = Integer.MAX_VALUE) int height) {
        Arguments.checkRange(height, 0, null);
        return scaled(new int2(Math.max(1, size.x * height / size.y), height));
    }



    /**
     * Creates a new image from the given file in png or jpg format.
     *
     * @param file The file to load from
     * @return The loaded image
     * @throws RuntimeIOException If an exception occurs loading the file
     */
    @NotNull
    @Contract("_->new")
    public static Image load(@NotNull String file) {
        Arguments.checkNull(file, "file");
        return new Image(Application.getImplementation().getImageFactory().createNew(file.startsWith("images/") ? file : ("images/" + file)));
    }

    /**
     * Returns the underlying image implementation of the image. This is an
     * internal method.
     *
     * @param image The image to get the implementation from
     * @return The image implementation of the image
     */
    @NotNull
    @Contract(pure = true)
    public static ImageImpl getImplementation(@NotNull Image image) {
        return Arguments.checkNull(image, "image").impl;
    }

    /**
     * Returns whether the given image is definitely blank (transparent). A return
     * value of {@code false} does <b>not</b> mean that the image is not blank.
     *
     * @param image The image to check
     * @return {@code true} If the application is sure the image is blank
     */
    @Contract(pure = true)
    public static boolean definitelyBlank(@NotNull Image image) {
        return image.definitelyBlank;
    }



    @NotNull
    private static ImageImpl createImpl(@NotNull int2 size) {
        Arguments.checkRange(size.x, 0, null);
        Arguments.checkRange(size.y, 0, null);
        if(size.x * size.y == 0)
            return new ImageImpl.ZeroSizeImageImpl(size);
        return Application.getImplementation().getImageFactory().createNew(size);
    }


    /**
     * Antialiasing mode. Clears up jagged edges. Warning: May not be available on all
     * implementations, and disclaimer: Java AA is horrible.
     */
    public enum AntialiasingMode {
        /**
         * Highest quality antialiasing.
         */
        HIGH,
        /**
         * Performance antialiasing.
         */
        LOW,
        /**
         * No antialiasing.
         */
        OFF
    }
}
