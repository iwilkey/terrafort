package dev.iwilkey.terrafort.ui.containers.interfaces;

import com.kotcrab.vis.ui.widget.VisTextButton;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.TEngine;
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
	
	private TPlayer  player;
	private TMinimap minimap;
	private float    time;
	
	public TMinimapInterface(TPlayer player) {
		this.player = player;
		minimap     = new TMinimap(player.getWorld());
		time        = 0.0f;
		minimap.update(0, 0, 4);
		setInternalPadding(8, 8, 8, 8);
		setExternalPadding(8, 0, 8, 0);
		setAnchor(TAnchor.TOP_RIGHT);
	}
	
	@Override
	public void pack(VisWindow window) {
		window.add(minimap).prefSize(128, 128);
		window.row();
		final VisTextButton button = new VisTextButton("+");
		button.getStyle().font = TUserInterface.getGameFont();
		button.getLabel().setFontScale(0.16f);
		window.add(button).pad(4);
		style.background = TDrawable.solidWithShadow(0x444444ff, 0x000000cc, (int)window.getWidth(), (int)window.getHeight(), 6, 6);
		window.setStyle(style);
	}
	
	@Override
	public void update() {
		time += TClock.dt();
		if(time > 0.3f) {
			minimap.update((int)player.getActualX(), (int)player.getActualY(), 4);
			
			System.out.println("Loaded chunks: " + TEngine.mChunksInMemory);
			System.out.println("Dormant chunks: " + TEngine.mChunksDormant);
			
			time = 0.0f;
		}
	}

}
