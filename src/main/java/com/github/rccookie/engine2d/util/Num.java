package com.github.rccookie.engine2d.util;

import java.util.Random;

import com.github.rccookie.geometry.performance.FastMath;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Range;

/**
 * Utility math class that includes more functions than {@link Math},
 * especially for floats.
 */
@SuppressWarnings({"SpellCheckingInspection", "GrazieInspection"})
public enum Num {
    ;

    /**
     * Returns the minimum of the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The smaller value of the two
     */
    @Contract(pure = true)
    public static int min(int a, int b) {
        return Math.min(a, b);
    }

    /**
     * Returns the maxmimum of the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The greater value of the two
     */
    @Contract(pure = true)
    public static int max(int a, int b) {
        return Math.max(a, b);
    }

    /**
     * Returns the minimum of the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The smaller value of the two
     */
    @Contract(pure = true)
    public static long min(long a, long b) {
        return Math.min(a, b);
    }

    /**
     * Returns the maxmimum of the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The greater value of the two
     */
    @Contract(pure = true)
    public static long max(long a, long b) {
        return Math.max(a, b);
    }

    /**
     * Returns the minimum of the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The smaller value of the two
     */
    @Contract(pure = true)
    public static float min(float a, float b) {
        return Math.min(a, b);
    }

    /**
     * Returns the maxmimum of the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The greater value of the two
     */
    @Contract(pure = true)
    public static float max(float a, float b) {
        return Math.max(a, b);
    }

    /**
     * Returns the minimum of the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The smaller value of the two
     */
    @Contract(pure = true)
    public static double min(double a, double b) {
        return Math.min(a, b);
    }

    /**
     * Returns the maxmimum of the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The greater value of the two
     */
    @Contract(pure = true)
    public static double max(double a, double b) {
        return Math.max(a, b);
    }

    /**
     * Clamps the given value between the specified lower and upper bound.
     * <p>a should be &lt;= b, otherwise the result may be anything.</p>
     *
     * @param x The value to clamp
     * @param a The lower bound
     * @param b The upper bound
     * @return The value clamped
     */
    @Contract(pure = true)
    public static int clamp(int x, int a, int b) {
        return Math.max(a, Math.min(b, x));
    }

    /**
     * Clamps the given value between the specified lower and upper bound.
     * <p>a should be &lt;= b, otherwise the result may be anything.</p>
     *
     * @param x The value to clamp
     * @param a The lower bound
     * @param b The upper bound
     * @return The value clamped
     */
    @Contract(pure = true)
    public static long clamp(long x, long a, long b) {
        return Math.max(a, Math.min(b, x));
    }

    /**
     * Clamps the given value between the specified lower and upper bound.
     * <p>a should be &lt;= b, otherwise the result may be anything.</p>
     *
     * @param x The value to clamp
     * @param a The lower bound
     * @param b The upper bound
     * @return The value clamped
     */
    @Contract(pure = true)
    public static float clamp(float x, float a, float b) {
        return Math.max(a, Math.min(b, x));
    }

    /**
     * Clamps the given value between the specified lower and upper bound.
     * <p>a should be &lt;= b, otherwise the result may be anything.</p>
     *
     * @param x The value to clamp
     * @param a The lower bound
     * @param b The upper bound
     * @return The value clamped
     */
    @Contract(pure = true)
    public static double clamp(double x, double a, double b) {
        return Math.max(a, Math.min(b, x));
    }

    /**
     * Returns the absolute value of the given value.
     *
     * @param x The value
     * @return The absolute of the value
     */
    public static int abs(int x) {
        return Math.abs(x);
    }

    /**
     * Returns the absolute value of the given value.
     *
     * @param x The value
     * @return The absolute of the value
     */
    public static long abs(long x) {
        return Math.abs(x);
    }

    /**
     * Returns the absolute value of the given value.
     *
     * @param x The value
     * @return The absolute of the value
     */
    public static float abs(float x) {
        return Math.abs(x);
    }

    /**
     * Returns the absolute value of the given value.
     *
     * @param x The value
     * @return The absolute of the value
     */
    public static double abs(double x) {
        return Math.abs(x);
    }

