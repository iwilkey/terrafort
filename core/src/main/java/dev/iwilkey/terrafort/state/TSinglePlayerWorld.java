package dev.iwilkey.terrafort.state;

import java.util.concurrent.ThreadLocalRandom;

import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.TState;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gui.TUserInterface;
import dev.iwilkey.terrafort.gui.interfaces.TKnowledgeBarInterface;
import dev.iwilkey.terrafort.gui.interfaces.TTechTreeInterface;
import dev.iwilkey.terrafort.persistent.TPersistent;
import dev.iwilkey.terrafort.world.TWorld;

/**
 * A single-player game of Terrafort.
 * @author Ian Wilkey (iwilkey)
 */
public final class TSinglePlayerWorld implements TState {
	
	private TKnowledgeBarInterface      knowledgeBar  = null;
	private TTechTreeInterface knowledgeTree = null;
	private TWorld             world         = null;
	
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
	}

	@Override
	public void render(float dt) {
		if(TInput.techTree) {
			if(knowledgeTree == null) {
				knowledgeTree = new TTechTreeInterface();
				TUserInterface.mAllocContainer(knowledgeTree);
			} else {
				TUserInterface.mFreeContainer(knowledgeTree);
				knowledgeTree = null;
			}
			TInput.techTree = false;
		}
		world.render(dt);
	}

	@Override
	public void stop() {
		TUserInterface.mFreeContainer(knowledgeBar);
		if(knowledgeTree != null)
			TUserInterface.mFreeContainer(knowledgeTree);
		world.dispose();
	}

	@Override
	public void resize(int nw, int nh) {
		if(knowledgeTree != null) {
			TUserInterface.mFreeContainer(knowledgeTree);
			knowledgeTree = new TTechTreeInterface();
			TUserInterface.mAllocContainer(knowledgeTree);
		}
	}

}
