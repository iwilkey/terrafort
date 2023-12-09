package dev.iwilkey.terrafort.state;

import java.util.concurrent.ThreadLocalRandom;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.TState;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.obj.entity.lifeform.TPlayer;
import dev.iwilkey.terrafort.obj.world.TSinglePlayerWorld;
import dev.iwilkey.terrafort.ui.TAnchor;
import dev.iwilkey.terrafort.ui.TUserInterface;
import dev.iwilkey.terrafort.ui.containers.TEngineMonitor;

/**
 * A single-player game of Terrafort.
 * @author Ian Wilkey (iwilkey)
 */
public class TSinglePlayer implements TState {
	
	TSinglePlayerWorld world;
	TEngineMonitor     monitor;
	
	@Override
	public void start() {
		TGraphics.setGlLineWidth(1.0f);
		TGraphics.setCameraSpeedToTarget(4.0f);
		world = new TSinglePlayerWorld(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE - 1));
		world.addObject(new TPlayer(world));
		monitor = new TEngineMonitor();
		monitor.setAnchor(TAnchor.TOP_LEFT);
		monitor.setExternalPadding(4, 0, 0, 4);
		TUserInterface.addContainer(monitor);
	}

	@Override
	public void render() {
		world.update((float)TClock.dt());
		world.render();
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
		TUserInterface.removeContainer(monitor);
		world.dispose();
	}

}
