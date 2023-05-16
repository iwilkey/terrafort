package dev.iwilkey.terrafort.object;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import dev.iwilkey.terrafort.state.State;

public final class GameObjectHandler {
	
	public static final int MAX_OBJS = (int)Math.pow(2, 20);
	
	private final State state;
	private final AtomicLong idGenerator;
	private final HashMap<Long, GameObject> activeObjects;
	private Iterator<Map.Entry<Long, GameObject>> iterator;

	public GameObjectHandler(State state) {
		this.state = state;
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
		// Add renderable.
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
		// Tick objs and see if they should be disposed of.
		iterator = activeObjects.entrySet().iterator();
		while(iterator.hasNext()) {
		    Map.Entry<Long, GameObject> entry = iterator.next();
		    long id = entry.getKey();
		    GameObject obj = entry.getValue();
		    if(obj.shouldDispose()) {
		    	// Update State RenderableProviders based on GameObject type.
		    	modifyRenderables(obj, false);
		    	obj.dispose();
		        iterator.remove();
		        System.out.println("[Terrafort Engine] Removed GameObject with ID " + id);
		    } else {
		        obj.tick();
		    }
		}
	}
	
	private void modifyRenderables(GameObject o, boolean add) {
		String type = "";
		if(o instanceof GameObject3)
			type = "go3";
		else if(o instanceof GameObject25)
			type = "go25";
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
				
				break;
			default: return;
		}
	}
	
	/**
	 * Getters and setters
	 */
	
	public State getState() {
		return state;
	}
}
