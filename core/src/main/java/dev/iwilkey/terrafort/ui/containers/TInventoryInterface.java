package dev.iwilkey.terrafort.ui.containers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.obj.entity.lifeform.TPlayer;
import dev.iwilkey.terrafort.ui.TAnchor;
import dev.iwilkey.terrafort.ui.widgets.TItemStackSlotCollectionInterface;

/**
 * Provides an interface for the user to interact with in-game items.
 * @author Ian Wilkey (iwilkey)
 */
public final class TInventoryInterface extends TContainer {
	
	public static final float PADDING = 2.0f;
	
	private final TPlayer owner;
	
	private TItemStackSlotCollectionInterface slots;
	private boolean                           expanded;    
	
	public TInventoryInterface(TPlayer owner) {
		this.owner = owner;
		expanded = false;
		TGraphics.POST_GAUSSIAN_BLUR.setPasses(64);
	}
	
	@Override
	public void pack(VisWindow window) {
		setInternalPadding(8, 8, 8, 8);
		setExternalPadding(0, 8, 0, 8);
		setAnchor(TAnchor.BOTTOM_LEFT);
		slots = new TItemStackSlotCollectionInterface(owner.getInventory(), 2);
		window.add(slots).expand();
	}

	@Override
	public void update() {
		slots.sync();
		if(Gdx.input.isKeyJustPressed(Keys.F)) {
			expanded = !expanded;
		}
	}
}
