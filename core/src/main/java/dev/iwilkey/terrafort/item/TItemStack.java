package dev.iwilkey.terrafort.item;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import dev.iwilkey.terrafort.gfx.TGraphics;

/**
 * An abstract representation of a collection of in-game items, resources, or materials.
 * @author Ian Wilkey (iwilkey)
 */
public final class TItemStack {

	private final TItem         item;
	private final TextureRegion icon;
	private int                 amt;
	
	/**
	 * Creates a new {@link TItemStack} with one item on the stack by default.
	 * @param item the {@link TItem} to represent.
	 */
	public TItemStack(TItem item) {
		amt       = 1;
		this.item = item;
		this.icon = new TextureRegion(TGraphics.DATA, 
									  item.getIcon().getDataOffsetX() * TGraphics.DATA_WIDTH,
									  item.getIcon().getDataOffsetY() * TGraphics.DATA_HEIGHT, 
									  item.getIcon().getDataSelectionWidth() * TGraphics.DATA_WIDTH,
									  item.getIcon().getDataSelectionHeight() * TGraphics.DATA_HEIGHT
									 );
	}
	
	/**
	 * Attempts to force set the item stack amount. Will be clamped to [0, item.getStackSize()].
	 * @param amt the amount to set to.
	 * @return the remaining amount that could not be added to the stack.
	 */
	public int setAmount(int amt) {
		amt      = Math.max(0, amt); // [0, ...]
		int max  = item.getStackSize();
		int diff = max - amt;
		if(diff < 0) {
			this.amt = max;
			return Math.abs(diff);
		}
		this.amt = amt;
		return 0;
	}
	
	/**
	 * Attempts to increment the stack amount. Nothing happens and returns false if the request would exceed the maximum item stack size.
	 */
	public boolean inc() {
		if(amt + 1 > item.getStackSize())
			return false;
		amt++;
		return true;
	}
	
	/**
	 * Attempts to decrement the stack amount. Nothing happens and returns false if the request would exceed the minimum item stack size (0).
	 */
	public boolean dec() {
		if(amt - 1 < 0)
			return false;
		amt--;
		return true;
	}
	
	/**
	 * Returns true if there are no items on the item stack.
	 */
	public boolean isEmpty() {
		return amt == 0;
	}
	
	/**
	 * Get the current amount of items on the stack.
	 */
	public int getAmount() {
		return amt;
	}
	
	/**
	 * Get the {@link TItem} represented by this stack.
	 * @return
	 */
	public TItem getItem() {
		return item;
	}
	
	/**
	 * Get the item icon in {@link TextureRegion} format.
	 */
	public TextureRegion getIcon() {
		return icon;
	}

}
