package dev.iwilkey.terrafort.item;

/**
 * A simple struct that pairs a {@link TItem} with an integer to define a magnitude, usually used to define an item's proportion in a recipe.
 * @author Ian Wilkey (iwilkey)
 */
public final class TItemSpec {
	
	/**
	 * The {@link TItem} that this specification represents.
	 */
	public final TItem item;
	
	/**
	 * The amount of {@link TItem} this specification calls for.
	 */
	public final int   amount;
	
	/**
	 * Creates a final {@link TItemSpec}. The fields cannot be changed after
	 * construction.
	 */
	public TItemSpec(final TItem item, final int amount) {
		this.item   = item;
		this.amount = amount;
	}

}
