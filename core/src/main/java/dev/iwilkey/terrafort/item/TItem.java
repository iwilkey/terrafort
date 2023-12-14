package dev.iwilkey.terrafort.item;

import dev.iwilkey.terrafort.gfx.TFrame;

/**
 * An enumeration of every possible item, resource, or material that can be collected, crafted and/or utilized in-game.
 * @author Ian Wilkey (iwilkey)
 */
public enum TItem {
		
	///////////////////////////////////////////////////////////////////////////
	// NATURAL
	///////////////////////////////////////////////////////////////////////////
	
	SHELL(new TFrame(6, 9, 1, 1), 256, TItemFunction.NATURAL, "Found naturally in [YELLOW]dunes[]."),
	
	
	///////////////////////////////////////////////////////////////////////////
	// FOOD
	///////////////////////////////////////////////////////////////////////////
	
	ESCARGOT(new TFrame(7, 6, 1, 1), 256, TItemFunction.FOOD, "A \"[ORANGE]food[]\". Restores 1 [ORANGE]nutrition.[]", SHELL, SHELL, null, SHELL);
	
	///////////////////////////////////////////////////////////////////////////
	// ENUM STRUCTURE (DO NOT MODIFY)
	///////////////////////////////////////////////////////////////////////////
	
	private final String        description;
	private final TItemFunction function;
	private final TItem         recipe[];
	private final TFrame        icon;
	private final int           stackSize;
	
	TItem(TFrame icon, int stackSize, TItemFunction category, String description, TItem... recipe) {	
		if(recipe.length == 0)
			recipe = new TItem[4];
		else if(recipe.length != 4) {
			throw new IllegalArgumentException("Every non-natural TItem must be specified with 4 null or non-null TItems!");
		}
		this.recipe      = recipe;
		this.function    = category;
		this.icon        = icon;
		this.description = description;
		this.stackSize   = stackSize;
	}
	
	/**
	 * Returns a list of {@link TItemSpec}s that are required to create the item. List may be empty.
	 */
	public TItem[] getRecipe() {
		return recipe;
	}
	
	/**
	 * Returns the {@link TItem}s {@link TItemFunction}.
	 */
	public TItemFunction getFunction() {
		return function;
	}
	
	/**
	 * Returns the {@link TFrame} associated with the item. It is the item's icon; it is meant to be
	 * a static, consistent graphical representation of the item no matter where it is rendered in the engine.
	 */
	public TFrame getIcon() {
		return icon;
	}
	
	/**
	 * A description of the {@link TItem}.
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * The total amount that the item can stack to. The idea is to make sure that small, insignificant items can stack higher than
	 * large, heavy items.
	 */
	public int getStackSize() {
		return stackSize;
	}
	
}
