package dev.iwilkey.terrafort.object;

import dev.iwilkey.terrafort.state.State;

public class GameObject {
	
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
	
	public void instantiation() {}
	public void tick() {}
	public void dispose() {}
	
}
