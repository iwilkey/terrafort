package dev.iwilkey.terrafort.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.item.TItemStack;
import dev.iwilkey.terrafort.ui.TUserInterface;

/**
 * A UI widget representing the functionality of a single {@link TItemStack} slot.
 * @author Ian Wilkey (iwilkey)
 */
public final class TItemStackSlot extends VisTable {

	public static final int SLOT_SIZE = 48;
	
	private TItemStackSlotCollectionInterface collectionInterface;
	private TItemStack                        item;
	private VisLabel                          amt;
	private VisImageButton                    slot;
	
	public TItemStackSlot(TItemStackSlotCollectionInterface collectionInterface) {
		super();
		
		this.collectionInterface = collectionInterface;
		slot = new VisImageButton((Drawable)null);
		amt  = new VisLabel();
		amt.getStyle().font = TUserInterface.getGameFont();
		amt.setFontScale(0.15f);
		add(slot).center().expand().fill().prefSize(SLOT_SIZE, SLOT_SIZE);
		slot.row();
		slot.add(amt).center().align(Align.center);
		slot.addListener(new ClickListener() {
			@Override
			public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
				collectionInterface.select(TItemStackSlot.this);
			}
		});
		
		TUserInterface.getDad().addSource(new Source(this) {
			@Override
			public Payload dragStart(InputEvent event, float x, float y, int pointer) {
				final Payload payload = new Payload();
		        final TItemStack itemStack = item;
		        if(item == null)
		        	return null;
		        payload.setObject(itemStack);
		        final Image dragActor = new Image(itemStack.getIcon());
		        payload.setDragActor(dragActor);
				return payload;
			}
		});
		
		TUserInterface.getDad().addTarget(new Target(this) {
			@Override
			public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
				return true;
			}
			@Override
			public void drop(Source source, Payload payload, float x, float y, int pointer) {
				final TItemStackSlot srcSlot = ((TItemStackSlot)source.getActor());
				srcSlot.getInterface().internalSwapRequest(srcSlot, TItemStackSlot.this);
				TItemStackSlot.this.getInterface().internalSwapRequest(srcSlot, TItemStackSlot.this);
				// TItemStackSlot.this.getInterface().select(TItemStackSlot.this);
			}
		});
	}
	
	public void setItem(TItemStack item) {
		this.item = item;
		update();
	}
	
	public TItemStackSlotCollectionInterface getInterface() {
		return collectionInterface;
	}
	
	public void tick() {
		if(item == null)
			return;
		amt.setText(String.format("x%d", item.getAmount()));
	}
	
	private void update() {
		if(item == null) {
			slot.getStyle().imageUp = null;
			amt.setText("");
			return;
		}
		final Image ico = new Image(item.getIcon());
		final Drawable icoD = ico.getDrawable();
		icoD.setMinHeight(SLOT_SIZE / 2);
		icoD.setMinWidth(SLOT_SIZE / 2);
		slot.getStyle().imageUp = icoD;
	}
	
	public void select() {
		slot.focusGained();
	}
	
	public void unselect() {
		slot.focusLost();
	}
	
	public TItemStack getItem() {
		return item;
	}

}
