package dev.iwilkey.terrafort.ui.containers.interfaces;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.item.TItemStack;
import dev.iwilkey.terrafort.item.TItemStackCollection;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.ui.TAnchor;
import dev.iwilkey.terrafort.ui.TDrawable;
import dev.iwilkey.terrafort.ui.TDroppable;
import dev.iwilkey.terrafort.ui.TUserInterface;
import dev.iwilkey.terrafort.ui.containers.TContainer;
import dev.iwilkey.terrafort.ui.widgets.TItemStackSlotWidget;

/**
 * Provides an interface for the user to interact with in-game items. Automatically syncs with the
 * {@link TItemStackCollection} (inventory) of given {@link TPlayer}.
 * @author Ian Wilkey (iwilkey)
 */
public final class THUDInterface extends TContainer {
	
	public static final int   COLUMNS      = 4;
	public static final int   BACK_COLOR   = 0x333333ff;
	public static final int   HEALTH_COLOR = 0xff0000ff;
	public static final int   HUNGER_COLOR = 0xfec293ff;
	public static final int   THIRST_COLOR = 0x84d3e9ff;
	public static final int   ENERGY_COLOR = 0xf2ce30ff;
	public static final int   THICKNESS_PX = 16;
	public static final float PADDING      = 2.0f;
	
	private static boolean             dragMutex = false;
	private static boolean             sellUp = false;
	
	private final  TPlayer              owner;
	
	private        Actor                seller;
	private        VisLabel             status;
	private        VisProgressBar       health;
	private        VisProgressBar       hunger;
	private        VisProgressBar       energy;
	private        TItemStackSlotWidget equipped;
	private        TItemStackSlotWidget slots[];
	
