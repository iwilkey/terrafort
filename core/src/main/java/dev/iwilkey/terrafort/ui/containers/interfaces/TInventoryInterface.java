package dev.iwilkey.terrafort.ui.containers.interfaces;

import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.item.TItemStackCollection;
import dev.iwilkey.terrafort.math.TMath;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.particulate.TItemDrop;
import dev.iwilkey.terrafort.ui.TAnchor;
import dev.iwilkey.terrafort.ui.TDrawable;
import dev.iwilkey.terrafort.ui.TDroppable;
import dev.iwilkey.terrafort.ui.containers.TContainer;
import dev.iwilkey.terrafort.ui.widgets.TForger;
import dev.iwilkey.terrafort.ui.widgets.TItemStackSlot;

/**
 * Provides an interface for the user to interact with in-game items. Automatically syncs with the
 * {@link TItemStackCollection} (inventory) of given {@link TPlayer}.
 * @author Ian Wilkey (iwilkey)
 */
public final class TInventoryInterface extends TContainer {
	
	public static final int   COLUMNS = 4;
	public static final float PADDING = 2.0f;
	
	public static Drawable       dropWithForger    = null;
	public static Drawable       dropWithoutForger = null;
	
	private final TPlayer        owner;
	
	private       TForger        forger;
	private       boolean        forgerUp;
	private       TItemStackSlot equipped;
	private       TItemStackSlot trash;
	private       TItemStackSlot slots[];
	
	public TInventoryInterface(TPlayer owner, boolean forgerUp) {
		super();
		setInternalPadding(8, 8, 8, 8);
		setExternalPadding(0, 8, 0, 8);
		setAnchor(TAnchor.BOTTOM_LEFT);
		this.owner = owner;
		this.forgerUp = forgerUp;
		if(forgerUp)
			forger = new TForger(owner);
	}
	
	@Override
	public void pack(VisWindow window) {
		// equip slot.
		VisTable etable = new VisTable();
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
		VisTable stable = new VisTable();
		TItemStackCollection collection = owner.getInventory();
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
		if(forgerUp) {
			window.add(forger);
			if(dropWithForger == null)
				dropWithForger = TDrawable.solidWithShadow(0x444444ff, 0x000000cc, (int)window.getWidth(), (int)window.getHeight(), 1, 2);
			style.background = dropWithForger;
		} else {
			if(dropWithoutForger == null)
				dropWithoutForger = TDrawable.solidWithShadow(0x444444ff, 0x000000cc, (int)window.getWidth(), (int)window.getHeight(), 2, 4);
			style.background = dropWithoutForger;
		}
		window.setStyle(style);
	}

	@Override
	public void update() {
		if(forgerUp)
			forger.tick();	
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
	
	@Override
	public void dispose() {
		super.dispose();
	}

}
