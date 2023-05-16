package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.collision.BoundingBox;

public interface RenderableProvider3 {
	public BoundingBox getBoundingBox();
	public ModelInstance getModelInstance();
}
