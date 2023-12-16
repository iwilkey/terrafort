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
import dev.iwilkey.terrafort.ui.widgets.TInformationWidget;

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
	private final VisProgressBar energy;
	private final VisLabel       energyLabel;
	
	public TPlayerStatisticsInterface(TPlayer player) {
		this.player = player;
		setAnchor(TAnchor.BOTTOM_RIGHT);
		setInternalPadding(8, 8, 8, 8);
		setExternalPadding(0, 8, 8, 0);
		final Drawable back = TDrawable.solid(BACK_COLOR, 1, THICKNESS_PX / 2);
		final ProgressBarStyle healthStyle = new ProgressBarStyle();
		healthStyle.background             = back;
		healthStyle.knob                   = TDrawable.solid(HEALTH_COLOR, 1, THICKNESS_PX / 2);
		healthStyle.knobBefore             = TDrawable.solid(HEALTH_COLOR, 1, THICKNESS_PX / 4);
		final ProgressBarStyle hungerStyle = new ProgressBarStyle();
		hungerStyle.background             = back;
		hungerStyle.knob                   = TDrawable.solid(HUNGER_COLOR, 1, THICKNESS_PX / 2);
		hungerStyle.knobBefore             = TDrawable.solid(HUNGER_COLOR, 1, THICKNESS_PX / 4);
		final ProgressBarStyle energyStyle = new ProgressBarStyle();
		energyStyle.background             = back;
		energyStyle.knob                   = TDrawable.solid(ENERGY_COLOR, 1, THICKNESS_PX / 2);
		energyStyle.knobBefore             = TDrawable.solid(ENERGY_COLOR, 1, THICKNESS_PX / 4);
		health = new VisProgressBar(0, TPlayer.PLAYER_MAX_HP, 1 / (float)TPlayer.PLAYER_MAX_HP,     false, healthStyle);
		hunger = new VisProgressBar(0, TPlayer.PLAYER_MAX_HUNGER, 1 / (float)TPlayer.PLAYER_MAX_HUNGER, false, hungerStyle);
		energy = new VisProgressBar(0, TPlayer.PLAYER_MAX_ENERGY, 1 / (float)TPlayer.PLAYER_MAX_ENERGY, false, energyStyle);
		healthLabel = new VisLabel();
		healthLabel.setFontScale(0.16f);
		hungerLabel = new VisLabel();
		hungerLabel.setFontScale(0.16f);
		energyLabel = new VisLabel();
		energyLabel.setFontScale(0.16f);
	}
	
	@Override
	public void pack(VisWindow window) {
		final VisTable internal = new VisTable();
		final VisTable healthTable = new VisTable();
		healthTable.add(new TInformationWidget("[RED]Health[]", 
				"This bar represents your player's \ncurrent [RED]health[].\n\n"
				+ "If it reaches zero, your player will\ndie and this game of [YELLOW]Terrafort[]\nwill end.")).padRight(8);
		healthTable.add(health).fill().expand();
		healthTable.add(healthLabel).padLeft(8);
		internal.add(healthTable);
		internal.row();
		final VisTable hungerTable = new VisTable();
		hungerTable.add(new TInformationWidget("[ORANGE]Nutrition[]", 
				"This bar represents your player's \ncurrent [ORANGE]nutrition[].\n\n"
				+ "The more [ORANGE]nutrition[] you have,\nthe faster your [YELLOW]energy[] replenishes.\n\n"
				+ "If your [ORANGE]nutrition[] drops to zero, you\nwill begin to periodically lose [RED]health[]\nuntil you eat.")).padRight(8);
		hungerTable.add(hunger).fill().expand();
		hungerTable.add(hungerLabel).padLeft(8);
		internal.add(hungerTable);
		internal.row();
		final VisTable energyTable = new VisTable();
		energyTable.add(new TInformationWidget("[YELLOW]Energy[]", 
				"This bar represents your player's\ncurrent [YELLOW]energy[].\n\n"
				+ "[YELLOW]Energy[] allows your player to complete\ntasks, run, and fight.\n\n"
				+ "[YELLOW]Energy[] naturally replenishes as you\nrest.")).padRight(8);
		energyTable.add(energy).fill().expand();
		energyTable.add(energyLabel).padLeft(8);
		internal.add(energyTable);
		window.add(internal).prefSize(256, 64).expand().fill();
		style.background = TDrawable.solidWithShadow(0x444444ff, 0x000000cc, 256, 64, 4, 4);
		window.setStyle(style);
	}

	@Override
	public void update() {
		health.setValue(player.getCurrentHP());
		healthLabel.setText(String.format("%d / %d", player.getCurrentHP(), TPlayer.PLAYER_MAX_HP));
		hunger.setValue(player.getHungerPoints());
		hungerLabel.setText(String.format("%d / %d", player.getHungerPoints(), TPlayer.PLAYER_MAX_HUNGER));
		energy.setValue(player.getEnergyPoints());
		energyLabel.setText(String.format("%d / %d", player.getEnergyPoints(), TPlayer.PLAYER_MAX_ENERGY));
	}

}
