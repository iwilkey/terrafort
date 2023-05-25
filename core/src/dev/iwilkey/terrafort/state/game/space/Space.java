package dev.iwilkey.terrafort.state.game.space;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;

import dev.iwilkey.terrafort.gfx.Renderer;
import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.bullet.BulletPhysicsTag;
import dev.iwilkey.terrafort.state.State;
import dev.iwilkey.terrafort.state.game.SinglePlayerGameState;
import dev.iwilkey.terrafort.state.game.object.creatable.Creatables;

public final class Space extends Environment {
	
	private final State state;
	private final SinglePlayerGameState game;
	private final ModelBatch shadowBatch;
	private final DirectionalShadowLight shadowLight;
	private long spaceSegGrid;
	
	public Space(State state, SinglePlayerGameState game) {
		this.state = state;
		this.game = game;
		shadowBatch = new ModelBatch(new DepthShaderProvider());
		set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.7f, .6f, 1f));
	    add((shadowLight = new DirectionalShadowLight(
	    		Renderer.SHADOW_MAP_WIDTH, 
	    		Renderer.SHADOW_MAP_HEIGHT, 
	    		Renderer.SHADOW_VIEWPORT_WIDTH, 
	    		Renderer.SHADOW_VIEWPORT_HEIGHT, 
	    		Renderer.SHADOW_NEAR, 
	    		Renderer.SHADOW_FAR)).set(1f, 1f, 1f, 40.0f, -35f, -35f)); 
		set(new ColorAttribute(ColorAttribute.Fog, 0.1f, 0.1f, 0.1f, 1f));
		shadowMap = shadowLight;
		spaceSegGrid = state.addGameObject(new GridSystem(state).setPhysicsTag(BulletPhysicsTag.BUILDING_PLATFORM).setShouldRender(false));
	}

	public void tick() {
		if(game.getTools().getCurrentCreatable() == Creatables.NONE)
			return;
		GameObject3 grid = (GameObject3)state.getGameObject(spaceSegGrid);
		grid.setPosition(GridSystem.snapToNearestGridPosition(state.getCamera().position));
	}
	
	public ModelBatch getShadowBatch() {
		return shadowBatch;
	}
	
	public DirectionalShadowLight getShadowLight() {
		return shadowLight;
	}
	
	public long getSpaceSegmentationGameObject() {
		return spaceSegGrid;
	}

}
