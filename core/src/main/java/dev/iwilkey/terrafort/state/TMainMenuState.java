package dev.iwilkey.terrafort.state;

import java.util.concurrent.ThreadLocalRandom;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.TEngine;
import dev.iwilkey.terrafort.TState;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.obj.world.TTerrain;
import dev.iwilkey.terrafort.obj.world.TWorld;
import dev.iwilkey.terrafort.ui.TUserInterface;
import dev.iwilkey.terrafort.ui.containers.interfaces.TMainMenuInterface;
import dev.iwilkey.terrafort.ui.containers.interfaces.TTerrafortCopyrightNotice;

/**
 * The main menu of Terrafort.
 * @author Ian Wilkey (iwilkey)
 */
public final class TMainMenuState implements TState {
	
	public static final float MOVEMENT_UPDATE_TIME  = 0.1f;
	public static final float DIRECTION_CHANGE_TIME = 4.0f;
	
	private TWorld             world;
	private TMainMenuInterface menu;
	private TTerrafortCopyrightNotice data;
	private float              px                    = 0;
	private float              py                    = 0;
	private float              dtx                   = 0;
	private float              dty                   = 0;
	private float              cpt                   = 0.0f;
	private float              mut                   = 0.0f;
	
	@Override
	public void start() {
		TGraphics.fadeIn(0.5f);
		TGraphics.setCameraSpeedToTarget(0.5f);
		TGraphics.POST_GAUSSIAN_BLUR.setPasses(16);
		TGraphics.POST_PROCESSING.addEffect(TGraphics.POST_GAUSSIAN_BLUR);
		world = new TWorld(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE - 1));
		locSwitch();
		menu = new TMainMenuInterface();
		menu.init();
		TUserInterface.mallocon(menu);
		data = new TTerrafortCopyrightNotice();
		data.init();
		TUserInterface.mallocon(data);
	}
	
	boolean start = false;
	
	@Override
	public void render() {
		cpt += TClock.dt();
		mut += TClock.dt();
		if(cpt > DIRECTION_CHANGE_TIME) {
			locSwitch();
			System.out.println("Chunks in memory: " + TEngine.mChunksInMemory);
			System.out.println("Chunks dormant: " + TEngine.mChunksDormant);
			System.out.println("Physical bodies: " + TEngine.mPhysicalBodies);
			System.out.println(" ");
			cpt = 0.0f;
		}
		if(mut > MOVEMENT_UPDATE_TIME) {
			px += dtx;
			py += dty;
			TGraphics.setCameraTargetPosition(px, py);
			mut = 0.0f;
		}
		world.update((float)TClock.dt(), Math.round(px / TTerrain.TILE_WIDTH), Math.round(py / TTerrain.TILE_HEIGHT));
	}

	@Override
	public void stop() {
		TGraphics.POST_PROCESSING.removeAllEffects();
		TUserInterface.freecon(menu);
		TUserInterface.freecon(data);
		world.dispose();
	}
	
	/**
	 * Generate a new random location and scroll vector.
	 */
	private void locSwitch() {
		px  = ThreadLocalRandom.current().nextInt(0, 65536);
		py  = ThreadLocalRandom.current().nextInt(0, 65536);
		dtx = (float)ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
		dty = (float)ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
		TGraphics.forceCameraPosition(px, py);
	}

}
