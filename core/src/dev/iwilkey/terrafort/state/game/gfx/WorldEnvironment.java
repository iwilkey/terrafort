package dev.iwilkey.terrafort.state.game.gfx;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;

public class WorldEnvironment extends Environment {
	
	public WorldEnvironment() {
		set(new ColorAttribute(ColorAttribute.AmbientLight, 0.6f, 0.6f, 0.6f, 1f));
		set(new ColorAttribute(ColorAttribute.Fog, 0.1f, 0.1f, 0.1f, 1f));
		add(new DirectionalLight().set(1f, 1f, 1f, -1, -1, -1));
	}
	
}
