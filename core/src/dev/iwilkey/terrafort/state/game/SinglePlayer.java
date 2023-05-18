package dev.iwilkey.terrafort.state.game;

import dev.iwilkey.terrafort.TerrafortEngine;
import dev.iwilkey.terrafort.asset.AssetBuffer;
import dev.iwilkey.terrafort.asset.AssetType;
import dev.iwilkey.terrafort.asset.TerrafortAsset;
import dev.iwilkey.terrafort.state.State;
import dev.iwilkey.terrafort.state.game.gfx.WorldEnvironment;
import dev.iwilkey.terrafort.state.game.object.Core;
import dev.iwilkey.terrafort.state.game.object.Crosshair;

public class SinglePlayer extends State {
	
	private Player player;
	private WorldEnvironment env;
	
	// GUI
	private ControlPanel panel;
	
	// GameObjects.
	private long crosshair;
	private long core;

	public SinglePlayer(TerrafortEngine engine) {
		super(engine, new AssetBuffer(
				new TerrafortAsset("vox/core/core.vox.obj", AssetType.MODEL),
				new TerrafortAsset("vox/clouds/clouds.vox.obj", AssetType.MODEL),
				new TerrafortAsset("texture/crosshair.png", AssetType.TEXTURE)
			));
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public long getCore() {
		return core;
	}
	
	public ControlPanel getPanel() {
		return panel;
	}
	
	@Override
	public void begin() {
		// Init 3D env.
		env = new WorldEnvironment();
		environment3 = env;
		crosshair = addGameObject(new Crosshair(this).setShouldRender(false));
		player = new Player(this, crosshair);
		camera3.setController(player);
		// GUI
		panel = new ControlPanel(this);
		// Create core.
		core = addGameObject(new Core(this).setPosition(0, 0, 0));
	}

	@Override
	public void tick() {}

	@Override
	public void gui() {
		panel.render();
	}

	@Override
	public void end() {}

}
