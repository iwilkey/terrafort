package dev.iwilkey.terrafort.state.game.properties;

import com.badlogic.gdx.Gdx;

import dev.iwilkey.terrafort.gfx.Alignment;
import dev.iwilkey.terrafort.gfx.Anchor;
import dev.iwilkey.terrafort.state.game.SinglePlayerGameState;
import imgui.ImGui;

public final class PlanetStatistics extends SinglePlayerGameProperty {

	public static final float SECONDS_PER_DAY = 10.0f;
	
	private long n0 = 100L;
	private long nt = n0;
	private long k = (long)Math.pow(10, 5);
	private float r = 0.0105f;
	private float d = 0.008f;
	private long day = 1L;
	private long ndtd = 0L;
	private long idtd = 0L;

	public PlanetStatistics(SinglePlayerGameState game) {
		super(game);
	}
	
	
	private float dayTick = 0.0f;
	@Override
	public void tick() {
		// Time.
		dayTick += Gdx.graphics.getDeltaTime();
		if(dayTick >= SECONDS_PER_DAY) {
			day++;
			ndtd += getNaturalDeathsPerDay();
			dayTick = 0;
		}
		nt = getPopulationAt(day);
	}
	
	@Override
	public void render() {
		ImGui.begin("Planet Statistics", IMGUI_WINDOW_FLAGS);
		ImGui.text(String.format("Day: %,d", getDay()));
		ImGui.text(String.format("Population: %,d", getCurrentPopulation()));
		ImGui.text(String.format("Max Population: %,d", getMaxPopulation()));
		ImGui.text(String.format("Natural Deaths to Date: %,d", getNaturalDeathsToDate()));
		ImGui.text(String.format("Innocent Deaths to Date: %,d", getInnocentDeathsToDate()));
		ImGui.text(String.format("Life Capacity: %.2f%%", getLife()));
		Alignment.alignGui(Anchor.BOTTOM_LEFT, 5.0f);
		ImGui.end();
	}
	
	public double getLife() {
		return ((double)nt / k) * 100.0f;
	}
	
	public long getPopulationAt(long time) {
		double population = k / (1 + ((k - n0) / n0) * Math.exp((-r * time)));
		population -= idtd;
		return (long)population;
	}
	
	public long getNaturalDeathsPerDay() {
		return (long)(d * (float)nt);
	}
	
	public long getNaturalDeathsToDate() {
		return ndtd;
	}
	
	public long getInnocentDeathsToDate() {
		return idtd;
	}
	
	public long getCurrentPopulation() {
		return nt;
	}
	
	public long getMaxPopulation() {
		return k;
	}
	
	public float getPerCapitaDeathRate() {
		return d;
	}
	
	public long getDay() {
		return day;
	}

}
