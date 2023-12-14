package dev.iwilkey.terrafort.ui.widgets;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.obj.world.TWorld;
import dev.iwilkey.terrafort.ui.TUserInterface;

/**
 * A simple Minimap UI widget. Shows an abstract notion of the given {@link TWorld}s terrain height.
 * @author Ian Wilkey (iwilkey)
 */
public final class TMinimap extends VisTable {
	
	public static final Color LOW_COLOR   = new Color().set(0x555555ff);
	public static final Color HIGH_COLOR  = new Color().set(0xffffffff);
	public static final Color UNDEF_COLOR = new Color().set(0x000000ff);
	public static final Color CROSS_COLOR = new Color().set(0xf2ce30ff);
	public static final byte  WIDTH_PX    = 64;
	public static final byte  HEIGHT_PX   = 64;
	public static final byte  HALF_WIDTH  = WIDTH_PX / 2;
	public static final byte  HALF_HEIGHT = HEIGHT_PX / 2;
	public static final byte  CROSS_SIZE  = 1;
	
	private TWorld   world;
	private Pixmap   pixels;
	private Texture  texture;
	private VisImage map;
	
	/**
	 * Creates a frash minimap, with given world.
	 */
	public TMinimap(TWorld world) {
		this.world = world;
		pixels = new Pixmap(WIDTH_PX, HEIGHT_PX, Pixmap.Format.RGBA8888);
        texture = new Texture(pixels);
        map = new VisImage(texture);
        add(map).expand().fill();
        addListener(new InputListener() {
        	@Override
		    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				TUserInterface.beginPopup("[TEAL]Minimap[]", "This tool represents your player's \ncurrent [TEAL]location[].\n\n"
										+ "As you explore the world, your\n[TEAL]minimap[] records it topographically.\n\n"
										+ "  - WHITE: Highest point\n"
										+ "  - [GRAY]DARK GRAY[]: Lowest point\n\n"
										+ "Use the '+' and '-' buttons to zoom.");
		    }
		    @Override
		    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
		    	TUserInterface.endPopup();
		    }
        });
	}
	
	/**
	 * Updates the minimap with given world location and tiles per pixel.
	 * 
	 * <p>
	 * Tiles per pixel indicates how many tiles are encapsulated by a pixel. For example, with
	 * a value of 4, then each pixel represents the midpoint height of a 4 by 4 set of tiles.
	 * </p>
	 */
	public void update(int playerWorldX, int playerWorldY, int tilesPerPixel) {
	    final int playerTileX = playerWorldX / TTerrainRenderer.TERRAIN_TILE_WIDTH;
	    final int playerTileY = playerWorldY / TTerrainRenderer.TERRAIN_TILE_HEIGHT;
	    final int startX      = playerTileX - (WIDTH_PX * tilesPerPixel / 2) + tilesPerPixel / 2;
	    final int startY      = playerTileY - (HEIGHT_PX * tilesPerPixel / 2) + tilesPerPixel / 2;
	    for (int x = 0; x < WIDTH_PX; x++) {
	        for (int y = 0; y < HEIGHT_PX; y++) {
	            int tileX         = startX + x * tilesPerPixel;
	            int tileY         = startY + y * tilesPerPixel;
	            int tileHeight    = world.checkTileHeightAt(tileX, tileY);
	            final Color col;
	            if(tileHeight == -1) col = UNDEF_COLOR;
	            else col = getGradientColor(tileHeight, TTerrainRenderer.TERRAIN_LEVELS - 1);
	            pixels.drawPixel(x, HEIGHT_PX - y - 1, Color.rgba8888(col));
	        }
	    }
	    for(int i = -CROSS_SIZE; i <= CROSS_SIZE; i++) {
	        if(HALF_WIDTH + i >= 0 && HALF_WIDTH + i < WIDTH_PX)
	            pixels.drawPixel(HALF_WIDTH + i, HALF_HEIGHT, Color.rgba8888(CROSS_COLOR));
	        if(HALF_HEIGHT + i >= 0 && HALF_HEIGHT + i < HEIGHT_PX)
	            pixels.drawPixel(HALF_WIDTH, HALF_HEIGHT + i, Color.rgba8888(CROSS_COLOR));
	    }
	    texture.draw(pixels, 0, 0);
	    map.setDrawable(new TextureRegionDrawable(new TextureRegion(texture)));
	}
	
	private Color getGradientColor(int height, int maxTerrainLevels) {
        float ratio = (float) height / (maxTerrainLevels - 1);
        return HIGH_COLOR.cpy().lerp(LOW_COLOR, ratio);
    }

}
