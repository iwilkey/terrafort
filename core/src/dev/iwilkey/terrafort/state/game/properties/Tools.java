package dev.iwilkey.terrafort.state.game.properties;

import dev.iwilkey.terrafort.gfx.Alignment;
import dev.iwilkey.terrafort.gfx.Anchor;
import dev.iwilkey.terrafort.state.game.SinglePlayerGameState;
import dev.iwilkey.terrafort.state.game.object.creatable.Creatables;
import dev.iwilkey.terrafort.state.game.object.resource.Resource;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiWindowFlags;

public final class Tools extends SinglePlayerGameProperty {
		
	private Creatables currentCreatable = Creatables.NONE;
	
	public Tools(SinglePlayerGameState game) {
		super(game);
	}

	@Override
	public void tick() {
		
	}

	@Override
	public void render() {
		ImGui.begin("Tools", IMGUI_WINDOW_FLAGS | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoTitleBar);
		ImGui.text("Current Tool: " + currentCreatable.getName());
		if(currentCreatable != Creatables.NONE) {
			ImGui.text("Can create?");
			ImGui.sameLine();
			ImGui.pushStyleColor(ImGuiCol.Text, 0.0f, 1.0f, 0.0f, 1.0f);
			ImGui.text("YES");
			ImGui.popStyleColor();
			ImGui.text("Resources Required");
			for(Resource r : currentCreatable.getRecipe()) {
				ImGui.text(String.format("\t" + r.getResource().getName() + " x %d", r.getCount()));
			}
		}
		Alignment.alignGui(Anchor.BOTTOM_CENTER, 5.0f);
		ImGui.end();
	}
	
	public Creatables getCurrentCreatable() {
		return currentCreatable;
	}
	
	public void setCurrentCreatable(Creatables creatable) {
		this.currentCreatable = creatable;
	}

}
