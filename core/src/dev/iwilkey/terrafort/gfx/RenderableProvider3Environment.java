package dev.iwilkey.terrafort.gfx;

import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalShadowLight;

public final class RenderableProvider3Environment extends Environment {
	
	private final DirectionalShadowLight mainLightSource;
	
	public RenderableProvider3Environment() {
		set(new ColorAttribute(ColorAttribute.AmbientLight, 0.8f, 0.7f, .6f, 1f));
		add((mainLightSource = new DirectionalShadowLight(
	    		Renderer.SHADOW_MAP_WIDTH, 
	    		Renderer.SHADOW_MAP_HEIGHT, 
	    		Renderer.SHADOW_VIEWPORT_WIDTH, 
	    		Renderer.SHADOW_VIEWPORT_HEIGHT, 
	    		Renderer.SHADOW_NEAR, 
	    		Renderer.SHADOW_FAR)).set(1f, 1f, 1f, 40.0f, -35f, -35f)); 
		set(new ColorAttribute(ColorAttribute.Fog, 0.1f, 0.1f, 0.1f, 1f));
		shadowMap = mainLightSource;
	}
	
	public DirectionalShadowLight getShadowLight() {
		return mainLightSource;
	}
	
}
