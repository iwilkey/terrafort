package dev.iwilkey.terrafort.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;

import dev.iwilkey.terrafort.TClock;
import dev.iwilkey.terrafort.TState;
import dev.iwilkey.terrafort.gfx.TGraphics;
import dev.iwilkey.terrafort.obj.TPlayer;
import dev.iwilkey.terrafort.obj.TWorld;

/**
 * @author Ian Wilkey (iwilkey)
 */
public class TDebugState implements TState {
	
	TWorld world;
	
	@Override
	public void start() {
		TGraphics.setGlLineWidth(1.0f);
		TGraphics.setCameraSpeedToTarget(4.0f);
		world   = new TWorld();
		world.addObject(new TPlayer(world));
	}

	@Override
	public void render() {
		world.update((float)TClock.dt());
		world.render();
		if(Gdx.input.isButtonJustPressed(Buttons.RIGHT)) {
			world.addPointLight(
					Math.round(world.getMousePositionInWorld().x), 
					Math.round(world.getMousePositionInWorld().y), 
					48, 
					new Color().set(0xeedd82aa)
			);
		}
		if(Gdx.input.isKeyJustPressed(Keys.Q))
			TGraphics.changeCameraZoom(false);
		if(Gdx.input.isKeyJustPressed(Keys.E))
			TGraphics.changeCameraZoom(true);
	}

	@Override
	public void stop() {

	}

}
