package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.g3d.decals.Decal;

/**
 * The `RenderableProvider25` interface represents a renderable provider for 2.5D rendering.
 * It defines methods to retrieve a Decal object and determine if billboard rendering should be applied.
 */
public interface RenderableProvider25 {
	
	/**
	 * Retrieves the Decal object associated with the renderable provider.
	 * @return The Decal object.
	 */
	public Decal getDecal();
	
	/**
	 * Determines whether billboard rendering should be applied to the renderable.
	 * @return `true` if billboard rendering should be applied, `false` otherwise.
	 */
	public boolean shouldBillboard();
	
}
