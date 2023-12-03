package dev.iwilkey.terrafort.gfx;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.shape.TRect;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.math.TNoise;
import dev.iwilkey.terrafort.obj.TPlayer;
import dev.iwilkey.terrafort.obj.TWorld;

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
	public static final int    TERRAIN_HEIGHT                   = 4;
	public static final int    TRANSITION_THICKNESS_FACTOR      = 6; // higher value = thinner transition borders.
	public static final int    TERRAIN_VIEWPORT_CULLING_PADDING = 3;
	
	public static final byte   DX[]          					= new byte[9];
	public static final byte   DY[]          					= new byte[9];
	
	public static final TFrame STONE                            = new TFrame(3, 0, 1, 1);
	public static final TFrame GRASS                            = new TFrame(4, 0, 1, 1);
	public static final TFrame SAND                             = new TFrame(5, 0, 1, 1);
	public static final TFrame WATER                            = new TFrame(6, 0, 1, 1);
	
	public static final TFrame LEVELS[]                         = new TFrame[TERRAIN_HEIGHT];
	public static final Color  TRANSITION_COLORS[]              = new Color[TERRAIN_HEIGHT - 1];
	
	static {
		
		LEVELS[0]                                               = TTerrainRenderer.STONE;
		TRANSITION_COLORS[0]                                    = new Color().set(0x868689ff);
		LEVELS[1] 					                            = TTerrainRenderer.GRASS;
		TRANSITION_COLORS[1]                                    = new Color().set(0x3D823Dff);
		LEVELS[2]           									= TTerrainRenderer.SAND;
		TRANSITION_COLORS[2]                                    = new Color().set(0xA38F4Eff);
		LEVELS[3] 												= TTerrainRenderer.WATER;
		
		DX[0] 													= 0;
		DX[1] 													= -1;
		DX[2]													= -1;
		DX[3] 													= 0;
		DX[4] 													= 1;
		DX[5] 													= 1;
		DX[6] 													= 1;
		DX[7] 													= 0;
		DX[8] 													= -1;
		DY[0] 													= 0;
		DY[1] 													= 0;
		DY[2] 													= -1;
		DY[3] 													= -1;
		DY[4] 													= -1;
		DY[5] 													= 0;
		DY[6] 													= 1;
		DY[7] 													= 1;
		DY[8] 													= 1;
		
	}
	
	/**
	 * Hashes every possible pair of edited tile coordinates and maps them to their respective heights. 
	 * TODO: Make this unique for every {@link TWorld}!
	 */
	public static final HashMap<Long, Integer> TEST_TERRAFORM_CACHE = new HashMap<>();
	
	/**
	 * Registers a new terrain height at tile (x, y).
	 * @param x the tile x value.
	 * @param y the tile y value.
	 * @param z the height to make the terrain.
	 */
	public static final void terraform(int x, int y, int z) {
		z                      = (int)TMath.clamp(z, 0, TERRAIN_HEIGHT - 1);
		final long tileHashKey = (((long)x) << 32) | (y & 0xffffffffL);
		TEST_TERRAFORM_CACHE.put(tileHashKey, z);
	}
	
	/**
	 * Returns the 
	 * @param world
	 * @param x
	 * @param y
	 * @return
	 */
	public static final int getTerrainHeightAt(TWorld world, int x, int y) {
		final long tileHashKey = (((long)x) << 32) | (y & 0xffffffffL);
		if(TEST_TERRAFORM_CACHE.containsKey(tileHashKey))
			return TEST_TERRAFORM_CACHE.get(tileHashKey);
		else {
			double v = TNoise.get(world.getSeed(), x * 0.01f, y * 0.01f);
			v        = (v + 1) / 2;
			return TMath.quantize(v, TERRAIN_HEIGHT);
		}
	}
	
	/**
	 * Render the terrain of a given world and player. This function is highly optimized and will only draw the tiles the player can see,
	 * with transitions to add depth.
	 * @param world the world the terrain belongs to.
	 * @param player the player.
	 */
	public static final void render(final TWorld world, final TPlayer player) {
		if(player == null) return;
	    final float camWidthWorldUnits  = TGraphics.CAMERA.viewportWidth * TGraphics.CAMERA.zoom;
	    final float camHeightWorldUnits = TGraphics.CAMERA.viewportHeight * TGraphics.CAMERA.zoom;
	    final int tilesInViewWidth      = Math.round(camWidthWorldUnits / TERRAIN_TILE_WIDTH) / 2;
	    final int tilesInViewHeight     = Math.round(camHeightWorldUnits / TERRAIN_TILE_HEIGHT) / 2;
	    final int playerTileX           = Math.round(player.getRenderX() / TERRAIN_TILE_WIDTH);
	    final int playerTileY           = Math.round(player.getRenderY() / TERRAIN_TILE_HEIGHT);
	    final int xs                    = playerTileX - (tilesInViewWidth + TERRAIN_VIEWPORT_CULLING_PADDING);
	    final int xe                    = playerTileX + (tilesInViewWidth + TERRAIN_VIEWPORT_CULLING_PADDING);
	    final int ys                    = playerTileY - (tilesInViewHeight + TERRAIN_VIEWPORT_CULLING_PADDING);
	    final int ye                    = playerTileY + (tilesInViewHeight + TERRAIN_VIEWPORT_CULLING_PADDING);
	    for (int i = xs; i <= xe; i++) {
	        for (int j = ys; j <= ye; j++) {
                final int vq = getTerrainHeightAt(world, i, j);
	        	for(int d = 1; d < 9; d++) {
	        		final int dx  = DX[d];
	        		final int dy  = DY[d];
	        		final int xx  = i + dx;
	        		final int yy  = j - dy;
	                final int vvq = getTerrainHeightAt(world, xx, yy);
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
		        TGraphics.draw(LEVELS[vq], i * TERRAIN_TILE_WIDTH, j * TERRAIN_TILE_HEIGHT, vq, TERRAIN_TILE_WIDTH, TERRAIN_TILE_HEIGHT);
	        }
	    }
	}
}
