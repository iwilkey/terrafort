package dev.iwilkey.terrafort.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * A static collection of various {@link Drawable}s that can be used for the construction of any UI interface.
 * @author Ian Wilkey (iwilkey)
 */
public final class TDrawable {

	/**
	 * Utility method to translate a given {@link Pixmap} into a {@link Drawable}.
	 */
	public static Drawable pixmapToDrawable(Pixmap pixels) {
		final Texture texture = new Texture(pixels);
		final Drawable ret = new TextureRegionDrawable(new TextureRegion(texture));
		return ret;
	}
	
	/**
	 * Utility method that translates a color (given in 0xrrggbbaa format) to a {@link Drawable}. 
	 */
	public static Drawable solid(int color, int width, int height) {
		final Pixmap pixel = new Pixmap(width, height, Pixmap.Format.RGBA8888);
		pixel.setColor(new Color().set(color));
		pixel.fill();
		final Drawable ret = pixmapToDrawable(pixel);
		pixel.dispose();
		return ret;
	}
	
	public static Drawable solidWithShadow(int color, int shadowColor, int width, int height, int offsetX, int offsetY) {
	    Pixmap pixmap = new Pixmap(width + offsetX, height + offsetY, Pixmap.Format.RGBA8888);
	    pixmap.setColor(new Color().set(shadowColor));
	    pixmap.fillRectangle(offsetX, offsetY, width, height);
	    pixmap.setColor(new Color().set(color));
	    pixmap.fillRectangle(0, 0, width, height);
	    final Drawable ret = pixmapToDrawable(pixmap);
	    pixmap.dispose();
	    return ret;
	}
	
}
