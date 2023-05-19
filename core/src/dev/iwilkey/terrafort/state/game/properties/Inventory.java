package dev.iwilkey.terrafort.state.game.properties;

import dev.iwilkey.terrafort.gfx.Alignment;
import dev.iwilkey.terrafort.gfx.Anchor;
import dev.iwilkey.terrafort.state.game.SinglePlayerGameState;
import dev.iwilkey.terrafort.state.game.object.resource.Resources;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public final class Inventory extends SinglePlayerGameProperty {

	public Inventory(SinglePlayerGameState game) {
		super(game);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render() {
		ImGui.begin("Resources", IMGUI_WINDOW_FLAGS);
		
		for(Resources resource : Resources.values()) {
			ImGui.text(String.format(resource.getName() + " x %d", 10));
		}
		
		Alignment.alignGui(Anchor.TOP_LEFT, 5.0f);
		ImGui.end();
	}

}
