package dev.iwilkey.terrafort.ui.containers.interfaces;

import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.item.TItemStackCollection;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.particulate.TItemDrop;
import dev.iwilkey.terrafort.ui.TAnchor;
import dev.iwilkey.terrafort.ui.TDroppable;
import dev.iwilkey.terrafort.ui.containers.TContainer;
import dev.iwilkey.terrafort.ui.widgets.TItemStackSlot;

/**
 * Provides an interface for the user to interact with in-game items. Automatically syncs with the
 * {@link TItemStackCollection} (inventory) of given {@link TPlayer}.
 * @author Ian Wilkey (iwilkey)
 */
public final class TInventoryInterface extends TContainer {
	
	public static final int   COLUMNS = 4;
	public static final float PADDING = 2.0f;
	
	private final TPlayer        owner;
	private       TItemStackSlot equipped;
	private       TItemStackSlot trash;
	private       TItemStackSlot slots[];
	
	public TInventoryInterface(TPlayer owner) {
		this.owner = owner;
	}
	
	@Override
	public void pack(VisWindow window) {
		setInternalPadding(8, 8, 8, 8);
		setExternalPadding(0, 8, 0, 8);
		setAnchor(TAnchor.BOTTOM_LEFT);
		
		// equip slot.
		final VisTable etable = new VisTable();
		equipped = new TItemStackSlot(new TDroppable() {
			@Override
			public void dropcall() {
				sync();
			}
		});
		etable.add(equipped).padBottom(PADDING).padTop(PADDING).padRight(PADDING * 2);
		etable.row();

		trash = new TItemStackSlot(new TDroppable() {
			@Override
			public void dropcall() {
				sync();
			}
		});
		etable.add(trash).padBottom(PADDING).padTop(PADDING).padRight(PADDING * 2);
		
		window.add(etable).expand();
		
		// other inventory slots.
		final VisTable stable = new VisTable();
		final TItemStackCollection collection = owner.getInventory();
		slots = new TItemStackSlot[collection.getItemStackCapacity()];
		for(int i = 0; i < collection.getItemStackCapacity(); i++) {
			final TItemStackSlot slot = new TItemStackSlot(new TDroppable() {
				@Override
				public void dropcall() {
					sync();
				}
			});
			if(i % COLUMNS == 0 && i != 0)
				stable.row();
			stable.add(slot).pad(PADDING);
			slots[i] = slot;
			slots[i].setItemStack(collection.getCollection()[i]);
		}
		window.add(stable).expand();
	}

	@Override
	public void update() {
		for(int i = 0; i < owner.getInventory().getItemStackCapacity(); i++) {
			slots[i].setItemStack(owner.getInventory().getCollection()[i]);
			slots[i].tick();
		}
		equipped.tick();
		// garbage collection.
		if(trash.getItemStack() != null) {
			// throw the item stack from the players perspective direction.
			int dirX = TMath.DX[owner.getFacingDirection()];
			int dirY = -TMath.DY[owner.getFacingDirection()];
			int sx   = (int)owner.getActualX() + (dirX * (TItemDrop.WORLD_SIZE * 2));
			int sy   = (int)owner.getActualY() + (dirY * (TItemDrop.WORLD_SIZE * 2));
			for(int i = 0; i < trash.getItemStack().getAmount(); i++)
				owner.getWorld().addObject(new TItemDrop(owner.getWorld(), sx, sy, dirX, dirY, trash.getItemStack().getItem()));
			trash.setItemStack(null);
		}
	}
	
	private void sync() {
		for(int i = 0; i < owner.getInventory().getItemStackCapacity(); i++) {
			if(slots[i].getItemStack() != null) {
				if(slots[i].getItemStack().isEmpty())
					slots[i].setItemStack(null);
			}
			owner.getInventory().setItemStack(i, slots[i].getItemStack());
		}
	}
}
