package dev.iwilkey.terrafort.state.game;

import dev.iwilkey.terrafort.state.game.properties.Blueprints;
import dev.iwilkey.terrafort.state.game.properties.Tools;
import dev.iwilkey.terrafort.state.game.properties.Intelligence;
import dev.iwilkey.terrafort.state.game.properties.Inventory;
import dev.iwilkey.terrafort.state.game.properties.PlanetStatistics;

public final class SinglePlayerGameState {
	
	private final SinglePlayerEngineState state;
	private final PlanetStatistics planetStatistics;
	private final Intelligence intelligence;
	private final Blueprints defenseControl;
	private final Tools tools;
	private final Inventory inventory;
	
	public SinglePlayerGameState(SinglePlayerEngineState state) {
		this.state = state;
		planetStatistics = new PlanetStatistics(this);
		intelligence = new Intelligence(this);
		defenseControl = new Blueprints(this);
		tools = new Tools(this);
		inventory = new Inventory(this);
	}
	
	public void tick() {
		planetStatistics.tick();
		intelligence.tick();
		defenseControl.tick();
		tools.tick();
		inventory.tick();
	}
	
	public void render() {
		planetStatistics.render();	
		intelligence.render();
		defenseControl.render();
		tools.render();
		inventory.render();
	}
	
	/**
	 * Simulation methods.
	 */
	
	public void beginThreat(long level) {
		
	}
	
	/**
	 * Module getters.
	 */
	
	public SinglePlayerEngineState getSinglePlayerEngineState() {
		return state;
	}
	
	public PlanetStatistics getPlanetStatistics() {
		return planetStatistics;
	}
	
	public Intelligence getIntelligence() {
		return intelligence;
	}
	
	public Blueprints getDefenseControl() {
		return defenseControl;
	}
	
	public Tools getTools() {
		return tools;
	}
	
	public Inventory getInventory() {
		return inventory;
	}
	
}
