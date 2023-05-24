package dev.iwilkey.terrafort.state.game;

import dev.iwilkey.terrafort.TerrafortEngine;
import dev.iwilkey.terrafort.state.State;
import dev.iwilkey.terrafort.state.game.gfx.Space;
import dev.iwilkey.terrafort.state.game.interaction.BuildingHandler;
import dev.iwilkey.terrafort.state.game.object.Core;
import dev.iwilkey.terrafort.state.game.object.Crosshair;

public class SinglePlayerEngineState extends State {
	
	private Player player;
	private Space env;
	private SinglePlayerGameState game;
	private BuildingHandler builder;
	private long crosshair;
	private long core;

	public SinglePlayerEngineState(TerrafortEngine engine) {
		super(engine);
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public long getCore() {
		return core;
	}
	
	public Space getSpatialEnvironment() {
		return env;
	}
	
	@Override
	public void begin() {
		// Create game properties. 
		game = new SinglePlayerGameState(this);
		builder = new BuildingHandler(this, game);
		
		// Create the spatial environment.
		env = new Space(this, game);
		environment3 = env;
		
		// Create constant GameObjects.
		crosshair = addGameObject(new Crosshair(this).setShouldRender(false));
		core = addGameObject(new Core(this).setPosition(0, 0, 0));
		
		// Add the player to the world.
		player = new Player(this, crosshair);
		camera3.setController(player);
	}

	@Override
	public void tick() {
		env.tick();
		game.tick();
		builder.tick();
	}

	@Override
	public void gui() {
		game.render();
	}

	@Override
	public void end() {}

}
