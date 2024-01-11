package dev.iwilkey.terrafort.world.terrain;

import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.math.TNoise;

/**
 * Utility class that returns terrain heights at given tile coordinates.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTerrainGenerator {
	
	public static int getHeightAt(long seed, long tileX, long tileY) {
		final float flb  = 0.00001f;
		final float fub  = 0.02f;
		final float freq = flb + layer(seed, tileX, tileY, 0.001f, 8) * (fub - flb);
	    return TMath.partition(layer(seed, tileX, tileY, freq, 16), TBiome.TERRAIN_LEVELS);
	}
	
	/**
	 * Requests an OpenSimplex noise value at given tile coordinates with given frequency and octaves.
	 * 
	 * <p>
	 * Returned noise value is continuous and in the closed interval [0, 1].
	 * </p>
	 */
	public static float layer(long seed, long tileX, long tileY, float freq, float octaves) {
		float total        = 0;
		float frequency    = freq;
		float amplitude    = 1;
		float maxAmplitude = 0;
	    for(int i = 0; i < octaves; i++) {
	        total += TNoise.get(seed, tileX * frequency, tileY * frequency) * amplitude;
	        maxAmplitude += amplitude;
	        amplitude /= 2;
	        frequency *= 2;
	    }
	    return (float)(((total / maxAmplitude) + 1.0) / 2.0);
	}

}
