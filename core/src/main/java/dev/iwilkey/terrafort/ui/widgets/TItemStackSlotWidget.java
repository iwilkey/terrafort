package dev.iwilkey.terrafort.ui.widgets;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Payload;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Source;
import com.badlogic.gdx.scenes.scene2d.utils.DragAndDrop.Target;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Null;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.item.TItemStack;
import dev.iwilkey.terrafort.ui.TDroppable;
import dev.iwilkey.terrafort.ui.TUserInterface;
import dev.iwilkey.terrafort.ui.containers.interfaces.THUDInterface;

/**
 * A UI widget representing the functionality of a single {@link TItemStack} slot.
 * @author Ian Wilkey (iwilkey)
 */
public final class TItemStackSlotWidget extends VisTable {
	
	public static final int SLOT_SIZE = 48;
	
	private TDroppable     droppable;
	private TItemStackSlotWidget slotHoveringAboveMe;
	private boolean        dragCancelled;
	private TItemStack     itemStack;
	private VisLabel       amtLabel;
	private VisImageButton slot;
	
	public TItemStackSlotWidget(TDroppable droppable) {
		super();
		this.droppable              = droppable;
		slotHoveringAboveMe         = null;
		dragCancelled               = false;
		slot                        = new VisImageButton((Drawable)null);
		amtLabel                    = new VisLabel();
		slot.getStyle().focusBorder = null;
		amtLabel.setStyle(TUserInterface.LABEL_STYLE);
		amtLabel.setFontScale(0.15f);
		slot.getImage().setTouchable(Touchable.disabled);
		amtLabel.setTouchable(Touchable.disabled);
		add(slot).center().expand().fill().prefSize(SLOT_SIZE, SLOT_SIZE);
		slot.row();
		slot.add(amtLabel).center().align(Align.center);
		/**
		 * Implementation of the {@link TPopup} system to trigger when the cursor enters and exits a {@link TItemStackSlot}.
		 */
		slot.addListener(new InputListener() {
			@Override
		    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				if(itemStack != null)
					TUserInterface.mallocpop(itemStack.getItem().is().getName(), itemStack.getItem().is().getDescription());
			}
		    @Override
		    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
		    	TUserInterface.freepop();
		    }
		});
		/**
		 * Implementation of source slot behavior during Drag & Drop operation.
		 */
		TUserInterface.getDad().addSource(new Source(this) {
			@Override
			public Payload dragStart(InputEvent event, float x, float y, int pointer) {
				final Payload payload = new Payload();
		        final TItemStack is = itemStack;
		        if(itemStack == null)
		        	return null;
		        payload.setObject(is);
		        final Image dragActor = new Image(is.getIcon());
		        dragActor.setScale(2.0f);
		        payload.setDragActor(dragActor);
		        THUDInterface.setDrag();
				return payload;
			}
			@Override
			public void drag (InputEvent event, float x, float y, int pointer) {
				TUserInterface.getDad().setDragActorPosition(2, -16);
				if(TItemStackSlotWidget.this.getItemStack() == null)
					dragCancelled = true;
				else if(TItemStackSlotWidget.this.getItemStack().isEmpty())
					dragCancelled = true;
			}
			@Override
			public void dragStop (InputEvent event, float x, float y, int pointer, @Null Payload payload, @Null Target target) {
				THUDInterface.unsetDrag();
			}
		});
		
		/**
		 * Implementation of target slot behavior during Drag & Drop operation.
		 */
		TUserInterface.getDad().addTarget(new Target(this) {
			@Override
			public boolean drag(Source source, Payload payload, float x, float y, int pointer) {
				slotHoveringAboveMe = ((TItemStackSlotWidget)source.getActor());
				if(slotHoveringAboveMe.dragCancelled)
					payload.getDragActor().setScale(0);
				return true;
			}
			
			@Override
			public void reset (Source source, Payload payload) {
				slotHoveringAboveMe = null;
			}
			
			@Override
			public void drop(Source source, Payload payload, float x, float y, int pointer) {
				final TItemStackSlotWidget srcSlot  = ((TItemStackSlotWidget)source.getActor());
				final TItemStackSlotWidget trgSlot  = TItemStackSlotWidget.this;
				if(srcSlot.dragCancelled || trgSlot.dragCancelled) {
					THUDInterface.unsetDrag();
					srcSlot.dragCancelled = false;
					trgSlot.dragCancelled = false;
					return;
				}
				// do nothing if they are the same slot.
				if(srcSlot == trgSlot)
					return;
				final TItemStack     srcStack = srcSlot.getItemStack();
				final TItemStack     trgStack = trgSlot.getItemStack();
				// we know that, in the "worst" case, src and trg slot will simply swap, so there is always room.
				if(srcStack != null && trgStack != null) {
					boolean sameItem = srcStack.getItem() == trgStack.getItem();
					if(sameItem) {
						int alo = srcStack.setAmount(srcStack.getAmount() + trgStack.getAmount());
						if(alo != 0) {
							trgStack.setAmount(alo);
							return;
						} else {
							trgSlot.setItemStack(null);
						}
					}
				}
				// finally, do a swap. this encapsulates all behavior.
				srcSlot.setItemStack(TItemStackSlotWidget.this.itemStack);
				TItemStackSlotWidget.this.setItemStack((TItemStack)payload.getObject());
				srcSlot.droppable.dropcall();
				TItemStackSlotWidget.this.droppable.dropcall();
				THUDInterface.unsetDrag();
			}
		});
	}

	public void setItemStack(TItemStack item) {
		itemStack = item;
		update();
	}
	
	public void tick() {
		// the all-powerful division drop feature ;)
		if(slotHoveringAboveMe != null) {
			final TItemStack hoverStack = slotHoveringAboveMe.getItemStack();
			final TItemStack myStack    = getItemStack();
			if(hoverStack != null) {
				if(TInput.drop) {
					if(myStack != null) {
						if(hoverStack.getItem().equals(myStack.getItem()))
							if(hoverStack.dec())
								getItemStack().inc();
					} else {
						if(hoverStack.dec()) {
							itemStack = new TItemStack(hoverStack.getItem());
							update();
						}
					}
					droppable.dropcall();
					TInput.drop = false;
				}
			}
		}
		if(itemStack == null)
			return;
		else {
			if(itemStack.isEmpty()) {
				setItemStack(null);
				return;
			}
		}
		amtLabel.setText(String.format("x%d", itemStack.getAmount()));
	}
	
	private void update() {
		if(itemStack == null) {
			slot.getStyle().imageUp = null;
			amtLabel.setText("");
			return;
		}
		final Image ico = new Image(itemStack.getIcon());
		final Drawable icoD = ico.getDrawable();
		icoD.setMinHeight(SLOT_SIZE / 2);
		icoD.setMinWidth(SLOT_SIZE / 2);
		slot.getStyle().imageUp = icoD;
	}

	public TItemStack getItemStack() {
		return itemStack;
	}

}
