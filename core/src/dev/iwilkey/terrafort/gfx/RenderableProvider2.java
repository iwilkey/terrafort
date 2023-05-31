package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * The `RenderableProvider2` interface represents a renderable provider for 2D rendering.
 * It defines methods to retrieve information about the renderable object such as texture region,
 * position, size, and tint.
 */
public interface RenderableProvider2 {
	
	/**
	 * Retrieves the texture region bound to the renderable object.
	 * @return The texture region.
	 */
	public TextureRegion getBindedRaster();
	
	/**
	 * Retrieves the x-coordinate of the renderable object's position.
	 * @return The x-coordinate.
	 */
	public int getX();
	
	/**
	 * Retrieves the y-coordinate of the renderable object's position.
	 * @return The y-coordinate.
	 */
	public int getY();
	
	/**
	 * Retrieves the width of the renderable object.
	 * @return The width.
	 */
	public int getWidth();
	
	/**
	 * Retrieves the height of the renderable object.
	 * @return The height.
	 */
	public int getHeight();
	
	/**
	 * Retrieves the tint color applied to the renderable object.
	 * @return The tint color.
	 */
	public Color getTint();
	
}
