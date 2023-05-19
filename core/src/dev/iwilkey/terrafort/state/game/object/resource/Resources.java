package dev.iwilkey.terrafort.state.game.object.resource;

public enum Resources {
	
	WOOD("Wood"),
	COPPER("Copper"),
	IRON("Iron");
	
	private String name;
	
	private Resources(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}
