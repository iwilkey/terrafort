package dev.iwilkey.terrafort.ui.containers.interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import com.kotcrab.vis.ui.widget.VisImage;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisScrollPane;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.item.TItem;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.particulate.TItemDrop;
import dev.iwilkey.terrafort.ui.TAnchor;
import dev.iwilkey.terrafort.ui.TDrawable;
import dev.iwilkey.terrafort.ui.TUserInterface;
import dev.iwilkey.terrafort.ui.containers.TContainer;
import dev.iwilkey.terrafort.ui.widgets.TInformationWidget;

/**
 * Provides a way for a {@link TPlayer} to buy any item using their currency.
 * @author Ian Wilkey
 */
public final class TShopInterface extends TContainer {
	
	public static final int      BULK_LEVELS  = 5;
	public static final int      WIDTH        = 16;
	public static final int      HEIGHT       = 9;
	
	public static final Drawable WINDOW_BG    = TDrawable.solid(0x444444ff, WIDTH, HEIGHT);
	public static final Drawable BLACK_BG     = TDrawable.solid(0x000000ff, WIDTH, HEIGHT);
	public static final Drawable GRAY_BG      = TDrawable.solid(0x111111ff, WIDTH, HEIGHT);
	public static final Drawable DARK_GRAY_BG = TDrawable.solid(0x222222ff, WIDTH, HEIGHT);
	
	private static final ArrayList<TItem> STOCK;
	
	private HashMap<TItem, VisTextButton[]> buy;
	
	static {
		// put all the game items in alphabetical order...
		STOCK = Arrays.asList(TItem.values()).stream()
		        .sorted(Comparator.comparing((TItem t) -> t.is().getFunction())
		                          .thenComparing(t -> t.is().getName()))
		        .collect(Collectors.toCollection(ArrayList::new));
	}
	
	private final TPlayer player;
	
	public TShopInterface(TPlayer player) {
		this.player = player;
	}
	
	@Override
	public void pack(VisWindow window) {
		setInternalPadding(8, 8, 8, 8);
		setExternalPadding(0, 8, 0, 0);
		setAnchor(TAnchor.CENTER_CENTER);
		final VisTable shop  = new VisTable();
		final VisTable title = new VisTable();
		final VisLabel sl    = new VisLabel();
		sl.setStyle(TUserInterface.LABEL_STYLE);
		sl.setFontScale(0.16f);
		sl.setText("Shop");
		sl.setAlignment(Align.center);
		title.add(sl).padBottom(6);
		title.row();
		title.addSeparator();
		shop.add(title).expand().fill().pad(8);
		shop.row();
		final VisTable body        = new VisTable();
		body.top();
		final VisScrollPane pane   = new VisScrollPane(body);
		pane.setFadeScrollBars(false);
		pane.getStyle().background = BLACK_BG;
		buy = new HashMap<>();
		int i = 0;
		for(TItem item : STOCK) {
			body.add(getEntryFor(item, ((i & 1) == 0)))
	            .expandX()
	            .fillX()
	            .minHeight(24)
	            .maxHeight(24)
	            .prefHeight(24);
	        body.row();
	        i++;
		}
		shop.add(pane).prefSize(451, Gdx.graphics.getHeight() / 2).fill().expand().padRight(3).padBottom(4);
		window.add(shop).prefSize(451, Gdx.graphics.getHeight() / 2);
		style.background = WINDOW_BG;
		window.setStyle(style);
		sync();
	}

	@Override
	public void update() {

	}
	
