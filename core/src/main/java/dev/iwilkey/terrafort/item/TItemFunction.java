package dev.iwilkey.terrafort.item;

import java.io.Serializable;

/**
 * Enumerates every possible function that a {@link TItem} might serve in Terrafort.
 * @author Ian Wilkey (iwilkey)
 */
public enum TItemFunction implements Serializable {
	
	/**
	 * This item is fundamental and found in nature.
	 */
	NTRL,
	
	/**
	 * This item can be consumed for nutrition.
	 */
	FOOD,
	
	/**
	 * This item is used in the construction of forts.
	 */
	FORT,
	
	/**
	 * This item is used to deal a great amount of damage to the item it targets/collides with.
	 */
	WPON;
	
}
