package dev.iwilkey.terrafort.item;

/**
 * A collection of {@link TItemStack}s.
 * @author Ian Wilkey (iwilkey)
 */
public final class TItemStackCollection {
	
	public static final int MAX_STACK = 256;
	
	private TItemStack items[];
	
	public TItemStackCollection(int capacity) {
		items = new TItemStack[capacity];
		for(int i = 0; i < capacity; i++)
			items[i] = null;
	}
	
	public boolean addItem(TItem item) {
		// Search for the approp. item stack...
		for(int i = 0; i < items.length; i++) {
			if(items[i] == null)
				continue;
			if(items[i].getItem() == item) {
				// found, but it might not be able to hold any more...
				if(items[i].getAmount() + 1 > MAX_STACK)
					continue;
				items[i].incAmount();
				return true;
			}
		}
		// Otherwise, search for an empty slot and create an item stack there.
		for(int i = 0; i < items.length; i++) {
			if(items[i] == null) {
				items[i] = new TItemStack(item);
				return true;
			}
		}
		// The item could not be added because the stack collection is full.
		return false;
	}
	
	public void setItemStack(int i, TItemStack stack) {
		items[i] = stack;
	}
	
	public int getItemStackCapacity() {
		return items.length;
	}
	
	public TItemStack[] getCollection() {
		return items;
	}
	
}
