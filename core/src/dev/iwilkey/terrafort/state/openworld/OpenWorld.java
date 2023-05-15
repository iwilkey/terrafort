package dev.iwilkey.terrafort.state.openworld;

import com.badlogic.gdx.Input.Keys;

import dev.iwilkey.terrafort.InputHandler;
import dev.iwilkey.terrafort.TerrafortEngine;
import dev.iwilkey.terrafort.asset.AssetBuffer;
import dev.iwilkey.terrafort.asset.AssetType;
import dev.iwilkey.terrafort.asset.TerrafortAsset;
import dev.iwilkey.terrafort.gfx.Alignment;
import dev.iwilkey.terrafort.gfx.Anchor;
import dev.iwilkey.terrafort.state.State;

import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public class OpenWorld extends State {
	
	private WorldEnvironment env;

	public OpenWorld(TerrafortEngine engine) {
		super(engine, new AssetBuffer(
				new TerrafortAsset("cube", "vox/cube/cube.vox.obj", AssetType.MODEL)
			));
	}

	@Override
	public void begin() {
		// Initialize the rendering environment.
		env = new WorldEnvironment();
		environment3 = env;
		
		// Test 3D object.
		// long objid = addGameObject(new GameObject3(this, "test"));
	}

	@Override
	public void tick() {
		if(InputHandler.keyJustDown(Keys.A))
			System.out.println("A down");
		if(InputHandler.keyJustUp(Keys.A))
			System.out.println("A up");
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
