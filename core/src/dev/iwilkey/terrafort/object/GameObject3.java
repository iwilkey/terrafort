package dev.iwilkey.terrafort.object;

import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import dev.iwilkey.terrafort.gfx.RenderableProvider3;
import dev.iwilkey.terrafort.state.State;

public abstract class GameObject3 extends GameObject implements RenderableProvider3 {
	
	private final BoundingBox boundingBox;
	private final  ModelInstance renderable;
	private Vector3 position;
	private Vector3 scale;
	
	public GameObject3(State state, String pathToLoadedModel) {
		super(state);
		Model model = state.getAssetManager().get(pathToLoadedModel, Model.class);
		renderable = new ModelInstance(model);
		boundingBox = new BoundingBox();
		renderable.calculateBoundingBox(boundingBox);
		position = new Vector3();
		scale = new Vector3();
	}
	
	public void setPosition(Vector3 pos) {
		setPosition(pos.x, pos.y, pos.z);
	}
	
	public void setPosition(float x, float y, float z) {
		position.set(x, y, z);
		renderable.transform.setToTranslation(position);
	}
	
	public void setRotation(Vector3 axis, float deg) {
		renderable.transform.setToRotation(axis, deg);
	}
	
	public void setScale(Vector3 scale) {
		setScale(scale.x, scale.y, scale.z);
	}
	
	public void setScale(float x, float y, float z) {
		scale.set(x, y, z);
		renderable.transform.setToScaling(scale);
		renderable.calculateBoundingBox(boundingBox);
	}
	
	public Vector3 getPosition() {
		return position.cpy();
	}
	
	public Vector3 getScale() {
		return scale.cpy();
	}

	@Override
	public ModelInstance getModelInstance() {
		return renderable;
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		return boundingBox;
	}

}