	private InputListener prompt(TItem item, int amt) {
		return new InputListener() {
			@Override
		    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
				long tot = item.is().getBaseBuyValuePerUnit() * amt;
				TUserInterface.mallocpop("Click to Purchase",
						item.is().getName()  + " @ " + currencyToString(item.is().getBaseBuyValuePerUnit()) + " x " + amt + " = " + currencyToString(tot));
			}
		    @Override
		    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
		    	TUserInterface.freepop();
		    }
		};
	}
	
	private ClickListener cashier(TItem item, int amt) {
		return new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				player.takeCurrency(item.is().getBaseBuyValuePerUnit() * amt);
				for(int i = 0; i < amt; i++) {
					if(!player.giveItem(item)) {
						// drop it because there's not enough room in the inventory...
						player.getWorld().addObject(new TItemDrop(player.getWorld(), player.getActualX(), player.getActualY(), item));
					}
				}
			}
		};
	}
	
	private VisTable getEntryFor(TItem item, boolean dark) {
		final VisTable ret = new VisTable();
		ret.setBackground((dark) ? DARK_GRAY_BG : GRAY_BG);
		final VisTable left = new VisTable();
		int dx = item.is().getIcon().getDataOffsetX();
		int dy = item.is().getIcon().getDataOffsetY();
		int dw = item.is().getIcon().getDataSelectionWidth();
		int dh = item.is().getIcon().getDataSelectionHeight();
		final VisImage icon = new VisImage(new TextureRegion(TGraphics.DATA,
					dx * TGraphics.DATA_WIDTH, 
					dy * TGraphics.DATA_HEIGHT, 
					dw * TGraphics.DATA_WIDTH, 
					dh * TGraphics.DATA_HEIGHT));
		left.left().add(icon).padLeft(10).center();
		final VisLabel label = new VisLabel("");
		label.setStyle(TUserInterface.LABEL_STYLE);
		label.setFontScale(0.16f);
		label.setText(item.is().getName());
		left.add(label).padLeft(8);
		final VisTable right = new VisTable();
		final VisLabel ppu = new VisLabel("");
		ppu.setStyle(TUserInterface.LABEL_STYLE);
		ppu.setFontScale(0.16f);
		ppu.setText("" + currencyToString(item.is().getBaseBuyValuePerUnit()));
		right.right().add(ppu).padRight(8);
		right.add(new TInformationWidget(item.is().getName(), item.is().getDescription())).padRight(8);
		buy.put(item, new VisTextButton[BULK_LEVELS]);
		for(int i = 0; i < BULK_LEVELS; i++) {
			int amt = 1 << i;
			final VisTextButton b = new VisTextButton("");
			b.getLabel().setTouchable(Touchable.disabled);
			b.addListener(prompt(item, amt));
			b.addListener(cashier(item, amt));
			b.getLabel().setStyle(TUserInterface.LABEL_STYLE);
			b.getLabel().setFontScale(0.16f);
			b.getLabel().setText("x" + amt);
			b.setFocusBorderEnabled(false);
			buy.get(item)[i] = b;
			right.add(b).padRight(8);
		}
		ret.add(left).expand().fill();
		ret.add(right).expand().fill();
		return ret;
	}
	
	/**
	 * Called when the player's funds have changed to ensure proper reflection of what is available
	 * using the bulk buy buttons.
	 */
	public void sync() {
		long playerCurrency = player.getNetWorth();
		for(Map.Entry<TItem, VisTextButton[]> entry : buy.entrySet()) {
			long ppu = entry.getKey().is().getBaseBuyValuePerUnit();
			for(int i = 0; i < BULK_LEVELS; i++) {
				if(playerCurrency - (ppu * (1 << i)) < 0) {
					entry.getValue()[i].setTouchable(Touchable.disabled);
					entry.getValue()[i].getLabel().setColor(Color.GRAY);
				} else {
					entry.getValue()[i].setTouchable(Touchable.enabled);
					entry.getValue()[i].getLabel().setColor(Color.WHITE);
				}
			}
		}
	}
	
	/**
	 * Converts a currency value to a renderable String.
	 */
	public static String currencyToString(long currency) {
		long vos = currency;
		long d   = vos / 100;
		long c   = vos % 100;
		return String.format("$%,d.%02d", d, c);
	}
}
