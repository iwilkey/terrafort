package dev.iwilkey.terrafort.tile;

import java.util.HashMap;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.math.TNoise;

/**
 * An collection of public integer values that delineate where a tile is found on the sprite sheet.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTile {
	
	public static final int CENTER        = 0;
	public static final int CENTER_LEFT   = 1;
	public static final int UPPER_LEFT    = 2;
	public static final int UPPER_CENTER  = 3;
	public static final int UPPER_RIGHT   = 4;
	public static final int CENTER_RIGHT  = 5;
	public static final int BOTTOM_RIGHT  = 6;
	public static final int BOTTOM_CENTER = 7;
	public static final int BOTTOM_LEFT   = 8;
	
	public static final int DX[] = {
	                                        0, 
	                                        -1, 
	                                        -1, 
	                                        0, 
	                                        1, 
	                                        1, 
	                                        1, 
	                                        0, 
	                                        -1	
									};
	
	public static final int DY[] = {
										    0, 
										    0, 
										    -1, 
										    -1, 
										    -1,
										    0, 
										    1, 
										    1, 
										    1
								   };
	
	/**
	 * Return an array of size 9 that encapsulates a center tile. 
	 * @param cx the center x tile to surround.
	 * @param cy the center y tile to surround.
	 * @return the an array of size 9.
	 */
	public static final TFrame[] surroundFrame(int cx, int cy) {
		TFrame arr[] = new TFrame[9];
		for(int i = 0; i < 9; i++) {
			int xx = cx + DX[i];
			int yy = cy + DY[i];
			arr[i] = new TFrame(xx, yy, 1, 1);
		}
		return arr;
	}
	
	/**
	 * A cache for the Tile Height Data (THD.) This will save already known tile heights to avoid recalculation.
	 * 
	 * <p>
	 * This is approach 1 of 2: the position key is a String, directly denoting the coordinates in "x,y" format. This undoubtedly takes a great deal of memory,
	 * and parsing and/or building Strings every tick may prove to be just as computationally inefficient as the problem it aims to solve.
	 * </p>
	 * 
	 * <p>
	 * <strong>NOTE:</strong> The concern above came true. Building the key string every frame is actually <strong>more</strong> computationally inefficient than just checking for the 
	 * noise every tick.
	 * </p>
	 */
	public static final HashMap<String, Integer> THD_CACHE = new HashMap<>();
	
	/**
	 * Returns an array of integers, denoting the terrain height in every cardinal and sub-cardinal direction. Used for infinite
	 * terrain computation.
	 * 
	 * <p>
	 * The performance of this function will dictate the overall performance of the game, because calling {@link TNoise}'s get() is rather expensive. The
	 * use of that function must be highly optimized and only called when absolutely necessary.
	 * </p>
	 * @param seed the seed of the world.
	 * @param cx the center x tile location.
	 * @param cy the center y tile location.
	 * @param freqX the noise x frequency.
	 * @param freqY the noise y frequency.
	 * @return An array of integers, denoting the terrain height in every cardinal and sub-cardinal direction.
	 */
	public static final int[] tileHeightData(long seed, int cx, int cy, float freqX, float freqY) {
		int arr[] = new int[9];
		for(int d = 0; d < 9; d++) {
    		int xx = cx + DX[d];
    		int yy = cy - DY[d];
    		String key = xx + "," + yy;
    		int vq;
    		if(!THD_CACHE.containsKey(key)) {
	    		double v  = TNoise.get(seed, xx * freqX, yy * freqY);
	        	v = (v + 1) / 2;
	        	vq = TMath.quantize(v, 3);
	        	THD_CACHE.put(key, vq);
    		}
        	arr[d] = THD_CACHE.get(key);
    	}
		return arr;
	}
	
	public static final TFrame GRASS[] = surroundFrame(4, 4);
	public static final TFrame SAND[]  = surroundFrame(4, 7);
	public static final TFrame WATER[] = surroundFrame(7, 4);
	
}
