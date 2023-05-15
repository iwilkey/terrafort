package dev.iwilkey.terrafort.object;

import dev.iwilkey.terrafort.state.State;

public abstract class GameObject {
	
	protected State state;
	protected long id;
	protected boolean shouldDispose = false;
	
	public GameObject(State state) {
		this.state = state;
	}
	
	public void destroy() {
		shouldDispose = true;
	}
	
	public boolean shouldDispose() {
		return shouldDispose;
	}
	
	public void setID(long id) {
		this.id = id;
	}
	
	public abstract void instantiation();
	public abstract void tick();
	public abstract void dispose();
	
}
