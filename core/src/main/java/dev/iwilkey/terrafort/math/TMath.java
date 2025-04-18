package dev.iwilkey.terrafort.math;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.gfx.TGraphics;

/**
 * Quick and efficient mathematical utilities for Terrafort.
 * @author Ian Wilkey (iwilkey)
 */
public final class TMath {

	public static final byte SOUTH      = 0;
	public static final byte SOUTH_EAST = 1;
	public static final byte EAST       = 2;
	public static final byte NORTH_EAST = 3;
	public static final byte NORTH      = 4;
	public static final byte NORTH_WEST = 5;
	public static final byte WEST       = 6;
	public static final byte SOUTH_WEST = 7;
	public static final byte CENTER     = 8;
	public static final byte DX[]       = new byte[9];
	public static final byte DY[]       = new byte[9];
	
	static {
		DX[CENTER] 						= 0;
		DY[CENTER] 						= 0;
		DX[SOUTH] 						= 0;
		DY[SOUTH] 						= 1;
		DX[SOUTH_EAST]					= 1;
		DY[SOUTH_EAST] 					= 1;
		DX[EAST] 						= 1;
		DY[EAST] 						= 0;
		DX[NORTH_EAST] 					= 1;
		DY[NORTH_EAST] 					= -1;
		DX[NORTH] 						= 0;
		DY[NORTH] 						= -1;
		DX[NORTH_WEST] 					= -1;
		DY[NORTH_WEST] 					= -1;
		DX[WEST] 						= -1;
		DY[WEST] 						= 0;
		DX[SOUTH_WEST] 					= -1;
		DY[SOUTH_WEST] 					= 1;
	}
	
	/**
	 * Returns the {@link TInput} cursor projected into world-space.
	 */
	public static Vector2 cursorToWorldSpace() {
		final Vector2 ret = new Vector2(TInput.cursorX, Gdx.graphics.getHeight() - TInput.cursorY);
		final float cw = Gdx.graphics.getWidth();
		final float ch = Gdx.graphics.getHeight();
		final float cx = TGraphics.WORLD_PROJ_MAT.position.x - (cw / 2f);
		final float cy = TGraphics.WORLD_PROJ_MAT.position.y - (ch / 2f);
		ret.add(cx, cy);
		return ret;
	}
	
	/**
	 * Returns the angle of the {@link TInput} cursor, in degrees, relative to the X-axis intersecting the center of the screen.
	 */
	public static float radialAngleOfCursorDeg() {
		final float cx  = Gdx.graphics.getWidth() / 2f;
		final float cy  = Gdx.graphics.getHeight() / 2f;
		float       ang = ((float)Math.atan2(cy - TInput.cursorY, TInput.cursorX - cx)) * (180f / (float)Math.PI);
		if(ang < 0) 
			ang += 360;
		return ang;
	}
	
	/**
	 * Clamp a floating point number to a lower and upper bound.
	 * @param in the number to clamp.
	 * @param min the minimum value.
	 * @param max the maximum value.
	 * @return the number, clamped to [min, max].
	 */
	public static float clamp(float in, float min, float max) {
		in = Math.max(min, in);
		in = Math.min(max, in);
		return in;
	}
	  
	/**
	 * This function will return an integer between 0 and levels - 1, indicating the partition region of the input value.
	 * <p>
	 * Ex. 0.24 equally partitioned to level 4 would return 1 because 0.24 lies in the first equal division of the [0, 1] interval in four slices.
	 * </p>
	 * <p>
	 * The function will also accept an array of float values "partitionMagnitude" that segment the interval [0, 1] unequally, where partitionMagnitude[i] 
	 * corresponds to what percentage partition segment "i" gets of [0, 1].
	 * </p>
	 */
	public static int partition(float value, int levels, float... partitionMagnitude) {
		if(!inInclusiveInterval(value, 0, 1))
			throw new IllegalArgumentException("TMath.segment(): first parameter \"value\" must be in the interval [0, 1].");
		if(partitionMagnitude.length == 0)
			return (int)Math.floor(value * levels);
		if(partitionMagnitude.length > levels)
			throw new IllegalArgumentException("TMath.segment(): given partitionMagnitude array length must be less than or equal to given levels.");
		float pb = 0.0f; // parition bound...
		for(int i = 0; i < levels; i++) {
			final float partitionSize = (partitionMagnitude.length > i) ? partitionMagnitude[i] : ((1.0f - pb) / (levels - partitionMagnitude.length));
			pb += partitionSize;
			if (value < pb)
				return i;
		}
		// edge case for v == 1.
		return levels - 1;
	}
	