    /**
     * Returns the positive difference between the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The difference between them, always positive
     */
    public static int diff(int a, int b) {
        return Math.abs(a - b);
    }

    /**
     * Returns the positive difference between the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The difference between them, always positive
     */
    public static long diff(long a, long b) {
        return Math.abs(a - b);
    }

    /**
     * Returns the positive difference between the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The difference between them, always positive
     */
    public static float diff(float a, float b) {
        return Math.abs(a - b);
    }

    /**
     * Returns the positive difference between the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The difference between them, always positive
     */
    public static double diff(double a, double b) {
        return Math.abs(a - b);
    }


    /**
     * Returns the average of the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The average of a and b
     */
    @Contract(pure = true)
    public static int average(int a, int b) {
        return (a + b) / 2;
    }

    /**
     * Returns the average of the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The average of a and b
     */
    @Contract(pure = true)
    public static long averate(long a, long b) {
        return (a + b) / 2;
    }

    /**
     * Returns the average of the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The average of a and b
     */
    @Contract(pure = true)
    public static float average(float a, float b) {
        return (a + b) / 2;
    }

    /**
     * Returns the average of the two values.
     *
     * @param a The first value
     * @param b The second value
     * @return The average of a and b
     */
    @Contract(pure = true)
    public static double average(double a, double b) {
        return (a + b) / 2;
    }


    public static int round(float x) {
        return Math.round(x);
    }

    public static int round(double x) {
        return Math.round((float) x);
    }

    public static long roundL(float x) {
        return Math.round((double) x);
    }

    public static long roundL(double x) {
        return Math.round(x);
    }

    public static int floor(float x) {
        return (int) Math.floor(x);
    }

    public static int floor(double x) {
        return (int) Math.floor(x);
    }

    public static long floorL(float x) {
        return (long) Math.floor(x);
    }

    public static long floorL(double x) {
        return (long) Math.floor(x);
    }

    public static int ceil(float x) {
        return (int) Math.ceil(x);
    }

    public static int ceil(double x) {
        return (int) Math.ceil(x);
    }

    public static long ceilL(float x) {
        return (long) Math.ceil(x);
    }

    public static long ceilL(double x) {
        return (long) Math.ceil(x);
    }


    /**
     * Returns the square root of the given value.
     *
     * @param x The value
     * @return The square root of the value
     */
    @Contract(pure = true)
    public static float sqrt(float x) {
        // May be replaced by float-optimized formula
        return (float) Math.sqrt(x);
    }

    /**
     * Returns e raised by the power of the given value
     *
     * @param x The exponent to raise e by
     * @return e^x
     */
    @Contract(pure = true)
    @Range(from = 0, to = Long.MAX_VALUE)
    public static float exp(float x) {
        return (float) Math.exp(x);
    }

    /**
     * Returns a raised by the power of b
     *
     * @param a The value to raise
     * @param b The exponent to raise a by
     * @return a^b
     */
    @Contract(pure = true)
    public static float pow(float a, float b) {
        return (float) Math.pow(a, b);
    }

    /**
     * Returns the natural logarithm of the given value.
     *
     * @param x The value
     * @return ln(x)
     */
    @Contract(pure = true)
    public static float ln(@Range(from = 0, to = Long.MAX_VALUE) float x) {
        return (float) Math.log(x);
    }

    /**
     * Returns the logarithm of x with the basis b. This is equivalent to
     * {@code ln(x) / ln(b)}, so the value may want to be cached elsewhere.
     *
     * @param b The basis for the logarithm
     * @param x The value to evaluate the logarithm at
     * @return log b(x)
     */
    @Contract(pure = true)
    public static float log(@Range(from = 0, to = Long.MAX_VALUE) float b, float x) {
        return (float) (Math.log(x) / Math.log(b));
    }

    /**
     * Returns the value of sin for the specified input.
     *
     * @param degrees The value to evaluate sin at
     * @return sin(degrees)
     */
    @Contract(pure = true)
    @Range(from = -1, to = 1)
    public static float sin(float degrees) {
        return FastMath.sin(degrees);
    }

