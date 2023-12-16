package dev.iwilkey.terrafort.item;

import dev.iwilkey.terrafort.item.canonical.TItemDefinition;
import dev.iwilkey.terrafort.item.canonical.natural.*;
import dev.iwilkey.terrafort.item.canonical.food.*;

/**
 * An enumeration of every possible item, resource, or material that can be collected, crafted and/or utilized in-game.
 * @author Ian Wilkey (iwilkey)
 */
public enum TItem {
		
	///////////////////////////////////////////////////////////////////////////
	// NATURAL
	///////////////////////////////////////////////////////////////////////////
	
	SHELL(new TShell()),
	LOG(new TLog()),

	///////////////////////////////////////////////////////////////////////////
	// FOOD
	///////////////////////////////////////////////////////////////////////////
	
	ESCARGOT(new TEscargot());
	
	///////////////////////////////////////////////////////////////////////////
	// ENUM STRUCTURE (DO NOT MODIFY)
	///////////////////////////////////////////////////////////////////////////
	
	private final TItemDefinition canonical;
	
	TItem(final TItemDefinition canonical) {	
		this.canonical = canonical;
	}
	
	public TItemDefinition is() {
		return canonical;
	}
	
}
