package dev.iwilkey.terrafort.gui.interfaces;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.gui.TAnchor;
import dev.iwilkey.terrafort.gui.TDrawable;
import dev.iwilkey.terrafort.gui.TPopable;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.gui.container.TStaticContainer;
import dev.iwilkey.terrafort.gui.lang.TLocale;
import dev.iwilkey.terrafort.gui.widgets.TKnowledgeSlotWidget;

/**
 * Provides an interface for the user to interact with equipped knowledge.
 * @author Ian Wilkey (iwilkey)
 */
public final class TKnowledgeBarInterface extends TStaticContainer {
	
	/**
	 * Icon for the "Toggle Knowledge Bar" button.
	 */
	public static final Drawable KNOWLEDGE_BAR_ICON = TDrawable.fromSpriteSheet("sheets/items-icons.png", 0, 2, 1, 1);
	
	/**
	 * The number of Knowledge Slots a player gets.
	 */
	public static final int KNOWLEDGE_SLOTS = 7;
	
	/**
	 * The space (in pixels) to the left and right of each knowledge slot.
	 */
	public static final int SLOT_PADDING = 4;
	
	/**
	 * The toggle Knowledge Tree button that may have to be updated internally due to changes in settings.
	 */
	public static TPopable knowledgeButtonLocale;
	
	/**
	 * The index of the currently selected slot.
	 */
	private int selected;
	
	/**
	 * The location of the contents of each Knowledge slot.
	 */
	private TKnowledgeSlotWidget[] slots;
	
	public TKnowledgeBarInterface(Object... objReference) {
		super(objReference);
		pack(internal);
	}
	
	public void pack(VisTable internal, Object... objReference) {
		setAnchor(TAnchor.BOTTOM_CENTER);
		setExternalPadding(0, 4, 0, 0);
		setInternalPadding(8, 8, 16, 16);
		final VisTable       knowledgeButtonTable = new VisTable();
		final VisImageButton knowledgeButton      = new VisImageButton(KNOWLEDGE_BAR_ICON);
		knowledgeButton.addListener(new ClickListener() {
			public void clicked(InputEvent event, float x, float y) {
				TInput.techTree = true;
			}
		});
		knowledgeButton.getImage().setTouchable(Touchable.disabled);
		knowledgeButton.addListener(new TPopable(TLocale.getLine(1), TLocale.getLine(2)));
		knowledgeButton.setFocusBorderEnabled(false);
		knowledgeButtonTable.add(knowledgeButton).expand().fillX().prefHeight(8f);
		final VisTable slotTable = new VisTable();
		slots = new TKnowledgeSlotWidget[KNOWLEDGE_SLOTS];
		for(int i = 0; i < KNOWLEDGE_SLOTS; i++) {
			slots[i]  = new TKnowledgeSlotWidget();
			float pad = SLOT_PADDING * TUserInterface.getGlobalScale();
			slotTable.add(slots[i]).pad(0, pad, 0, pad).center().pad(SLOT_PADDING);
		}
		internal.add(knowledgeButtonTable).top().expand().fillX().prefHeight(8f).padTop(4f);
		internal.row();
		internal.add(slotTable).center();
		window.add(internal);
		selected = 0;
		slots[selected].select();
	}

	public void update(float dt) {
		if(TInput.slotLeft) {
			select(selected - 1);
			TInput.slotLeft = false;
		} else if(TInput.slotRight) {
			select(selected + 1);
			TInput.slotRight = false;
		}
	}
	
	/**
	 * Selects an Knowledge Slot in the inventory (mod KNOWLEDGE_SLOTS value.)
	 */
	public void select(int index) {
		if(index < 0)
			index = KNOWLEDGE_SLOTS - 1;
		index %= KNOWLEDGE_SLOTS;
		if(index != selected) {
			slots[selected].reset();
			slots[index].select();
			selected = index;
		}
	}
	
}