	public THUDInterface(TPlayer owner) {
		super();
		setInternalPadding(8, 8, 8, 8);
		setExternalPadding(0, 8, 0, 0);
		setAnchor(TAnchor.BOTTOM_CENTER);
		this.owner = owner;
		// drag and drop into environment...
		seller = new Actor();
		seller.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		TUserInterface.getDad().addTarget(new Target(seller) {
			@Override
			public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
				final TItemStack over = (TItemStack)payload.getObject();
				if(!sellUp) {
					long vos = over.getItem().is().getBaseSellValuePerUnit() * over.getAmount();
					long d = vos / 100;
					long c = vos % 100;
					String rend = String.format("$%,d.%02d", d, c);
					TUserInterface.mallocpop("[YELLOW]Sell[]", "Sell [YELLOW]" + over.getAmount() + " " + over.getItem().is().getName() + "[] for"
							+ "\n+" + rend);
					sellUp = true;
				}
				return true;
			}
			@Override
			public void reset (Source source, Payload payload) {
				TUserInterface.freepop();
				sellUp = false;
			}
			@Override
			public void drop(Source source, Payload payload, float x, float y, int pointer) {
				TUserInterface.freepop();
				final TItemStack over = (TItemStack)payload.getObject();
				long vos = over.getItem().is().getBaseSellValuePerUnit() * over.getAmount();
				over.setAmount(0);
				sync();
				// give the money.
				owner.giveCurrency(vos);
				sellUp = false;
			}
		});
		TUserInterface.getMom().addActor(seller);
	}
	
	@Override
	public void pack(VisWindow window) {
		final VisTable funds = new VisTable();
		status   = new VisLabel("");
		status.setStyle(TUserInterface.LABEL_STYLE);
		status.setFontScale(0.19f);
		status.setAlignment(Align.center);
		funds.add(status).center().padBottom(2);
		funds.row();
		funds.addSeparator(false).padTop(2);
		window.add(funds).expand().padBottom(2).fill();
		window.row();
		final VisTable stats = new VisTable();
		final Drawable back = TDrawable.solid(BACK_COLOR, 1, THICKNESS_PX / 2);
		final ProgressBarStyle healthStyle = new ProgressBarStyle();
		healthStyle.background             = back;
		healthStyle.knob                   = TDrawable.solid(HEALTH_COLOR, 1, THICKNESS_PX / 2);
		healthStyle.knobBefore             = TDrawable.solid(HEALTH_COLOR, 1, THICKNESS_PX / 4);
		health                             = new VisProgressBar(0, TPlayer.PLAYER_MAX_HP, 1 / (float)TPlayer.PLAYER_MAX_HP, false, healthStyle);
		final ProgressBarStyle hungerStyle = new ProgressBarStyle();
		hungerStyle.background             = back;
		hungerStyle.knob                   = TDrawable.solid(HUNGER_COLOR, 1, THICKNESS_PX / 2);
		hungerStyle.knobBefore             = TDrawable.solid(HUNGER_COLOR, 1, THICKNESS_PX / 4);
		hunger                             = new VisProgressBar(0, TPlayer.PLAYER_MAX_HUNGER, 1 / (float)TPlayer.PLAYER_MAX_HUNGER, false, hungerStyle);
		final ProgressBarStyle energyStyle = new ProgressBarStyle();
		energyStyle.background             = back;
		energyStyle.knob                   = TDrawable.solid(ENERGY_COLOR, 1, THICKNESS_PX / 2);
		energyStyle.knobBefore             = TDrawable.solid(ENERGY_COLOR, 1, THICKNESS_PX / 4);
		energy                             = new VisProgressBar(0, TPlayer.PLAYER_MAX_ENERGY, 1 / (float)TPlayer.PLAYER_MAX_ENERGY, false, energyStyle);
		final int barpad = 8;
		stats.add(hunger).padLeft(barpad);
		stats.add(health).padRight(barpad).padLeft(barpad);
		stats.add(energy).padRight(barpad);
		window.add(stats).expand();
		window.row();
		final VisTable inventory = new VisTable();
		final VisTable etable    = new VisTable();
		final VisTable eslot     = new VisTable();
		equipped = new TItemStackSlotWidget(new TDroppable() {
			@Override
			public void dropcall() {
				sync();
			}
		});
		/*
		eslot.add(new TInformationWidget("[YELLOW]Equipped Slot[]", "Items placed in this slot\n"
				+ "[YELLOW]equip[] your player with the item's\n"
				+ "functionality.\n\n"
				+ "Every item has a specific [YELLOW][ACTION][]\nthat is performed "
				+ "during an equipped\n"
				+ "[PURPLE][ATTACK][] action.\n\n"
				+ "Read about an item's action by\nhovering "
				+ "over it in your inventory\nor the [YELLOW]Forger[] tool.")).padRight(4);
				*/
		eslot.add(equipped);
		etable.add(eslot).padBottom(PADDING).padTop(PADDING).padRight(PADDING * 2);
		etable.row();
		/*
		tslot.add(new TInformationWidget("[RED]Discard Slot[]", "Items placed in this slot will\n"
				+ "[RED]eject[] from your inventory.")).padRight(4);
				*/
		// etable.row();
		/*
		etable.add(new TInformationWidget("[YELLOW]Inventory[]", 
				"Your [YELLOW]Inventory[] provides an interface\n"
				+ "for you to interact with in-game items.\n\n"
				+ "Use [PURPLE][CURSOR 1][] to Drag & Drop an item\ninto any slot.\n\n"
				+ "Use [PURPLE][CURSOR 2][] while dragging to drop\n"
				+ "one item into the slot below, if possible.\n\n"
				+ "Alike items will stack according to their\n[YELLOW]Max Stack Size[].\n\n"
				+ "Unalike items will [YELLOW]swap[] slots with each\nother.\n\n"
				+ "To create an item you cannot find in nature,\nuse [PURPLE][F][] to toggle the [YELLOW]Market[]")).padTop(10).padLeft(22);
				*/
		inventory.add(etable).expand();
		inventory.addSeparator(true).padRight(2);
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
		inventory.add(stable);
		window.add(inventory).prefHeight(56);
		style.background = TDrawable.solidWithShadow(0x444444ff, 0x000000cc, (int)window.getWidth() / 2, (int)window.getHeight() / 2, 2, 2);
		window.setStyle(style);
	}

	@Override
	public void update() {
		String money = owner.currencyRenderString();
		String time  = owner.getWorld().worldTimeTo12hrClock();
		String day   = "Day " + (owner.getWorld().getWave() + 1);
		status.setText(String.format(" [YELLOW][F][] Shop [GRAY]|[] %s [GRAY]|[] %s [GRAY]|[] %s", money, time, day));
		health.setValue(owner.getCurrentHP());
		hunger.setValue(owner.getHungerPoints());
		energy.setValue(owner.getEnergyPoints());
		for(int i = 0; i < owner.getInventory().getItemStackCapacity(); i++) {
			slots[i].setItemStack(owner.getInventory().getCollection()[i]);
			slots[i].tick();
		}
		equipped.setItemStack(owner.getEquipped());
		equipped.tick();
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
	
	public void resize(int nw, int nh) {
		seller.setSize(nw, nh);
	}
	
	@Override
	public void dispose() {
		super.dispose();
	}

}
