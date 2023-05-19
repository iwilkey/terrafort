package dev.iwilkey.terrafort.state.game.properties;

import dev.iwilkey.terrafort.gfx.Alignment;
import dev.iwilkey.terrafort.gfx.Anchor;
import dev.iwilkey.terrafort.state.game.SinglePlayerGameState;
import dev.iwilkey.terrafort.state.game.object.creatable.Creatables;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public final class Blueprints extends SinglePlayerGameProperty {

	public Blueprints(SinglePlayerGameState game) {
		super(game);
	}
	
	@Override
	public void tick() {
		
	}
	
	@Override
	public void render() {
		ImGui.setNextWindowSize(-1, 300);
		ImGui.begin("Blueprints", ImGuiWindowFlags.NoMove);
		if(ImGui.treeNode("Structural              ")) {
			for(Creatables creatable : Creatables.values()) {
				if(creatable.getType() == "Structural") {
					if(ImGui.button(creatable.getName())) {
						getGame().getTools().setCurrentCreatable(creatable);
					}
				}
			}
			ImGui.treePop();
		}
		
		if(ImGui.treeNode("Industrial              ")) {
			ImGui.treePop();
		}
		
		if(ImGui.treeNode("Turrets              ")) {
			ImGui.treePop();
		}
		
		if(ImGui.button("None")) {
			getGame().getTools().setCurrentCreatable(Creatables.NONE);
		}
		Alignment.alignGui(Anchor.BOTTOM_RIGHT, 5.0f);
		ImGui.end();
	}

}
