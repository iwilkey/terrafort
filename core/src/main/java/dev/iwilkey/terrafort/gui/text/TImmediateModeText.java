package dev.iwilkey.terrafort.gui.text;

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
	
}
