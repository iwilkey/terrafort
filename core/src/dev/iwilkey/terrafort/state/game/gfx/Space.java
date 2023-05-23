package dev.iwilkey.terrafort.state.game.gfx;

import org.lwjgl.opengl.GL20;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;
import com.badlogic.gdx.graphics.g3d.utils.DepthShaderProvider;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.bullet.BulletPrimitive;
import dev.iwilkey.terrafort.physics.bullet.BulletWrapper;
import dev.iwilkey.terrafort.state.State;
import dev.iwilkey.terrafort.state.game.SinglePlayerGameState;
import dev.iwilkey.terrafort.state.game.object.creatable.Creatables;

public class Space extends Environment {
	
	public static class Segmentation extends GameObject3 {

		public static final int VERTEX_ATTRIBUTES = 
				VertexAttributes.Usage.Position | 
				VertexAttributes.Usage.ColorUnpacked;
		public static final float SIZE = (float)Math.pow(2, 2);
		public static final int DIVISIONS = (int)SIZE * 2;
		public static final float INITIAL_HEIGHT = 0f;
		public static final Color COLOR = new Color(0.5f, 0.5f, 0.5f, 1f);
		
		public Segmentation(State state) {
			super(state, "tf_space_seg", BulletPrimitive.CUBOID, 0.0f);
		}
		
		public static Model createSegmentationGrid() {
			final ModelBuilder modelBuilder = new ModelBuilder();
	        modelBuilder.begin();
	        MeshPartBuilder builder = modelBuilder.part("tf_space_seg", GL20.GL_LINES, VERTEX_ATTRIBUTES, new Material());
	        builder.setColor(COLOR);
	        for(int i = 0; i <= DIVISIONS; i++) {
	            builder.line(-(SIZE / 2), INITIAL_HEIGHT, -(SIZE / 2) + (SIZE / DIVISIONS) * i, 
	                         (SIZE / 2), INITIAL_HEIGHT, -(SIZE / 2) + (SIZE / DIVISIONS) * i);
	            builder.line(-(SIZE / 2) + (SIZE / DIVISIONS) * i, INITIAL_HEIGHT, -(SIZE / 2), 
	                         -(SIZE / 2) + (SIZE / DIVISIONS) * i, INITIAL_HEIGHT, (SIZE / 2));
	        }
	        return modelBuilder.end();
		}

		@Override
		public void instantiation() {
			setPhysicsBodyType(BulletWrapper.STATIC_FLAG);
		}

		@Override
		public void tick() {}
		@Override
		public void dispose() {}
		
	}
	
	private static final int SHADOW_MAP_WIDTH = (int)Math.pow(2, 12);
	private static final int SHADOW_MAP_HEIGHT = SHADOW_MAP_WIDTH;
	private static final float SHADOW_VIEWPORT_WIDTH = 40f;
	private static final float SHADOW_VIEWPORT_HEIGHT = SHADOW_VIEWPORT_WIDTH;
	private static final float SHADOW_NEAR = 0.0f;
	private static final float SHADOW_FAR = 1024.0f;
	
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
	    		SHADOW_MAP_WIDTH, 
	    		SHADOW_MAP_HEIGHT, 
	    		SHADOW_VIEWPORT_WIDTH, 
	    		SHADOW_VIEWPORT_HEIGHT, 
	    		SHADOW_NEAR, 
	    		SHADOW_FAR)).set(1f, 1f, 1f, 40.0f, -35f, -35f)); 
		set(new ColorAttribute(ColorAttribute.Fog, 0.1f, 0.1f, 0.1f, 1f));
		shadowMap = shadowLight;
		spaceSegGrid = state.addGameObject(new Segmentation(state));
	}
	
	public void tick() {
		if(game.getTools().getCurrentCreatable() == Creatables.NONE) {
			state.getGameObject(spaceSegGrid).setShouldRender(false);
			return;
		}
		state.getGameObject(spaceSegGrid).setShouldRender(true);
		GameObject3 grid = (GameObject3)state.getGameObject(spaceSegGrid);
		Vector3 playerPos = state.getCamera().position;
		// Find the nearest snap.
		int x = ((int)playerPos.x);
		int y = ((int)playerPos.y) - 1;
		int z = ((int)playerPos.z);
		grid.setPosition(x, y, z);
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
