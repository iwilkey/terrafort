package dev.iwilkey.terrafort.state;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.TInput;
import dev.iwilkey.terrafort.TState;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.obj.entity.lifeform.TPlayer;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * @author Ian Wilkey (iwilkey)
 */
public class TDebugState implements TState {
	
	TWorld world;
	
	@Override
	public void start() {
		TGraphics.setGlLineWidth(1.0f);
		TGraphics.setCameraSpeedToTarget(4.0f);
		world   = new TWorld(512);
		world.addObject(new TPlayer(world));
	}

	@Override
	public void render() {
		world.update((float)TClock.dt());
		world.render();
		/*
		if(Gdx.input.isButtonJustPressed(Buttons.LEFT)) {
			final Vector2 mouseWorldTileCoords = TMath.translateScreenToTileCoordinates(Gdx.input.getX(), Gdx.input.getY());
			world.setTileHeightAt((int)mouseWorldTileCoords.x, (int)mouseWorldTileCoords.y, 1);
			world.addPointLight((int)mouseWorldTileCoords.x * TTerrainRenderer.TERRAIN_TILE_WIDTH, 
								(int)mouseWorldTileCoords.y * TTerrainRenderer.TERRAIN_TILE_HEIGHT, 
								64, new Color().set(0xffffff34));
		}
		*/
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
