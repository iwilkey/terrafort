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
import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.InputHandler;
import dev.iwilkey.terrafort.InputHandler.KeyBinding;
import dev.iwilkey.terrafort.asset.TerrafortAssetHandler;
import dev.iwilkey.terrafort.gfx.RenderableProvider3;
import dev.iwilkey.terrafort.object.GameObject;
import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.bullet.BulletPhysicsTag;
import dev.iwilkey.terrafort.state.State;
import dev.iwilkey.terrafort.state.game.SinglePlayerEngineState;
import dev.iwilkey.terrafort.state.game.SinglePlayerGameState;
import dev.iwilkey.terrafort.state.game.gfx.Space;
import dev.iwilkey.terrafort.state.game.object.creatable.Creatables;
import dev.iwilkey.terrafort.state.game.object.decal.Hitpoint;
import dev.iwilkey.terrafort.state.game.object.truss.WoodenTruss;

public final class BuildingHandler {
	
	public static class Selection implements RenderableProvider3 {
		
		private State state;
		private ModelInstance renderable;
		private boolean isValid;
		
		public Selection(State state) {
			this.state = state;
			renderable = new ModelInstance(TerrafortAssetHandler.getVoxelModel("tf_building_handler_selection"));
			renderable.transform.scl(Space.Segmentation.SQUARE_SIZE);
		}
		
