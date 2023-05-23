package dev.iwilkey.terrafort.object;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.badlogic.gdx.utils.Disposable;

import dev.iwilkey.terrafort.gfx.ViewportResizable;
import dev.iwilkey.terrafort.physics.bullet.BulletWrapper;
import dev.iwilkey.terrafort.state.State;

public final class GameObjectHandler implements ViewportResizable, Disposable {
	
	public static final int MAX_OBJS = (int)Math.pow(2, 20);
	
	private final State state;
	private final BulletWrapper physics;
	private final AtomicLong idGenerator;
	private final HashMap<Long, GameObject> activeObjects;
	
	private Iterator<Map.Entry<Long, GameObject>> iterator;

	public GameObjectHandler(State state) {
		this.state = state;
		physics = new BulletWrapper();
		idGenerator = new AtomicLong(0);
		activeObjects = new HashMap<>();
		iterator = activeObjects.entrySet().iterator();
	}
	
	public long create(GameObject o) {
		if(activeObjects.size() + 1 > MAX_OBJS) {
			System.out.println("[Terrafort Engine] You cannot create a GameObject right now because it would defy the maximum amount of objects allowed in a state!");
			System.exit(-1);
			return -1;
		}
		long id = idGenerator.incrementAndGet();
		activeObjects.put(id, o);
		o.setID(id);
		// Handle adding 3D objects to the physics engine.
		if(o instanceof GameObject3) {
			GameObject3 obj3 = (GameObject3)o;
			PhysicsIdentity iden = obj3.getPhysicsIdentity();
			iden.getBody().setUserValue((int)id);
			iden.getBody().proceedToTransform(obj3.getModelInstance().transform);
			physics.getDynamicsWorld().addRigidBody(iden.getBody());
		}
		modifyRenderables(o, true);
		o.instantiation();
		System.out.println("[Terrafort Engine] Created a new GameObject with ID " + id);
		return id;
	}
	
	public GameObject get(long id) {
		if(activeObjects.containsKey(id)) {
			return activeObjects.get(id);
		} else {
			System.out.println("[Terrafort Engine] You cannot retrieve a GameObject that doesn't exist. ID " + id);
			return null;
		}
	}
	
	public void tick() {
		// Tick the physics engine.
		physics.tick();
		// Tick objs and see if they should be disposed of.
		iterator = activeObjects.entrySet().iterator();
		while(iterator.hasNext()) {
		    Map.Entry<Long, GameObject> entry = iterator.next();
		    long id = entry.getKey();
		    GameObject obj = entry.getValue();
		    if(obj.shouldDispose()) {
		    	// Update State RenderableProviders based on GameObject type.
		    	modifyRenderables(obj, false);
		    	// Handle removing 3D from physics engine.
		    	if(obj instanceof GameObject3) {
		    		GameObject3 obj3 = (GameObject3)obj;
		    		PhysicsIdentity iden = obj3.getPhysicsIdentity();
		    		physics.getDynamicsWorld().removeRigidBody(iden.getBody());
		    		iden.dispose();
		    		obj3.getMotion().dispose();
		    	}
		    	obj.dispose();
		        iterator.remove();
		        System.out.println("[Terrafort Engine] Removed GameObject with ID " + id);
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
	
	private void modifyRenderables(GameObject o, boolean add) {
		String type = getType(o);
		// TODO: Add support for other types of GameObjectX.
		switch(type) {
			case "go3":
				if(add) state.getProvider3().add((GameObject3)o);
				else state.getProvider3().removeValue((GameObject3)o, false);
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
	
	private boolean isRendering(GameObject o) {
		String type = getType(o);
		switch(type) {
			case "go3":
				return state.getProvider3().contains((GameObject3)o, false);
			case "go25":
				return state.getProvider25().contains((GameObject25)o, false);
			case "go2":
				return state.getProvider2().contains((GameObject2)o, false);
			default: return false;
		}
	}
	
	private String getType(GameObject o) {
		if(o instanceof GameObject3)
			return "go3";
		else if(o instanceof GameObject25)
			return "go25";
		else 
			return "go2";
	}
	
	/**
	 * Getters and setters
	 */
	
	public State getState() {
		return state;
	}
	
	public long activeObjectCount() {
		return activeObjects.size();
	}
	
	public HashMap<Long, GameObject> getActiveObjects() {
		return activeObjects;
	}
	
	public BulletWrapper getPhysicsEngine() {
		return physics;
	}
	
	/**
	 * Interfaces
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

	@Override
	public void dispose() {
		physics.dispose();
		activeObjects.clear();
	}
}
