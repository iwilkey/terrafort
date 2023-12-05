package dev.iwilkey.terrafort.gfx;

import java.util.HashMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.gfx.shape.TRect;
import dev.iwilkey.terrafort.obj.TObject;
import dev.iwilkey.terrafort.obj.entity.lifeform.TPlayer;
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
	public static final int    TERRAIN_LEVELS                   = 4;
	public static final int    TRANSITION_THICKNESS_FACTOR      = 4; // higher value = thinner transition borders.
	public static final int    TERRAIN_VIEWPORT_CULLING_PADDING = 4;
	
	public static final byte   DX[]          					= new byte[9];
	public static final byte   DY[]          					= new byte[9];
	
	public static final TFrame STONE                            = new TFrame(3, 0, 1, 1);
	public static final TFrame GRASS                            = new TFrame(4, 0, 1, 1);
	public static final TFrame SAND                             = new TFrame(5, 0, 1, 1);
	public static final TFrame WATER                            = new TFrame(6, 0, 1, 1);
	
	public static final TFrame LEVELS[]                         = new TFrame[TERRAIN_LEVELS];
	public static final Color  TRANSITION_COLORS[]              = new Color[TERRAIN_LEVELS - 1];
	
	/**
	 * A dynamic way to keep track of tiles that require a physical presence, like Stone.
	 */
	private static final HashMap<Long, TObject> TILE_PHYSICALS  = new HashMap<>();
	
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
	 * Render the terrain of a given world and player. This function is highly optimized and will only draw the tiles the player can see,
	 * with transitions to add depth.
	 * @param world the world the terrain belongs to.
	 * @param player the player.
	 */
	public static void render(final TWorld world, final TPlayer player) {
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
	    // remove physicals that are outside current tile viewport.
	    Array<Long> deadHash = new Array<>();
	    for(final long hash : TILE_PHYSICALS.keySet()) {
	    	int x = (int)(hash >> 32);
	    	int y = (int)hash;
	    	if(((x < xs) || (x > xe)) && ((y < ys) || (y > ye)))
	    		deadHash.add(hash);
	    }
	    for(final long hash : deadHash) {
	    	world.removeObject(TILE_PHYSICALS.get(hash));
    		TILE_PHYSICALS.remove(hash);
	    }
	    for (int i = xs; i <= xe; i++) {
	        for (int j = ys; j <= ye; j++) {
                final int vq = world.getTileHeightAt(i, j);
	        	for(int d = 1; d < 9; d++) {
	        		final int dx  = DX[d];
	        		final int dy  = DY[d];
	        		final int xx  = i + dx;
	        		final int yy  = j - dy;
	                final int vvq = world.getTileHeightAt(xx, yy);
	                if(vvq != vq && vvq > vq) {
	                	// check if it's stone...
	                	if(vvq == 1) {
	                		// Since manageTilePhysicals handles everything else, all this should be concerned with doing is allocating physicals
	                		// to stone tiles that don't have one yet...
	                		long hash = (((long)i) << 32) | (j & 0xffffffffL);
	                		if(!TILE_PHYSICALS.containsKey(hash)) {
	                			// Add a new physical and hash it...
	                			final TObject tilePhysical = new TObject(world, 
	                													 false, 
	                													 i * TERRAIN_TILE_WIDTH,
	                													 j * TERRAIN_TILE_HEIGHT,
	                													 0,
	                													 TERRAIN_TILE_WIDTH,
	                													 TERRAIN_TILE_HEIGHT,
	                													 TERRAIN_TILE_WIDTH / 2,
	                													 TERRAIN_TILE_HEIGHT / 2,
	                													 3,
	                													 0,
	                													 1,
	                													 1,
	                													 new Color().set(0));
	                			world.addObject(tilePhysical);
	                			TILE_PHYSICALS.put(hash, tilePhysical);
	                		}
	                	}
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
	
	/**
	 * Manually removes a physical at given tile location. Will do nothing if there is no physical there.
	 * @param world the world the physical exists in.
	 * @param x the tile x coordinate of the physical.
	 * @param y the tile y coordinate of the physical.
	 */
	public static void removePhysicalAt(TWorld world, int x, int y) {
		long hash = (((long)x) << 32) | (y & 0xffffffffL);
		if(TILE_PHYSICALS.containsKey(hash)) {
			world.removeObject(TILE_PHYSICALS.get(hash));
			TILE_PHYSICALS.remove(hash);
		}
	}

	/**
	 * Cleans up dynamically allocated memory during runtime. Usually called when switching between worlds or states.
	 */
	public static void gc() {
		TILE_PHYSICALS.clear();
	}
}
