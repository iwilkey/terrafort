package dev.iwilkey.terrafort.math;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.TTerrainRenderer;

/**
 * Quick and efficient mathematical utilities.
 * @author Ian Wilkey (iwilkey)
 */
public final class TMath {
	
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
	 * Translates coordinates in screen-space to world-space coordinates by using inverse projection
	 * of the projection matrix.
	 * @return a new {@link Vector2} that contains the world-space coordinates.
	 */
	public static Vector2 translateScreenToWorldCoordinates(int x, int y) {
		final Vector3 screenCoords = new Vector3(x, y, 0);
	    final Vector3 worldCoords  = TGraphics.CAMERA.unproject(screenCoords);
	    return new Vector2(worldCoords.x, worldCoords.y);
	}
	
	/**
	 * Returns a coordinate that is the input coordinate rounded to the nearest world tile grid location.
	 * @param x the x value input coordinate.
	 * @param y the y value input coordinate.
	 * @return a rounded {@link Vector2} representing the input coordinates rounded to the nearest tile grid location.
	 */
	public static Vector2 roundToWorldTileGrid(float x, float y) {
		x = Math.round(x / TTerrainRenderer.TERRAIN_TILE_WIDTH) * TTerrainRenderer.TERRAIN_TILE_WIDTH;
		y = Math.round(y / TTerrainRenderer.TERRAIN_TILE_HEIGHT) * TTerrainRenderer.TERRAIN_TILE_HEIGHT;
		return new Vector2(x, y);
	}
	
	/**
	 * Translates coordinates in screen-space to tile-space coordinates.
	 * @param x the x value input coordinate.
	 * @param y the y value input coordinate.
	 * @return A {@link Vector2} in tile-space coordinates.
	 */
	public static Vector2 translateScreenToTileCoordinates(int x, int y) {
		final Vector2 ret = translateScreenToWorldCoordinates(x, y);
		ret.set(roundToWorldTileGrid(ret.x, ret.y));
		ret.x = Math.round(ret.x / TTerrainRenderer.TERRAIN_TILE_WIDTH);
		ret.y = Math.round(ret.y / TTerrainRenderer.TERRAIN_TILE_HEIGHT);
		return ret;
	}
	
	/**
	 * This function will return an integer between 0 and levels - 1, indicating the quantization region of the input value.
	 * 
	 * <p>
	 * Ex. 0.24 quantized to level 4 would return 1 because 0.24 lies in the first equal division of the [0, 1] interval in four slices.
	 * </p>
	 * @param value the value to quantize. Must be on an interval [0, 1].
	 * @param levels the level to quantize to. Must be at least 1.
	 * @return
	 */
	public static int quantize(double value, int levels) {
		if(value < 0 || value > 1)
			return -1;
		if(levels < 1)
			return -1;
		int ret = (int)Math.floor(value * levels);
		return Math.min(ret, levels - 1);
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
	
}
