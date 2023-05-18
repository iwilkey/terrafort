package dev.iwilkey.terrafort.state.game;

import dev.iwilkey.terrafort.gfx.Alignment;
import dev.iwilkey.terrafort.gfx.Anchor;
import dev.iwilkey.terrafort.state.State;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;

public final class ControlPanel {
	
	private static final int FLAGS = ImGuiWindowFlags.NoCollapse | 
									 ImGuiWindowFlags.NoMove | 
									 ImGuiWindowFlags.AlwaysAutoResize;
	
	private State state;
	
	public ControlPanel(State state) {
		this.state = state;
	}
	
	private void info() {
		ImGui.begin("Planet Statistics", FLAGS);
		ImGui.text("Population: 102,203 / 200,000");
		ImGui.text("Life: 68.12%");
		ImGui.text("Defense Funding: $102.30");
		Alignment.alignGui(Anchor.BOTTOM_CENTER, 5.0f);
		ImGui.end();
	}
	
	public void render() {
		info();
	}
	
}