    /**
     * Returns the value of cosin for the specified input.
     *
     * @param degrees The value to evaluate cosin at
     * @return cos(degrees)
     */
    @Contract(pure = true)
    @Range(from = -1, to = 1)
    public static float cos(float degrees) {
        return FastMath.cos(degrees);
    }

    /**
     * Returns the value of tangens for the specified input.
     *
     * @param degrees The value to evaluate tangens at
     * @return tan(degrees)
     */
    @Contract(pure = true)
    public static float tan(float degrees) {
        return sin(degrees) / cos(degrees);
    }

    /**
     * Returns the value of cotangens for the specified input.
     *
     * @param degrees The value to evaluate cotangens at
     * @return cot(degrees)
     */
    @Contract(pure = true)
    public static float cot(float degrees) {
        return cos(degrees) / sin(degrees);
    }

    /**
     * Returns the value of arcos sin for the specified input.
     *
     * @param x The value to evaluate arcos sin at
     * @return asin(x)
     */
    @Contract(pure = true)
    public static float asin(@Range(from = -1, to = 1) float x) {
        return FastMath.asin(x) * Convert.TO_DEGREES;
    }

    /**
     * Returns the value of arcos cosin for the specified input.
     *
     * @param x The value to evaluate arcos cosin at
     * @return acos(x)
     */
    @Contract(pure = true)
    public static float acos(@Range(from = -1, to = 1) float x) {
        return FastMath.acos(x) * Convert.TO_DEGREES;
    }

    /**
     * Returns the value of arcos tangens for the specified input.
     *
     * @param x The value to evaluate arcos tangens at
     * @return atan(x)
     */
    @Contract(pure = true)
    public static float atan(float x) {
        return (float) Math.atan(x) * Convert.TO_DEGREES;
    }

    /**
     * Returns the value of arcos cotangens for the specified input.
     *
     * @param x The value to evaluate arcos cotangens at
     * @return acot(x)
     */
    @Contract(pure = true)
    public static float acot(float x) {
        return atan(1 / x);
    }

    /**
     * Returns the angle of the given complex number to the x-axis.
     *
     * @param y The imaginary component
     * @param x The real component
     * @return The angle between the x-axis and the vector to that number
     */
    @Contract(pure = true)
    @Range(from = -90, to = 90)
    public static float atan2(float y, float x) {
        return (float) Math.atan2(y, x) * Convert.TO_DEGREES;
    }


    /**
     * Evaluates the sigmiod function at the specified point.
     *
     * @param x The point to evaluate at
     * @return The value of the sigmoid function at that point
     */
    @Contract(pure = true)
    @Range(from = 0, to = 1)
    public static float sigmoid(float x) {
        return 1 / (1 + exp(-x));
    }

    /**
     * Similar to the sigmoid function, but stretched and offset to be within the
     * range (-1,1), so that {@code sigmoid(0) == 0}.
     *
     * @param x The point to evaluate at
     * @return The value of the transformed sigmoid function at that point
     */
    @Contract(pure = true)
    @Range(from = -1, to = 1)
    public static float sigmoid1(float x) {
        return 2 / (1 + exp(-x)) - 1;
    }


    /**
     * Random object.
     */
    private static final Random random = new Random();

    /**
     * Returns a random float in the range [0,1).
     *
     * @return A random float
     */
    @Range(from = 0, to = 1)
    public static float randF() {
        return random.nextFloat();
    }

    /**
     * Returns a random double in the range [0,1).
     *
     * @return A random double
     */
    @Range(from = 0, to = 1)
    public static double rand() {
        return random.nextDouble();
    }

    /**
     * Returns a random int.
     *
     * @return A random int
     */
    public static int randI() {
        return random.nextInt();
    }

    /**
     * Returns a random positive int smaller that the specified limit.
     *
     * @param limit The exclusive upper bound
     * @return A positive random int smaller that the limit
     */
    public static int randI(int limit) {
        return random.nextInt(limit);
    }

    /**
     * Returns a random long.
     *
     * @return A random long
     */
    public static long randL() {
        return random.nextLong();
    }
}
