package dev.iwilkey.terrafort.obj.world;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.shape.TRect;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.math.TNoise;
import dev.iwilkey.terrafort.obj.entity.element.TBoulder;
import dev.iwilkey.terrafort.obj.entity.element.TBush;
import dev.iwilkey.terrafort.obj.entity.element.TFlower;
import dev.iwilkey.terrafort.obj.entity.element.TNaturalElement;
import dev.iwilkey.terrafort.obj.entity.element.TShell;
import dev.iwilkey.terrafort.obj.entity.element.TTree;

/**
 * A utility class that facilitates the efficient rendering of an infinite set of tiles,
 * representing {@link TWorld} terrain that provides the seed for layered OpenSimplex noise.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTerrain {
	
	public static final int    TILE_WIDTH                       = 8;
	public static final int    TILE_HEIGHT                      = 8;
	public static final int    HALF_TILE_WIDTH                  = TILE_WIDTH / 2;
	public static final int    HALF_TILE_HEIGHT                 = TILE_HEIGHT / 2;
	public static final int    TERRAIN_LEVELS                   = 7;
	public static final int    TRANSITION_THICKNESS_FACTOR      = 6; // higher value = thinner transition borders.
	public static final int    TERRAIN_VIEWPORT_CULLING_PADDING = 4;

	public static final int    STONE_HIGH_TILE                  = 0;
	public static final int    STONE_MEDIUM_TILE                = 1;
	public static final int    STONE_LOW_TILE                   = 2;
	public static final int    ASPHALT_TILE                     = 3;
	public static final int    GRASS_TILE                       = 4;
	public static final int    SAND_TILE                        = 5;
	public static final int    WATER_TILE                       = 6;
	
	public static final TFrame STONE_HIGH                       = new TFrame(6, 0, 1, 1);
	public static final TFrame STONE_MEDIUM                     = new TFrame(5, 0, 1, 1);
	public static final TFrame STONE_LOW                        = new TFrame(4, 0, 1, 1);
	public static final TFrame ASPHALT                          = new TFrame(3, 0, 1, 1);
	public static final TFrame GRASS                            = new TFrame(4, 1, 1, 1);
	public static final TFrame SAND                             = new TFrame(5, 1, 1, 1);
	public static final TFrame WATER                            = new TFrame(6, 1, 1, 1);
	
	public static final TFrame LEVELS[]                         = new TFrame[TERRAIN_LEVELS];
	public static final Color  TRANSITION_COLORS[]              = new Color[TERRAIN_LEVELS - 1];
	
	static {
		LEVELS[STONE_HIGH_TILE]                                 = STONE_HIGH;
		LEVELS[STONE_MEDIUM_TILE]                               = STONE_MEDIUM;
		LEVELS[STONE_LOW_TILE]                                  = STONE_LOW;
		LEVELS[ASPHALT_TILE]                                    = ASPHALT;
		LEVELS[GRASS_TILE]                                      = GRASS;
		LEVELS[SAND_TILE]                                       = SAND;
		LEVELS[WATER_TILE]                                      = WATER;
		TRANSITION_COLORS[0]                                    = new Color().set(0xB5ABA6ff);
		TRANSITION_COLORS[1]                                    = new Color().set(0x968C87ff);
		TRANSITION_COLORS[2]                                    = new Color().set(0x7A706Bff);
		TRANSITION_COLORS[3]                                    = new Color().set(0x605650ff);
		TRANSITION_COLORS[4]                                    = new Color().set(0x3D823Dff);
		TRANSITION_COLORS[5]                                    = new Color().set(0x998649ff);
	}
	
	/**
	 * Render the terrain of a given world and center tile location.
	 */
	public static void render(final TWorld world, int tileX, int tileY) {
		final float camWidthWorldUnits  = TGraphics.CAMERA.viewportWidth * TGraphics.CAMERA.zoom;
	    final float camHeightWorldUnits = TGraphics.CAMERA.viewportHeight * TGraphics.CAMERA.zoom;
	    final int tilesInViewWidth      = Math.round(camWidthWorldUnits / TILE_WIDTH) / 2;
	    final int tilesInViewHeight     = Math.round(camHeightWorldUnits / TILE_HEIGHT) / 2;
	    final int xs                    = tileX - (tilesInViewWidth + TERRAIN_VIEWPORT_CULLING_PADDING);
	    final int xe                    = tileX + (tilesInViewWidth + TERRAIN_VIEWPORT_CULLING_PADDING);
	    final int ys                    = tileY - (tilesInViewHeight + TERRAIN_VIEWPORT_CULLING_PADDING);
	    final int ye                    = tileY + (tilesInViewHeight + TERRAIN_VIEWPORT_CULLING_PADDING);
	    for (int i = xs; i <= xe; i++) {
	        for (int j = ys; j <= ye; j++) {
                final int vq = world.getOrGenerateTileHeightAt(i, j);
	        	for(int d = 0; d < 8; d++) {
	        		final int dx  = TMath.DX[d];
	        		final int dy  = TMath.DY[d];
	        		final int xx  = i + dx;
	        		final int yy  = j - dy;
	                final int vvq = world.getOrGenerateTileHeightAt(xx, yy);
	                if(vvq != vq && vvq > vq) {
	                	float borderCX               = (i + 0.5f) * TILE_WIDTH;
	                    float borderCY               = (j + 0.5f) * TILE_HEIGHT;
	                    float borderWidth            = TILE_WIDTH / TRANSITION_THICKNESS_FACTOR;
	                    float borderHeight           = TILE_HEIGHT / TRANSITION_THICKNESS_FACTOR;
	                    final float halfBorderWidth  = borderWidth / 2;
	                    final float halfBorderHeight = borderHeight / 2;
	                    final TRect border           = new TRect(-1, -1, -1, -1);
	                    border.setColor(TRANSITION_COLORS[vq]);
	                    if(dx == -1 && dy == 1) {
	                        borderCX -= HALF_TILE_WIDTH - halfBorderWidth;
	                        borderCY -= HALF_TILE_HEIGHT - halfBorderHeight;
	                    } else if(dx == -1 && dy == 0) {
	                        borderCX -= HALF_TILE_WIDTH - halfBorderWidth;
	                        borderHeight = TILE_HEIGHT;
	                    } else if(dx == -1 && dy == -1) {
	                        borderCX -= HALF_TILE_WIDTH - halfBorderWidth;
	                        borderCY += HALF_TILE_HEIGHT - halfBorderHeight;
	                    } else if(dx == 0 && dy == 1) {
	                        borderCY -= HALF_TILE_HEIGHT - halfBorderHeight;
	                        borderWidth = TILE_WIDTH;
	                    } else if(dx == 0 && dy == -1) {
	                        borderCY += HALF_TILE_HEIGHT - halfBorderHeight;
	                        borderWidth = TILE_WIDTH;
	                    } else if(dx == 1 && dy == 1) {
	                        borderCX += HALF_TILE_WIDTH - halfBorderWidth;
	                        borderCY -= HALF_TILE_HEIGHT - halfBorderHeight;
	                    } else if(dx == 1 && dy == 0) {
	                        borderCX += HALF_TILE_WIDTH - halfBorderWidth;
	                        borderHeight = TILE_HEIGHT;
	                    } else if(dx == 1 && dy == -1) {
	                        borderCX += HALF_TILE_WIDTH - halfBorderWidth;
	                        borderCY += HALF_TILE_HEIGHT - halfBorderHeight;
	                    }
	                    border.setCX(borderCX - HALF_TILE_WIDTH);
	                    border.setCY(borderCY - HALF_TILE_HEIGHT);
	                    border.setWidth(borderWidth);
	                    border.setHeight(borderHeight);
	                    TGraphics.draw(border, false);
	                }
	        	}
	        	final int xx = i * TILE_WIDTH;
	        	final int yy = j * TILE_HEIGHT;
	        	TGraphics.draw(LEVELS[vq], xx, yy, xx, yy, vq, TILE_WIDTH, TILE_HEIGHT, Color.WHITE.cpy(), true);
	        }
	    }
	}
	
	/**
	 * Facilitates the infinite generation of terrain returning a properly terrain-level mapped integer value from 
	 * several layers of OpenSimplex noise.
	 */
	public static int requestTerrainHeight(long seed, int tileX, int tileY) {
		final float flb  = 0.000001f;
		final float fub  = 0.002f;
		final float freq = flb + layer(seed, tileX, tileY, 0.001f, 8) * (fub - flb);
	    return TERRAIN_LEVELS - TMath.partition(layer(seed, tileX, tileY, freq, 16), TERRAIN_LEVELS, 0.30f, 0.10f, 0.20f) - 1;
	}
	
	/**
	 * Facilitates the infinite generation of terrain decoration: {@link TNaturalElement}s.
	 */
	public static TNaturalElement requestNaturalElement(TWorld world, int tileX, int tileY, int tileZ) {
		final float skip = layer(world.getSeed(), tileX, tileY, 0.01f, 16);
		// there may still be a chance, even so, that no element is placed where one should be...
		TNaturalElement element = null;
		switch(tileZ) {
			case WATER_TILE:
				break;
			case SAND_TILE:
				if(Math.random() > 0.30f) {
					for(int i = 0; i < 8; i += 2) {
						int xx = tileX + TMath.DX[i];
						int yy = tileY + TMath.DY[i];
						if(world.checkTileHeightAt(xx, yy) == WATER_TILE)
							return new TBush(world, tileX, tileY);
					}
				}
				if(Math.random() > skip)
					return null;
				if(Math.random() < 0.10)
					return new TShell(world, tileX, tileY);
				
				break;
			case GRASS_TILE:
				if(Math.random() > skip)
					return null;
				final int seg = TMath.partition(layer(world.getSeed(), tileX, tileY, 0.01f, 8), 5, 0.50f);
				switch(seg) {
					case 0:
						return new TTree(world, tileX, tileY);
					case 2:
						return new TBush(world, tileX, tileY);
					case 4:
						return new TFlower(world, tileX, tileY);
				}
				break;
			case ASPHALT_TILE:
			case STONE_LOW_TILE:
			case STONE_MEDIUM_TILE:
			case STONE_HIGH_TILE:
				if(Math.random() > skip)
					return null;
				if(layer(world.getSeed(), tileX, tileY, 0.01f, 8) > 0.25f)
					return new TBoulder(world, tileX, tileY);
			default:;
		}
		return element;
	}
	
	/**
	 * Requests an OpenSimplex noise value at given tile coordinates with given frequency and octaves.
	 * 
	 * <p>
	 * Returned noise value is continuous and in the closed interval [0, 1].
	 * </p>
	 */
	private static float layer(long seed, int tileX, int tileY, float freq, float octaves) {
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
