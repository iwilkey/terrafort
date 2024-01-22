package dev.iwilkey.terrafort.state;

import java.util.concurrent.ThreadLocalRandom;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.TState;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.gui.interfaces.TSettingsInterface;
import dev.iwilkey.terrafort.gui.interfaces.TGameStateInterface;
import dev.iwilkey.terrafort.gui.interfaces.TKnowledgeBarInterface;
import dev.iwilkey.terrafort.gui.interfaces.TKnowledgeTreeInterface;
import dev.iwilkey.terrafort.persistent.TPersistent;
import dev.iwilkey.terrafort.world.TWorld;

/**
 * A single-player game of Terrafort.
 * @author Ian Wilkey (iwilkey)
 */
public final class TSinglePlayerWorld implements TState {
	
	private static TWorld world = null;
	
	@Override
	public void start() {
		TGraphics.setCameraSpeedToTarget(4.0f);
		// Load world from persistent memory given name...
		if(!TPersistent.pathExists("world/world.dat")) {
			world = new TWorld("world", ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE - 1));
		} else {
			// loading from persistent memory...
			world = (TWorld)TPersistent.load("world/world.dat");
			world.loadFromPersistent();
		}
		world.getClient().knowledgeBar = new TKnowledgeBarInterface();
		TUserInterface.mAllocContainer(world.getClient().knowledgeBar);
		world.getClient().gameState = new TGameStateInterface(world.getClient());
		TUserInterface.mAllocContainer(world.getClient().gameState);
		world.getClient().settings = new TSettingsInterface();
		TUserInterface.mAllocContainer(world.getClient().settings);
	}
	
	@Override
	public void render(float dt) {
		if(TInput.techTree && world.getClient().settings.getState()) {
			if(world.getClient().knowledgeTree == null) {
				world.getClient().knowledgeTree = new TKnowledgeTreeInterface();
				TUserInterface.mAllocContainer(world.getClient().knowledgeTree);
				TGraphics.requestBlurState(true, 1.0f);
			} else {
				TUserInterface.mFreeContainer(world.getClient().knowledgeTree);
				world.getClient().knowledgeTree = null;
				TUserInterface.mFreePrompt();
				TGraphics.requestBlurState(false, 1.0f);
			}
			TInput.techTree = false;
		}
		world.render(dt);
	}

	@Override
	public void stop() {
		TUserInterface.mFreeContainer(world.getClient().knowledgeBar);
		TUserInterface.mFreeContainer(world.getClient().gameState);
		TUserInterface.mFreeContainer(world.getClient().settings);
		if(world.getClient().knowledgeTree != null)
			TUserInterface.mFreeContainer(world.getClient().knowledgeTree);
		world.dispose();
		System.out.println("Disposed!");
	}

	@Override
	public void resize(int nw, int nh) {
		if(world.getClient().knowledgeTree != null) {
			TUserInterface.mFreeContainer(world.getClient().knowledgeTree);
			world.getClient().knowledgeTree = new TKnowledgeTreeInterface();
			TUserInterface.mAllocContainer(world.getClient().knowledgeTree);
		}
		if(!world.getClient().settings.getState())
			world.getClient().settings.setState(false);
	}
	
}
