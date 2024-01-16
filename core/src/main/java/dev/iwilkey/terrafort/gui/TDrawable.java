package dev.iwilkey.terrafort.gui;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

import dev.iwilkey.terrafort.gfx.TGraphics;

/**
 * Utility class for creating {@link Drawable}s from various data structures.
 * @author Ian Wilkey (iwilkey)
 */
public final class TDrawable {
	
	/**
	 * Returns a {@link Drawable} from specified sheet location. Sprite sheet must be registered with the {@link TGraphics} module.
	 */
	public static final Drawable fromSpriteSheet(String spriteSheet, int dataX, int dataY, int dataWidth, int dataHeight) {
		final Texture tex = TGraphics.getSheetGLTex(spriteSheet);
		final TextureRegion reg = new TextureRegion(tex, 
													dataX * TGraphics.DATA_WIDTH, 
													dataY * TGraphics.DATA_HEIGHT, 
													dataWidth * TGraphics.DATA_WIDTH, 
													dataHeight * TGraphics.DATA_HEIGHT);
		return new TextureRegionDrawable(reg);
	}
	
	/**
	 * Returns a {@link TDrawable} from a specified color (RGBA int format.)
	 */
	public static final Drawable solidColor(int color) {
	    final Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
	    pixmap.setColor(color);
	    pixmap.fill();
	    final Texture texture = new Texture(pixmap);
	    pixmap.dispose();
	    return new TextureRegionDrawable(new TextureRegion(texture));
	}
	
}
