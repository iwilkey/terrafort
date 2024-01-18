package dev.iwilkey.terrafort.gui.text;

import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

import dev.iwilkey.terrafort.gui.TUserInterface;

/**
 * Standalone text is rendered to the screen with specified data, screen or world position, font point, color, and wrapping.
 * It is the fastest way to convey meaning to the client as it doesn't require managing an external UI container.
 * @author Ian Wilkey (iwilkey)
 */
public interface TImmediateModeText {
	
	/**
	 * The glyphs the text should render.
	 */
	public String getData();
	
	/**
	 * Returns true if the coordinates specified are in screen or world-space. World-space coordinates will automatically be projected to screen-space. This obviously means
	 * it may not be rendered if it falls outside the world-camera viewport.
	 */
	public boolean worldSpace();
	
	/**
	 * Returns true if the text should be rendered with a Terrafort-style drop-shadow.
	 */
	public boolean dropShadow();
	
	/**
	 * The x coordinate of the immediate text.
	 */
	public int getX();
	
	/**
	 * The y coordinate of the immediate text.
	 */
	public int getY();
	
	/**
	 * Returns the dimensions, in pixels, of this text.
	 */
	default Vector2 getDimensions() {
		// Use the layout already defined in the user interface module...
		final GlyphLayout layout = TUserInterface.getGlyphLayout();
		// Make sure to set the size of the immediate mode text...
		TUserInterface.getGameFont().getData().setScale((float)getPoint() / TUserInterface.BASE_FONT_SIZE);
		layout.setText(TUserInterface.getGameFont(), getData());
		return new Vector2(layout.width, layout.height);
	}

	/**
	 * The color the text should be rendered in. RGBA int format.
	 */
	public int getColor();
	
	/**
	 * Returns the font point (size) of the text.
	 */
	public int getPoint();
	
	/**
	 * Returns the target wrapping width. If 0 or less, no wrapping is applied.
	 */
	public int getWrapping();
	
	/**
	 * The target alignment for the grouped text.
	 */
	default int getAlignment() {
		return Align.center;
	}
	
}
