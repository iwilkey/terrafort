package dev.iwilkey.terrafort.object;

import com.badlogic.gdx.graphics.g3d.ModelInstance;

import dev.iwilkey.terrafort.gfx.RenderableProvider3;
import dev.iwilkey.terrafort.state.State;

public abstract class GameObject3 extends GameObject implements RenderableProvider3 {

	private ModelInstance renderable;
	
	public GameObject3(State state, String pathToLoadedModel) {
		super(state);	
	}

	@Override
	public ModelInstance getModelInstance() {
		return renderable;
	}

}
