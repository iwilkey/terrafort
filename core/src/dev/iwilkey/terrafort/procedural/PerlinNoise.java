package dev.iwilkey.terrafort.procedural;

import java.util.Random;

/**
 * A class for generating Perlin Noise.
 * Perlin Noise is a gradient noise developed by Ken Perlin, useful in graphical applications
 * to generate procedural textures, shapes, terrains, and other structures.
 * 
 * @author iwilkey
 */
public final class PerlinNoise {
	
	/** The original permutation vector. */
    private static final int[] PERMUTATION = { 151,160,137,91,90,15,
        131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
        190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
        88,237,149,56,87,174,20,125,136,171,168, 68,175,74,165,71,134,139,48,27,166,
        77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
        102,143,54, 65,25,63,161, 1,216,80,73,209,76,132,187,208, 89,18,169,200,196,
        135,130,116,188,159,86,164,100,109,198,173,186, 3,64,52,217,226,250,124,123,
        5,202,38,147,118,126,255,82,85,212,207,206,59,227,47,16,58,17,182,189,28,42,
        223,183,170,213,119,248,152, 2,44,154,163, 70,221,153,101,155,167, 43,172,9,
        129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
        251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
        49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
        138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
    };
    
    /** The permutation vector, doubled for wrap-around. */
    private final int[] p = new int[512];
    
    /**
     * Creates a new PerlinNoise instance with the given seed.
     * The seed is used to shuffle the permutation vector.
     *
     * @param seed The seed to use for the permutation vector shuffle.
     */
    public PerlinNoise(long seed) {
        shuffle(seed);
        for(int i = 0; i < 256; i++) {
            p[256 + i] = p[i] = PERMUTATION[i];
        }
    }
    
    /**
     * Shuffles the permutation vector using the given seed.
     *
     * @param seed The seed to use for the shuffle.
     */
    private void shuffle(long seed) {
        Random random = new Random(seed);
        for(int i = 0; i < PERMUTATION.length; i++) {
            int j = random.nextInt(PERMUTATION.length);
            int temp = PERMUTATION[i];
            PERMUTATION[i] = PERMUTATION[j];
            PERMUTATION[j] = temp;
        }
    }
    
    /**
     * Easing function as defined by Ken Perlin.
     * This smooths the curve at the beginning and end.
     *
     * @param t The value to apply the easing to.
     * @return The eased value.
     */
    private double fade(double t) {
        return t * t * t * (t * (t * 6 - 15) + 10);
    }
    
    /**
     * Linear interpolation function.
     *
     * @param t The value to interpolate.
     * @param a The first point.
     * @param b The second point.
     * @return The interpolated value.
     */
    private double lerp(double t, double a, double b) {
        return a + t * (b - a);
    }

    /**
     * Gradient function.
     *
     * @param hash The hash value.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The gradient value.
     */
    private double grad(int hash, double x, double y) {
        int h = hash & 3;
        double u = h < 2 ? x : y,
               v = h < 2 ? y : x;
        return ((h & 1) == 0 ? u : -u) + ((h & 2) == 0 ? 2.0 * v : -2.0 * v);
    }
    
    /**
     * Noise generation function.
     *
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @return The Perlin noise value at the given coordinates.
     */
    public double noise(double x, double y) {
        int X = (int) Math.floor(x) & 255,
            Y = (int) Math.floor(y) & 255;
        x -= Math.floor(x);
        y -= Math.floor(y);
        double u = fade(x),
               v = fade(y);
        int A = p[X] + Y, 
            AA = p[A], 
            AB = p[A + 1],
            B = p[X + 1] + Y, 
            BA = p[B], 
            BB = p[B + 1];
        return lerp(v, lerp(u, grad(p[AA], x, y),
                                grad(p[BA], x - 1, y)),
                       lerp(u, grad(p[AB], x, y - 1),
                                grad(p[BB], x - 1, y - 1)));
    }
    
}
