package dev.iwilkey.terrafort.world.terrain;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.world.TChunk;

/**
 * The standard Terrafort biome. Includes mappings for water, sand, grass, and rock.
 * @author Ian Wilkey (iwilkey)
 */
public final class TDefaultBiome extends TBiome {
	
	public static final TFrame LEVEL_0_WATER = new TFrame(0, 0, 1, 1);
	public static final TFrame LEVEL_1_SAND  = new TFrame(1, 0, 1, 1);
	public static final TFrame LEVEL_2_GRASS = new TFrame(2, 0, 1, 1);
	public static final TFrame LEVEL_3_ROCK  = new TFrame(3, 0, 1, 1);
	
	/**
	 * The {@link TChunk} that is given implies the chunk is of the biome {@link TDefaultBiome}.
	 */
	public TDefaultBiome(TChunk parent) {
		super(parent, new TFrame[] {
					LEVEL_0_WATER, 
					LEVEL_1_SAND, 
					LEVEL_2_GRASS, 
					LEVEL_3_ROCK 
				}, new Color[] {
				   new Color().set(0x998649ff),
				   new Color().set(0x3D823Dff),
				   new Color().set(0x605650ff),
				   new Color().set(0x7A706Bff)
		});
	}

}
