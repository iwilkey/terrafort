package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

/**
 * The `RenderableProvider3` interface represents a renderable provider for 3D rendering.
 * It defines methods to retrieve the bounding box, dimensions, and model instance of the renderable.
 */
public interface RenderableProvider3 {
	
	/**
	 * Retrieves the bounding box of the renderable.
	 * @return The bounding box.
	 */
	public BoundingBox getBoundingBox();
	
	/**
	 * Retrieves the dimensions of the renderable.
	 * @return The dimensions as a Vector3.
	 */
	public Vector3 getDimensions();
	
	/**
	 * Retrieves the model instance associated with the renderable.
	 * @return The ModelInstance object.
	 */
	public ModelInstance getModelInstance();
	
}
