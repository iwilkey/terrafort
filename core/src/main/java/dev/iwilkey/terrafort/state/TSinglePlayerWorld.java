package dev.iwilkey.terrafort.state;

import java.util.concurrent.ThreadLocalRandom;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.TState;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.gui.interfaces.TSettingsInterface;
import dev.iwilkey.terrafort.gui.widgets.TKnowledgeTreeWidget;
import dev.iwilkey.terrafort.obj.mob.TPlayer;
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
	
	private static TWorld                  world         = null;
	private static TKnowledgeTreeInterface knowledgeTree = null;
	private static TKnowledgeBarInterface  knowledgeBar  = null;
	
	private TGameStateInterface            gameState     = null;
	private TSettingsInterface             settings      = null;
	
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
		knowledgeBar = new TKnowledgeBarInterface();
		TUserInterface.mAllocContainer(knowledgeBar);
		gameState = new TGameStateInterface(world.getClient());
		TUserInterface.mAllocContainer(gameState);
		settings = new TSettingsInterface();
		TUserInterface.mAllocContainer(settings);
	}
	
	@Override
	public void render(float dt) {
		if(TInput.techTree) {
			if(knowledgeTree == null) {
				knowledgeTree = new TKnowledgeTreeInterface();
				TUserInterface.mAllocContainer(knowledgeTree);
				TGraphics.requestBlurState(true, 1.0f);
			} else {
				TUserInterface.mFreeContainer(knowledgeTree);
				knowledgeTree = null;
				TUserInterface.mFreePrompt();
				TGraphics.requestBlurState(false, 1.0f);
			}
			TInput.techTree = false;
		}
		world.render(dt);
	}

	@Override
	public void stop() {
		TUserInterface.mFreeContainer(knowledgeBar);
		TUserInterface.mFreeContainer(gameState);
		TUserInterface.mFreeContainer(settings);
		if(knowledgeTree != null)
			TUserInterface.mFreeContainer(knowledgeTree);
		world.dispose();
	}

	@Override
	public void resize(int nw, int nh) {
		if(knowledgeTree != null) {
			TUserInterface.mFreeContainer(knowledgeTree);
			knowledgeTree = new TKnowledgeTreeInterface();
			TUserInterface.mAllocContainer(knowledgeTree);
		}
		if(!settings.getState())
			settings.setState(false);
	}
	
	/**
	 * The player's knowledge bar.
	 */
	public static TKnowledgeBarInterface getKnowlegeBar() {
		return knowledgeBar;
	}
	
	/**
	 * Get the player's knowledge tree widget for rebuilding (if needed.) May return null.
	 */
	public static TKnowledgeTreeWidget getTree() {
		return knowledgeTree.tree;
	}
	
	/**
	 * Get the player.
	 */
	public static TPlayer getClient() {
		return world.getClient();
	}

}
