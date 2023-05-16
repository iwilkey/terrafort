package dev.iwilkey.terrafort.state.openworld;

import dev.iwilkey.terrafort.TerrafortEngine;
import dev.iwilkey.terrafort.asset.AssetBuffer;
import dev.iwilkey.terrafort.asset.AssetType;
import dev.iwilkey.terrafort.asset.TerrafortAsset;
import dev.iwilkey.terrafort.gfx.Alignment;
import dev.iwilkey.terrafort.gfx.Anchor;
import dev.iwilkey.terrafort.state.State;
import dev.iwilkey.terrafort.state.openworld.gfx.WorldEnvironment;
import dev.iwilkey.terrafort.state.openworld.object.Cube;
import dev.iwilkey.terrafort.state.openworld.object.Player;
import dev.iwilkey.terrafort.state.openworld.object.Test25;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class OpenWorld extends State {
	
	private Player player;
	private WorldEnvironment env;

	public OpenWorld(TerrafortEngine engine) {
		super(engine, new AssetBuffer(
				new TerrafortAsset("cube", "vox/cube/cube.vox.obj", AssetType.MODEL),
				new TerrafortAsset("crosshair", "texture/crosshair.png", AssetType.TEXTURE)
			));
	}
	
	long cube;
	long test25;
	@Override
	public void begin() {
		// Initialize the rendering environment.
		env = new WorldEnvironment();
		environment3 = env;
		// Set up the player.
		player = new Player(this);
		// Set up camera controller.
		camera3.setController(player);
		// Test 3D object.
		cube = addGameObject(new Cube(this));
		// Test 25.
		test25 = addGameObject(new Test25(this, true));
	}

	@Override
	public void tick() {

	}

	@Override
	public void gui() {
		ImGui.begin("Test", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.AlwaysAutoResize);
		ImGui.text("Hello, world!");
		Alignment.alignGui(Anchor.BOTTOM_LEFT, 5.0f);
		ImGui.end();
	}

	@Override
	public void end() {
		// TODO Auto-generated method stub
		
	}

}
