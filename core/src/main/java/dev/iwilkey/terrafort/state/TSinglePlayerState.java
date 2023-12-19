package dev.iwilkey.terrafort.state;

import java.util.concurrent.ThreadLocalRandom;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.TState;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.obj.entity.mob.TPlayer;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * A single-player game of Terrafort.
 * @author Ian Wilkey (iwilkey)
 */
public class TSinglePlayerState implements TState {
	
	TPlayer player;
	TWorld  world;

	@Override
	public void start() {
		TGraphics.setGlLineWidth(1.0f);
		TGraphics.setCameraSpeedToTarget(4.0f);
		TGraphics.POST_PROCESSING.addEffect(TGraphics.POST_FXAA);
		world = new TWorld(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE - 1));
		player = (TPlayer)world.addObject(new TPlayer(world));
		TGraphics.fadeIn(0.5f);
	}

	@Override
	public void render() {
		world.update((float)TClock.dt());
		if(TInput.zoomOut) {
			TGraphics.requestCameraZoomChange(false);
			TInput.zoomOut = false;
		}
		if(TInput.zoomIn) {
			TGraphics.requestCameraZoomChange(true);
			TInput.zoomIn = false;
		}
	}

	@Override
	public void stop() {
		world.dispose();
	}

}
