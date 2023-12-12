package dev.iwilkey.terrafort.item;

import dev.iwilkey.terrafort.gfx.TFrame;

/**
 * An enumeration of every possible item, resource, or material that can be collected and utilized in-game.
 * @author Ian Wilkey (iwilkey)
 */
public enum TItem {
	
	TEST_ITEM(new TFrame(4, 2, 1, 1), 256),
	SHELL(new TFrame(6, 9, 1, 1), 256);
	
	private final TFrame icon;
	private final int    stackSize;
	
	TItem(TFrame icon, int stackSize) {
		this.icon      = icon;
		this.stackSize = stackSize;
	}
	
	/**
	 * Returns the {@link TFrame} associated with the item. It is the item's icon; it is meant to be
	 * a static, consistent graphical representation of the item no matter where it is rendered in the engine.
	 */
	public TFrame getIcon() {
		return icon;
	}
	
	/**
	 * The total amount that the item can stack to. The idea is to make sure that small, insignificant items can stack higher than
	 * large, heavy items.
	 */
	public int getStackSize() {
		return stackSize;
	}
	
}
