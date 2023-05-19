package dev.iwilkey.terrafort.state.game.object.creatable;

import dev.iwilkey.terrafort.state.game.object.resource.Resource;
import dev.iwilkey.terrafort.state.game.object.resource.Resources;

public enum Creatables {
	
	NONE("None", "None"),
	WOODEN_TRUSS("Wooden Truss", "Structural", new Resource(Resources.WOOD, 6)),
	COPPER_TRUSS("Copper Truss", "Structural", new Resource(Resources.WOOD, 2), new Resource(Resources.COPPER, 4)),
	IRON_TRUSS("Iron Truss", "Structural", new Resource(Resources.WOOD, 2), new Resource(Resources.IRON, 4));
	
	private String name;
	private String type;
	private Resource[] recipe;
	
	private Creatables(String name, String type, Resource... recipe) {
		this.name = name;
		this.type = type;
		this.recipe = recipe;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public Resource[] getRecipe() {
		return recipe;
	}

}
