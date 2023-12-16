package dev.iwilkey.terrafort.state;

import java.util.concurrent.ThreadLocalRandom;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.TState;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.gfx.TRenderableRaw;
import dev.iwilkey.terrafort.gfx.TTerrainRenderer;
import dev.iwilkey.terrafort.math.TInterpolator;
import dev.iwilkey.terrafort.obj.world.TWorld;

/**
 * The main menu of Terrafort.
 * @author Ian Wilkey (iwilkey)
 */
public final class TMainMenuState implements TState {
	
	public static final float MOVEMENT_UPDATE_TIME  = 0.1f;
	public static final float DIRECTION_CHANGE_TIME = 4.0f;
	
	private TWorld                world;
	private Texture               terrafortTile = new Texture(Gdx.files.internal("terrafort-title.png"));
	private TRenderableRaw logo          = new TRenderableRaw(terrafortTile);
	private float                 px            = 0;
	private float                 py            = 0;
	private float                 dtx           = 0;
	private float                 dty           = 0;
	private float                 cpt           = 0.0f;
	private float                 mut           = 0.0f;
	
	@Override
	public void start() {
		logo.width  = 328 * 2;
		logo.height = 38 * 2;
		logo.x      = 0;
		logo.y      = 0;
		dtx = (float)ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
		dty = (float)ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
		TGraphics.fadeIn(0.5f);
		TGraphics.setCameraSpeedToTarget(0.5f);
		world = new TWorld(ThreadLocalRandom.current().nextInt(0, Integer.MAX_VALUE - 1));
	}
	
	boolean start = false;
	
	@Override
	public void render() {
		cpt += TClock.dt();
		mut += TClock.dt();
		if(cpt > DIRECTION_CHANGE_TIME) {
			px = ThreadLocalRandom.current().nextInt(0, 65536);
			py = ThreadLocalRandom.current().nextInt(0, 65536);
			TGraphics.forceCameraPosition(px, py);
			dtx = (float)ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
			dty = (float)ThreadLocalRandom.current().nextDouble(-1.0, 1.0);
			if(!start) {
				// add UI containers.
				start = true;
			}
			cpt = 0.0f;
			
		}
		if(mut > MOVEMENT_UPDATE_TIME) {
			px += dtx;
			py += dty;
			TGraphics.setCameraTargetPosition(px, py);
			mut = 0.0f;
		}
		logo.x = (int)((Gdx.graphics.getWidth() / 2f) - (logo.width / 2f));
		logo.y = Gdx.graphics.getHeight() - (logo.height * 2);
		TGraphics.draw(logo);
		world.update((float)TClock.dt(), Math.round(px / TTerrainRenderer.TERRAIN_TILE_WIDTH), Math.round(py / TTerrainRenderer.TERRAIN_TILE_HEIGHT));
	}

	@Override
	public void stop() {
		terrafortTile.dispose();
		TGraphics.POST_PROCESSING.removeAllEffects();
		world.dispose();
	}

}
