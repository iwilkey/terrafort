package dev.iwilkey.terrafort.state.game.object.resource;

public final class Resource {
	
	private final Resources resource;
	private final long count;
	
	public Resource(final Resources resource, final long count) {
		this.resource = resource;
		this.count = count;
	}
	
	public Resources getResource() {
		return resource;
	}
	
	public long getCount() {
		return count;
	}

}
