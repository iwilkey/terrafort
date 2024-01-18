package dev.iwilkey.terrafort.gui.interfaces;

import com.kotcrab.vis.ui.widget.VisTable;

import dev.iwilkey.terrafort.gui.TAnchor;
import dev.iwilkey.terrafort.gui.container.TStaticContainer;
import dev.iwilkey.terrafort.gui.widgets.TTextWidget;
import dev.iwilkey.terrafort.math.TInterpolator;
import dev.iwilkey.terrafort.obj.mob.TPlayer;

/**
 * Updates the player on the current state of the game. Includes their health, funds, time of day, etc.
 * @author Ian Wilkey (iwilkey)
 *
 */
public final class TGameStateInterface extends TStaticContainer {
	
	/**
	 * The current monitoree.
	 */
	private final TPlayer player;
	
	/**
	 * Renders the amount of funds the monitored player has.
	 */
	private TTextWidget funds;
	
	/**
	 * Gives inertia to rendered player funds.
	 */
	private TInterpolator playerFunds;
	
	
	/**
	 * Creates a new game state interface. Player must be referenced.
	 */
	public TGameStateInterface(Object... objReference) {
		super(objReference);
		if(!(objReference[0] instanceof TPlayer) || objReference.length != 1)
			throw new IllegalArgumentException("[Terrafort Game Engine] First and only argument to the TGameStateInterface must be the TPlayer to monitor.");
		player = (TPlayer)objReference[0];
		playerFunds = new TInterpolator(player.getFunds());
		playerFunds.setSpeed(32.0f);
		pack(internal);
	}
	
	@Override
	public void pack(VisTable internal, Object... objReference) {
		setAnchor(TAnchor.BOTTOM_LEFT);
		setExternalPadding(0, 4, 0, 4);
		setInternalPadding(8, 8, 16, 16);
		funds = new TTextWidget("[GREEN]1,203[] Funds");
		funds.setFontScale(0.18f);
		internal.add(funds);
		window.add(internal);
	}
	
	@Override
	public void update(float dt) {
		playerFunds.set(player.getFunds());
		playerFunds.update(dt);
        final String pf = player.fundsToString((long)playerFunds.get());
		funds.setText("[GREEN]" + pf + "[] Funds");
	}

}
