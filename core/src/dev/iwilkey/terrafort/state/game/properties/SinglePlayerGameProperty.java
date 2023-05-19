package dev.iwilkey.terrafort.state.game.properties;

import dev.iwilkey.terrafort.state.game.SinglePlayerGameState;
import imgui.flag.ImGuiWindowFlags;

public abstract class SinglePlayerGameProperty {
	
	protected static final int IMGUI_WINDOW_FLAGS =  
			 ImGuiWindowFlags.NoMove | 
			 ImGuiWindowFlags.AlwaysAutoResize;
	
	private final SinglePlayerGameState game;
	
	public SinglePlayerGameProperty(SinglePlayerGameState game) {
		this.game = game;
	}
	
	public abstract void tick();
	public abstract void render();
	
	public final SinglePlayerGameState getGame() {
		return game;
	}
	
}
