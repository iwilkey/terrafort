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

import dev.iwilkey.terrafort.gfx.Renderer;
import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.bullet.BulletPhysicsTag;
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
		public static final float SIZE = (float)Math.pow(2, 8);
		public static final int DIVISIONS = (int)SIZE * 2;
		public static final float SQUARE_SIZE = (SIZE / DIVISIONS);
		public static final float INITIAL_HEIGHT = 0f;
		public static final Color COLOR = new Color(0.8f, 0.8f, 0.8f, 0f);

		public Segmentation(State state) {
			super(state, "tf_space_seg", BulletPrimitive.CUBOID, 0.0f);
		}
		
		private static final Vector3 selectionGridScaleDimensions = new Vector3();
		private static final Vector3 hitpointTranslation = new Vector3();
		private static final Vector3 snapPositionToGrid = new Vector3();
		
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
		
		public static Vector3 translateHitpointToGridSpace(Vector3 hitpoint) {
		    // Calculate the position of the hitpoint in terms of division coordinates
		    float gridX = (float)Math.floor(hitpoint.x / SQUARE_SIZE);
		    float gridY = hitpoint.y + (SQUARE_SIZE / 2); // the grid is a plane, so the height is constant
		    float gridZ = (float)Math.floor(hitpoint.z / SQUARE_SIZE);
		    // Convert back to world coordinates and adjust for the center of the division
		    hitpointTranslation.set(gridX * SQUARE_SIZE + SQUARE_SIZE / 2, gridY, gridZ * SQUARE_SIZE + SQUARE_SIZE / 2);
			return hitpointTranslation;
		}
		
		public static Vector3 translateSelectionToGridScaleDimensions(Vector3 start, Vector3 end) {
			// Find out how many blocks each dimension select.
			int xBlocks = (int)((Math.abs((start.x - end.x)) / SQUARE_SIZE) + (2 * SQUARE_SIZE));
			int yBlocks = (int)((Math.abs((start.y - end.y)) / SQUARE_SIZE) + (3 * SQUARE_SIZE));
			int zBlocks = (int)((Math.abs((start.z - end.z)) / SQUARE_SIZE) + (2 * SQUARE_SIZE));
			selectionGridScaleDimensions.set(xBlocks, yBlocks, zBlocks);
			return selectionGridScaleDimensions;
		}
		
		public static Vector3 snapToNearestGridPosition(Vector3 position) {
			float x = Math.round(position.x / SQUARE_SIZE) * SQUARE_SIZE;
			float y = (Math.round((position.y - 3) /SQUARE_SIZE) * SQUARE_SIZE) + 0.3f;
			float z = Math.round(position.z / SQUARE_SIZE) * SQUARE_SIZE;
			snapPositionToGrid.set(x, y, z);
			return snapPositionToGrid;
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
		spaceSegGrid = state.addGameObject(new Segmentation(state).setPhysicsTag(BulletPhysicsTag.BUILDING_PLATFORM));
	}

	public void tick() {
		GameObject3 grid = (GameObject3)state.getGameObject(spaceSegGrid);
		if(game.getTools().getCurrentCreatable() == Creatables.NONE) {
			grid.setShouldRender(false);
			return;
		}
		grid.setShouldRender(false);
		grid.setPosition(Segmentation.snapToNearestGridPosition(state.getCamera().position));
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
