package dev.iwilkey.terrafort.object;

import dev.iwilkey.terrafort.state.State;

public abstract class GameObject {
	
	protected State state;
	protected long id;
	protected boolean shouldRender = true;
	protected boolean shouldDispose = false;
	
	public GameObject(State state) {
		this.state = state;
	}
	
	public boolean shouldRender() {
		return shouldRender;
	}
	
	public boolean shouldDispose() {
		return shouldDispose;
	}
	
	public GameObject setID(long id) {
		this.id = id;
		return this;
	}
	
	public GameObject setShouldDispose() {
		shouldDispose = true;
		return this;
	}
	
	public GameObject setShouldRender(boolean verdict) {
		shouldRender = verdict;
		return this;
	}
	
	public abstract void instantiation();
	public abstract void tick();
	public abstract void dispose();
	
}
