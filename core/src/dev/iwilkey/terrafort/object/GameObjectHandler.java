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
		
		// Update State RenderableProviders based on GameObject type?
		
		o.instantiation();
		System.out.println("[Terrafort Engine] Created a new GameObject with ID " + id);
		return id;
	}
	
	public void tick() {
		// Tick objs and see if they should be disposed of.
		iterator = activeObjects.entrySet().iterator();
		while(iterator.hasNext()) {
		    Map.Entry<Long, GameObject> entry = iterator.next();
		    GameObject obj = entry.getValue();
		    if(obj.shouldDispose()) {
		    	// Update State RenderableProviders based on GameObject type.
		    	
		    	obj.dispose();
		        iterator.remove();
		    } else {
		        obj.tick();
		    }
		}
	}
	
	/**
	 * Getters and setters
	 */
	
	public State getState() {
		return state;
	}
}
