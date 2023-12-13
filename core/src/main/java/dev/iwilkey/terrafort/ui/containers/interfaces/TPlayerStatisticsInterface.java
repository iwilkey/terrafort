package dev.iwilkey.terrafort.ui.containers.interfaces;

import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar.ProgressBarStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.kotcrab.vis.ui.widget.VisLabel;
import com.kotcrab.vis.ui.widget.VisProgressBar;
import com.kotcrab.vis.ui.widget.VisTable;
import com.kotcrab.vis.ui.widget.VisWindow;

import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.ui.TAnchor;
import dev.iwilkey.terrafort.ui.TDrawable;
import dev.iwilkey.terrafort.ui.containers.TContainer;

/**
 * Renders a given {@link TPlayer}s statistics during their lifetime. The statistics could include health, hunger, thirst, and energy.
 * @author Ian Wilkey (iwilkey)
 */
public final class TPlayerStatisticsInterface extends TContainer {
	
	public static final int BACK_COLOR   = 0x333333ff;
	public static final int HEALTH_COLOR = 0xff0000ff;
	public static final int HUNGER_COLOR = 0xfec293ff;
	public static final int THIRST_COLOR = 0x84d3e9ff;
	public static final int ENERGY_COLOR = 0xf2ce30ff;
	public static final int THICKNESS_PX = 8;

	private final TPlayer        player;
	private final VisProgressBar health;
	private final VisLabel       healthLabel;
	private final VisProgressBar hunger;
	private final VisLabel       hungerLabel;
	private final VisProgressBar thirst;
	private final VisLabel       thirstLabel;
	private final VisProgressBar energy;
	private final VisLabel       energyLabel;
	
	public TPlayerStatisticsInterface(TPlayer player) {
		this.player = player;
		setAnchor(TAnchor.BOTTOM_RIGHT);
		setInternalPadding(8, 8, 8, 8);
		setExternalPadding(0, 8, 8, 0);
		final Drawable back = TDrawable.solid(0x333333ff, 1, THICKNESS_PX);
		final ProgressBarStyle healthStyle = new ProgressBarStyle();
		healthStyle.background             = back;
		healthStyle.knob                   = TDrawable.solid(HEALTH_COLOR, 1, THICKNESS_PX / 2);
		healthStyle.knobBefore             = TDrawable.solid(HEALTH_COLOR, 1, THICKNESS_PX / 4);
		final ProgressBarStyle hungerStyle = new ProgressBarStyle();
		hungerStyle.background             = back;
		hungerStyle.knob                   = TDrawable.solid(HUNGER_COLOR, 1, THICKNESS_PX / 2);
		hungerStyle.knobBefore             = TDrawable.solid(HUNGER_COLOR, 1, THICKNESS_PX / 4);
		final ProgressBarStyle thirstStyle = new ProgressBarStyle();
		thirstStyle.background             = back;
		thirstStyle.knob                   = TDrawable.solid(THIRST_COLOR, 1, THICKNESS_PX / 2);
		thirstStyle.knobBefore             = TDrawable.solid(THIRST_COLOR, 1, THICKNESS_PX / 4);
		final ProgressBarStyle energyStyle = new ProgressBarStyle();
		energyStyle.background             = back;
		energyStyle.knob                   = TDrawable.solid(ENERGY_COLOR, 1, THICKNESS_PX / 2);
		energyStyle.knobBefore             = TDrawable.solid(ENERGY_COLOR, 1, THICKNESS_PX / 4);
		health = new VisProgressBar(0, TPlayer.PLAYER_MAX_HP, 1 / (float)TPlayer.PLAYER_MAX_HP,     false, healthStyle);
		hunger = new VisProgressBar(0, TPlayer.PLAYER_MAX_HUNGER, 1 / (float)TPlayer.PLAYER_MAX_HUNGER, false, hungerStyle);
		thirst = new VisProgressBar(0, TPlayer.PLAYER_MAX_THIRST, 1 / (float)TPlayer.PLAYER_MAX_THIRST, false, thirstStyle);
		energy = new VisProgressBar(0, TPlayer.PLAYER_MAX_ENERGY, 1 / (float)TPlayer.PLAYER_MAX_ENERGY, false, energyStyle);
		healthLabel = new VisLabel();
		healthLabel.setFontScale(0.16f);
		hungerLabel = new VisLabel();
		hungerLabel.setFontScale(0.16f);
		thirstLabel = new VisLabel();
		thirstLabel.setFontScale(0.16f);
		energyLabel = new VisLabel();
		energyLabel.setFontScale(0.16f);
	}
	
	@Override
	public void pack(VisWindow window) {
		final VisTable internal = new VisTable();
		internal.add(health).fill().expand();
		internal.add(healthLabel).padLeft(8);
		internal.row();
		internal.add(hunger).fill().expand();
		internal.add(hungerLabel).padLeft(8);
		internal.row();
		internal.add(thirst).fill().expand();
		internal.add(thirstLabel).padLeft(8);
		internal.row();
		internal.add(energy).fill().expand();
		internal.add(energyLabel).padLeft(8);
		window.add(internal).prefSize(256, 64).expand().fill();
	}

	@Override
	public void update() {
		health.setValue(player.getCurrentHP());
		healthLabel.setText(String.format("%d / %d", player.getCurrentHP(), TPlayer.PLAYER_MAX_HP));
		hunger.setValue(player.getHungerPoints());
		hungerLabel.setText(String.format("%d / %d", player.getHungerPoints(), TPlayer.PLAYER_MAX_HUNGER));
		thirst.setValue(player.getThirstPoints());
		thirstLabel.setText(String.format("%d / %d", player.getThirstPoints(), TPlayer.PLAYER_MAX_THIRST));
		energy.setValue(player.getEnergyPoints());
		energyLabel.setText(String.format("%d / %d", player.getEnergyPoints(), TPlayer.PLAYER_MAX_ENERGY));
	}

}
