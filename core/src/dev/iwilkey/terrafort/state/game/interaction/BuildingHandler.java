package dev.iwilkey.terrafort.state.game.interaction;

import com.badlogic.gdx.math.Vector3;

import dev.iwilkey.terrafort.InputHandler;
import dev.iwilkey.terrafort.InputHandler.KeyBinding;
import dev.iwilkey.terrafort.state.game.SinglePlayerEngineState;
import dev.iwilkey.terrafort.state.game.SinglePlayerGameState;
import dev.iwilkey.terrafort.state.game.object.creatable.Creatables;
import dev.iwilkey.terrafort.state.game.object.decal.Hitpoint;
import dev.iwilkey.terrafort.state.game.object.truss.Cube;

public final class BuildingHandler {
	
	private final SinglePlayerEngineState engine;
	private final SinglePlayerGameState game;
	private Hitpoint posSel;
	
	public BuildingHandler(SinglePlayerEngineState engine, SinglePlayerGameState game) {
		this.engine = engine;
		this.game = game;
		long psi = engine.addGameObject(new Hitpoint(engine));
		posSel = (Hitpoint)engine.getGameObject(psi);
	}
	
	public void tick() {
		if(game.getTools().getCurrentCreatable() != Creatables.NONE && engine.getPlayer().isFocused()) {
			Vector3 hitpoint = engine.doRaycastForPoint(50.0f);
			if(hitpoint != null) {
				
				if(InputHandler.cursorJustDown(KeyBinding.getBinding("Action"))) {
					engine.addGameObject(new Cube(engine).setPosition(hitpoint));
				}
				
				posSel.setShouldRender(true);
				posSel.setPosition(hitpoint);
			} else posSel.setShouldRender(false);
		} else posSel.setShouldRender(false);
	}
	
}
