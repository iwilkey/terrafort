package dev.iwilkey.terrafort.object;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.Terrafort;
import dev.iwilkey.terrafort.gfx.ViewportResizable;
import dev.iwilkey.terrafort.physics.TerrafortPhysicsCore;
import dev.iwilkey.terrafort.state.State;
import dev.iwilkey.terrafort.utilities.TerrafortUniqueIDGenerator;

/**
 * The GameObjectHandler class is responsible for managing and handling game objects in the Terrafort Engine.
 * @author iwilkey
 */
public final class GameObjectHandler implements ViewportResizable, Disposable {
	
	public static final int MAX_OBJS = (int)Math.pow(2, 20);
	
	private final State state;
	private TerrafortUniqueIDGenerator idGenerator;
	private final TerrafortPhysicsCore physics;
	private final HashMap<Long, GameObject> activeObjects;
	
	private Iterator<Map.Entry<Long, GameObject>> iterator;

	/**
	 * Creates a new GameObjectHandler instance with the specified state.
	 * @param state the state of the game object handler
	 */
	public GameObjectHandler(State state) {
		this.state = state;
		activeObjects = new HashMap<>();
		iterator = activeObjects.entrySet().iterator();
		idGenerator = new TerrafortUniqueIDGenerator();
		physics = new TerrafortPhysicsCore();
	}
	
	/**
	 * Creates a new game object and adds it to the handler.
	 * @param o the game object to create
	 * @return the ID assigned to the created game object
	 */
	public long create(GameObject o) {
		if(activeObjects.size() + 1 > MAX_OBJS)
			Terrafort.fatal("You cannot create a GameObject right now because it would exceed the maximum amount of objects allowed in a state!");
		long id = idGenerator.next();
		o.setID(id);
		activeObjects.put(id, o);
		if(o instanceof GameObject3) {
			GameObject3 obj3 = (GameObject3)o;
			PhysicsIdentity iden = obj3.getPhysicsIdentity();
			iden.getBody().setUserValue((int)id);
			iden.getBody().proceedToTransform(obj3.getModelInstance().transform);
			physics.getDynamicsWorld().addRigidBody(iden.getBody());
		}
		modifyRenderables(o, true);
		o.instantiation();
		Terrafort.log("Created a new GameObject with ID " + id);
		return id;
	}
	
	/**
	 * Retrieves a game object by its ID.
	 * @param id the ID of the game object to retrieve
	 * @return the game object with the specified ID, or null if not found
	 */
	public GameObject get(long id) {
		if(activeObjects.containsKey(id)) {
			return activeObjects.get(id);
		} else {
			Terrafort.log("You cannot retrieve a GameObject that doesn't exist. ID " + id);
			return null;
		}
	}
	
	/**
	 * Updates the game objects and performs disposal if necessary.
	 */
	public void tick() {
		physics.tick();
		iterator = activeObjects.entrySet().iterator();
		while(iterator.hasNext()) {
		    Map.Entry<Long, GameObject> entry = iterator.next();
		    long id = entry.getKey();
		    GameObject obj = entry.getValue();
		    if(obj.shouldDispose()) {
		    	modifyRenderables(obj, false);
		    	if(obj instanceof GameObject3) {
		    		GameObject3 obj3 = (GameObject3)obj;
		    		PhysicsIdentity iden = obj3.getPhysicsIdentity();
		    		physics.getDynamicsWorld().removeRigidBody(iden.getBody());
		    		iden.dispose();
		    		obj3.getMotion().dispose();
		    	}
		    	obj.dispose();
		        iterator.remove();
		        Terrafort.log("Removed GameObject with ID " + id);
		    } else {
		    	if(!obj.shouldRender()) {
		    		if(isRendering(obj))
		    			modifyRenderables(obj, false);
		    	} else {
		    		if(!isRendering(obj))
		    			modifyRenderables(obj, true);
		    	}
		        obj.tick();
		    }
		}
	}
	
	/**
	 * Modifies the renderables based on the game object's type.
	 * @param o the game object
	 * @param add true to add the game object to the renderables, false to remove it
	 */
	private void modifyRenderables(GameObject o, boolean add) {
		String type = getType(o);
		switch(type) {
			case "go3":
				GameObject3 obj3 = (GameObject3)o;
				if(add) {
					if(!obj3.isStatic()) {
						state.getProvider3().add(obj3);
					} else {
						state.getStaticRenderableProviderCacheSystem().addStatic(obj3);
					}
				} else {
					if(!obj3.isStatic())
						state.getProvider3().removeValue(obj3, false);
					else
						state.getStaticRenderableProviderCacheSystem().removeStatic(obj3);
				}
				break;
			case "go25":
				if(add) state.getProvider25().add((GameObject25)o);
				else state.getProvider25().removeValue((GameObject25)o, false);
				break;
			case "go2":
				if(add) state.getProvider2().add((GameObject2)o);
				else state.getProvider2().removeValue((GameObject2)o, false);
				break;
			default: return;
		}
	}
	
	/**
	 * Checks if a game object is being rendered.
	 * @param o the game object
	 * @return true if the game object is being rendered, false otherwise
	 */
	private boolean isRendering(GameObject o) {
		String type = getType(o);
		switch(type) {
			case "go3":
				GameObject3 obj3 = (GameObject3)o;
				if(!obj3.isStatic())
					return state.getProvider3().contains(obj3, false);
				else return state.getStaticRenderableProviderCacheSystem().contains(obj3);
			case "go25":
				return state.getProvider25().contains((GameObject25)o, false);
			case "go2":
				return state.getProvider2().contains((GameObject2)o, false);
			default: return false;
		}
	}
	
	/**
	 * Determines the type of the game object.
	 * @param o the game object
	 * @return the type of the game object
	 */
	private String getType(GameObject o) {
		if(o instanceof GameObject3)
			return "go3";
		else if(o instanceof GameObject25)
			return "go25";
		else 
			return "go2";
	}
	
	/**
	 * Returns the state of the game object handler.
	 * @return the state
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * Returns the number of active game objects.
	 * @return the number of active game objects
	 */
	public long activeObjectCount() {
		return activeObjects.size();
	}
	
	/**
	 * Returns the map of active game objects.
	 * @return the map of active game objects
	 */
	public HashMap<Long, GameObject> getActiveObjects() {
		return activeObjects;
	}
	
	/**
	 * Returns the physics engine of the game object handler.
	 * @return the physics engine
	 */
	public TerrafortPhysicsCore getPhysicsEngine() {
		return physics;
	}
	
	/**
	 * Resizes the viewport and notifies the viewport-resizable game objects.
	 * @param newWidth the new width of the viewport
	 * @param newHeight the new height of the viewport
	 */
	@Override
	public void onViewportResize(int newWidth, int newHeight) {
		for(Map.Entry<Long, GameObject> entry : activeObjects.entrySet()) {
			if(entry.getValue() instanceof ViewportResizable) {
				ViewportResizable obj = (ViewportResizable)(entry.getValue());
				obj.onViewportResize(newWidth, newHeight);
			}
		}
	}

	/**
	 * Disposes the game object handler and releases any resources.
	 */
	@Override
	public void dispose() {
		physics.dispose();
		activeObjects.clear();
	}
}
