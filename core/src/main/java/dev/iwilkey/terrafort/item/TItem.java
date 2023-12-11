package dev.iwilkey.terrafort.item;

import dev.iwilkey.terrafort.gfx.TFrame;

/**
 * An enumeration of every possible item, resource, or material that can be collected and utilized in-game.
 * @author Ian Wilkey (iwilkey)
 */
public enum TItem {
	
	TEST_ITEM(new TFrame(4, 2, 1, 1));
	
	private TFrame icon;
	
	TItem(TFrame icon) {
		this.icon = icon;
	}
	
	public TFrame getIcon() {
		return icon;
	}
	
}
