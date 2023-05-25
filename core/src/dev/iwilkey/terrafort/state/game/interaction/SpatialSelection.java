package dev.iwilkey.terrafort.state.game.interaction;

import org.lwjgl.opengl.GL20;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

import dev.iwilkey.terrafort.asset.TerrafortAssetHandler;
import dev.iwilkey.terrafort.gfx.RenderableProvider3;
import dev.iwilkey.terrafort.state.State;
import dev.iwilkey.terrafort.state.game.space.GridSystem;

public class SpatialSelection implements RenderableProvider3 {
	
	public static final int SELECTION_VOLUME_MAX = 64;
	private final State state;
	private final ModelInstance renderable;
	private boolean isValid;
	
	public SpatialSelection(final State state) {
		this.state = state;
		renderable = new ModelInstance(TerrafortAssetHandler.getVoxelModel("tf_building_handler_selection"));
		renderable.transform.scl(GridSystem.SQUARE_SIZE);
	}
	
	@SuppressWarnings("deprecation")
	public static Model createBuildingHandlerSelectionModel() {
		final ModelBuilder builder = new ModelBuilder();
		builder.begin();
		Color selectionColor = new Color(Color.YELLOW);
		Material material = new Material(ColorAttribute.createDiffuse(selectionColor));
		MeshPartBuilder mesher = builder.part("tf_building_handler_selection", GL20.GL_LINES, GridSystem.VERTEX_ATTRIBUTES, material);
		mesher.box(1f, 1f, 1f);
		return builder.end();
	}
	
	public void shouldRender() {
		if(!state.getProvider3().contains(this, false)) {
			state.getProvider3().add(this);
		}
	}
	
	private void setRenderingColor(Color color) {
		for(Material material : renderable.materials) {
		    ColorAttribute colorAttribute = (ColorAttribute) material.get(ColorAttribute.Diffuse);
		    if(colorAttribute != null) {
		        colorAttribute.color.set(color);
		    }
		}
	}
	
	public void shouldNotRender() {
		state.getProvider3().removeValue(this, false);
	}
	
	public SpatialSelection setPosition(float x, float y, float z) {
		renderable.transform.translate(x - renderable.transform.getTranslation(new Vector3()).x, y - renderable.transform.getTranslation(new Vector3()).y, z - renderable.transform.getTranslation(new Vector3()).z);
		return this;
	}
	
	private Vector3 selectionDimensions = new Vector3();
	private Vector3 centerPoint = new Vector3();
	public SpatialSelection setSelection(Vector3 gridStart, Vector3 gridEnd) {
	    selectionDimensions = GridSystem.translateSelectionToGridScaleDimensions(gridStart, gridEnd);
	    renderable.transform.setToScaling(selectionDimensions.x * GridSystem.SQUARE_SIZE, 
	    		selectionDimensions.y * GridSystem.SQUARE_SIZE, 
	    		selectionDimensions.z * GridSystem.SQUARE_SIZE);
	    // Calculate the center point of the selection
	    centerPoint.set((gridStart.x + gridEnd.x) / 2,
	    		(gridStart.y + gridEnd.y) / 2,
	    		(gridStart.z + gridEnd.z) / 2);
	    // Translate the instance to the center point of the selection
	    renderable.transform.setTranslation(centerPoint);
	    validateSelection(gridStart, gridEnd);
	    return this;
	}
	
	private Vector3 currentScale = new Vector3();
	private Vector3 inverseScale = new Vector3();
	public SpatialSelection resetSelection() {
	    // Get the current scale of the model instance
	    renderable.transform.getScale(currentScale);
	    // Calculate the inverse scale factor
	    inverseScale.set(GridSystem.SQUARE_SIZE / currentScale.x, 
			    		 GridSystem.SQUARE_SIZE / currentScale.y,
			    		 GridSystem.SQUARE_SIZE / currentScale.z);
	    // Apply the inverse scale
	    renderable.transform.scale(inverseScale.x, inverseScale.y, inverseScale.z);
	    setRenderingColor(Color.YELLOW);
	    return this;
	}
	
	public void setValid() {
		isValid = true;
	}
	
	public void setInvalid() {
		isValid = false;
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	private void validateSelection(Vector3 gridStart, Vector3 gridEnd) {
		int volumeOfSelection = (int)(selectionDimensions.x * selectionDimensions.y * selectionDimensions.z);
		if(volumeOfSelection > SELECTION_VOLUME_MAX) {
			setInvalid();
		} else setValid();
		if(isValid()) setRenderingColor(Color.GREEN);
		else setRenderingColor(Color.RED);
	}
	
	@Override
	public BoundingBox getBoundingBox() {
		return null;
	}

	@Override
	public Vector3 getDimensions() {
		return null;
	}

	@Override
	public ModelInstance getModelInstance() {
		return renderable;
	}
}
