package dev.iwilkey.terrafort.ui.widgets;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.kotcrab.vis.ui.widget.VisImageButton;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.item.TItemFunction;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.ui.TDrawable;
import dev.iwilkey.terrafort.ui.TUserInterface;

/**
 * A UI interface that allows the creation of non-natural items through an efficient and fluid carousel interface.
 * @author Ian Wilkey (iwilkey)
 */
public final class TForgerWidget extends VisTable implements Disposable {
	
	private static final HashMap<TItemFunction, Array<VisImageButton>> ITEM_STOCK = new HashMap<>();
	
	private static TPlayer       player      = null;
	private static VisTextButton forgeButton = null;
	private static VisLabel      selectLabel = null;
	private static TItem         selected    = null;
	
	static {
		for(TItemFunction function : TItemFunction.values()) {
			if(function == TItemFunction.NATURAL)
				continue;
			ITEM_STOCK.put(function, new Array<>());
			for(TItem item : TItem.values()) {
				if(item.is().getFunction() == function) {
					final TextureRegion icon = new TextureRegion(TGraphics.DATA, 
					  item.is().getIcon().getDataOffsetX() * TGraphics.DATA_WIDTH,
					  item.is().getIcon().getDataOffsetY() * TGraphics.DATA_HEIGHT, 
					  item.is().getIcon().getDataSelectionWidth() * TGraphics.DATA_WIDTH,
					  item.is().getIcon().getDataSelectionHeight() * TGraphics.DATA_HEIGHT
					);
		        	final Image ico = new Image(icon);
		    		final Drawable icoD = ico.getDrawable();
		    		icoD.setMinHeight(16);
		    		icoD.setMinWidth(16);
		    		final VisImageButton itemButton = new VisImageButton(icoD);
		    		itemButton.getImage().setTouchable(Touchable.disabled);
		    		itemButton.getStyle().focusBorder = null;
		    		itemButton.getStyle().up = null;
		            itemButton.addListener(new ClickListener() {
		            	@Override
		            	public void clicked (InputEvent event, float x, float y) {
		            		select(item);
		            	}
		            });
		            itemButton.addListener(new InputListener() {
						@Override
					    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
							String recp = "";
							for(int i = 0; i < item.is().getRecipe().length; i++)
								recp += " [YELLOW][x" + item.is().getRecipe()[i].amount + "][] " + item.is().getRecipe()[i].item.is().getName() + ((i != item.is().getRecipe().length - 1) ? "\n" : "");
							final String out = String.format("%s\n\n[PINK][REQUIRES][]\n%s\n\nClick to select!", item.is().getDescription(), recp);
							TUserInterface.beginPopup(item.is().getName(), out);
					    }
					    @Override
					    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
					    	TUserInterface.endPopup();
					    }
					});
		            ITEM_STOCK.get(function).add(itemButton);
				}
			}
		}
	}
	
	private HashMap<TItemFunction, VisTextButton> itemFunctionTabs;
	private VisTable                              body;

	public TForgerWidget(TPlayer player) {
		pad(8);
		resetState();
		TForgerWidget.player        = player;
		itemFunctionTabs      = new HashMap<>();
		final VisTable header = new VisTable();
        for(TItemFunction function : TItemFunction.values()) {
        	if(function == TItemFunction.NATURAL)
        		continue;
            final VisTextButton tabButton = new VisTextButton(function.name());
            tabButton.getLabel().setStyle(TUserInterface.LABEL_STYLE);
            tabButton.getLabel().setFontScale(0.16f);
            tabButton.getLabel().setColor(Color.GRAY);
            tabButton.setFocusBorderEnabled(false);
            tabButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                	populateForFunction(function);
                	select(null);
                }
            });
            itemFunctionTabs.put(function, tabButton);
            header.add(tabButton).pad(1);
        }
        add(header).expandX().fillX();
        row();
        addSeparator();
		body                      = new VisTable();
		final VisScrollPane items = new VisScrollPane(body);
		items.setFadeScrollBars(false);
		items.getStyle().background = TDrawable.solid(0x000000ff, 1, 1);
		add(items).prefSize(256, 128).expand().fill();
		selectLabel = new VisLabel("Select an Item Function Tab!");
		selectLabel.setStyle(TUserInterface.LABEL_STYLE);
		selectLabel.setFontScale(0.16f);
		body.add(selectLabel);
		row();
		addSeparator();
		final VisTable footer = new VisTable();
		forgeButton = new VisTextButton("Select an Item!");
		forgeButton.setTouchable(Touchable.disabled);
		forgeButton.getLabel().setColor(Color.GRAY);
		forgeButton.getLabel().setStyle(TUserInterface.LABEL_STYLE);
		forgeButton.getLabel().setFontScale(0.16f);
		forgeButton.setFocusBorderEnabled(false);
		forgeButton.addListener(new ClickListener() {
			@Override
	        public void clicked(InputEvent event, float x, float y) {
				forge();
	        }
		});
		footer.add(new TInformationWidget("[YELLOW]Forger[]", "Your [YELLOW]Forger[] tool provides a way to create"
				+ " items that cannot\nbe found naturally.\n\n"
				+ "Creating an item happens in three easy steps.\n\n"
				+ " [1] Use [PURPLE][CURSOR 1][] to select the item's functionality using the\n[TEAL]TABS[] at the top.\n\n"
				+ " [2] Use [PURPLE][CURSOR 1][] to find and select the item using the\ncenter [TEAL]CAROUSEL[].\n\n"
				+ " [3] Use [PURPLE][CURSOR 1][] to click the [TEAL]CREATE BUTTON[] at the bottom.\n\n"
				+ "[GRAY]Note that the CREATE BUTTON may be grayed out. This is\nbecause your Inventory is missing one or"
				+ " more items that\n"
				+ "are required to create the target item.[]\n\n"
				+ "Use [PURPLE][F][] to toggle the [YELLOW]Forger[] tool.")).padRight(8);
		footer.add(forgeButton).pad(1);
		add(footer).expand().fill();
	}
	
	public void tick() {
	}
	
	/**
	 * Called when there is a change in the players inventory.
	 */
	public static void sync() {
		if(selected != null) {
			forgeButton.setText("Create " + selected.name());
			if(!player.canForgeItem(selected)) {
				forgeButton.setTouchable(Touchable.disabled);
				forgeButton.getLabel().setColor(Color.GRAY);
			} else {
				forgeButton.setTouchable(Touchable.enabled);
				forgeButton.getLabel().setColor(Color.WHITE);
			}
		} else {
			forgeButton.setText("Select an Item!");
			forgeButton.setTouchable(Touchable.disabled);
			forgeButton.getLabel().setColor(Color.GRAY);
		}
	}
	
	/**
	 * Algorithm to match unique recipes with {@link TItem}s in memory to the users current forge slot configuration.
	 * Returns the matched item which will be added to the players inventory in exchange for slotted items.
	 */
	private void forge() {
		player.forge(selected);
	}
	
	/**
	 * Adds the appropriate tabs and encapsulated items into the stop.
	 */
	private void populateForFunction(TItemFunction function) {
		for(Map.Entry<TItemFunction, VisTextButton> e : itemFunctionTabs.entrySet()) {
			final TItemFunction func = e.getKey();
			final VisTextButton but  = e.getValue();
			if(func == function) but.getLabel().setColor(Color.WHITE);
			else but.getLabel().setColor(Color.GRAY);
		}
		body.clear();
		final Array<VisImageButton> stock = ITEM_STOCK.get(function);
		for(VisImageButton but : stock)
			 body.add(but).size(48, 48).pad(8);
	}
	
	/*
	 * Selects a new item to forge. Checks if it's possible for the player to create it.
	 */
	private static void select(TItem item) {
		if(item == selected)
			return;
		selected = item;
		sync();
	}

	@Override
	public void dispose() {
		resetState();
	}
	
	private void resetState() {
		selectLabel = null;
		forgeButton = null;
		player      = null;
		selected    = null;
	}
	
}
