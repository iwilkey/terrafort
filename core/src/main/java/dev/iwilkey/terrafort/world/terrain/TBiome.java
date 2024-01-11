package dev.iwilkey.terrafort.world.terrain;

import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.gfx.TFrame;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.TRect;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.world.TChunk;
import dev.iwilkey.terrafort.world.TWorld;

/**
 * A collection of {@link TFrame} that are mapped to a specific height that instruct the engine how to render a terrain tile.
 * @author Ian Wilkey (iwilkey)
 */
public abstract class TBiome {
	
	public static final int  MIN_HEIGHT     = 0;
	public static final int  MAX_HEIGHT     = 3;
	public static final int  WATER_LEVEL    = 0;
	public static final int  SAND_LEVEL     = 1;
	public static final int  GRASS_LEVEL    = 2;
	public static final int  ROCK_LEVEL     = 3;
	public static final int  TERRAIN_LEVELS = (MAX_HEIGHT - MIN_HEIGHT) + 1;
	
	protected final TFrame[] levels;
	protected final Color[]  transitions;
	protected final TChunk   parent;
	
	public TBiome(TChunk parent, TFrame[] levels, Color[] transitions) {
		if(levels.length != TERRAIN_LEVELS || transitions.length != TERRAIN_LEVELS)
			throw new IllegalArgumentException("Must provide a TFrame and Color array the same length as the TERRAIN_LEVELS constant! Should be: " + TERRAIN_LEVELS + ", not length " + levels.length + ".");
		this.parent      = parent;
		this.levels      = levels;
		this.transitions = transitions;
	}
	
	public void render(float dt, long tileX, long tileY) {
    	final int vq = parent.getOrGenerateTile(tileX, tileY);
        for (int d = 0; d < 8; d++) {
            final int dx  = TMath.DX[d];
            final int dy  = TMath.DY[d];
            final long xx = tileX - dx;
            final long yy = tileY + dy;
            final int vvq = parent.getParent().getOrGenerateChunkThatContains(xx, yy).getOrGenerateTile(xx, yy);
            if (vvq != vq && vvq > vq) {
            	float       borderCX         = (xx + 0.5f) * TWorld.TILE_SIZE;
                float       borderCY         = (yy + 0.5f) * TWorld.TILE_SIZE;
                float       borderWidth      = TWorld.TILE_SIZE / 6;
                float       borderHeight     = TWorld.TILE_SIZE / 6;
                final float halfBorderWidth  = borderWidth / 2;
                final float halfBorderHeight = borderHeight / 2;
                final TRect border           = new TRect(-1, -1, -1, -1);
                border.setColor(transitions[vq]);
                if(dx == -1 && dy == 1) {
                    borderCX -= TWorld.HALF_TILE_SIZE - halfBorderWidth;
                    borderCY -= TWorld.HALF_TILE_SIZE - halfBorderHeight;
                } else if(dx == -1 && dy == 0) {
                    borderCX -= TWorld.HALF_TILE_SIZE - halfBorderWidth;
                    borderHeight = TWorld.TILE_SIZE;
                } else if(dx == -1 && dy == -1) {
                    borderCX -= TWorld.HALF_TILE_SIZE - halfBorderWidth;
                    borderCY += TWorld.HALF_TILE_SIZE - halfBorderHeight;
                } else if(dx == 0 && dy == 1) {
                    borderCY -= TWorld.HALF_TILE_SIZE - halfBorderHeight;
                    borderWidth = TWorld.TILE_SIZE;
                } else if(dx == 0 && dy == -1) {
                    borderCY += TWorld.HALF_TILE_SIZE - halfBorderHeight;
                    borderWidth = TWorld.TILE_SIZE;
                } else if(dx == 1 && dy == 1) {
                    borderCX += TWorld.HALF_TILE_SIZE - halfBorderWidth;
                    borderCY -= TWorld.HALF_TILE_SIZE - halfBorderHeight;
                } else if(dx == 1 && dy == 0) {
                    borderCX += TWorld.HALF_TILE_SIZE - halfBorderWidth;
                    borderHeight = TWorld.TILE_SIZE;
                } else if(dx == 1 && dy == -1) {
                    borderCX += TWorld.HALF_TILE_SIZE - halfBorderWidth;
                    borderCY += TWorld.HALF_TILE_SIZE - halfBorderHeight;
                }
                border.setCX(borderCX - TWorld.HALF_TILE_SIZE);
                border.setCY(borderCY - TWorld.HALF_TILE_SIZE);
                border.setWidth(borderWidth);
                border.setHeight(borderHeight);
                border.setDepth(254);
                TGraphics.draw(border);
            }
        }
    	TGraphics.draw("sheets/natural.png", levels[vq], tileX * TWorld.TILE_SIZE, tileY * TWorld.TILE_SIZE, 255, TWorld.TILE_SIZE, TWorld.TILE_SIZE, Color.WHITE);
	}
}
