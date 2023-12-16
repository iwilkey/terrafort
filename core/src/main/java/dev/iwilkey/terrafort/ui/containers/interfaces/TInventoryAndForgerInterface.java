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
import dev.iwilkey.terrafort.ui.widgets.TForgerWidget;
import dev.iwilkey.terrafort.ui.widgets.TInformationWidget;
import dev.iwilkey.terrafort.ui.widgets.TItemStackSlotWidget;

/**
 * Provides an interface for the user to interact with in-game items. Automatically syncs with the
 * {@link TItemStackCollection} (inventory) of given {@link TPlayer}.
 * @author Ian Wilkey (iwilkey)
 */
public final class TInventoryAndForgerInterface extends TContainer {
	
	public static final int   COLUMNS = 4;
	public static final float PADDING = 2.0f;
	
	public static Drawable       dropWithForger    = null;
	public static Drawable       dropWithoutForger = null;
	
	private static boolean       dragMutex = false;
	
	private final TPlayer        owner;

	private       TForgerWidget        forger;
	private       boolean              forgerUp;
	private       TItemStackSlotWidget equipped;
	private       TItemStackSlotWidget trash;
	private       TItemStackSlotWidget slots[];
	
	public TInventoryAndForgerInterface(TPlayer owner, boolean forgerUp) {
		super();
		setInternalPadding(8, 8, 8, 8);
		setExternalPadding(0, 8, 0, 8);
		setAnchor(TAnchor.BOTTOM_LEFT);
		this.owner = owner;
		this.forgerUp = forgerUp;
		if(forgerUp)
			forger = new TForgerWidget(owner);
	}
	
	@Override
	public void pack(VisWindow window) {
		final VisTable etable = new VisTable();
		final VisTable eslot  = new VisTable();
		equipped = new TItemStackSlotWidget(new TDroppable() {
			@Override
			public void dropcall() {
				sync();
			}
		});
		eslot.add(new TInformationWidget("[YELLOW]Equipped Slot[]", "Items placed in this slot\n"
				+ "[YELLOW]equip[] your player with the item's\n"
				+ "functionality.\n\n"
				+ "Every item has a specific [YELLOW]ACTION[]\nthat is performed "
				+ "during an equipped\n"
				+ "[PURPLE][ATTACK][] action.\n\n"
				+ "Read about an item's action by\nhovering "
				+ "over it in your inventory\nor the [YELLOW]Forger[] tool.")).padRight(4);
		eslot.add(equipped);
		etable.add(eslot).padBottom(PADDING).padTop(PADDING).padRight(PADDING * 2);
		etable.row();
		final VisTable tslot = new VisTable();
		trash = new TItemStackSlotWidget(new TDroppable() {
			@Override
			public void dropcall() {
				sync();
			}
		});
		tslot.add(new TInformationWidget("[RED]Discard Slot[]", "Items placed in this slot will\n"
				+ "[RED]eject[] from your inventory.")).padRight(4);
		tslot.add(trash);
		etable.add(tslot).padBottom(PADDING).padTop(PADDING).padRight(PADDING * 2);
		etable.row();
		etable.add(new TInformationWidget("[YELLOW]Inventory[]", 
				"Your [YELLOW]Inventory[] provides an interface\n"
				+ "for you to interact with in-game items.\n\n"
				+ "Use [PURPLE][CURSOR 1][] to Drag & Drop an item\ninto any slot.\n\n"
				+ "Use [PURPLE][CURSOR 2][] while dragging to drop\n"
				+ "one item into the slot below, if possible.\n\n"
				+ "Alike items will stack according to their\n[YELLOW]Max Stack Size[].\n\n"
				+ "Unalike items will [YELLOW]swap[] slots with each\nother.\n\n"
				+ "To create an item you cannot find in nature,\nuse [PURPLE][F][] to toggle the [YELLOW]Forger[] tool.")).padTop(10).padLeft(22);
		window.add(etable).expand();
		// other inventory slots.
		final VisTable stable = new VisTable();
		final TItemStackCollection collection = owner.getInventory();
		slots = new TItemStackSlotWidget[collection.getItemStackCapacity()];
		for(int i = 0; i < collection.getItemStackCapacity(); i++) {
			final TItemStackSlotWidget slot = new TItemStackSlotWidget(new TDroppable() {
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
		equipped.setItemStack(owner.getEquipped());
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
			owner.setEquipped(equipped.getItemStack());
		}
		forgerShouldSync();
	}
	
	public void forgerShouldSync() {
		if(forgerUp)
			TForgerWidget.sync();
	}
	
	public static void setDrag() {
		dragMutex = true;
	}
	
	public static void unsetDrag() {
		dragMutex = false;
	}
	
	public static boolean dragMutex() {
		return dragMutex;
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}

}