	/**
	 * Returns whether or not a given value is in the interval [lb, rb].
	 */
	public static boolean inInclusiveInterval(float val, float lb, float rb) {
		return (val >= lb && val <= rb);
	}
	
	/**
	 * Returns the order of magnitude (figures) of a given number, base 10. For example, 100,000 has an order of 6 because it takes 6 arranged numbers to
	 * represent it.
	 */
	public static int getFigures(long number) {
		int fig = 0;
		while(number != 0) {
			number /= 10;
			fig++;
		}
		return fig;
	}
	
	/**
	 * The inverse "fast inverse square root" from the Quake 3 engine. Just for fun :)
	 * 
	 * <p>
	 * DO NOT USE. Compared to the native {@link Math.sqrt()} function, this approximation is not only inaccurate, but takes up to 20 times longer
	 * to process. This addition to the Terrafort codebase is a homage to the clever engineers who originally developed this when native sqrt() functions were expensive and poorly
	 * optimized.
	 * </p>
	 * 
	 * <p>
	 * Learn more here: <i>https://stackoverflow.com/questions/1349542/john-carmacks-unusual-fast-inverse-square-root-quake-iii</i>
	 * </p>
	 * 
	 * @param number the number to approximate the square root of.
	 * @return the approximated square root of the given number.
	 */
	public static float quakeSqrt(float number) {
		final float xhalf = 0.5f * number;
	    int i = Float.floatToIntBits(number);
	    i = 0x5f3759df - (i >> 1);
	    number = Float.intBitsToFloat(i);
	    number *= (1.5f - xhalf * number * number);
	    return 1.0f / number;
	}
	
	/**
	 * Round a given number to a given amount of decimals.
	 * @param in the number to round.
	 * @param decimals the number of decimals to round to.
	 * @return the rounded number.
	 */
	public static float roundTo(float in, float decimals) {
		float d = (float)decimals;
		return (float)Math.round(in * d) / d;
	}
	
	/**
     * Performs linear interpolation between two values.
     *
     * @param a The start value.
     * @param b The end value.
     * @param t The interpolation factor, where 0 is the start value and 1 is the end value.
     * @return The interpolated value between 'a' and 'b'.
     */
	public static double lerp(double a, double b, double t) {
		return a + t * (b - a);
    }
	
	/**
	 * Uses ThreadLocalRandom to generate a random float between [a, b) of uniform distribution.
	 * @param a the lower bound (inclusive).
	 * @param b the upper bound (exclusive).
	 * @return a random number [a, b) of uniform distribution.
	 */
	public static float nextFloat(float a, float b) {
		return (float)ThreadLocalRandom.current().nextDouble(a, b);
	}
	
	/**
	 * Returns a number of the list that is selected with uniform distribution.
	 * @param values the amount of values to pick from
	 * @return a number from the list, all with equal chance to be picked.
	 */
	public static float equalPick(float... values) {
		return values[ThreadLocalRandom.current().nextInt(0, values.length)];
	}
	
	/**
     * Selects a value from the provided values array based on the specified likelihoods in the chances array.
     * Each index in the chances array represents the probability of selecting the corresponding index in the values array.
     * The sum of the chances array should be 1 to represent a valid probability distribution.
     * 
     * @param values Array of values to choose from.
     * @param chances Array representing the probability of each index being chosen, must sum to 1.
     * @return A value from the values array, selected based on the specified probabilities in the chances array.
     */
    public static float unequalPick(float[] values, float[] chances) {
    	if(chances.length != values.length)
    		throw new IllegalArgumentException("TMath.unequalPick(): Chance and value vector should be of equal length!");
    	if(chances.length == 0) 
    		throw new IllegalArgumentException("TMath.unequalPick(): Chance and value vector should have at least one value!");
        float sum = 0;
        for(final float chance : chances)
            sum += chance;
        if (Math.abs(sum - 1.0f) > 0.0001)
        	throw new IllegalArgumentException("TMath.unequalPick(): Sum of chances must be approximately 1!");
        final float rf = ThreadLocalRandom.current().nextFloat();
        float cp = 0.0f;
        for (int i = 0; i < chances.length; i++) {
            cp += chances[i];
            if (rf < cp)
                return values[i];
        }
        return values[values.length - 1];
    }
	
}
