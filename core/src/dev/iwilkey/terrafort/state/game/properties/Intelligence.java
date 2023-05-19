package dev.iwilkey.terrafort.state.game.properties;

import com.badlogic.gdx.Gdx;

import dev.iwilkey.terrafort.gfx.Alignment;
import dev.iwilkey.terrafort.gfx.Anchor;
import dev.iwilkey.terrafort.state.game.SinglePlayerGameState;
import imgui.ImGui;
import imgui.flag.ImGuiCol;

public final class Intelligence extends SinglePlayerGameProperty {
	
	private long level = 1;
	private long baseGivenTime = (60 * 3);
	private long linearAddTime = 60L;
	private long secondsUntilThreat = baseGivenTime;
	
	public Intelligence(SinglePlayerGameState game) {
		super(game);
	}
	
	private float threatTick = 0.0f;
	@Override
	public void tick() {
		threatTick += Gdx.graphics.getDeltaTime();
		if(threatTick >= 1.0f) {
			secondsUntilThreat--;
			if(secondsUntilThreat <= 0) {
				getGame().beginThreat(level);
				level++;
				secondsUntilThreat = baseGivenTime + (level * linearAddTime);
			}
			threatTick = 0.0f;
		}
	}
	
	@Override
	public void render() {
		ImGui.begin("Threat Intelligence", IMGUI_WINDOW_FLAGS);
		ImGui.text("Status:");
		ImGui.sameLine();
		// If the wave is not done...
		// ImGui.pushStyleColor(ImGuiCol.Text, 1.0f, 0.0f, 0.0f, 1.0f);
		// ImGui.text("IMMINENT");
		// If the wave is done.
		ImGui.pushStyleColor(ImGuiCol.Text, 0.0f, 1.0f, 0.0f, 1.0f);
		ImGui.text("NEUTRALIZED");
		ImGui.popStyleColor();
		ImGui.text(String.format("Threat level: %d", level));
		ImGui.text(String.format("Incoming in %d:" + getSecTillThreat() + "     ", getMinTillThreat()));
		Alignment.alignGui(Anchor.TOP_RIGHT, 5.0f);
		ImGui.end();
	}

	public long getMinTillThreat() {
		return secondsUntilThreat / 60;
	}
	
	public String getSecTillThreat() {
		long sec = secondsUntilThreat % 60;
		if(sec <= 9) 
			return '0' + Long.toString(sec);
		return Long.toString(sec);
	}

}
