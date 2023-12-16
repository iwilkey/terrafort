package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.shape.TRect;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A utility class that facilitates the efficient rendering of an infinite set of tiles,
 * representing {@link TWorld} terrain that provides the seed for layered OpenSimplex noise.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTerrainRenderer {
	
	public static final int    TERRAIN_TILE_WIDTH               = 8;
	public static final int    TERRAIN_TILE_HEIGHT              = 8;
	public static final int    HALF_TERRAIN_TILE_WIDTH          = TERRAIN_TILE_WIDTH / 2;
	public static final int    HALF_TERRAIN_TILE_HEIGHT         = TERRAIN_TILE_HEIGHT / 2;
	public static final int    TERRAIN_LEVELS                   = 7;
	public static final int    TRANSITION_THICKNESS_FACTOR      = 8; // higher value = thinner transition borders.
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
	 * Render the terrain of a given world and player. This function is highly optimized and will only draw the tiles the player can see,
	 * with transitions to add depth.
	 * @param world the world the terrain belongs to.
	 * @param player the player.
	 */
	public static void render(final TWorld world, int tileX, int tileY) {
		final float camWidthWorldUnits  = TGraphics.CAMERA.viewportWidth * TGraphics.CAMERA.zoom;
	    final float camHeightWorldUnits = TGraphics.CAMERA.viewportHeight * TGraphics.CAMERA.zoom;
	    final int tilesInViewWidth      = Math.round(camWidthWorldUnits / TERRAIN_TILE_WIDTH) / 2;
	    final int tilesInViewHeight     = Math.round(camHeightWorldUnits / TERRAIN_TILE_HEIGHT) / 2;
	    final int playerTileX           = tileX;
	    final int playerTileY           = tileY;
	    final int xs                    = playerTileX - (tilesInViewWidth + TERRAIN_VIEWPORT_CULLING_PADDING);
	    final int xe                    = playerTileX + (tilesInViewWidth + TERRAIN_VIEWPORT_CULLING_PADDING);
	    final int ys                    = playerTileY - (tilesInViewHeight + TERRAIN_VIEWPORT_CULLING_PADDING);
	    final int ye                    = playerTileY + (tilesInViewHeight + TERRAIN_VIEWPORT_CULLING_PADDING);
	    for (int i = xs; i <= xe; i++) {
	        for (int j = ys; j <= ye; j++) {
                final int vq = world.getOrGenerateTileHeightAt(i, j);
	        	for(int d = 1; d < 9; d++) {
	        		final int dx  = TMath.DX[d];
	        		final int dy  = TMath.DY[d];
	        		final int xx  = i + dx;
	        		final int yy  = j - dy;
	                final int vvq = world.getOrGenerateTileHeightAt(xx, yy);
	                if(vvq != vq && vvq > vq) {
	                	float borderCX               = (i + 0.5f) * TERRAIN_TILE_WIDTH;
	                    float borderCY               = (j + 0.5f) * TERRAIN_TILE_HEIGHT;
	                    float borderWidth            = TERRAIN_TILE_WIDTH / TRANSITION_THICKNESS_FACTOR;
	                    float borderHeight           = TERRAIN_TILE_HEIGHT / TRANSITION_THICKNESS_FACTOR;
	                    final float halfBorderWidth  = borderWidth / 2;
	                    final float halfBorderHeight = borderHeight / 2;
	                    final TRect border           = new TRect(-1, -1, -1, -1);
	                    border.setColor(TRANSITION_COLORS[vq]);
	                    if(dx == -1 && dy == 1) {
	                        borderCX -= HALF_TERRAIN_TILE_WIDTH - halfBorderWidth;
	                        borderCY -= HALF_TERRAIN_TILE_HEIGHT - halfBorderHeight;
	                    } else if(dx == -1 && dy == 0) {
	                        borderCX -= HALF_TERRAIN_TILE_WIDTH - halfBorderWidth;
	                        borderHeight = TERRAIN_TILE_HEIGHT;
	                    } else if(dx == -1 && dy == -1) {
	                        borderCX -= HALF_TERRAIN_TILE_WIDTH - halfBorderWidth;
	                        borderCY += HALF_TERRAIN_TILE_HEIGHT - halfBorderHeight;
	                    } else if(dx == 0 && dy == 1) {
	                        borderCY -= HALF_TERRAIN_TILE_HEIGHT - halfBorderHeight;
	                        borderWidth = TERRAIN_TILE_WIDTH;
	                    } else if(dx == 0 && dy == -1) {
	                        borderCY += HALF_TERRAIN_TILE_HEIGHT - halfBorderHeight;
	                        borderWidth = TERRAIN_TILE_WIDTH;
	                    } else if(dx == 1 && dy == 1) {
	                        borderCX += HALF_TERRAIN_TILE_WIDTH - halfBorderWidth;
	                        borderCY -= HALF_TERRAIN_TILE_HEIGHT - halfBorderHeight;
	                    } else if(dx == 1 && dy == 0) {
	                        borderCX += HALF_TERRAIN_TILE_WIDTH - halfBorderWidth;
	                        borderHeight = TERRAIN_TILE_HEIGHT;
	                    } else if(dx == 1 && dy == -1) {
	                        borderCX += HALF_TERRAIN_TILE_WIDTH - halfBorderWidth;
	                        borderCY += HALF_TERRAIN_TILE_HEIGHT - halfBorderHeight;
	                    }
	                    border.setCX(borderCX - HALF_TERRAIN_TILE_WIDTH);
	                    border.setCY(borderCY - HALF_TERRAIN_TILE_HEIGHT);
	                    border.setWidth(borderWidth);
	                    border.setHeight(borderHeight);
	                    TGraphics.draw(border, false);
	                }
	        	}
	        	final int xx = i * TERRAIN_TILE_WIDTH;
	        	final int yy = j * TERRAIN_TILE_HEIGHT;
	        	TGraphics.draw(LEVELS[vq], xx, yy, xx, yy, vq, TERRAIN_TILE_WIDTH, TERRAIN_TILE_HEIGHT, Color.WHITE.cpy(), true);
	        }
	    }
	}
}
