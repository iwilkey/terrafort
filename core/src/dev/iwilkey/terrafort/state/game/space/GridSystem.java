package dev.iwilkey.terrafort.state.game.space;

import org.lwjgl.opengl.GL20;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.bullet.BulletPrimitive;
import dev.iwilkey.terrafort.physics.bullet.BulletWrapper;
import dev.iwilkey.terrafort.state.State;

public final class GridSystem extends GameObject3 {
	
	public static final int VERTEX_ATTRIBUTES = 
			VertexAttributes.Usage.Position | 
			VertexAttributes.Usage.ColorUnpacked;
	
	public static final float SIZE = (float)Math.pow(2, 8);
	public static final int DIVISIONS = (int)SIZE * 2;
	public static final float SQUARE_SIZE = (SIZE / DIVISIONS);
	public static final float INITIAL_HEIGHT = 0f;
	public static final Color COLOR = new Color(0.8f, 0.8f, 0.8f, 0f);

	public GridSystem(State state) {
		super(state, "tf_space_seg", BulletPrimitive.CUBOID, 0.0f);
	}
	
	private static final Vector3 selectionGridScaleDimensions = new Vector3();
	private static final Vector3 hitpointTranslation = new Vector3();
	private static final Vector3 snapPositionToGrid = new Vector3();
	
	public static Model createSegmentationGridModel() {
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
	
}
