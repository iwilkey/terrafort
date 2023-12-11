package dev.iwilkey.terrafort.ui.widgets;

import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.item.TItemStack;
import dev.iwilkey.terrafort.item.TItemStackCollection;

/**
 * An interactive grid of {@link TItemStack}s.
 * @author Ian Wilkey (iwilkey)
 */
public final class TItemStackSlotCollectionInterface extends VisTable {
	
	public static final float PADDING  = 2.0f;
	
	private long                 currentlySelected;
	private int                  maxRows;
	private int                  columns;
	private TItemStackCollection collection;
	private TItemStackSlot[][] 	 slots;
	
	public TItemStackSlotCollectionInterface(TItemStackCollection collection, int maxRows) {
		this.collection = collection;
		this.maxRows    = maxRows;
		columns         = (int)Math.ceil((float)collection.getItemStackCapacity() / maxRows);
		slots           = new TItemStackSlot[maxRows][columns];
		bake();
	}
	
	private void bake() {
		clear();
		int s = 0;
		for (int i = 0; i < maxRows; i++) {
            for (int j = 0; j < columns; j++) {
            	if(s >= collection.getItemStackCapacity())
            		return;
                final TItemStackSlot slot = new TItemStackSlot(this);
                slots[i][j]               = slot;
                slots[i][j].setItem(collection.getCollection()[s]);
                add(slot).pad(PADDING);
                s++;
            }
            row();
        }
		select(0, 0);
	}
	
	/**
	 * Called to sync the collection interface with the actual abstract collection.
	 */
	public void sync() {
		
		if(TInput.upArrow) {
			int ci = ((int)(currentlySelected >> 32) + 1) % maxRows;
			int cj = (int)(currentlySelected);
			select(ci, cj);
			TInput.upArrow = false;
		} else if(TInput.downArrow) {
			int ci = ((int)(currentlySelected >> 32) - 1);
			if(ci < 0) ci = maxRows - 1;
			int cj = (int)(currentlySelected);
			select(ci, cj);
			TInput.downArrow = false;
		} else if(TInput.leftArrow) {
			int ci = (int)(currentlySelected >> 32);
			int cj = ((int)(currentlySelected) - 1);
			if(cj < 0) cj = columns - 1;
			select(ci, cj);
			TInput.leftArrow = false;
		} else if(TInput.rightArrow) {
			int ci = (int)(currentlySelected >> 32);
			int cj = ((int)(currentlySelected) + 1) % columns;
			select(ci, cj);
			TInput.rightArrow = false;
		}
		int s = 0;
		for(int i = 0; i < maxRows; i++) {
			for(int j = 0; j < columns; j++) { 
				if(s >= collection.getItemStackCapacity())
            		return;
				slots[i][j].tick();
				slots[i][j].setItem(collection.getCollection()[s]);
				s++;
			}
		}
	}
	
	/**
	 * Called when an internal drag and drop or swap operation has been accepted.
	 * @param source the source item slot.
	 * @param target the target item slot.
	 */
	public void internalSwapRequest(TItemStackSlot source, TItemStackSlot target) {
		int s = 0;
		for(int i = 0; i < maxRows; i++) {
			for(int j = 0; j < columns; j++) { 
				if(s >= collection.getItemStackCapacity())
            		return;
				if(slots[i][j].equals(source))
					collection.setItemStack(s, target.getItem());
				else if(slots[i][j].equals(target))
					collection.setItemStack(s, source.getItem());
				s++;
			}
		}
	}
	
	public void select(int i, int j) {
		int s = 0;
		for(int ii = 0; ii < maxRows; ii++) {
			for(int jj = 0; jj < columns; jj++) { 
				if(s >= collection.getItemStackCapacity())
            		return;
				slots[ii][jj].unselect();
				s++;
			}
		}
		slots[i][j].select();
		currentlySelected = (((long)i) << 32) | (j & 0xffffffffL);
	}
	
	public void select(TItemStackSlot slot) {
		int s = 0;
		for(int ii = 0; ii < maxRows; ii++) {
			for(int jj = 0; jj < columns; jj++) { 
				if(s >= collection.getItemStackCapacity())
            		return;
				slots[ii][jj].unselect();
				if(slots[ii][jj].equals(slot)) {
					slot.select();
					currentlySelected = (((long)ii) << 32) | (jj & 0xffffffffL);
				}
				s++;
			}
		}
	}
}