		@SuppressWarnings("deprecation")
		public static Model createBuildingHandlerSelection() {
			final ModelBuilder builder = new ModelBuilder();
			builder.begin();
			Color selectionColor = new Color(Color.YELLOW); // change this to your desired color
			Material material = new Material(ColorAttribute.createDiffuse(selectionColor));
			MeshPartBuilder mesher = builder.part("tf_building_handler_selection", GL20.GL_LINES, Space.Segmentation.VERTEX_ATTRIBUTES, material);
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
		
		public Selection setPosition(float x, float y, float z) {
			renderable.transform.translate(x - renderable.transform.getTranslation(new Vector3()).x, y - renderable.transform.getTranslation(new Vector3()).y, z - renderable.transform.getTranslation(new Vector3()).z);
			return this;
		}
		
		Vector3 selectionDimensions = new Vector3();
		Vector3 centerPoint = new Vector3();
		public Selection setSelection(Vector3 gridStart, Vector3 gridEnd) {
		    selectionDimensions = Space.Segmentation.translateSelectionToGridScaleDimensions(gridStart, gridEnd);
		    renderable.transform.setToScaling(selectionDimensions.x * Space.Segmentation.SQUARE_SIZE, 
		    		selectionDimensions.y * Space.Segmentation.SQUARE_SIZE, 
		    		selectionDimensions.z * Space.Segmentation.SQUARE_SIZE);
		    // Calculate the center point of the selection
		    centerPoint.set((gridStart.x + gridEnd.x) / 2,
		    		(gridStart.y + gridEnd.y) / 2,
		    		(gridStart.z + gridEnd.z) / 2);
		    // Translate the instance to the center point of the selection
		    renderable.transform.setTranslation(centerPoint);
		    validateSelection(gridStart, gridEnd);
		    return this;
		}
		
		Vector3 currentScale = new Vector3();
		Vector3 inverseScale = new Vector3();
		public Selection resetSelection() {
		    // Get the current scale of the model instance
		    renderable.transform.getScale(currentScale);
		    // Calculate the inverse scale factor
		    inverseScale.set(Space.Segmentation.SQUARE_SIZE / currentScale.x, 
		    		Space.Segmentation.SQUARE_SIZE / currentScale.y,
		    		 Space.Segmentation.SQUARE_SIZE / currentScale.z);
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
			// selectionDimensions = Space.Segmentation.translateSelectionToGridScaleDimensions(gridStart, gridEnd);
			int volumeOfSelection = (int)(selectionDimensions.x * selectionDimensions.y * selectionDimensions.z);
			if(volumeOfSelection > 64) {
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
	
	private final SinglePlayerEngineState engine;
	private final SinglePlayerGameState game;
	private final Selection selection;
	private Hitpoint posSel;
	
	public BuildingHandler(SinglePlayerEngineState engine, SinglePlayerGameState game) {
		this.engine = engine;
		this.game = game;
		selection = new Selection(engine);
		long psi = engine.addGameObject(new Hitpoint(engine), false);
		posSel = (Hitpoint)engine.getGameObject(psi);
	}
	
	boolean isSelecting = false;
	final Vector3 initialGridSelection = Vector3.Zero.cpy();
	public void tick() {
	    if (isPlayerFocusedOnCreatable()) {
	        Vector3 hitpoint = getGridSpaceHitpoint();
	        if (hitpoint != null) {
	            processHitPoint(hitpoint);
	        } else {
	            resetIfSelecting();
	            disableRendering();
	        }
	    } else {
	        disableRendering();
	    }
	}

	private boolean isPlayerFocusedOnCreatable() {
	    return game.getTools().getCurrentCreatable() != Creatables.NONE && engine.getPlayer().isFocused();
	}

	private Vector3 getGridSpaceHitpoint() {
	    Vector3 hitpoint = engine.doRaycastForPoint(8.0f, BulletPhysicsTag.BUILDING_PLATFORM);
	    return hitpoint != null ? Space.Segmentation.translateHitpointToGridSpace(hitpoint) : null;
	}

	private void processHitPoint(Vector3 hitpoint) {
	    renderSelectionAndPosition(hitpoint);
	    handleSelectionState(hitpoint);
	    if (isSelecting) {
	        selection.setSelection(initialGridSelection, hitpoint);
	    } else {
	        resetAndPositionSelection(hitpoint);
	    }
	}

	private void handleSelectionState(Vector3 hitpoint) {
	    if (isActionPressed()) {
	        startSelection(hitpoint);
	    } else if (isActionReleased()) {
	    	if(isSelecting && selection.isValid()) {
		    	addBlocksToSelection(hitpoint);
		        endSelection();
	    	} else if(!selection.isValid()) {
	    		resetIfSelecting();
	    		disableRendering();
	    	}
	    }
	}
	
	private void addBlocksToSelection(Vector3 finalSelection) {
	    // Calculate if it's even possible, etc.
	    Array<GameObject> newBlocks = new Array<>();
	    float diffX = finalSelection.x - initialGridSelection.x;
	    float diffY = finalSelection.y - initialGridSelection.y;
	    float diffZ = finalSelection.z - initialGridSelection.z;
	    float startX = (diffX < 0) ? finalSelection.x : initialGridSelection.x;
	    float endX = (diffX < 0) ? initialGridSelection.x : finalSelection.x;
	    float startY = (diffY < 0) ? finalSelection.y : initialGridSelection.y;
	    float endY = (diffY < 0) ? initialGridSelection.y : finalSelection.y;
	    float startZ = (diffZ < 0) ? finalSelection.z : initialGridSelection.z;
	    float endZ = (diffZ < 0) ? initialGridSelection.z : finalSelection.z;
	    for (float x = startX; x <= endX; x += Space.Segmentation.SQUARE_SIZE) {
	        for (float y = startY; y <= endY; y += Space.Segmentation.SQUARE_SIZE) {
	            for (float z = startZ; z <= endZ; z += Space.Segmentation.SQUARE_SIZE) {
	                // Decide what exact block to place.
	                GameObject3 block = new WoodenTruss(engine);
	                block.setStatic();
	                // Scale to fit the grid.
	                block.getModelInstance().transform.setToScaling(Space.Segmentation.SQUARE_SIZE, Space.Segmentation.SQUARE_SIZE, Space.Segmentation.SQUARE_SIZE);
	                // Calculate the center point of the current cube division.
	                Vector3 position = new Vector3(x, y, z);
	                // Position the instance.
	                block.getModelInstance().transform.setTranslation(position);
	                // Add the new block to the list.
	                newBlocks.add(block);
	            }
	        }
	    }
	    engine.addGameObjects(newBlocks, true);
	}

	private void renderSelectionAndPosition(Vector3 hitpoint) {
	    selection.shouldRender();
	    posSel.setShouldRender(true);
	    posSel.setPosition(hitpoint);
	}

	private void resetIfSelecting() {
	    if(isSelecting) {
	        isSelecting = false;
	        selection.resetSelection();
	    }
	}

	private void disableRendering() {
	    posSel.setShouldRender(false);
	    selection.shouldNotRender();
	}

	private void startSelection(Vector3 hitpoint) {
	    initialGridSelection.set(hitpoint);
	    isSelecting = true;
	}

	private void endSelection() {
	    initialGridSelection.set(Vector3.Zero);
	    isSelecting = false;
	}

	private void resetAndPositionSelection(Vector3 hitpoint) {
	    selection.resetSelection();
	    selection.setPosition(hitpoint.x, hitpoint.y, hitpoint.z);
	}

	private boolean isActionPressed() {
	    return InputHandler.cursorJustDown(KeyBinding.getBinding("Action"));
	}

	private boolean isActionReleased() {
	    return InputHandler.cursorJustUp(KeyBinding.getBinding("Action"));
	}
	
}
