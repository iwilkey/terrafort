package dev.iwilkey.terrafort.ui.containers.interfaces;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.ui.TAnchor;
import dev.iwilkey.terrafort.ui.TDrawable;
import dev.iwilkey.terrafort.ui.TUserInterface;
import dev.iwilkey.terrafort.ui.containers.TContainer;
import dev.iwilkey.terrafort.ui.widgets.TMinimap;

/**
 * An interface to render a {@link TMinimap} widget.
 * @author Ian Wilkey (iwilkey)
 */
public final class TMinimapInterface extends TContainer {
	
	public static final float UPDATE_TIME = 0.3f;
	public static final int   MIN_SCALE   = 2;
	public static final int   MAX_SCALE   = 16;
	
	private TPlayer  player;
	private TMinimap minimap;
	private VisLabel scaleLabel;
	private int      scale;
	private float    time;
	
	public TMinimapInterface(TPlayer player) {
		this.player = player;
		minimap     = new TMinimap(player.getWorld());
		time        = 0.0f;
		scale       = 4;
		minimap.update(0, 0, 4);
		setInternalPadding(8, 8, 8, 8);
		setExternalPadding(8, 0, 8, 0);
		setAnchor(TAnchor.TOP_RIGHT);
	}
	
	@Override
	public void pack(VisWindow window) {
		window.add(minimap).prefSize(128 * 1.5f, 128 * 1.5f);
		window.row();
		
		scaleLabel = new VisLabel((scale * scale) + " tle/px");
		scaleLabel.getStyle().font = TUserInterface.getGameFont();
		scaleLabel.setFontScale(0.13f);
		final VisTable controls = new VisTable();
		final VisTextButton plus = new VisTextButton("+");
		plus.addListener(new ClickListener() {
			@Override
		    public void clicked(InputEvent event, float x, float y) {
				if(scale - 1 >= MIN_SCALE) {
					scale--;
					scaleLabel.setText((scale * scale) + " tle/px");
					time = UPDATE_TIME;
				}
		    }
		});
		plus.getStyle().font = TUserInterface.getGameFont();
		plus.setFocusBorderEnabled(false);
		plus.getLabel().setFontScale(0.16f);
		controls.add(plus).prefSize(16).pad(2);
		final VisTextButton minus = new VisTextButton("-");
		minus.addListener(new ClickListener() {
			@Override
		    public void clicked(InputEvent event, float x, float y) {
				if(scale + 1 <= MAX_SCALE) {
					scale++;
					scaleLabel.setText((scale * scale) + " tle/px");
					time = UPDATE_TIME;
				}
		    }
		});
		minus.setFocusBorderEnabled(false);
		minus.getLabel().setFontScale(0.16f);
		controls.add(minus).prefSize(16).pad(2);
		controls.add(scaleLabel).pad(4);
		
		window.add(controls).expand().fill();
		
		// add drop shadow
		style.background = TDrawable.solidWithShadow(0x444444ff, 0x000000cc, (int)window.getWidth(), (int)window.getHeight(), 6, 6);
		window.setStyle(style);
	}
	
	@Override
	public void update() {
		time += TClock.dt();
		if(time > UPDATE_TIME) {
			minimap.update((int)player.getActualX(), (int)player.getActualY(), scale);
			time = 0.0f;
		}
	}

}
