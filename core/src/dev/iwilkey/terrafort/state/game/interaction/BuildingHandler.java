package dev.iwilkey.terrafort.state.game.interaction;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import dev.iwilkey.terrafort.InputHandler;
import dev.iwilkey.terrafort.InputHandler.KeyBinding;
import dev.iwilkey.terrafort.object.GameObject;
import dev.iwilkey.terrafort.object.GameObject3;
import dev.iwilkey.terrafort.physics.bullet.BulletPhysicsTag;
import dev.iwilkey.terrafort.state.game.SinglePlayerEngineState;
import dev.iwilkey.terrafort.state.game.SinglePlayerGameState;
import dev.iwilkey.terrafort.state.game.object.creatable.Creatables;
import dev.iwilkey.terrafort.state.game.object.decal.Hitpoint;
import dev.iwilkey.terrafort.state.game.object.truss.WoodenTruss;
import dev.iwilkey.terrafort.state.game.space.GridSystem;

public final class BuildingHandler {
	
	private final SinglePlayerEngineState engine;
	private final SinglePlayerGameState game;
	private final SpatialSelection selection;
	private Hitpoint posSel;
	
	public BuildingHandler(SinglePlayerEngineState engine, SinglePlayerGameState game) {
		this.engine = engine;
		this.game = game;
		selection = new SpatialSelection(engine);
		long psi = engine.addGameObject(new Hitpoint(engine));
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
	    return hitpoint != null ? GridSystem.translateHitpointToGridSpace(hitpoint) : null;
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
	    for (float x = startX; x <= endX; x += GridSystem.SQUARE_SIZE) {
	        for (float y = startY; y <= endY; y += GridSystem.SQUARE_SIZE) {
	            for (float z = startZ; z <= endZ; z += GridSystem.SQUARE_SIZE) {
	                // Decide what exact block to place.
	                GameObject3 block = new WoodenTruss(engine);
	                block.setStatic();
	                // Scale to fit the grid.
	                block.getModelInstance().transform.setToScaling(GridSystem.SQUARE_SIZE, GridSystem.SQUARE_SIZE, GridSystem.SQUARE_SIZE);
	                // Calculate the center point of the current cube division.
	                Vector3 position = new Vector3(x, y, z);
	                // Position the instance.
	                block.getModelInstance().transform.setTranslation(position);
	                // Add the new block to the list.
	                newBlocks.add(block);
	            }
	        }
	    }
	    engine.addGameObjects(newBlocks);
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
