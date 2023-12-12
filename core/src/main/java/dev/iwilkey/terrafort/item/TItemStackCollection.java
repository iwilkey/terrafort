package dev.iwilkey.terrafort.item;

/**
 * A collection of {@link TItemStack}s.
 * @author Ian Wilkey (iwilkey)
 */
public final class TItemStackCollection {
	
	/**
	 * The maximum number of like items that can be stacked on one another.
	 */
	public static final int MAX_STACK = 256;
	
	private final TItemStack items[];
	
	/**
	 * Creates a fresh, empty stack collection.
	 * @param capacity
	 */
	public TItemStackCollection(int capacity) {
		items = new TItemStack[capacity];
		for(int i = 0; i < capacity; i++)
			items[i] = null;
	}
	
	/**
	 * Add an item to the stack collection. Tries to add in the most optimal way possible.
	 * @param item the item.
	 * @return whether or not the item could be added.
	 */
	public boolean addItem(TItem item) {
		int firstNull = -1;
		// always try to stack first.
		for(int i = 0; i < items.length; i++) {
			// if the slot is null, we might be able to use this slot later.
			if(items[i] == null) {
				if(firstNull == -1)
					firstNull = i;
				continue;
			}
			if(items[i].getItem() == item)
				// if inc returns false, the slot is fully stacked.
				if(items[i].inc())
					return true;
		}
		// if first null is -1, there's no empty slots. the item cannot be added.
		if(firstNull == -1) 
			return false;
		items[firstNull] = new TItemStack(item);
		return true;
	}
	
	/**
	 * Force sets an index i to a given {@link TItemStack}. Called internally, usually to sync UI representation with this
	 * abstract one.
	 */
	public void setItemStack(int i, TItemStack stack) {
		items[i] = stack;
	}
	
	/**
	 * Returns the amount of {@link TItemStack}s allowed in the collection.
	 */
	public int getItemStackCapacity() {
		return items.length;
	}
	
	/**
	 * Returns a direct reference to this {@link TItemStack} collection array in memory.
	 */
	public TItemStack[] getCollection() {
		return items;
	}
	
}
