package dev.iwilkey.terrafort.item.canonical;

import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;

/**
 * A callback method that relates every {@link TItem} to a specific action that occurs when it is equipped and used by a {@link TPlayer}.
 * @author Ian Wilkey (iwilkey)
 */
public interface TItemAction {
	
	/**
	 * Callback function that defines the effects of an item when equipped and used. Returns whether or not the use request should "count," i.e. decrement itself from it's {@link TItemStack}.
	 */
	public boolean use(TPlayer player);
	
}
