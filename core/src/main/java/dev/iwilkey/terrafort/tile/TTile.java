package dev.iwilkey.terrafort.tile;

import dev.iwilkey.terrafort.gfx.TFrame;

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
	public static final TFrame[] surround(int cx, int cy) {
		TFrame arr[] = new TFrame[9];
		for(int i = 0; i < 9; i++) {
			int xx = cx + DX[i];
			int yy = cy + DY[i];
			arr[i] = new TFrame(xx, yy, 1, 1);
		}
		return arr;
	}
	
	public static final TFrame GRASS[] = surround(3, 3);
	public static final TFrame SAND[]  = surround(4, 7);
	public static final TFrame WATER   = new TFrame(7, 4, 1, 1);
	
}
