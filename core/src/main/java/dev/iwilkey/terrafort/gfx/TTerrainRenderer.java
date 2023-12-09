package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.shape.TRect;
import dev.iwilkey.terrafort.obj.entity.lifeform.TPlayer;
import dev.iwilkey.terrafort.obj.world.TSinglePlayerWorld;

/**
 * A utility class that facilitates the efficient rendering of an infinite set of tiles,
 * representing {@link TSinglePlayerWorld} terrain that provides the seed for layered OpenSimplex noise.
 * @author Ian Wilkey (iwilkey)
 */
public final class TTerrainRenderer {
	
	public static final int    TERRAIN_TILE_WIDTH               = 8;
	public static final int    TERRAIN_TILE_HEIGHT              = 8;
	public static final int    HALF_TERRAIN_TILE_WIDTH          = TERRAIN_TILE_WIDTH / 2;
	public static final int    HALF_TERRAIN_TILE_HEIGHT         = TERRAIN_TILE_HEIGHT / 2;
	public static final int    TERRAIN_LEVELS                   = 5;
	public static final int    TRANSITION_THICKNESS_FACTOR      = 4; // higher value = thinner transition borders.
	public static final int    TERRAIN_VIEWPORT_CULLING_PADDING = 4;
	
	public static final int    STONE_TILE                       = 0;
	public static final int    ASPHALT_TILE                     = 1;
	public static final int    GRASS_TILE                       = 2;
	public static final int    SAND_TILE                        = 3;
	public static final int    WATER_TILE                       = 4;
	
	public static final TFrame STONE                            = new TFrame(3, 0, 1, 1);
	public static final TFrame ASPHALT                          = new TFrame(4, 1, 1, 1);
	public static final TFrame GRASS                            = new TFrame(4, 0, 1, 1);
	public static final TFrame SAND                             = new TFrame(5, 0, 1, 1);
	public static final TFrame WATER                            = new TFrame(6, 0, 1, 1);
	
	public static final byte   DX[]          					= new byte[9];
	public static final byte   DY[]          					= new byte[9];
	
	public static final TFrame LEVELS[]                         = new TFrame[TERRAIN_LEVELS];
	
	public static final Color  TRANSITION_COLORS[]              = new Color[TERRAIN_LEVELS - 1];
	
	static {
		LEVELS[0]                                               = TTerrainRenderer.STONE;
		TRANSITION_COLORS[0]                                    = new Color().set(0x868689FF);
		LEVELS[1]                                               = TTerrainRenderer.ASPHALT;
		TRANSITION_COLORS[1]                                    = new Color().set(0x3E3E3FFF);
		LEVELS[2] 					                            = TTerrainRenderer.GRASS;
		TRANSITION_COLORS[2]                                    = new Color().set(0x3D823DFF);
		LEVELS[3]           									= TTerrainRenderer.SAND;
		TRANSITION_COLORS[3]                                    = new Color().set(0xA38F4EFF);
		LEVELS[4] 												= TTerrainRenderer.WATER;
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
	public static void render(final TSinglePlayerWorld world, final TPlayer player) {
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
                final int vq = world.getTileHeightAt(i, j);
	        	for(int d = 1; d < 9; d++) {
	        		final int dx  = DX[d];
	        		final int dy  = DY[d];
	        		final int xx  = i + dx;
	        		final int yy  = j - dy;
	                final int vvq = world.getTileHeightAt(xx, yy);
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
	        	TGraphics.draw(LEVELS[vq], i * TERRAIN_TILE_WIDTH, j * TERRAIN_TILE_HEIGHT, vq, TERRAIN_TILE_WIDTH, TERRAIN_TILE_HEIGHT, Color.WHITE.cpy(), true);
	        }
	    }
	}
}
